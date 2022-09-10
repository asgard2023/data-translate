package cn.org.opendfl.translateDemo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import tk.mybatis.spring.annotation.MapperScan;

/**
 * @author chenjh
 */
@SpringBootApplication(scanBasePackages = {"cn.org.opendfl.translate", "cn.org.opendfl.translateDemo"})
@EnableConfigurationProperties
@MapperScan(basePackages = {"cn.org.opendfl.translate", "cn.org.opendfl.translateDemo"})
public class DataTranslateDemoApplication {
    public static final Logger logger = LoggerFactory.getLogger(DataTranslateDemoApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(DataTranslateDemoApplication.class, args);
    }

    //自创建RestTemplate
    @Bean
    public RestTemplate restTemplate() {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(30000);// 设置连接超时，单位毫秒
        requestFactory.setReadTimeout(30000);  //设置读取超时
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setRequestFactory(requestFactory);
        logger.info("RestTemplate initialization complete");
        return restTemplate;
    }
}
