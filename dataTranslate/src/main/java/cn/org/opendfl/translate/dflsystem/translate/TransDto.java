package cn.org.opendfl.translate.dflsystem.translate;

import lombok.Data;
import cn.org.opendfl.translate.dflsystem.po.TrTransDataPo;

@Data
public class TransDto extends TrTransDataPo {
    private String transTypeCode;
    private String dataNids;
    private String dataSids;
}
