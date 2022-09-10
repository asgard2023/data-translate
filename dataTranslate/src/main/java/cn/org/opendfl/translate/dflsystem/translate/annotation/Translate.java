package cn.org.opendfl.translate.dflsystem.translate.annotation;

import java.lang.annotation.*;

/**
 * 返回方法启动翻译功能
 *
 * @author chenjh
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Translate {
    String field() default "";
}
