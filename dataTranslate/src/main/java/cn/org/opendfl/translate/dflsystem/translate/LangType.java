package cn.org.opendfl.translate.dflsystem.translate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * 国际化语言类型
 *
 * @author chenjh
 */
public enum LangType {
    /**
     * 中文简体
     */
    ZH(LangCodes.ZH, "zh", "zh-CN", "中文", Locale.SIMPLIFIED_CHINESE),
    /**
     * 中文繁体
     */
    TW(LangCodes.TW, "cht", "zh-TW", "繁体", Locale.TRADITIONAL_CHINESE),
    /**
     * 日语
     */
    JA(LangCodes.JA, "jp", "ja", "日语", Locale.JAPAN),
    /**
     * 英语
     */
    EN(LangCodes.EN, "en", "en", "英语", Locale.ENGLISH);
    protected static final List<String> LANG_TYPES = Arrays.asList(LangType.values()).stream().map(t -> t.code).collect(Collectors.toList());
    public static final String SUPPORT_LANG = "zh,cht,jp,tw,ja,";
    public static final String NONE_LANG = "noneLang";//表示整个数据不做国际化，即不显示
    static final Logger logger = LoggerFactory.getLogger(LangType.class);
    public final String code;
    public final String baiduCode;
    public final String googleCode;
    public final String descs;
    public final Locale locale;

    LangType(String code, String baiduCode, String googleCode, String descs, Locale locale) {
        this.code = code;
        this.baiduCode = baiduCode;
        this.googleCode = googleCode;
        this.descs = descs;
        this.locale = locale;
    }

    public static LangType parse(String code) {
        if (code == null || code.length() < 2) {
            return null;
        }
        LangType[] codes = LangType.values();
        for (LangType cur : codes) {
            if (cur.code.equalsIgnoreCase(code) || cur.code.equalsIgnoreCase(code.substring(0, 2))) {
                return cur;
            }
        }
        logger.warn("----setLang lang={} invalid, allow(zh,en,tw,ja)", code);
        return null;
    }

    public static LangType parseBaidu(String code) {
        if (code == null || code.length() < 2) {
            return null;
        }
        LangType[] codes = LangType.values();
        for (LangType cur : codes) {
            if (cur.code.equalsIgnoreCase(code) || cur.baiduCode.equals(code)) {
                return cur;
            }
        }
        return null;
    }

    public static LangType parseGoogle(String code) {
        if (code == null || code.length() < 2) {
            return null;
        }
        LangType[] codes = LangType.values();
        for (LangType cur : codes) {
            if (cur.code.equalsIgnoreCase(code) || cur.googleCode.equals(code)) {
                return cur;
            }
        }
        return null;
    }

    private static LangType defaultLang = null;

    public static LangType getDefault() {
        if (defaultLang == null) {
            defaultLang = LangType.ZH;
        }
        return defaultLang;
    }

    public static void setDefaultLang(LangType defaultLang){
        LangType.defaultLang=defaultLang;
    }

    public static Locale getLocale(String lang) {
        LangType langType = LangType.parse(lang);
        if (langType == null) {
            langType = getDefault();
        }
        return langType.locale;
    }
}
