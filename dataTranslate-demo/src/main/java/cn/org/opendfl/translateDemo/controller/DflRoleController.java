package cn.org.opendfl.translateDemo.controller;


import cn.org.opendfl.base.BaseController;
import cn.org.opendfl.base.MyPageInfo;
import cn.org.opendfl.base.PageVO;
import cn.org.opendfl.exception.ResultData;
import cn.org.opendfl.exception.ValidateUtils;
import cn.org.opendfl.translate.dflsystem.translate.TranslateUtil;
import cn.org.opendfl.translateDemo.biz.IDflRoleBiz;
import cn.org.opendfl.translateDemo.po.DflRolePo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

/**
 * @Version V1.0
 * @Title: DflRolecontroller
 * @Description: 角色表 Controller
 * @Author: Created by chenjh
 * @Date: 2022-5-3 20:25:42
 */
@Api(tags = "角色接口")
@RestController
@RequestMapping("dflRole")
public class DflRoleController extends BaseController {

    static Logger logger = LoggerFactory.getLogger(DflRoleController.class);

    @Resource
    private IDflRoleBiz dflRoleBiz;

    /**
     * 返回翻译属性
     *
     * @return 属性名
     */
    @GetMapping("/transFields")
    public ResultData transFields(int id) {
        return ResultData.success(TranslateUtil.getTranslateType(DflRolePo.class));
    }

    /**
     * 角色表列表查询
     *
     * @param request
     * @param entity
     * @param pageInfo
     * @return java.lang.Object
     * @author chenjh
     * @date 2022-5-3 20:25:42
     */
    @ApiOperation(value = "角色列表", notes = "角色列表翻页查询")
    @RequestMapping(value = "/list", method = {RequestMethod.POST, RequestMethod.GET})
    public MyPageInfo<DflRolePo> queryPage(HttpServletRequest request, DflRolePo entity, MyPageInfo<DflRolePo> pageInfo) {
        if (entity == null) {
            entity = new DflRolePo();
        }
        entity.setIfDel(0);
        if (pageInfo.getPageSize() == 0) {
            pageInfo.setPageSize(getPageSize());
        }
        pageInfo = dflRoleBiz.findPageBy(entity, pageInfo, this.createAllParams(request));
        pageInfo.setDicts(TranslateUtil.dictMap(DflRolePo.class));
        TranslateUtil.transformLangsByTrnasType(request, pageInfo.getList());
        return pageInfo;
    }

    @ApiOperation(value = "角色列表(easyui)", notes = "角色列表翻页查询，用于兼容easyui的rows方式")
    @RequestMapping(value = "/list2", method = {RequestMethod.POST, RequestMethod.GET})
    public PageVO<DflRolePo> findByPage2(HttpServletRequest request, DflRolePo entity, MyPageInfo<DflRolePo> pageInfo) {
        logger.debug("-------findByPage-------");
        this.pageSortBy(pageInfo);
        pageInfo = queryPage(request, entity, pageInfo);
        return new PageVO(pageInfo);
    }

    @ApiOperation(value = "角色列表查询", notes = "用于页面的角色下拉列表功能，只显示第一页，最多显示100条，支持条件筛选(code或name)以方便联想输入")
    @RequestMapping(value = {"name/list"}, method = {RequestMethod.POST, RequestMethod.GET})
    @ResponseStatus(value = HttpStatus.OK)
    public List<DflRolePo> findNameByPage(HttpServletRequest request, DflRolePo user) {
        logger.info("-------findNameByPage-------");
        MyPageInfo<DflRolePo> pageInfo = new MyPageInfo<>();
        pageInfo.setPageNum(1);
        pageInfo.setPageSize(100);
        pageInfo = queryPage(request, user, pageInfo);
        return pageInfo.getList();
    }

    /**
     * 角色表 新增
     *
     * @param request
     * @param entity
     * @return ResultData
     * @author chenjh
     * @date 2022-5-3 20:25:42
     */
    @ApiOperation(value = "添加角色", notes = "添加一个角色")
    @RequestMapping(value = "/save", method = {RequestMethod.POST, RequestMethod.GET})
    public ResultData edit(@Valid DflRolePo entity, HttpServletRequest request) {
        if (entity.getId() != null && entity.getId() > 0) {
            return update(entity, request);
        }

        entity.setModifyUser(getCurrentUserId());
        entity.setCreateUser(getCurrentUserId());
        dflRoleBiz.saveDflRole(entity);
        return ResultData.success(entity.getId());
    }

    /**
     * 角色表 更新
     *
     * @param request
     * @param entity
     * @return ResultData
     * @author chenjh
     * @date 2022-5-3 20:25:42
     */
    @ApiOperation(value = "修改角色", notes = "根据传入的角色信息修改")
    @RequestMapping(value = "/update", method = {RequestMethod.POST, RequestMethod.GET})
    public ResultData update(DflRolePo entity, HttpServletRequest request) {
        entity.setModifyUser(getCurrentUserId());
        dflRoleBiz.updateDflRole(entity);
        return ResultData.success(entity.getId());
    }

    /**
     * 角色表 删除
     *
     * @param request
     * @param id      数据id
     * @return ResultData
     * @author chenjh
     * @date 2022-5-3 20:25:42
     */
    @ApiOperation(value = "删除角色", notes = "根据传入id进行删除状态修改(即软删除)")
    @RequestMapping(value = "/delete", method = {RequestMethod.POST, RequestMethod.DELETE})
    public ResultData delete(@RequestParam(name = "id", required = false) Integer id, HttpServletRequest request) {
        ValidateUtils.notNull(id, "id不能为空");
        String remark = request.getParameter("remark");
        int v = dflRoleBiz.deleteDflRole(id, this.getCurrentUserId(), remark);
        return ResultData.success(v);
    }
}