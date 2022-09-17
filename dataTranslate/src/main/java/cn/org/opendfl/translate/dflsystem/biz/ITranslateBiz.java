package cn.org.opendfl.translate.dflsystem.biz;

import cn.org.opendfl.translate.dflsystem.translate.LangType;

/**
 * 数据翻译接口
 */
public interface ITranslateBiz {
    public LangType getLangType(String source, String lang);

    public String getTransResult(String query, String to);

    public String getTransResult(String query, String from, String to);
}
