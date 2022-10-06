# springboot data translation tool
springboot, springmvc data translation tool, supports the function of enabling data translation data attributes through annotations, storing translation results in the database, and supports modifying the translation results.

## Features
* Support springboot, springmvc
* Support mysql, postgresql, etc.
* Support easyui, jqgrid, layui front-end modification of function translation
* Support the use of java annotations
* Support Baidu, Youdao, Google and other translation api
* Support to use guava to cache translation results when querying
* Support initial translation with translation api and save to database.
* It is supported to modify the translation results, and the modified results shall prevail.
* It is recommended to use numbers as id
* Support for multi-threaded concurrent translation of content (may be limited by vendor API frequency)

## Relevant information
* Baidu translation api https://fanyi-api.baidu.com/doc/21
* google translate api https://cloud.google.com/translate/docs/quickstarts?csw=1
* Other translation apis can be extended by yourself

## Quick Start
http://localhost:8080/index.html  
http://localhost:8080/doc.html  
http://translate-demo.opendfl.org.cn/index.html

## use
* 1, import ddependency for maven
```xml
<dependency>
    <groupId>cn.org.opendfl</groupId>
    <artifactId>data-translate</artifactId>
    <version>1.4</version>
</dependency>
```
* 2, in the PO class to be translated, add @TranslateType, @TranslateField to get started quickly
```java

/**
 * 支持翻译的PO类，加上@TranslateType
 */
@Data
@TranslateType
public class DflUserPo implements Serializable {
    private Integer id;
    
    /**
     * 昵称，要翻译的属性
     */
    @TranslateField
    private String nickname;

    /**
     * 描述信息，要翻译的属性
     */
    @TranslateField
    private String descs;
    
}
```
* 3, call the translation function on the returned list
```java
@RestController
@RequestMapping("dflUser")
public class DflUserController{
    @RequestMapping(value = "/listByLang", method = {RequestMethod.POST, RequestMethod.GET})
    public List<DflUserPo> listByLang(HttpServletRequest request, DflUserPo entity, MyPageInfo<DflUserPo> pageInfo) {
        String lang = request.getHeader(RequestParams.LANG);//lang=en,jp等，主语言zh不翻译
        if (StringUtils.isBlank(lang)) {
            lang = request.getParameter(RequestParams.LANG);
        }
        logger.debug("-------listByLang--lang={}", lang);
        this.pageSortBy(pageInfo);
        pageInfo = dflUserBiz.findPageBy(entity, pageInfo, this.createAllParams(request));

        if (StringUtils.isNotBlank(lang)) {
            //Method 1: invokes interface translation processing
            TranslateUtil.transform(lang, pageInfo.getList(), true);
        }
        return pageInfo.getList();
    }
}
```
* 4, method return value annotation @Translate

```java
import cn.org.opendfl.translate.dflsystem.translate.annotation.Translate;

@RestController
@RequestMapping("dflUser")
public class DflUserController {
    //Method 2, through annotation translation processing
    @Translate
    @RequestMapping(value = "/listByLang2", method = {RequestMethod.POST, RequestMethod.GET})
    public List<DflUserPo> listByLang2(HttpServletRequest request, DflUserPo entity, MyPageInfo<DflUserPo> pageInfo) {
        this.pageSortBy(pageInfo);
        pageInfo = dflUserBiz.findPageBy(entity, pageInfo, this.createAllParams(request));
        return pageInfo.getList();
    }
}
```