package cn.org.opendfl.translate.constant;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public final class RequestParams {
    private RequestParams() {

    }

    /**
     * 用户ID
     * 在yml配置文件中修改opendfl。defaultAttrName
     */
    public static String USER_ID = "userId";
    /**
     * 设备号(header参数)
     * 可在yml配置文件中修改opendfl。defaultDeviceId
     */
    public static String DEVICE_ID = "device_id";
    /**
     * 系统类(header参数)
     * 示例:ios/android/h5/wx等
     * 见ReqSysType.java
     */
    public static String SYS_TYPE = "sys_type";
    /**
     * origin(header参数)
     */
    public static String ORIGIN = "origin";
    /**
     * username
     */
    public static final String USERNAME = "username";
    /**
     * APP包名(header参数)
     */
    public static final String PKG = "pkg";

    /**
     * 语言(header参数)
     */
    public static final String LANG = "lang";

    public static final String AUTHORIZATION = "authorization";
}
