package cn.org.opendfl.translate.dflsystem.controller;

import cn.org.opendfl.translate.config.DataTranslateConfiguration;
import cn.org.opendfl.translate.dflsystem.vo.AppInfoVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;


/**
 * transInfo服务信息
 *
 * @author chenjh
 */
@Api(tags = "transInfo")
@Slf4j
@RestController
@RequestMapping("transInfo")
public class TransInfoController {
    @Resource
    private DataTranslateConfiguration dataTranslateConfiguration;

    @ApiOperation(value = "baseInfo", notes = "baseInfo")
    @RequestMapping(value = "baseInfo", method = {RequestMethod.GET})
    public AppInfoVo getBaseInfo(HttpServletRequest request) {
        AppInfoVo appInfoVo = new AppInfoVo();
        appInfoVo.setVersion(dataTranslateConfiguration.getVersion());
        appInfoVo.setTransType(dataTranslateConfiguration.getTransType());
        appInfoVo.setTypeDists(dataTranslateConfiguration.getTypeDists());
        appInfoVo.setDefaultLang(dataTranslateConfiguration.getDefaultLang());
        return appInfoVo;
    }

}
