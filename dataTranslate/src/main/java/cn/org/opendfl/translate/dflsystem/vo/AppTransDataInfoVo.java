package cn.org.opendfl.translate.dflsystem.vo;

import lombok.Data;

/**
 * 翻译数据信息
 *
 * @author chenjh
 */
@Data
public class AppTransDataInfoVo {
    /**
     * 系统已用本地缓存大小
     */
    private long localCacheSize;

    /**
     * 翻译内容redis缓存时间
     */
    private int redisCacheMinute;

    /**
     * 翻译内容本地缓存时间
     */
    private int localCacheMinute;
    /**
     * 本地guava缓存最大个数
     */
    private int localCacheMaxCount;

    private int timeValue;
}
