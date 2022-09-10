package cn.org.opendfl.translate.dflsystem.biz;

import cn.org.opendfl.translate.dflsystem.translate.LangCodes;
import lombok.extern.slf4j.Slf4j;
import cn.org.opendfl.translate.DataTranslateApplication;
import cn.org.opendfl.translate.clients.TranslateBaiduRest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(classes = DataTranslateApplication.class)
@ActiveProfiles(value = "test")
@Slf4j
public class TranslateBaiduRestTest {
    @Autowired
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
