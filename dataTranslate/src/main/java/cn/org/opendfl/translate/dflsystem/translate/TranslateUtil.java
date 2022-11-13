package cn.org.opendfl.translate.dflsystem.translate;


import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.org.opendfl.translate.dflsystem.biz.ITrTransTypeBiz;
import cn.org.opendfl.translate.dflsystem.biz.ITranslateBiz;
import cn.org.opendfl.translate.dflsystem.po.TrTransTypePo;
import cn.org.opendfl.translate.dflsystem.translate.annotation.TranslateField;
import cn.org.opendfl.translate.dflsystem.translate.annotation.TranslateType;
import cn.org.opendfl.translate.dflsystem.vo.TransCountVo;
import cn.org.opendfl.translate.utils.CommUtils;
import cn.org.opendfl.translate.utils.RequestUtils;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * 数据翻译工具类
 *
 * @author chenjh
 */
@Slf4j
@Component
public class TranslateUtil {
    private TranslateUtil() {

    }

    private static ITrTransTypeBiz trTransTypeBiz;

    private static ITranslateBiz translateBiz;

    private static Executor asyncExecutor;


    private static Cache<String, IdInfoVo> classTransTypes = CacheBuilder.newBuilder().expireAfterWrite(24, TimeUnit.HOURS)
            .maximumSize(1000).build();


    @Resource
    public void setTransTypeBiz(ITrTransTypeBiz trTransTypeBiz) {
        TranslateUtil.trTransTypeBiz = trTransTypeBiz;
    }

    @Resource
    public void setTranslateBiz(ITranslateBiz translateBiz) {
        TranslateUtil.translateBiz = translateBiz;
    }

    @Resource
    public void setAsyncExecutor(Executor asyncExecutor) {
        TranslateUtil.asyncExecutor = asyncExecutor;
    }

    private static Map<String, TransCountVo> transCounterMap = new ConcurrentHashMap<>();


    public static IdInfoVo getTranslateType(String classSimipleName) {
        return classTransTypes.getIfPresent(classSimipleName);
    }

    /**
     * 获取类的注解@TranslateType信息
     *
     * @param clazz 翻译PO对象类
     * @return
     */
    public static IdInfoVo getTranslateType(Class<?> clazz) {
        String className = clazz.getSimpleName();
        IdInfoVo idInfoVo = classTransTypes.getIfPresent(className);
        if (idInfoVo == null) {
            TranslateType translateType = clazz.getAnnotation(TranslateType.class);
            if (translateType != null) {
                String code = translateType.code();
                if ("className".equals(code)) {
                    code = clazz.getSimpleName();
                }
                List<String> fields = getTranslateFields(clazz);
                idInfoVo = new IdInfoVo(code, translateType.idType(), translateType.idField(), fields, 0);
                autoSaveTransType(idInfoVo, translateType);
                classTransTypes.put(className, idInfoVo);
                return idInfoVo;
            }
        }
        return idInfoVo;
    }

    private static void autoSaveTransType(IdInfoVo idInfoVo, TranslateType translateType) {
        Integer transTypeId = trTransTypeBiz.getTransTypeId(idInfoVo.getCode());
        if (transTypeId == null) {
            TrTransTypePo trTransTypePo = new TrTransTypePo();
            trTransTypePo.setCode(idInfoVo.getCode());
            trTransTypePo.setTypeCode(translateType.typeCode());
            trTransTypePo.setIdType(idInfoVo.getIdType());
            trTransTypePo.setIdField(idInfoVo.getIdField());
            trTransTypePo.setIfDel(0);
            trTransTypePo.setStatus(1);
            trTransTypeBiz.saveTrTransType(trTransTypePo);
            idInfoVo.setTransTypeId(trTransTypePo.getId());
        } else {
            idInfoVo.setTransTypeId(transTypeId);
        }
    }


    /**
     * 获取带Translate注解的属性
     *
     * @param classz 翻译PO对象类
     * @return
     */
    private static List<String> getTranslateFields(Class<?> classz) {
        Field[] fields = classz.getDeclaredFields();
        Field field;
        List<String> fieldNames = new ArrayList<>();
        String fieldName = null;
        for (int i = 0; i < fields.length; i++) {
            try {
                fieldName = fields[i].getName();
                field = classz.getDeclaredField(fieldName);
                TranslateField column = field.getAnnotation(TranslateField.class); // 获取指定类型注解
                if (column != null) {
                    fieldNames.add(fieldName);
                }
            } catch (NoSuchFieldException e) {
                log.warn("----getTranslateFields--className={} i, field={} error={}", classz.getSimpleName(), i, fieldName, e.getMessage());
            }
        }
        return fieldNames;
    }

