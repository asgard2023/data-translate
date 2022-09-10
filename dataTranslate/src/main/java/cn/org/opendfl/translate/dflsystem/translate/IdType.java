package cn.org.opendfl.translate.dflsystem.translate;

/**
 * id属性类型
 *
 * @author chenjh
 */
public enum IdType {
    NUM(IdType.TYPE_NUM, "num"),
    STRING(IdType.TYPE_STRING, "string");
    private final int type;
    private final String code;

    public static final int TYPE_NUM = 1;
    public static final int TYPE_STRING = 3;

    IdType(int type, String code) {
        this.type = type;
        this.code = code;
    }

    public int getType() {
        return type;
    }

    public String getCode() {
        return code;
    }
}
