package cn.org.opendfl.translate.dflsystem.vo;

import lombok.Data;

import java.util.Date;

/**
 * 翻译数据统计
 *
 * @author chenjh
 */
@Data
public class TransCountVo {
    private String lang;
    private String code;
    private Integer cout;

    private Date maxCreateTime;
    private Date maxUpdateTime;
}
