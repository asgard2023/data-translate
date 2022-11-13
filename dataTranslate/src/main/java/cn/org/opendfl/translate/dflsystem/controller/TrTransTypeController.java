package cn.org.opendfl.translate.dflsystem.controller;

import cn.org.opendfl.base.BaseController;
import cn.org.opendfl.base.MyPageInfo;
import cn.org.opendfl.base.PageVO;
import cn.org.opendfl.exception.PermissionDeniedException;
import cn.org.opendfl.exception.ResultData;
import cn.org.opendfl.exception.ValidateUtils;
import cn.org.opendfl.translate.config.DataTranslateConfiguration;
import cn.org.opendfl.translate.dflsystem.biz.ITrTransTypeBiz;
import cn.org.opendfl.translate.dflsystem.po.TrTransTypePo;
import cn.org.opendfl.translate.dflsystem.translate.IdInfoVo;
import cn.org.opendfl.translate.dflsystem.translate.TranslateUtil;
import cn.org.opendfl.translate.dflsystem.vo.TransTypeCountVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 翻译类型 Controller
 *
 * @author chenjh
 */
@Api(tags = "翻译类型接口")
@RestController
@RequestMapping("dflsystem/trTransType")
public class TrTransTypeController extends BaseController {

    static Logger logger = LoggerFactory.getLogger(TrTransTypeController.class);

    @Resource
    private ITrTransTypeBiz trTransTypeBiz;

    @Resource
    private DataTranslateConfiguration dataTranslateConfiguration;

    /**
     * 翻译类型列表查询
     *
     * @param request  请求req
     * @param entity   翻译类型对象
     * @param pageInfo 翻页对象
     * @return MyPageInfo 带翻页的数据集
     * @date 2022年9月4日 下午7:42:24
     */
    @ApiOperation(value = "翻译类型列表", notes = "翻译类型列表翻页查询")
    @RequestMapping(value = "list", method = {RequestMethod.POST, RequestMethod.GET})
    public MyPageInfo<TrTransTypePo> queryPage(HttpServletRequest request, TrTransTypePo entity, MyPageInfo<TrTransTypePo> pageInfo) {
        if (entity == null) {
            entity = new TrTransTypePo();
        }
        entity.setIfDel(0);
        if (pageInfo.getPageSize() == 0) {
            pageInfo.setPageSize(getPageSize());
        }
        Map<String, Object> params = this.createAllParams(request);
        pageInfo = trTransTypeBiz.findPageBy(entity, pageInfo, params);
        return pageInfo;
    }

    @ApiOperation(value = "翻译类型列表(easyui)", notes = "翻译类型列表翻页查询，用于兼容easyui的rows方式")
    @RequestMapping(value = "/list2", method = {RequestMethod.POST, RequestMethod.GET})
    public PageVO<TrTransTypePo> findByPage(HttpServletRequest request, TrTransTypePo entity, MyPageInfo<TrTransTypePo> pageInfo) {
        logger.debug("-------findByPage-------");
        this.pageSortBy(pageInfo);
        pageInfo = queryPage(request, entity, pageInfo);
        return new PageVO(pageInfo);
    }

    /**
     * 翻译类型 新增
     *
     * @param request 请求req
     * @param entity  翻译类型对象
     * @return ResultData 返回数据
     * @date 2022年9月4日 下午7:42:24
     */
    @ApiOperation(value = "添加翻译类型", notes = "添加一个翻译类型")
    @RequestMapping(value = "save", method = {RequestMethod.POST, RequestMethod.GET})
    public ResultData edit(TrTransTypePo entity, HttpServletRequest request) {
        if (entity.getId() != null && entity.getId() > 0) {
            return update(entity, request);
        }
        entity.setUpdateUser(getCurrentUserId());
        entity.setCreateUser(getCurrentUserId());
        trTransTypeBiz.saveTrTransType(entity);
        return ResultData.success();
    }

    /**
     * 翻译类型 更新
     *
     * @param request 请求req
     * @param entity  翻译类型对象
     * @return ResultData 返回数据
     * @date 2022年9月4日 下午7:42:24
     */
    @ApiOperation(value = "修改翻译类型", notes = "根据传入的角色信息修改")
    @RequestMapping(value = "update", method = {RequestMethod.POST, RequestMethod.GET})
    public ResultData update(TrTransTypePo entity, HttpServletRequest request) {
        entity.setUpdateUser(getCurrentUserId());
        int v = trTransTypeBiz.updateTrTransType(entity);
        return ResultData.success(v);
    }

    /**
     * 翻译类型 删除
     *
     * @param request 请求req
     * @param id      翻译类型对象
     * @return ResultData 返回数据
     * @date 2022年9月4日 下午7:42:24
     */
    @ApiOperation(value = "删除翻译类型 ", notes = "根据传入id进行删除状态修改(即软删除)")
    @RequestMapping(value = "delete", method = {RequestMethod.POST, RequestMethod.GET})
    public ResultData delete(@RequestParam(name = "id", required = false) Integer id, HttpServletRequest request) {
        ValidateUtils.notNull(id, "id不能为空");
        String remark = request.getParameter("remark");
        int v = trTransTypeBiz.deleteTrTransType(id, this.getCurrentUserId(), remark);
        return ResultData.success(v);
    }

    /**
     * 数据翻译重复查询
     *
     * @param request 请求req
     * @return ResultData 返回数据
     */
    @ApiOperation(value = "类型编码统计", notes = "类型编码统计")
    @RequestMapping(value = "transTypeCount", method = {RequestMethod.POST, RequestMethod.GET})
    public ResultData findTransTypeCount(HttpServletRequest request) {
        List<TransTypeCountVo> list = trTransTypeBiz.findTransTypeCount();
        return ResultData.success(list);
    }

    @ApiOperation(value = "翻译使用量统计", notes = "翻译使用量统计")
    @RequestMapping(value = "transCounts", method = {RequestMethod.POST, RequestMethod.GET})
    public ResultData transCounts(HttpServletRequest request) {
        String auth = request.getParameter("auth");
        if (!StringUtils.equals(dataTranslateConfiguration.getTransCountAuth(), auth)) {
            throw new PermissionDeniedException("auth错误");
        }
        return ResultData.success(TranslateUtil.getTransCounterMap());
    }

    @ApiOperation(value = "支持的目标语言", notes = "支持的目标语言")
    @RequestMapping(value = "typeDists", method = {RequestMethod.POST, RequestMethod.GET})
    public ResultData typeDists(HttpServletRequest request) {
        return ResultData.success(dataTranslateConfiguration.getTypeDists());
    }

    @ApiOperation(value = "支持翻译的属性查询", notes = "支持翻译的属性查询")
    @RequestMapping(value = "transFields", method = {RequestMethod.POST, RequestMethod.GET})
    public ResultData getTransFields(@RequestParam("className") String className, HttpServletRequest request) {
        IdInfoVo idInfoVo = TranslateUtil.getTranslateType(className);
        String transFields = null;
        if (idInfoVo != null) {
            transFields = idInfoVo.getTransFields().stream().collect(Collectors.joining(","));
        }
        return ResultData.success(transFields);
    }
}