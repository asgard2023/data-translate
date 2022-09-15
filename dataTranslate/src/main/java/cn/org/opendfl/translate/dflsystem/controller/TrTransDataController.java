package cn.org.opendfl.translate.dflsystem.controller;

import cn.org.opendfl.translate.base.BaseController;
import cn.org.opendfl.translate.base.MyPageInfo;
import cn.org.opendfl.translate.base.PageVO;
import cn.org.opendfl.translate.dflsystem.biz.ITrTransDataBiz;
import cn.org.opendfl.translate.dflsystem.biz.ITrTransTypeBiz;
import cn.org.opendfl.translate.dflsystem.po.TrTransDataPo;
import cn.org.opendfl.translate.dflsystem.translate.TransDto;
import cn.org.opendfl.translate.dflsystem.vo.TransDataCountVo;
import cn.org.opendfl.translate.dflsystem.vo.TransRepeatVo;
import cn.org.opendfl.translate.exception.FailedException;
import cn.org.opendfl.translate.exception.ResultData;
import cn.org.opendfl.translate.exception.ValidateUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 数据翻译 Controller
 *
 * @author chenjh
 */
@Api(tags = "数据翻译接口")
@RestController
@RequestMapping("dflsystem/trTransData")
public class TrTransDataController extends BaseController {

    static Logger logger = LoggerFactory.getLogger(TrTransDataController.class);

    @Autowired
    private ITrTransDataBiz trTransDataBiz;

    @Autowired
    private ITrTransTypeBiz trTransTypeBiz;

    /**
     * 数据翻译列表查询
     *
     * @param request  请求req
     * @param entity   数据翻译对象
     * @param pageInfo 翻页对象
     * @return MyPageInfo 带翻页的数据集
     * @date 2022年9月4日 下午7:43:53
     */
    @ApiOperation(value = "数据翻译列表", notes = "数据翻译列表翻页查询")
    @RequestMapping(value = "list", method = {RequestMethod.POST, RequestMethod.GET})
    public MyPageInfo<TrTransDataPo> queryPage(HttpServletRequest request, TrTransDataPo entity, TransDto transDto, MyPageInfo<TrTransDataPo> pageInfo) {
        if (entity == null) {
            entity = new TrTransDataPo();
        }
        entity.setIfDel(0);
        if (pageInfo.getPageSize() == 0) {
            pageInfo.setPageSize(getPageSize());
        }
        Map<String, Object> params = this.createAllParams(request);
        params.put("transDto", transDto);
        pageInfo = trTransDataBiz.findPageBy(entity, pageInfo, params);
        return pageInfo;
    }

    @ApiOperation(value = "数据翻译列表", notes = "数据翻译列表翻页查询")
    @RequestMapping(value = "listDict", method = {RequestMethod.POST, RequestMethod.GET})
    public MyPageInfo<TrTransDataPo> queryPage(HttpServletRequest request, TransDto transDto, MyPageInfo<TrTransDataPo> pageInfo) {

        if (pageInfo.getPageSize() == 0) {
            pageInfo.setPageSize(getPageSize());
        }
        Map<String, Object> params = this.createAllParams(request);
        pageInfo = trTransDataBiz.findPageBy(transDto, pageInfo, params);
        return pageInfo;
    }

    @ApiOperation(value = "数据翻译列表(easyui)", notes = "数据翻译列表翻页查询，用于兼容easyui的rows方式")
    @RequestMapping(value = "/list2", method = {RequestMethod.POST, RequestMethod.GET})
    public PageVO<TrTransDataPo> findByPage(HttpServletRequest request, TrTransDataPo entity, TransDto transDto, MyPageInfo<TrTransDataPo> pageInfo) {
        logger.debug("-------findByPage-------");
        this.pageSortBy(pageInfo);
        pageInfo = queryPage(request, entity, transDto, pageInfo);
        return new PageVO(pageInfo);
    }

