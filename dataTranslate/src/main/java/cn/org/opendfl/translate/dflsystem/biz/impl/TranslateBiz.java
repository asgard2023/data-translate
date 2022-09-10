package cn.org.opendfl.translate.dflsystem.biz.impl;

import cn.org.opendfl.translate.clients.TranslateBaiduRest;
import cn.org.opendfl.translate.clients.TranslateGoogleApi;
import cn.org.opendfl.translate.config.DataTranslateConfiguration;
import cn.org.opendfl.translate.dflsystem.biz.ITranslateBiz;
import cn.org.opendfl.translate.dflsystem.translate.TransType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TranslateBiz implements ITranslateBiz {
    @Autowired
    private TranslateBaiduRest translateBaiduRest;

    @Autowired
    private TranslateGoogleApi translateGoogleApi;

    @Autowired
    private DataTranslateConfiguration dataTranslateConfiguration;

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
            return query + ":invalid transType:" + dataTranslateConfiguration.getTransType();
        }
    }
}
