package cn.org.opendfl.translate.dflsystem.vo;

import lombok.Data;

import java.util.Date;

/**
 * 翻译数据统计
 * TransTypePo的数据统计
 *
 * @author chenjh
 */
@Data
public class TransTypeCountVo {
    private String typeCode;
    private Integer cout;

    private Date maxCreateTime;
    private Date maxUpdateTime;
}
