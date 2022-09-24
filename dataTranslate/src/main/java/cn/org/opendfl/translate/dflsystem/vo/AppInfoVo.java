package cn.org.opendfl.translate.dflsystem.vo;

import lombok.Data;

/**
 * 信息系统
 *
 * @author chenjh
 */
@Data
public class AppInfoVo {
    /**
     * 版本号
     */
    private String version;
    /**
     * 默认语言
     */
    private String defaultLang;
    /**
     * 支持的目标语言类型
     */
    private String typeDists;
    /**
     * 勫译工具类型，比如baidu
     */
    private String transType;
    /**
     * 系统启动时间
     */
    private long startTime;
    /**
     * 当前时间
     */
    private long systemTime;

    private AppTransDataInfoVo transData;

}