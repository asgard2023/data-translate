package cn.org.opendfl.translate.dflsystem.controller;

import cn.org.opendfl.translate.base.RequestUtils;
import cn.org.opendfl.translate.config.DataTranslateConfiguration;
import cn.org.opendfl.translate.config.TaskPoolConfig;
import cn.org.opendfl.translate.config.vo.TaskPoolVo;
import cn.org.opendfl.translate.dflsystem.translate.TranslateTrans;
import cn.org.opendfl.translate.dflsystem.vo.AppInfoVo;
import cn.org.opendfl.translate.dflsystem.vo.AppTransDataInfoVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
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

    @Resource
    private TaskPoolConfig taskPoolConfig;
    private static final long startTime = System.currentTimeMillis();

    @ApiOperation(value = "baseInfo", notes = "baseInfo")
    @GetMapping(value = "baseInfo")
    public AppInfoVo getBaseInfo(HttpServletRequest request) {
        log.info("----baseInfo-ip={} lang={}", request.getRemoteAddr(), RequestUtils.getLang(request));
        long currentTime = System.currentTimeMillis();
        AppInfoVo appInfoVo = new AppInfoVo();
        appInfoVo.setSystemTime(currentTime);
        appInfoVo.setStartTime(startTime);
        appInfoVo.setVersion(dataTranslateConfiguration.getVersion());
        appInfoVo.setTransType(dataTranslateConfiguration.getTransType());
        appInfoVo.setTypeDists(dataTranslateConfiguration.getTypeDists());
        appInfoVo.setDefaultLang(dataTranslateConfiguration.getDefaultLang());

        appInfoVo.setTaskPool(new TaskPoolVo(taskPoolConfig));

        AppTransDataInfoVo transDataInfoVo = new AppTransDataInfoVo();
        appInfoVo.setTransData(transDataInfoVo);
        transDataInfoVo.setLocalCacheSize(TranslateTrans.getTransDataCacheSize());
        transDataInfoVo.setLocalCacheMinute(TranslateTrans.TRANS_DATA_LOCAL_CACHE_MINUTE);
        transDataInfoVo.setLocalCacheMaxCount(TranslateTrans.TRANS_DATA_LOCAL_CACHE_MAX);
        transDataInfoVo.setRedisCacheMinute(TranslateTrans.TRANS_DATA_REDIS_CACHE_MINUTE);
        transDataInfoVo.setTimeValue(TranslateTrans.getTimeValue(currentTime));
        return appInfoVo;
    }

}