    /**
     * 统计使用量，记录最后一次异常
     *
     * @param source
     * @param lang
     * @param idInfoVo
     * @param errorMsg
     */
    private static void logCounter(String source, String lang, IdInfoVo idInfoVo, String errorMsg) {
        long time = System.currentTimeMillis();
        TransCountVo transCountVo = transCounterMap.get(idInfoVo.getCode());
        if (transCountVo == null) {
            transCountVo = new TransCountVo();
            transCountVo.setFirstTime(time);
            transCountVo.setTotalCounter(new AtomicInteger());
            transCountVo.setSourceCounter(new ConcurrentHashMap<>(10));
            transCounterMap.put(idInfoVo.getCode(), transCountVo);
        }
        transCountVo.setMaxTime(time);
        transCountVo.getTotalCounter().incrementAndGet();
        AtomicInteger sourceCounter = transCountVo.getSourceCounter().get(source + "_" + lang);
        if (sourceCounter == null) {
            sourceCounter = new AtomicInteger();
            transCountVo.getSourceCounter().put(source + "_" + lang, sourceCounter);
        }
        sourceCounter.incrementAndGet();
        if (StringUtils.isNotBlank(errorMsg)) {
            transCountVo.setErrorMsg(errorMsg);
            transCountVo.setErrorTime(time);
            if (transCountVo.getErrorCounter() == null) {
                transCountVo.setErrorCounter(new AtomicInteger());
            }
            transCountVo.getErrorCounter().incrementAndGet();
        }
    }

    public static List<TransCountVo> getTransCounterMap() {
        List<TransCountVo> list = new ArrayList<>(transCounterMap.size());
        Set<Map.Entry<String, TransCountVo>> entirySet = transCounterMap.entrySet();
        for (Map.Entry<String, TransCountVo> entry : entirySet) {
            TransCountVo transCountVo = entry.getValue();
            transCountVo.setCode(entry.getKey());
            list.add(transCountVo);
        }
        return list;
    }


    /**
     * 翻译转换处理
     * <p>
     * 支持翻译时自动保存到数据库
     *
     * @param langParam    目标语言
     * @param list         数据list
     * @param isTransField 是否翻译后修改属性值
     * @author chenjh
     */
    public static void transform(final String source, final String langParam, final List<?> list, final boolean isTransField) {
        if (StringUtils.isEmpty(langParam) || CollectionUtils.isEmpty(list)) {
            return;
        }
        final LangType langType = translateBiz.getLangType(source, langParam.trim());
        final String lang = langType.code;

        if (LangType.getDefault() == langType) {
            return;//默认中文不用翻译
        }
        final Class<?> z = list.get(0).getClass();
        IdInfoVo idInfoVo = getTranslateType(z);
        if (idInfoVo == null) {
            return;
        }
        final List<String> fields = idInfoVo.getTransFields();
        if (CollectionUtils.isEmpty(fields)) {
            log.warn("----transform--source={} className={} transFields empty", source, idInfoVo.getCode());
            return;
        }


        final List<Object> idList = getListIds(list, idInfoVo);
        if (CollectionUtils.isEmpty(idList)) {
            log.warn("----transform--source={} idList empty", source);
            return;
        }

        final List<String> idLangList = idList.stream().map(id -> id + "_" + lang).collect(Collectors.toList());
        final Map<String, Map<String, String>> dataIdFieldMap = TranslateTrans.getDataIdFieldMap(idInfoVo, lang, idList, idLangList);

        List<TransFieldVo> transFieldList = new ArrayList<>(list.size());
        for (int i = 0; i < list.size(); i++) {
            Object obj = list.get(i);
            Object id = idList.get(i);
            String key = id + "_" + lang;
            Map<String, String> fieldMap = dataIdFieldMap.computeIfAbsent(key, k -> new HashMap<>());
            List<TransFieldVo> transFieldVos = needTransField(idInfoVo, id, fieldMap, obj);
            if (CollectionUtils.isNotEmpty(transFieldVos)) {
                transFieldList.addAll(transFieldVos);
            }
        }

        TransErrorVo transErrorVo = translateFieldContentAsync(isTransField, lang, idInfoVo, dataIdFieldMap, transFieldList);
        logCounter(source, lang, idInfoVo, transErrorVo.getErrorMsg());
        log.debug("----transform--lang={} clazzName={} fields={}", lang, idInfoVo.getCode(), fields);
    }

    /**
     * 找出要进行翻译的数据
     *
     * @param idInfoVo
     * @param id
     * @param fieldMap
     * @param obj
     * @return
     */
    private static List<TransFieldVo> needTransField(IdInfoVo idInfoVo, Object id, Map<String, String> fieldMap, Object obj) {
        List<TransFieldVo> needTransFieldList = new ArrayList<>();
        TransFieldVo transFieldVo = null;
        for (String field : idInfoVo.getTransFields()) {
            String content = fieldMap.get(field);
            if (StringUtils.isBlank(content)) {
                String value = (String) ReflectUtil.getFieldValue(obj, field);
                if (StringUtils.isNotBlank(value)) {
                    transFieldVo = new TransFieldVo(obj, id, field, value, null);
                    needTransFieldList.add(transFieldVo);
                }
            }
        }
        return needTransFieldList;
    }

