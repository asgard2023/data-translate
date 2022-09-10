package cn.org.opendfl.translate.dflsystem.controller;

import cn.org.opendfl.translate.base.BaseController;
import cn.org.opendfl.translate.base.MyPageInfo;
import cn.org.opendfl.translate.base.PageVO;
import cn.org.opendfl.translate.dflsystem.biz.ITrTransTypeBiz;
import cn.org.opendfl.translate.dflsystem.po.TrTransTypePo;
import cn.org.opendfl.translate.exception.ResultData;
import cn.org.opendfl.translate.exception.ValidateUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

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

    @Autowired
    private ITrTransTypeBiz trTransTypeBiz;

    /**
     * 翻译类型列表查询
     *
     * @param request  请求req
     * @param entity   翻译类型对象
     * @param pageInfo 翻页对象
     * @return MyPageInfo 带翻页的数据集
     * @author Generator
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
     * @author Generator
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
     * @author Generator
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
     * @param entity  翻译类型对象
     * @return ResultData 返回数据
     * @author Generator
     * @date 2022年9月4日 下午7:42:24
     */
    @ApiOperation(value = "删除翻译类型 ", notes = "根据传入id进行删除状态修改(即软删除)")
    @RequestMapping(value = "delete", method = {RequestMethod.POST, RequestMethod.GET})
    public ResultData delete(TrTransTypePo entity, HttpServletRequest request) {
        ValidateUtils.notNull(entity.getId(), "id不能为空");
        String remark = request.getParameter("remark");
        int v = trTransTypeBiz.deleteTrTransType(entity.getId(), this.getCurrentUserId(), remark);
        return ResultData.success(v);
    }
}