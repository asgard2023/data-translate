package cn.org.opendfl.translate.base;

import cn.org.opendfl.translate.dflsystem.translate.LangCodes;

import javax.servlet.http.HttpServletRequest;

public class RequestUtils {

    public static String getLang(HttpServletRequest request) {
        String lang = request.getHeader(RequestParams.LANG);
        if (lang == null) {
            //默认中文
            return LangCodes.ZH;
        }
        if (lang.indexOf('-') != -1) {
            //如en-CN
            return lang.split("-")[0];
        }
        if (!LangCodes.ZH.equals(lang) && !LangCodes.EN.equals(lang) && !LangCodes.JA.equals(lang)) {
            return LangCodes.EN;
        }
        return lang;
    }
}
