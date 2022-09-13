package cn.org.opendfl.translate.config;


import cn.org.opendfl.translate.base.MyPageInfo;
import cn.org.opendfl.translate.base.PageVO;
import cn.org.opendfl.translate.base.RequestParams;
import cn.org.opendfl.translate.dflsystem.translate.LangType;
import cn.org.opendfl.translate.dflsystem.translate.TranslateUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.connector.RequestFacade;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.aop.aspectj.MethodInvocationProceedingJoinPoint;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * @author chenjh
 * @Translate注解的方法拦截器，以便于快速支持返回值翻译
 */
@Component
@Aspect
@Slf4j
public class TranslateAspect {
    @Pointcut("@annotation(cn.org.opendfl.translate.dflsystem.translate.annotation.Translate)")
    public void translateCut() {

    }


    @AfterReturning(pointcut = "translateCut()", returning = "result")
    public void doAfterReturning(JoinPoint joinPoint, Object result) {
        MethodInvocationProceedingJoinPoint proceedingJoinPoint = (MethodInvocationProceedingJoinPoint) joinPoint;
        Object[] args = proceedingJoinPoint.getArgs();
        String lang = null;
        String uri = null;
        String firstStringParam = null;
        for (Object arg : args) {
            if (arg instanceof RequestFacade) {
                RequestFacade request = ((RequestFacade) arg);
                uri = request.getRequestURI();
                lang = (request.getParameter(RequestParams.LANG));
                if (StringUtils.isBlank(lang)) {
                    lang = request.getHeader(RequestParams.LANG);
                }
            } else if (arg instanceof String && LangType.SUPPORT_LANG.indexOf(arg + ",") >= 0) {
                firstStringParam = (String) arg;
            }
        }

        if (StringUtils.isBlank(lang)) {
            lang = firstStringParam;
        }

        String source = uri;
        try {
            if (source == null) {
                source = joinPoint.getSignature().getName();
            }
            translateResultByLang(source, lang, result);
        } catch (Exception e) {
            log.warn("-----translateResultByLang--lang={} uri={} error={}", lang, source, e.getMessage());
        }
    }

    /**
     * @param result
     * @param lang
     */
    private static void translateResultByLang(String source, String lang, Object result) {
        if (StringUtils.isNotBlank(lang)) {
            List list = null;
            if (result instanceof MyPageInfo) {
                list = ((MyPageInfo) result).getList();
            } else if (result instanceof PageVO) {
                Collection collection = ((PageVO) result).getRows();
                list = new ArrayList(collection);
            } else if (result instanceof List) {
                list = (List) result;
            } else {
                list = Arrays.asList(result);
            }
            TranslateUtil.transform(source, lang, list, true);
        }
    }
}
