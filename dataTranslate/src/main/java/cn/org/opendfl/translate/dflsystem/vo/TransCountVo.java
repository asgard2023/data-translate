package cn.org.opendfl.translate.dflsystem.vo;

import lombok.Data;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Data
public class TransCountVo {
    private String code;
    private AtomicInteger totalCounter;
    private long maxTime;
    private long firstTime;
    private String errorMsg;
    private long errorTime;
    private Map<String, AtomicInteger> sourceCounter;
}
