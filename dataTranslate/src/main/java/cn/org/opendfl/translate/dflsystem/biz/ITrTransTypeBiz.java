package cn.org.opendfl.translate.dflsystem.biz;

import cn.org.opendfl.base.IBaseService;
import cn.org.opendfl.translate.dflsystem.po.TrTransTypePo;
import cn.org.opendfl.translate.dflsystem.vo.TransTypeCountVo;

import java.util.List;

/**
 * 翻译类型 业务接口
 *
 * @author chenjh
 * @copyright 2022 opendfl Inc. All rights reserved.
 */
public interface ITrTransTypeBiz extends IBaseService<TrTransTypePo> {
    TrTransTypePo getDataById(Integer id);

    TrTransTypePo getDataById(Integer id, String ignoreFields);

    TrTransTypePo getDataByByCode(String code, String ignoreFields, String includeFields);

    public Integer getTransTypeId(String code);

    public String getTypeCode(Integer id);

    /**
     * 翻译类型 保存
     *
     * @param entity
     * @return Integer
     * @date 2022年9月4日 下午7:42:24
     */
    Integer saveTrTransType(TrTransTypePo entity);

    /**
     * 翻译类型 更新
     *
     * @param entity
     * @return Integer
     * @date 2022年9月4日 下午7:42:24
     */
    Integer updateTrTransType(TrTransTypePo entity);

    /**
     * 翻译类型 删除
     *
     * @param id       主键ID
     * @param operUser 操作人
     * @param remark   备注
     * @return Integer
     * @date 2022年9月4日 下午7:42:24
     */
    Integer deleteTrTransType(Integer id, Integer operUser, String remark);

    /**
     * typeCode翻译类型统计
     *
     * @return
     */
    List<TransTypeCountVo> findTransTypeCount();
}