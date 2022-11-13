package cn.org.opendfl.translate.dflsystem.translate;

import lombok.Data;

@Data
public class TransErrorVo {
    /**
     * 最新错误信息
     */
    private String errorMsg;
    private Long errorTime;
}
