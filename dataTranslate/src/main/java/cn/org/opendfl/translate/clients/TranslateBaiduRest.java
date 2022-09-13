package cn.org.opendfl.translate.clients;

import cn.hutool.json.JSONUtil;
import cn.org.opendfl.translate.clients.vo.BaiduTransVo;
import cn.org.opendfl.translate.config.DataTranslateConfiguration;
import cn.org.opendfl.translate.dflsystem.translate.LangType;
import cn.org.opendfl.translate.exception.FailedException;
import cn.org.opendfl.translate.exception.ParamErrorException;
import cn.org.opendfl.translate.exception.UnknownException;
import cn.org.opendfl.translate.utils.CommUtils;
import cn.org.opendfl.translate.utils.MD5;
import cn.org.opendfl.translate.utils.TransApiBaidu;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * 百度翻译api
 * <p>
 * 接口文档： https://fanyi-api.baidu.com/doc/21
 *
 * @author chenjh
 */
@Service
@Slf4j
public class TranslateBaiduRest {
    @Autowired
    private DataTranslateConfiguration dataTranslateConfiguration;

    private Map<String, String> buildParams(String query, String from, String to) {
        String appid = dataTranslateConfiguration.getAppid();
        String securityKey = dataTranslateConfiguration.getSecurityKey();

        Map<String, String> params = new HashMap<String, String>();
        params.put("q", query);
        params.put("from", from);
        params.put("to", to);
        params.put("appid", appid);

        // 随机数
        String salt = String.valueOf(System.currentTimeMillis() % 10000000);
        params.put("salt", salt);

        // 签名
        String src = appid + query + salt + securityKey; // 加密前的原文
        params.put("sign", MD5.md5(src));

        return params;
    }


    @Autowired
    private RestTemplate restTemplate;

    public Object getTransResultApi(String query, String from, String to) {
        String appid = dataTranslateConfiguration.getAppid();
        if (StringUtils.isBlank(appid)) {
            return query + ":appid empty";
        }
        if (StringUtils.isBlank(from)) {
            from = "auto";
        }
        LangType langType = LangType.parseBaidu(to);
        if (langType == null) {
            return query + ":" + to + " invalid";
        }
        //将语言编码转成baidu可识别的编码
        to = langType.baiduCode;
        String securityKey = dataTranslateConfiguration.getSecurityKey();
        TransApiBaidu transApi = new TransApiBaidu(appid, securityKey);
        String resultRemote = transApi.getTransResult(query, from, to);
        return getTransResultByBody(query, to, resultRemote);
    }

    public String getTransResult(String query, String from, String to) {
        if (StringUtils.isBlank(dataTranslateConfiguration.getAppid())) {
            throw new FailedException("baidi appid empty, pls check config");
        }
        if (StringUtils.isBlank(from)) {
            from = "auto";
        }

        LangType langType = LangType.parseBaidu(to);
        if (langType == null) {
            throw new ParamErrorException(to + " not support, see LangType.java");
        }
        //将语言编码转成baidu可识别的编码
        to = langType.baiduCode;
        String apiUrl = dataTranslateConfiguration.getApiUrl();
        Map<String, String> params = buildParams(query, from, to);
        String sendUrl = CommUtils.getUrlWithQueryString(apiUrl, params);
        log.debug("----getTransResult--url={}", sendUrl);

        String resultRemote = null;
        try {
            ResponseEntity<String> exchanges = restTemplate.getForEntity(sendUrl, String.class);
            resultRemote = getTransResultByBody(query, to, exchanges.getBody());
        } catch (RestClientException e) {
            log.error("---getTransResult--query={} from={} error={}", query, from, e.getMessage());
            resultRemote = e.getMessage();
            throw new UnknownException(resultRemote);
        }
        log.info("----getTransResult--query={}, resultRemote={}", query, resultRemote);
        return resultRemote;
    }

    private String getTransResultByBody(String query, String to, String body) {
        BaiduTransVo baiduTransVo = JSONUtil.toBean(body, BaiduTransVo.class);
        if (StringUtils.isNotBlank(baiduTransVo.getError_code()) && !BaiduTransVo.SUCCESS_CODE.equals(baiduTransVo.getError_code())) {
            log.warn("---getTransResult--query={} to={} body={}", query, to, body);
            throw new FailedException(baiduTransVo.getError_code()+","+body);
        }
        if (CollectionUtils.isNotEmpty(baiduTransVo.getTrans_result())) {
            return baiduTransVo.getTrans_result().get(0).get("dst");
        }
        return null;
    }

}
