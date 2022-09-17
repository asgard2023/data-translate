package cn.org.opendfl.translate.dflsystem.biz.impl;

import cn.org.opendfl.translate.clients.TranslateBaiduRest;
import cn.org.opendfl.translate.clients.TranslateGoogleApi;
import cn.org.opendfl.translate.config.DataTranslateConfiguration;
import cn.org.opendfl.translate.dflsystem.biz.ITranslateBiz;
import cn.org.opendfl.translate.dflsystem.translate.LangType;
import cn.org.opendfl.translate.dflsystem.translate.TransType;
import cn.org.opendfl.translate.exception.FailedException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 翻译接口
 *
 * @author chenjh
 */
@Service
public class TranslateBiz implements ITranslateBiz {
    @Autowired
    private TranslateBaiduRest translateBaiduRest;

    @Autowired
    private TranslateGoogleApi translateGoogleApi;

    @Autowired
    private DataTranslateConfiguration dataTranslateConfiguration;

    public LangType getLangType(String lang) {
        LangType langType = null;
        if (StringUtils.equals(TransType.BAIDU.getType(), dataTranslateConfiguration.getTransType())) {
            langType = LangType.parseBaidu(lang);
        } else if (StringUtils.equals(TransType.GOOGLE.getType(), dataTranslateConfiguration.getTransType())) {
            langType = LangType.parseGoogle(lang);
        }
        else {
            langType = LangType.parse(lang);
        }
        if(langType == null){
            langType=LangType.getDefault();
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
