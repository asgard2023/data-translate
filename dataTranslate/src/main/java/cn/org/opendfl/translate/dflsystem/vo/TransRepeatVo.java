package cn.org.opendfl.translate.dflsystem.vo;

import lombok.Data;

/**
 * 翻译数据重复
 *
 * @author chenjh
 */
@Data
public class TransRepeatVo {
    private Object dataId;
    private String code;
    private String lang;
    private Integer cout;
}
