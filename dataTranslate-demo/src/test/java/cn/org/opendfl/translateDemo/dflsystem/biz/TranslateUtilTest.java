package cn.org.opendfl.translateDemo.dflsystem.biz;

import cn.org.opendfl.translate.dflsystem.translate.IdInfoVo;
import cn.org.opendfl.translate.dflsystem.translate.TranslateTrans;
import cn.org.opendfl.translate.dflsystem.translate.TranslateUtil;
import cn.org.opendfl.translateDemo.DataTranslateDemoApplication;
import cn.org.opendfl.translateDemo.po.DflUserPo;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@SpringBootTest(classes = DataTranslateDemoApplication.class)
@ActiveProfiles(value = "test")
@Slf4j
public class TranslateUtilTest {


    @Test
    void getTranslateType() {
        IdInfoVo idInfoVo = TranslateUtil.getTranslateType(DflUserPo.class);
        System.out.println(JSON.toJSONString(idInfoVo));
    }

    @Test
    void getDataIdFieldMap() {
        IdInfoVo idInfoVo = TranslateUtil.getTranslateType(DflUserPo.class);
        String lang = "en";
        List<Object> idList = Arrays.asList(5, 7);
        List<String> idLangList = idList.stream().map(id -> id + "_" + lang).collect(Collectors.toList());
        Map<String, Map<String, String>> dataIdFieldMap = TranslateTrans.getDataIdFieldMap(idInfoVo, lang, idList, idLangList);
        System.out.println(JSON.toJSONString(dataIdFieldMap));

        dataIdFieldMap = TranslateTrans.getDataIdFieldMap(idInfoVo, lang, idList, idLangList);
        System.out.println(JSON.toJSONString(dataIdFieldMap));

        dataIdFieldMap = TranslateTrans.getDataIdFieldMap(idInfoVo, lang, idList, idLangList);
        System.out.println(JSON.toJSONString(dataIdFieldMap));
    }

    @Test
    public void getTimeValue(){
        Long timeMinute = System.currentTimeMillis()/TranslateTrans.TIME_MINUTE_IN_MILLIS%60;
        int timeValue=timeMinute.intValue()%60/TranslateTrans.REDIS_KEY_TRANS_DATA_EXPIRE_MINUTE;
        System.out.println(timeValue+" "+TranslateTrans.getTimeValue(System.currentTimeMillis()));


    }
}
