package cn.org.opendfl.translate.dflsystem.translate;

import cn.org.opendfl.translate.dflsystem.biz.ITrTransDataBiz;
import cn.org.opendfl.translate.dflsystem.po.TrTransDataPo;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    @Autowired
    public void setTrTransDataBiz(ITrTransDataBiz trTransDataBiz) {
        TranslateTrans.trTransDataBiz = trTransDataBiz;
    }

    private static ITrTransDataBiz getBiz() {
        return trTransDataBiz;
    }


    private static Cache<String, Map<String, String>> dataIdFieldMap = CacheBuilder.newBuilder().expireAfterWrite(1, TimeUnit.MINUTES)
            .maximumSize(2000).build();

    public static Map<String, Map<String, String>> getDataIdFieldMap(IdInfoVo idInfoVo, String lang, List<Object> idList, List<String> idLangList) {
        List<String> fields = idInfoVo.getTransFields();
        Map<String, Map<String, String>> dataIdFieldMap = null;

        Map<String, Map<String, String>> dataIdCacheMap = TranslateTrans.dataIdFieldMap.getAllPresent(idLangList);
        List<Object> unexistIdList = idList.stream().filter(id -> !dataIdCacheMap.containsKey(id + "_" + lang)).collect(Collectors.toList());
        if (idInfoVo.getIdType() == IdType.STRING.getType()) {
            dataIdFieldMap = getBiz().getValueMapCacheByIdStr(idInfoVo.getLangDataTypeId(), lang, fields, unexistIdList);
        } else {
            dataIdFieldMap = getBiz().getValueMapCacheByIdNum(idInfoVo.getLangDataTypeId(), lang, fields, unexistIdList);
        }
        if (MapUtils.isNotEmpty(dataIdFieldMap)) {
            TranslateTrans.dataIdFieldMap.putAll(dataIdFieldMap);
        }
        if (MapUtils.isNotEmpty(dataIdCacheMap)) {
            dataIdFieldMap.putAll(dataIdCacheMap);
        }

        return dataIdFieldMap;
    }

    public static void putDataIdField(Object id, String lang, String field, String content) {
        String key = id + "_" + lang;
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
        Object id = dataNid != null ? dataNid : dataSid;
        TranslateTrans.putDataIdField(id, lang, field, content);
        trTransDataBiz.saveTrTransData(trTransDataPo);
    }
}
