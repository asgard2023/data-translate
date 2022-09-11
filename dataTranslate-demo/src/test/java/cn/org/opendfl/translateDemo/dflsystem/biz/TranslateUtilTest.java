package cn.org.opendfl.translateDemo.dflsystem.biz;

import cn.org.opendfl.translate.dflsystem.translate.IdInfoVo;
import cn.org.opendfl.translate.dflsystem.translate.TranslateTrans;
import cn.org.opendfl.translate.dflsystem.translate.TranslateUtil;
import cn.org.opendfl.translateDemo.DataTranslateDemoApplication;
import cn.org.opendfl.translateDemo.po.DflUserPo;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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
    private Gson gson = new GsonBuilder().create();

    @Test
    void getTranslateType() {
        IdInfoVo idInfoVo = TranslateUtil.getTranslateType(DflUserPo.class);
        System.out.println(gson.toJson(idInfoVo));
    }

    @Test
    void getDataIdFieldMap() {
        IdInfoVo idInfoVo = TranslateUtil.getTranslateType(DflUserPo.class);
        String lang = "en";
        List<Object> idList = Arrays.asList(5, 7);
        List<String> idLangList = idList.stream().map(id -> id + "_" + lang).collect(Collectors.toList());
        Map<String, Map<String, String>> dataIdFieldMap = TranslateTrans.getDataIdFieldMap(idInfoVo, lang, idList, idLangList);
        System.out.println(gson.toJson(dataIdFieldMap));

        dataIdFieldMap = TranslateTrans.getDataIdFieldMap(idInfoVo, lang, idList, idLangList);
        System.out.println(gson.toJson(dataIdFieldMap));

        dataIdFieldMap = TranslateTrans.getDataIdFieldMap(idInfoVo, lang, idList, idLangList);
        System.out.println(gson.toJson(dataIdFieldMap));
    }
}