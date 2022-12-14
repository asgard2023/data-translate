# springboot数据翻译组件
即支持翻译数据属性，并把翻译结果存到数据库，加载时自动缓存翻译结果1分钟，以减少频繁调用数据库。

## Quick Start

http://localhost:8080/index.html  
http://localhost:8080/doc.html

## 功能特性

* 支持springboot,springmvc
* 支持mysql,postgresql等
* 支持easyui,jqgrid,layui前端对功能翻译的修改
* 支持java注解使用
* 支持百度,有道，google等翻译api
* 支持查询时用guava缓存翻译结果

## 相关资料
* 百度翻译api https://fanyi-api.baidu.com/doc/21
* google翻译api https://cloud.google.com/translate/docs/quickstarts?csw=1
* 其他翻译api 可自行扩展

## 使用
* 1,在要翻译的PO类，加上@TranslateType，@TranslateField快速上手
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
* 2,在返回的list调用翻译功能
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
            //翻译转换
            TranslateUtil.transform(lang, pageInfo.getList(), true);
        }
        return pageInfo.getList();
    }
}
```
* 3,方法返回值注解@Translate

```java
import cn.org.opendfl.translate.dflsystem.translate.annotation.Translate;

@RestController
@RequestMapping("dflUser")
public class DflUserController {
    @Translate
    @RequestMapping(value = "/listByLang2", method = {RequestMethod.POST, RequestMethod.GET})
    public List<DflUserPo> listByLang2(HttpServletRequest request, DflUserPo entity, MyPageInfo<DflUserPo> pageInfo) {
        this.pageSortBy(pageInfo);
        pageInfo = dflUserBiz.findPageBy(entity, pageInfo, this.createAllParams(request));
        return pageInfo.getList();
    }
}
```