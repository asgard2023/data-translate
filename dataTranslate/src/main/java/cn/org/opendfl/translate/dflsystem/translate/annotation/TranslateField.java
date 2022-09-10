package cn.org.opendfl.translate.dflsystem.translate.annotation;

import java.lang.annotation.*;

/**
 * 翻译属性
 *
 * @author chenjh
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface TranslateField {

}
