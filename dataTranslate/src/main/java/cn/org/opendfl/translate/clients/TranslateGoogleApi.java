package cn.org.opendfl.translate.clients;

import cn.org.opendfl.exception.FailedException;
import cn.org.opendfl.exception.ParamErrorException;
import cn.org.opendfl.translate.config.DataTranslateConfiguration;
import cn.org.opendfl.translate.dflsystem.translate.LangType;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * google翻译
 * 接口文档：https://cloud.google.com/translate/docs/quickstarts?csw=1
 *
 * @author chenjh
 */
@Service
public class TranslateGoogleApi {
    @Resource
    private DataTranslateConfiguration dataTranslateConfiguration;

    public String getTransResult(String query, String from, String to) {
        LangType langType = LangType.parseGoogle(to);
        if (langType == null) {
            throw new ParamErrorException(to + " invalid");
        }
        //将语言编码转成google可识别的编码
        to = langType.googleCode;
//        Translate translate = TranslateOptions.newBuilder().setApiKey(dataTranslateConfiguration.getSecurityKey()).build().getService();
//
//        // Translates some text into Russian
//        Translation translation =
//                translate.translate(
//                        query,
//                        Translate.TranslateOption.sourceLanguage(from),
//                        Translate.TranslateOption.targetLanguage(to));
//        return translation.getTranslatedText();
        throw new FailedException("请引入google的oogle-cloud-translate依赖包，并修改以上代码");
    }
}
