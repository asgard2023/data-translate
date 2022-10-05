package cn.org.opendfl.translate.config;


import cn.org.opendfl.translate.base.MyPageInfo;
import cn.org.opendfl.translate.base.PageVO;
import cn.org.opendfl.translate.base.RequestUtils;
import cn.org.opendfl.translate.dflsystem.translate.TranslateUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
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
        HttpServletRequest request = getRequest();
        if (request == null) {
            return;
        }
        String lang = RequestUtils.getLang(request, null);
        String source = request.getRequestURI();
        try {
            if (source != null) {
                translateResultByLang(source, lang, result);
            }
        } catch (Exception e) {
            log.warn("-----translateResultByLang--lang={} uri={} error={}", lang, source, e.getMessage());
        }
    }

    public HttpServletRequest getRequest() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            return null;
        }
        return attributes.getRequest();
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
