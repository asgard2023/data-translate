package cn.org.opendfl.translateDemo.dflsystem.biz;

import cn.hutool.json.JSONUtil;
import cn.org.opendfl.translateDemo.DataTranslateDemoApplication;
import cn.org.opendfl.translateDemo.biz.IDflUserBiz;
import cn.org.opendfl.translateDemo.po.DflUserPo;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(classes = DataTranslateDemoApplication.class)
@ActiveProfiles(value = "test")
@Slf4j
public class IDfUserBizTest {
    @Autowired
    private IDflUserBiz dflUserBiz;

    @Test
    void getDataById() {
        DflUserPo dflUserPo = dflUserBiz.getDataById(5, "ifDel,createTime,createUser");
        System.out.println(JSONUtil.toJsonStr(dflUserPo));
        Assertions.assertEquals("test", dflUserPo.getUsername());
        Assertions.assertNull(dflUserPo.getIfDel(), "ifDel ignored");
    }
}
