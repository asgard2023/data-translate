package cn.org.opendfl.translate.dflsystem.translate;


import cn.org.opendfl.translate.base.RequestUtils;
import cn.org.opendfl.translate.dflsystem.biz.ITrTransTypeBiz;
import cn.org.opendfl.translate.dflsystem.biz.ITranslateBiz;
import cn.org.opendfl.translate.dflsystem.po.TrTransTypePo;
import cn.org.opendfl.translate.dflsystem.translate.annotation.TranslateField;
import cn.org.opendfl.translate.dflsystem.translate.annotation.TranslateType;
import cn.org.opendfl.translate.utils.CommUtils;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
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


    private static Cache<String, IdInfoVo> classTransTypes = CacheBuilder.newBuilder().expireAfterWrite(24, TimeUnit.HOURS)
            .maximumSize(1000).build();


    @Autowired
    public void setTransTypeBiz(ITrTransTypeBiz trTransTypeBiz) {
        TranslateUtil.trTransTypeBiz = trTransTypeBiz;
    }

    @Autowired
    public void setTranslateBiz(ITranslateBiz translateBiz) {
        TranslateUtil.translateBiz = translateBiz;
    }

    /**
     * 获取类的注解@TranslateType信息
     *
     * @param clazz 翻译PO对象类
     * @return
     */
    public static IdInfoVo getTranslateType(Class<?> clazz) {
        String className = clazz.getName();
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
                autoSaveTransType(idInfoVo);
                classTransTypes.put(className, idInfoVo);
                return idInfoVo;
            }
        }
        return idInfoVo;
    }

    private static void autoSaveTransType(IdInfoVo idInfoVo) {
        Integer transTypeId = trTransTypeBiz.getTransTypeId(idInfoVo.getCode());
        if (transTypeId == null) {
            TrTransTypePo trTransTypePo = new TrTransTypePo();
            trTransTypePo.setCode(idInfoVo.getCode());
            trTransTypePo.setIdType(idInfoVo.getIdType());
            trTransTypePo.setIdField(idInfoVo.getIdField());
            trTransTypePo.setIfDel(0);
            trTransTypePo.setStatus(1);
            trTransTypeBiz.saveTrTransType(trTransTypePo);
            idInfoVo.setLangDataTypeId(trTransTypePo.getId());
        } else {
            idInfoVo.setLangDataTypeId(transTypeId);
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
                fields[i].setAccessible(true);
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
     * 翻译转换处理
     * <p>
     * 支持翻译时自动保存到数据库
     *
     * @param lang         目标语言
     * @param list         数据list
     * @param isTransField 是否翻译后修改属性值
     * @author chenjh
     */
    public static void transform(String lang, List<?> list, boolean isTransField) {
        if (StringUtils.isEmpty(lang) || CollectionUtils.isEmpty(list)) {
            return;
        }
        if (LangCodes.ZH.equals(lang)) {
            return;//默认中文不用翻译
        }
        Class<?> z = list.get(0).getClass();
        String className = z.getSimpleName();
        IdInfoVo idInfoVo = getTranslateType(z);
        if (idInfoVo == null) {
            log.debug("----transform--className={} idInfoVo is null", className);
            return;
        }
        List<String> fields = idInfoVo.getTransFields();
        if (CollectionUtils.isEmpty(fields)) {
            log.debug("----transform--className={} transFields empty", className);
            return;
        }


        List<Object> idList = getListIds(list, idInfoVo);
        if (CollectionUtils.isEmpty(idList)) {
            log.debug("----transform--idList empty");
            return;
        }

        List<String> idLangList = idList.stream().map(id -> id + "_" + lang).collect(Collectors.toList());
        Map<String, Map<String, String>> dataIdFieldMap = TranslateTrans.getDataIdFieldMap(idInfoVo, lang, idList, idLangList);

        int count = 0;
        Long dataNid = null;
        String dataSid = null;
        Map<String, String> fieldMap = null;
        for (int i = 0; i < list.size(); i++) {
            Object obj = list.get(i);
            Object id = idList.get(i);
            if (id == null) {
                continue;
            }
            if (idInfoVo.getIdType() == IdType.STRING.getType()) {
                dataSid = (String) id;
            } else {
                dataNid = CommUtils.getLong(id);
            }
            fieldMap = dataIdFieldMap.get(id + "_" + lang);
            if (fieldMap == null) {
                fieldMap = new HashMap<>();
            }
            for (String field : fields) {
                try {
                    transUnexistField(lang, isTransField, idInfoVo, dataNid, dataSid, fieldMap, obj, field);
                    count++;
                } catch (Exception e) {
                    log.warn("----transform--lang={} className={} field={} id={} error={}", lang, className, field, id, e.getMessage());
                    throw new RuntimeException("field " + field + " error=" + e.getMessage());
                }
            }
        }
        log.debug("----transform--lang={} clazzName={} fields={} count={}", lang, className, fields, count);
    }

    private static void transUnexistField(String lang, boolean isTransField, IdInfoVo idInfoVo, Long dataNid, String dataSid, Map<String, String> fieldMap, Object obj, String field)
            throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        String content = fieldMap.get(field);
//                    log.info("----transUnexistField--lang={} field={} fieldMap={}", lang, field, fieldMap);
        if (StringUtils.isBlank(content)) {
            String value = BeanUtils.getProperty(obj, field);
            if (StringUtils.isNotBlank(value)) {
                content = translateBiz.getTransResult(value, lang);
                fieldMap.put(field, content);
                if (StringUtils.isNotBlank(content)) {
                    TranslateTrans.autoSaveTransResult(idInfoVo.getLangDataTypeId(), dataNid, dataSid, field, lang, content);
                }
            }
        }
        if (isTransField) {
            BeanUtils.setProperty(obj, field, content);
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
        transform(lang, list, true);
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
        log.info("----transformLangsByTrnasType--uri={} transTypeDist={}", request.getRequestURI(), transTypeDist);
        if (StringUtils.isNotBlank(transTypeDist)) {
            String[] langs = transTypeDist.split(",");
            for (String lang : langs) {
                TranslateUtil.transform(lang, list, false);
            }
        }
    }

}
