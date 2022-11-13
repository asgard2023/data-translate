package cn.org.opendfl.translate.config;

import cn.org.opendfl.translate.dflsystem.translate.LangType;
import cn.org.opendfl.translate.dflsystem.translate.TransType;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class DataTranslateConfiguration {
    private String version = "1.6";
    /**
     * 用于/dflsystem/trTransType/transCounts接口的密码验证
     */
    private String transCountAuth = "chenjhtest";
    /**
     * 主语言，遇到此编码的，不进行翻译，直接显示
     */
    private String defaultLang = "zh";
    /**
     * 支持的目标语言
     */
    private String typeDists = "en,jp,tw";
    private String transType = TransType.BAIDU.getType();
    private String appid;
    private String securityKey;
    /**
     * 翻译接口地址
     */
    private String apiUrl;

    public void setDefaultLang(String defaultLang) {
        this.defaultLang = defaultLang;
        LangType langType = LangType.parse(defaultLang);
        if (langType != null) {
            LangType.setDefaultLang(langType);
            log.info("----translate.defaultLang={}");
        }
        else{
            log.warn("----translate.defaultLang={} invalid", defaultLang);
        }
    }

}
