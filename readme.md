# springboot数据翻译工具
springboot,springmvc数据翻译工具，即支持通过注解开启数据翻译数据属性功能，把翻译结果存到数据库，并支持翻译结果的修改保存。

## 功能特性
* 支持springboot,springmvc
* 支持mysql,postgresql等
* 支持easyui,jqgrid,layui前端对功能翻译的修改
* 支持java注解使用
* 支持百度,有道，google等翻译api
* 支持查询时用guava 1分钟以及redis 20分钟缓存翻译结果
* 支持用翻译api进行初始翻译，并保存到数据库。
* 支持对翻译结果进行修改，并以修改后的结果为准。
* 建议使用数字做id

## 相关资料
* 百度翻译api https://fanyi-api.baidu.com/doc/21
* google翻译api https://cloud.google.com/translate/docs/quickstarts?csw=1
* 其他翻译api 可自行扩展

## Quick Start
http://localhost:8080/index.html  
http://localhost:8080/doc.html

## 使用
* 1 maven引入依赖包
```xml
<dependency>
    <groupId>cn.org.opendfl</groupId>
    <artifactId>data-translate</artifactId>
    <version>1.2</version>
</dependency>
```
* 2,在要翻译的PO类，加上@TranslateType，@TranslateField快速上手
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
* 3,在返回的list调用翻译功能
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
            //方式一，调用接口翻译处理
            TranslateUtil.transform(lang, pageInfo.getList(), true);
        }
        return pageInfo.getList();
    }
}
```
* 4,方法返回值注解@Translate

```java
import cn.org.opendfl.translate.dflsystem.translate.annotation.Translate;

@RestController
@RequestMapping("dflUser")
public class DflUserController {
    //方式二，通过注解翻译处理
    @Translate
    @RequestMapping(value = "/listByLang2", method = {RequestMethod.POST, RequestMethod.GET})
    public List<DflUserPo> listByLang2(HttpServletRequest request, DflUserPo entity, MyPageInfo<DflUserPo> pageInfo) {
        this.pageSortBy(pageInfo);
        pageInfo = dflUserBiz.findPageBy(entity, pageInfo, this.createAllParams(request));
        return pageInfo.getList();
    }
}
```