    /**
     * 异步并发翻译属内容
     *
     * @param isTransField   是否替换属性
     * @param lang           要翻译的语言
     * @param idInfoVo       数据类型
     * @param dataIdFieldMap 数据ID对应的属性map
     * @param transFieldList 要翻译的属性list
     * @return 返回一个异常信息
     */
    private static TransErrorVo translateFieldContentAsync(boolean isTransField, String lang, IdInfoVo idInfoVo, Map<String, Map<String, String>> dataIdFieldMap, List<TransFieldVo> transFieldList) {
        final TransErrorVo transErrorVo = new TransErrorVo();
        if (CollectionUtils.isEmpty(transFieldList)) {
            return transErrorVo;
        }
        CompletableFuture[] futureArray = transFieldList.stream()
                .map(data -> CompletableFuture
                        .runAsync(() -> {
                            try {
                                String key = data.getId() + "_" + lang;
                                Map<String, String> fieldMap = dataIdFieldMap.computeIfAbsent(key, k -> new HashMap<>());
                                transFieldContent(lang, isTransField, idInfoVo, fieldMap, data);
                            } catch (Exception e) {
                                log.warn("----transform--lang={} className={} field={} id={} error={}", lang, idInfoVo.getCode(), data.getId(), e.getMessage());
                                if (transErrorVo.getErrorMsg() == null) {
                                    transErrorVo.setErrorMsg(lang + ":" + data.getField() + ":" + data.getId() + ":" + e.getMessage());
                                }
                            }
                        }, asyncExecutor)).toArray(CompletableFuture[]::new);
        CompletableFuture.allOf(futureArray).join();
        return transErrorVo;
    }

    /**
     * 翻译属性内容
     *
     * @param lang         要翻译的语言
     * @param isTransField 是否替换属性
     * @param idInfoVo     数据类型
     * @param fieldMap     fieldMap
     * @param transFieldVo 翻译属性对象
     */
    private static void transFieldContent(String lang, boolean isTransField, IdInfoVo idInfoVo, Map<String, String> fieldMap, TransFieldVo transFieldVo) {
        Long dataNid = null;
        String dataSid = null;
        Object id = transFieldVo.getId();
        if (idInfoVo.getIdType() == IdType.STRING.getType()) {
            dataSid = (String) id;
        } else {
            dataNid = CommUtils.getLong(id);
        }


        String field = transFieldVo.getField();
        String content = translateBiz.getTransResult(transFieldVo.getValue(), lang);
        if (StringUtils.isNotBlank(content)) {
            fieldMap.put(field, content);
            TranslateTrans.autoSaveTransResult(idInfoVo.getTransTypeId(), dataNid, dataSid, field, lang, content);
            if (isTransField) {
                ReflectUtil.setFieldValue(transFieldVo.getObj(), field, content);
            }
        }
    }

    private static List<Object> getListIds(List<?> list, IdInfoVo idInfoVo) {
        String idFieldName = idInfoVo.getIdField();
        List<Object> idList = new ArrayList<>(list.size());
        for (Object obj : list) {
            String id = null;
            try {
                id = BeanUtils.getProperty(obj, idFieldName);
            } catch (Exception e) {
                log.warn("----transform--field={} id={} error={}", idFieldName, id, e.getMessage());
            }

            if (id != null) {
                if (IdType.TYPE_NUM == idInfoVo.getIdType()) {
                    idList.add(CommUtils.getLong(id));
                } else {
                    idList.add(id);
                }
            }
        }
        return idList;
    }

    public static void transform(HttpServletRequest request, List<?> list) {
        String lang = RequestUtils.getLang(request);
        String uri = request.getRequestURI();
        transform(uri, lang, list, true);
    }

    /**
     * 参数transTypeDist中的所有语全部语言类型分别进行一次国际化处理
     *
     * @param request 对参数transTypeDist中的所有语
     * @param list    数据列表
     * @author chenjh
     */
    public static void transformLangsByTrnasType(HttpServletRequest request, List<?> list) {
        String transTypeDist = request.getParameter("transTypeDist");
        log.debug("----transformLangsByTrnasType--uri={} transTypeDist={}", request.getRequestURI(), transTypeDist);
        String uri = request.getRequestURI();
        if (StringUtils.isNotBlank(transTypeDist)) {
            String[] langs = transTypeDist.split(",");
            for (String lang : langs) {
                TranslateUtil.transform(uri, lang, list, false);
            }
        }
    }

    /**
     * 用于向前端返回transFields
     *
     * @param clazz
     * @return
     */
    public static Map<String, Object> dictMap(Class<?> clazz) {
        return MapUtil.of("typeInfo", getTranslateType(clazz));
    }

}
