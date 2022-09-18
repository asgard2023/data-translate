package cn.org.opendfl.translate.dflsystem.biz;

import cn.org.opendfl.translate.DataTranslateApplication;
import cn.org.opendfl.translate.clients.TranslateBaiduRest;
import cn.org.opendfl.translate.dflsystem.translate.LangCodes;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import javax.annotation.Resource;

@SpringBootTest(classes = DataTranslateApplication.class)
@ActiveProfiles(value = "test")
@Slf4j
public class TranslateBaiduRestTest {
    @Resource
    private TranslateBaiduRest translateBaiduRest;

    @Test
    void getTransResultApi() {
        System.out.println(translateBaiduRest.getTransResultApi("测试", LangCodes.ZH, LangCodes.EN));
    }

    @Test
    void getTransResult() {
        System.out.println(translateBaiduRest.getTransResult("测试", LangCodes.ZH, LangCodes.EN));
    }
}
