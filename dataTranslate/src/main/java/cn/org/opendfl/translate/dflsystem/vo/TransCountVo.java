package cn.org.opendfl.translate.dflsystem.vo;

import lombok.Data;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 翻译状态收集
 * 翻译次数计算，异常次数计算，异常信息
 */
@Data
public class TransCountVo {
    /**
     * 编码
     */
    private String code;
    /**
     * 总调用次数
     */
    private AtomicInteger totalCounter;
    /**
     * 最大计用时间
     */
    private long maxTime;
    /**
     * 首次调用时间
     */
    private long firstTime;
    /**
     * 最新错误信息
     */
    private String errorMsg;
    private long errorTime;
    /**
     * 错误次数
     */
    private AtomicInteger errorCounter;
    /**
     * 来源调用次数
     */
    private Map<String, AtomicInteger> sourceCounter;
}
