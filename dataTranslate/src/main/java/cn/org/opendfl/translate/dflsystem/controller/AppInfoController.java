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
 * appInfo
 *
 * @author chenjh
 */
@Api(tags = "appInfo")
@Slf4j
@RestController
@RequestMapping("api")
public class AppInfoController {
    @Resource
    private DataTranslateConfiguration dataTranslateConfiguration;

    @ApiOperation(value = "appInfo", notes = "appInfo")
    @RequestMapping(value = "info", method = {RequestMethod.GET})
    public AppInfoVo getAppInfo(HttpServletRequest request) {
        AppInfoVo appInfoVo = new AppInfoVo();
        appInfoVo.setVersion(dataTranslateConfiguration.getVersion());
        appInfoVo.setTransType(dataTranslateConfiguration.getTransType());
        appInfoVo.setTypeDists(dataTranslateConfiguration.getTypeDists());
        appInfoVo.setDefaultLang(dataTranslateConfiguration.getDefaultLang());
        return appInfoVo;
    }

}
