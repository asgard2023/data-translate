package cn.org.opendfl.translate.dflsystem.translate;

import cn.org.opendfl.translate.dflsystem.biz.ITrTransDataBiz;
import cn.org.opendfl.translate.dflsystem.po.TrTransDataPo;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 数据翻译
 * <p>
 * 优先取本地方便(1分钟)，然后取数据库，没有则自动调用翻译api接口进行翻译，并存到数据库
 *
 * @author chenjh
 */
@Slf4j
@Component
public class TranslateTrans {
    private TranslateTrans() {

    }

    private static ITrTransDataBiz trTransDataBiz;

    private static RedisTemplate<String, String> redisTemplateString;

    @Resource
    public void setTrTransDataBiz(ITrTransDataBiz trTransDataBiz) {
        TranslateTrans.trTransDataBiz = trTransDataBiz;
    }

    @Resource(name = "redisTemplateString")
    public void setRedisTemplateString(RedisTemplate<String, String> redisTemplateString) {
        TranslateTrans.redisTemplateString = redisTemplateString;
    }

    private static ITrTransDataBiz getBiz() {
        return trTransDataBiz;
    }

    public static long getTransDataCacheSize() {
        return dataIdFieldMap.size();
    }

    private static final Cache<String, Map<String, String>> dataIdFieldMap = CacheBuilder.newBuilder().expireAfterWrite(1, TimeUnit.MINUTES)
            .maximumSize(3000).build();


    /**
     * 翻译数据redis缓存时间20分钟
     * exp: 5,10,20,30,60，可以10分钟，20分1趾，30分钟的缓存
     */
    public static final int REDIS_KEY_TRANS_DATA_EXPIRE_MINUTE = 20;
    public static final Cache<String, Long> redisKeyMap = CacheBuilder.newBuilder().expireAfterWrite(REDIS_KEY_TRANS_DATA_EXPIRE_MINUTE, TimeUnit.MINUTES)
            .maximumSize(300).build();

    public static final int TIME_MINUTE_IN_MILLIS = 60000;


    /**
     * 确保redis缓存时段内，timeValue一样，以便于使hashKey也一样
     *
     * @param curTime
     * @return
     */
    public static int getTimeValue(long curTime) {
        //算出每小时的分钟数对应的10分钟数，以便于使redisKey按20分钟缓存一份
        Long timeMinute = curTime / TIME_MINUTE_IN_MILLIS % 60;
        return timeMinute.intValue() % 60 / REDIS_KEY_TRANS_DATA_EXPIRE_MINUTE;
    }


    private static Map<String, Map<String, String>> getDataIdFieldCache(final IdInfoVo idInfoVo, final String field, final String lang, final List<Object> idList, final int timeValue) {
        final Map<String, Map<String, String>> dataIdFieldMap = new ConcurrentHashMap<>(idList.size());
        List idStringList = idList.stream().map(t -> "" + t).collect(Collectors.toList());
        String id;
        String content;

        final String redisKeyField = getRedisKeyField(idInfoVo.getCode(), field, lang, timeValue);
        final List<String> contentList = redisTemplateString.opsForHash().multiGet(redisKeyField, idStringList);
        Map<String, String> fieldValueMap = null;
        for (int i = 0; i < idStringList.size(); i++) {
            id = (String) idStringList.get(i);
            String key = id + "_" + lang;
            content = contentList.get(i);
            if (content != null) {
                fieldValueMap = dataIdFieldMap.computeIfAbsent(key, k -> new HashMap<>());
                fieldValueMap.put(field, content);
            }
        }

        return dataIdFieldMap;
    }

    /**
     * 更新redis缓存
     *
     * @param idInfoVo
     * @param lang
     * @param timeValue
     * @param unexistIdList
     * @param dataIdFieldMap
     */
    private static void putDataIdFieldCache(final IdInfoVo idInfoVo, String field, final String lang, final int timeValue, final List<Object> unexistIdList, Map<String, Map<String, String>> dataIdFieldMap) {
        String key;
        String content;

        final String redisKeyField = getRedisKeyField(idInfoVo.getCode(), field, lang, timeValue);
        final Map<String, String> idContentMap = new HashMap<>();
        for (Object id : unexistIdList) {
            key = id + "_" + lang;
            Map<String, String> fieldValueMap = dataIdFieldMap.get(key);
            if (fieldValueMap == null) {
                continue;
            }
            content = fieldValueMap.get(field);
            if (content != null) {
                idContentMap.put("" + id, content);
            }
        }

        if (MapUtils.isNotEmpty(idContentMap)) {
            redisTemplateString.opsForHash().putAll(redisKeyField, idContentMap);
            expireTimeHashCache(redisKeyField, REDIS_KEY_TRANS_DATA_EXPIRE_MINUTE);
        }
    }

    public static final String getRedisKeyField(final String code, final String field, final String lang, final int timeValue) {
        return "trans:" + code + ":" + lang + ":" + timeValue + ":" + field;
    }

    public static void evictDataIdFieldCache(final String transTypeCode, final String field, final String lang, final int timeValue, final List<Object> idList) {
        String redisKey = getRedisKeyField(transTypeCode, field, lang, timeValue);
        for(Object id: idList) {
            redisTemplateString.opsForHash().delete(redisKey, ""+id);
        }

    }

    public static void evictDataIdFieldCache(final String transTypeCode, final String field, final String lang, final Object id) {
        if (id == null) {
            return;
        }
        if (transTypeCode == null) {
            return;
        }
        int timeValue = getTimeValue(System.currentTimeMillis());
        evictDataIdFieldCache(transTypeCode, field, lang, timeValue, Arrays.asList(id));
    }


