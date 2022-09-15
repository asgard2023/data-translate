//package cn.org.opendfl.translate.dflsystem.biz;
//
//import com.google.cloud.translate.Translate;
//import com.google.cloud.translate.TranslateOptions;
//import com.google.cloud.translate.Translation;
//import org.junit.jupiter.api.Test;
//
//public class TranslateGoogleTest {
//
//    @Test
//    public void testTrans2(){
//        Translate translate = TranslateOptions.newBuilder().setApiKey("AIzaSyDaKJ42hY_5M8aTZ5xLRNHNm6I3yz0-u7E").build().getService();
//        // The text to translate
//        String text = "Hello, world!";
//
//        // Translates some text into Russian
//        Translation translation =
//                translate.translate(
//                        text,
////                Translate.TranslateOption.sourceLanguage("en"),
//                        Translate.TranslateOption.targetLanguage("ja"));
//
//        System.out.printf("Text: %s%n", text);
//        System.out.printf("Translation: %s%n", translation.getTranslatedText());
//    }
//}
