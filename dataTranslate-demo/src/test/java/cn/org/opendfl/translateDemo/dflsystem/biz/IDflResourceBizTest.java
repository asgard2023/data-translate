package cn.org.opendfl.translateDemo.dflsystem.biz;

import cn.hutool.json.JSONUtil;
import cn.org.opendfl.translateDemo.DataTranslateDemoApplication;
import cn.org.opendfl.translateDemo.biz.IDflResourceBiz;
import cn.org.opendfl.translateDemo.po.DflResourcePo;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import javax.annotation.Resource;
import java.util.Date;

@SpringBootTest(classes = DataTranslateDemoApplication.class)
@ActiveProfiles(value = "test")
@Slf4j
public class IDflResourceBizTest {
    @Resource
    private IDflResourceBiz dflResourceBiz;

    @Test
    void getDataById() {
        DflResourcePo dflUserPo = dflResourceBiz.getDataById(5, "ifDel,createTime,createUser");
        System.out.println(JSONUtil.toJsonStr(dflUserPo));
        Assertions.assertEquals("test", dflUserPo.getUri());
        Assertions.assertNull(dflUserPo.getIfDel(), "ifDel ignored");
    }

    @Test
    public void initInsertTest() {
        for (int i = 0; i < 100; i++) {
            DflResourcePo resourcePo = new DflResourcePo();
            resourcePo.setName("test_" + i);
            resourcePo.setUri("test_" + i);
            resourcePo.setIfDel(0);
            resourcePo.setCreateUser(0);
            resourcePo.setCreateTime(new Date());
            this.dflResourceBiz.saveDflResource(resourcePo);
        }
    }
}
