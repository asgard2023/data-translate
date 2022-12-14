package cn.org.opendfl.translateDemo.biz;

import cn.org.opendfl.base.IBaseService;
import cn.org.opendfl.translateDemo.po.DflResourcePo;

/**
 * @Version V1.0
 * @Title: IDflResourceBiz
 * @Description: 菜单资源管理 业务接口
 * @author chenjh
 * @Date: 2022-8-6 23:03:15
 * @Company: opendfl
 * @Copyright: 2022 opendfl Inc. All rights reserved.
 */
public interface IDflResourceBiz extends IBaseService<DflResourcePo> {
    public DflResourcePo getDataById(Integer id);

    public DflResourcePo getDataById(Integer id, String ignoreFields);

    /**
     * 菜单资源管理 保存
     *
     * @param entity
     * @return Integer
     
     * @date 2022-8-6 23:03:15
     */
    Integer saveDflResource(DflResourcePo entity);

    /**
     * 菜单资源管理 更新
     *
     * @param entity
     * @return Integer
     
     * @date 2022-8-6 23:03:15
     */
    Integer updateDflResource(DflResourcePo entity);

    /**
     * 菜单资源管理 删除
     *
     * @param id       主键ID
     * @param operUser 操作人
     * @param remark   备注
     * @return Integer
     
     * @date 2022-8-6 23:03:15
     */
    Integer deleteDflResource(Integer id, Integer operUser, String remark);
}