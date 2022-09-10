package cn.org.opendfl.translate.clients.vo;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class BaiduTransVo {
    public final static String SUCCESS_CODE = "52000";
    private String error_code;
    private String error_msg;
    private String from;
    private String to;
    private List<Map<String, String>> trans_result;
}