    /**
     * 不能用于含有大量redisKey的数据集处理，比如含userId或roomId等
     *
     * @param redisKey
     * @param minute
     */
    public static void expireTimeHashCache(String redisKey, int minute) {
        if (redisKeyMap.getIfPresent(redisKey) == null) {
            redisKeyMap.put(redisKey, System.currentTimeMillis());
            redisTemplateString.expire(redisKey, minute, TimeUnit.MINUTES);
        }
    }


    public static Map<String, Map<String, String>> getDataIdFieldMap(final IdInfoVo idInfoVo, final String lang, final List<Object> idList, final List<String> idLangList) {
        List<String> fields = idInfoVo.getTransFields();
        Map<String, Map<String, String>> dataIdFieldResultMap = new HashMap<>(idList.size());
        Map<String, Map<String, String>> dataIdFieldDbMap = null;
        int timeValue = getTimeValue(System.currentTimeMillis());

        //支持本地缓存
        Map<String, Map<String, String>> dataIdFieldLocalMap = TranslateTrans.dataIdFieldMap.getAllPresent(idLangList);
        for (String field : fields) {
            List<Object> unexistIdList = idList.stream().filter(id -> {
                Map<String, String> fieldMap = dataIdFieldLocalMap.get(id + "_" + lang);
                return fieldMap == null || !fieldMap.containsKey(field);
            }).collect(Collectors.toList());

            //支持redis缓存
            Map<String, Map<String, String>> dataIdFieldRedisMap = getDataIdFieldCache(idInfoVo, field, lang, unexistIdList, timeValue);

            //查询数据库
            unexistIdList = unexistIdList.stream().filter(id -> !dataIdFieldRedisMap.containsKey(id + "_" + lang)).collect(Collectors.toList());
            if (idInfoVo.getIdType() == IdType.STRING.getType()) {
                dataIdFieldDbMap = getBiz().getValueMapCacheByIdStr(idInfoVo.getTransTypeId(), lang, Arrays.asList(field), unexistIdList);
            } else {
                dataIdFieldDbMap = getBiz().getValueMapCacheByIdNum(idInfoVo.getTransTypeId(), lang, Arrays.asList(field), unexistIdList);
            }

            //数据库查到后，同时更新本地缓存，redis缓存
            if (MapUtils.isNotEmpty(dataIdFieldDbMap)) {
                putDataIdFieldAll(dataIdFieldResultMap, dataIdFieldDbMap);
                putDataIdFieldCache(idInfoVo, field, lang, timeValue, unexistIdList, dataIdFieldDbMap);
                putDataIdFieldAll(dataIdFieldDbMap);
            }
            if (MapUtils.isNotEmpty(dataIdFieldLocalMap)) {
                putDataIdFieldAll(dataIdFieldResultMap, dataIdFieldLocalMap);
            }

            //redis查到后，更新本地缓存
            if (MapUtils.isNotEmpty(dataIdFieldRedisMap)) {
                putDataIdFieldAll(dataIdFieldResultMap, dataIdFieldRedisMap);
                putDataIdFieldAll(dataIdFieldRedisMap);
            }
        }

        return dataIdFieldResultMap;
    }

    public static void putDataIdFieldAll(Map<String, Map<String, String>> dataIdFieldDbMap) {
        Set<Map.Entry<String, Map<String, String>>> sets = dataIdFieldDbMap.entrySet();
        for (Map.Entry<String, Map<String, String>> entity : sets) {
            String key = entity.getKey();
            Map<String, String> fieldMap = dataIdFieldMap.getIfPresent(key);
            if (fieldMap != null) {
                fieldMap.putAll(entity.getValue());
            } else {
                fieldMap = entity.getValue();
            }
            dataIdFieldMap.put(key, fieldMap);
        }
    }

    public static void putDataIdFieldAll(Map<String, Map<String, String>> dataIdFieldDbMap, Map<String, Map<String, String>> dataIdFieldDbMapNew) {
        Set<Map.Entry<String, Map<String, String>>> sets = dataIdFieldDbMapNew.entrySet();
        for (Map.Entry<String, Map<String, String>> entity : sets) {
            String key = entity.getKey();
            Map<String, String> fieldMap = dataIdFieldDbMap.get(key);
            if (fieldMap != null) {
                fieldMap.putAll(entity.getValue());
            } else {
                fieldMap = entity.getValue();
            }
            dataIdFieldDbMap.put(key, fieldMap);
        }
    }

    public static void putDataIdField(Object dataId, String lang, String field, String content) {
        String key = dataId + "_" + lang;
        Map<String, String> fieldMap = dataIdFieldMap.getIfPresent(key);
        if (fieldMap == null) {
            fieldMap = new HashMap<>();
        }
        fieldMap.put(field, content);
        dataIdFieldMap.put(key, fieldMap);
    }

    public static void autoSaveTransResult(Integer dataTypeId, Long dataNid, String dataSid, String field, String lang, String content) {
        TrTransDataPo trTransDataPo = new TrTransDataPo();
        trTransDataPo.setTransTypeId(dataTypeId);
        trTransDataPo.setCode(field);
        trTransDataPo.setDataSid(dataSid);
        trTransDataPo.setDataNid(dataNid);
        trTransDataPo.setLang(lang);
        trTransDataPo.setContent(content);
        trTransDataPo.setStatus(1);
        trTransDataPo.setIfDel(0);
        Object dataId = dataNid != null ? dataNid : dataSid;
        TranslateTrans.putDataIdField(dataId, lang, field, content);
        trTransDataBiz.saveTrTransData(trTransDataPo);
    }
}
