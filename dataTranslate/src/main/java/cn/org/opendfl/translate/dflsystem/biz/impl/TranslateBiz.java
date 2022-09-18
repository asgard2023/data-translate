package cn.org.opendfl.translate.dflsystem.biz.impl;

import cn.org.opendfl.translate.clients.TranslateBaiduRest;
import cn.org.opendfl.translate.clients.TranslateGoogleApi;
import cn.org.opendfl.translate.config.DataTranslateConfiguration;
import cn.org.opendfl.translate.dflsystem.biz.ITranslateBiz;
import cn.org.opendfl.translate.dflsystem.translate.LangType;
import cn.org.opendfl.translate.dflsystem.translate.TransType;
import cn.org.opendfl.translate.exception.FailedException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 翻译接口
 *
 * @author chenjh
 */
@Slf4j
@Service
public class TranslateBiz implements ITranslateBiz {
    @Resource
    private TranslateBaiduRest translateBaiduRest;

    @Resource
    private TranslateGoogleApi translateGoogleApi;

    @Resource
    private DataTranslateConfiguration dataTranslateConfiguration;

    private static final int TIME_MINUTE_IN_MILLIS = 60000;
    private static final Map<String, Long> logTimeMap = new ConcurrentHashMap<>();

    public LangType getLangType(String source, String lang) {
        LangType langType = null;
        if (StringUtils.equals(TransType.BAIDU.getType(), dataTranslateConfiguration.getTransType())) {
            langType = LangType.parseBaidu(lang);
        } else if (StringUtils.equals(TransType.GOOGLE.getType(), dataTranslateConfiguration.getTransType())) {
            langType = LangType.parseGoogle(lang);
        } else {
            langType = LangType.parse(lang);
        }
        if (langType == null) {
            long curTime = System.currentTimeMillis();
            String key = source + "_" + lang;
            Long langTypeLogTime = logTimeMap.get(key);
            //用于减少日志输出量，即每1分钟只输出一次
            if (langTypeLogTime == null || curTime - langTypeLogTime > TIME_MINUTE_IN_MILLIS) {
                logTimeMap.put(key, curTime);
                log.warn("---getLangType--source={} lang={} invalid, use default", source, lang);
            }
            langType = LangType.getDefault();
        }
        return langType;
    }

    @Override
    public String getTransResult(String query, String to) {
        String defaultLang = dataTranslateConfiguration.getDefaultLang();
        return getTransResult(query, defaultLang, to);
    }

    public String getTransResult(String query, String from, String to) {
        String transType = dataTranslateConfiguration.getTransType();
        if (TransType.BAIDU.getType().equals(transType)) {
            return translateBaiduRest.getTransResult(query, from, to);
        } else if (TransType.GOOGLE.getType().equals(transType)) {
            return translateGoogleApi.getTransResult(query, from, to);
        } else {
            throw new FailedException("invalid transType:" + dataTranslateConfiguration.getTransType());
        }
    }
}
