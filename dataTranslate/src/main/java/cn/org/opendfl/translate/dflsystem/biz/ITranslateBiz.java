package cn.org.opendfl.translate.dflsystem.biz;

/**
 * 数据翻译接口
 */
public interface ITranslateBiz {
    public String getTransResult(String query, String to);
    public String getTransResult(String query, String from, String to);
}
