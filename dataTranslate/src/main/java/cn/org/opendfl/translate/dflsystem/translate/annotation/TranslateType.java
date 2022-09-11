package cn.org.opendfl.translate.dflsystem.translate.annotation;


import cn.org.opendfl.translate.dflsystem.translate.IdType;

import java.lang.annotation.*;

/**
 * 翻译类型
 *
 * @author chenjh
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface TranslateType {
    /**
     * 默认className表示自动取类的simpleName
     * 比须是唯一编码，否则会引起错乱
     *
     * @return 编码
     */
    String code() default "className";

    /**
     * id属性名，默认id
     *
     * @return id属性名
     */
    String idField() default "id";

    /**
     * id类型
     *
     * @return 类型
     */
    int idType() default IdType.TYPE_NUM;

    /**
     * 类型编码
     * 仅做分类用，比如模块分类，微服务分类等
     * 仅在初始化时有用，数据库有了之后可以修改，不受此影响
     *
     * @return
     */
    String typeCode() default "";

    /**
     * 表名，用于
     *
     * @return tableName
     */
    String tableName() default "default";

    /**
     * 对该表的缓存类型
     *
     * @return 缓存数据量
     */
    int cacheSize() default 1000;

}
