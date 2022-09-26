package cn.org.opendfl.translate.dflsystem.translate;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TransFieldVo {
    private Object obj;
    private Object id;
    private String field;
    private String value;

    private String transContent;
}
