package cn.org.opendfl.translate.config;

import cn.org.opendfl.translate.dflsystem.translate.TransType;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 数据翻译配置
 *
 * @author chenjh
 */
@Configuration
@Data
@ConfigurationProperties(prefix = "translate")
public class DataTranslateConfiguration {
    /**
     * 主语言，遇到此编码的，不进行翻译，直接显示
     */
    private String defaultLang = "zh";
    /**
     * 未用
     */
    private String transLanguages = "en,jp";
    private String transType = TransType.BAIDU.getType();
    private String appid;
    private String securityKey;
    /**
     * 翻译接口地址
     */
    private String apiUrl;

}
