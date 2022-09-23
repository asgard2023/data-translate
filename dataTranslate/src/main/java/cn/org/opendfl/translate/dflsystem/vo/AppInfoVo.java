package cn.org.opendfl.translate.dflsystem.vo;

import lombok.Data;

@Data
public class AppInfoVo {
    private String version;
    private String defaultLang;
    private String typeDists;
    private String transType;
    private long startTime = System.currentTimeMillis();
}
