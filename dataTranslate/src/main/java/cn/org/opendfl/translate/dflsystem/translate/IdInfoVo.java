package cn.org.opendfl.translate.dflsystem.translate;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
public class IdInfoVo implements Serializable {
    private String code;
    private int idType;
    private String idField;
    private List<String> transFields;
    private Integer transTypeId;
}
