package cn.org.opendfl.translate.dflsystem.translate;

/**
 * 支持的翻译api类型
 *
 * @author chenjh
 */
public enum TransType {
    BAIDU("baidu"),
    GOOGLE("google");

    private final String type;

    TransType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public static TransType parse(String type) {
        if (type == null || type.length() < 2) {
            return null;
        }
        TransType[] types = TransType.values();
        for (TransType cur : types) {
            if (cur.getType().equals(type)) {
                return cur;
            }
        }
        return null;
    }

}
