package cn.org.opendfl.translate.dflsystem.translate;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
public class IdInfoVo implements Serializable {
    private final String code;
    private final int idType;
    private final String idField;
    private final List<String> transFields;
    private Integer transTypeId;
}
