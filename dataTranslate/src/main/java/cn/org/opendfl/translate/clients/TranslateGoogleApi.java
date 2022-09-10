package cn.org.opendfl.translate.clients;

import cn.org.opendfl.translate.config.DataTranslateConfiguration;
import cn.org.opendfl.translate.dflsystem.translate.LangType;
import com.google.cloud.translate.Translate;
import com.google.cloud.translate.TranslateOptions;
import com.google.cloud.translate.Translation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * google翻译
 * 接口文档：https://cloud.google.com/translate/docs/quickstarts?csw=1
 *
 * @author chenjh
 */
@Service
public class TranslateGoogleApi {
    @Autowired
    private DataTranslateConfiguration dataTranslateConfiguration;

    public String getTransResult(String query, String from, String to) {
        LangType langType = LangType.parseGoogle(to);
        if (langType == null) {
            return query + ":" + to + " invalid";
        }
        //将语言编码转成google可识别的编码
        to = langType.googleCode;
        Translate translate = TranslateOptions.newBuilder().setApiKey(dataTranslateConfiguration.getSecurityKey()).build().getService();

        // Translates some text into Russian
        Translation translation =
                translate.translate(
                        query,
                        Translate.TranslateOption.sourceLanguage(from),
                        Translate.TranslateOption.targetLanguage(to));
        return translation.getTranslatedText();
    }
}
