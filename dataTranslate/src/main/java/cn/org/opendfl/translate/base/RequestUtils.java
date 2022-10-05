package cn.org.opendfl.translate.base;

import cn.org.opendfl.translate.dflsystem.translate.LangCodes;

import javax.servlet.http.HttpServletRequest;

public class RequestUtils {
    private RequestUtils() {

    }

    public static String getLang(HttpServletRequest request) {
        return getLang(request, LangCodes.ZH);
    }

    public static String getLang(HttpServletRequest request, String defaultLang) {
        String lang = request.getHeader(RequestParams.LANG);
        if (lang == null) {
            lang = request.getParameter(RequestParams.LANG);
        }
        if (lang == null) {
            //默认中文
            return defaultLang;
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