    @ApiOperation(value = "添加数据翻译", notes = "添加一个数据翻译")
    @RequestMapping(value = "/saveJson", method = {RequestMethod.POST, RequestMethod.GET})
    public Object saveJson(@RequestBody TrTransDataPo transDataPo, HttpServletRequest request) {
        Validate.notNull(transDataPo.getCode(), "code is not null");
        Validate.notNull(transDataPo.getContent(), "content is not null");
        transDataPo.setCode(transDataPo.getCode().trim());
        transDataPo.setContent(transDataPo.getContent().trim());
        if (transDataPo.getTransTypeId() == null) {
            Integer transTypeId = this.trTransTypeBiz.getTransTypeId(transDataPo.getTransTypeCode());
            if (transTypeId == null) {
                throw new FailedException("transTypeCode:" + transDataPo.getTransTypeCode() + " invalid");
            }
            transDataPo.setTransTypeId(transTypeId);
            transDataPo.setStatus(1);
        }
        return edit(transDataPo, request);
    }

    /**
     * 数据翻译 新增
     *
     * @param request 请求req
     * @param entity  数据翻译对象
     * @return ResultData 返回数据
     * @date 2022年9月4日 下午7:43:53
     */
    @ApiOperation(value = "添加数据翻译", notes = "添加一个数据翻译")
    @RequestMapping(value = "save", method = {RequestMethod.POST, RequestMethod.GET})
    public ResultData edit(TrTransDataPo entity, HttpServletRequest request) {
        TrTransDataPo search = new TrTransDataPo();
        search.setCode(entity.getCode());
        search.setLang(entity.getLang());
        search.setTransTypeId(entity.getTransTypeId());
        search.setDataNid(entity.getDataNid());
        search.setDataSid(entity.getDataSid());
        List<TrTransDataPo> list = this.trTransDataBiz.findBy(search);
        if (CollectionUtils.isNotEmpty(list)) {
            TrTransDataPo exist = list.get(0);
            entity.setId(exist.getId());
            //如果值没变不保存
            if (!StringUtils.equals(exist.getContent(), entity.getContent())) {
                this.update(entity, request);
            }
        } else {
            entity.setCreateTime(new Date());
            entity.setUpdateTime(new Date());
            trTransDataBiz.saveTrTransData(entity);
        }
        return ResultData.success();
    }

    /**
     * 数据翻译 更新
     *
     * @param request 请求req
     * @param entity  数据翻译对象
     * @return ResultData 返回数据
     * @date 2022年9月4日 下午7:43:53
     */
    @ApiOperation(value = "修改数据翻译", notes = "根据传入的角色信息修改")
    @RequestMapping(value = "update", method = {RequestMethod.POST, RequestMethod.GET})
    public ResultData update(TrTransDataPo entity, HttpServletRequest request) {
        entity.setUpdateUser(getCurrentUserId());
        int v = trTransDataBiz.updateTrTransData(entity);
        return ResultData.success(v);
    }

    /**
     * 数据翻译 删除
     *
     * @param request 请求req
     * @param id      数据翻译对象
     * @return ResultData 返回数据
     * @date 2022年9月4日 下午7:43:53
     */
    @ApiOperation(value = "删除数据翻译 ", notes = "根据传入id进行删除状态修改(即软删除)")
    public ResultData delete(@RequestParam(name = "id", required = false) Long id, HttpServletRequest request) {
        ValidateUtils.notNull(id, "id不能为空");
        String remark = request.getParameter("remark");
        int v = trTransDataBiz.deleteTrTransData(id, this.getCurrentUserId(), remark);
        return ResultData.success(v);
    }

    /**
     * 翻译数据个数统计
     *
     * @param request     请求req
     * @param transTypeId 数据类型id
     * @return ResultData 返回数据
     */
    @ApiOperation(value = "翻译数据个数统计", notes = "翻译数据个数统计")
    @RequestMapping(value = "transCount", method = {RequestMethod.POST, RequestMethod.GET})
    public ResultData findTransCount(@RequestParam("transTypeId") Integer transTypeId, HttpServletRequest request) {
        List<TransDataCountVo> list = trTransDataBiz.findTransCount(transTypeId);
        return ResultData.success(list);
    }

    /**
     * 数据翻译重复查询
     *
     * @param request     请求req
     * @param transTypeId 数据类型id
     * @return ResultData 返回数据
     */
    @ApiOperation(value = "数据翻译重复查询", notes = "数据翻译重复查询")
    @RequestMapping(value = "transRepeat", method = {RequestMethod.POST, RequestMethod.GET})
    public ResultData findRepeatData(@RequestParam("transTypeId") Integer transTypeId, HttpServletRequest request) {
        List<TransRepeatVo> list = trTransDataBiz.findRepeatData(transTypeId);
        return ResultData.success(list);
    }
}