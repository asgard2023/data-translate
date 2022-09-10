package cn.org.opendfl.translate.dflsystem.biz;

import cn.org.opendfl.translate.base.IBaseService;
import cn.org.opendfl.translate.dflsystem.po.TrTransDataPo;

import java.util.List;
import java.util.Map;

/**
 * 数据翻译 业务接口
 *
 * @author chenjh
 * @copyright 2022 opendfl Inc. All rights reserved.
 */
public interface ITrTransDataBiz extends IBaseService<TrTransDataPo> {
    TrTransDataPo getDataById(Long id);

    TrTransDataPo getDataById(Long id, String ignoreFields);

    Map<String, Map<String, String>> getValueMapCacheByIdNum(Integer dataTypeId, String lang, List<String> fields, List<Object> idList);

    Map<String, Map<String, String>> getValueMapCacheByIdStr(Integer dataTypeId, String lang, List<String> fields, List<Object> idList);

    /**
     * 数据翻译 保存
     *
     * @param entity
     * @return Integer
     * @author Generator
     * @date 2022年9月4日 下午7:43:53
     */
    Integer saveTrTransData(TrTransDataPo entity);

    /**
     * 数据翻译 更新
     *
     * @param entity
     * @return Integer
     * @author Generator
     * @date 2022年9月4日 下午7:43:53
     */
    Integer updateTrTransData(TrTransDataPo entity);

    /**
     * 数据翻译 删除
     *
     * @param id       主键ID
     * @param operUser 操作人
     * @param remark   备注
     * @return Integer
     * @author Generator
     * @date 2022年9月4日 下午7:43:53
     */
    Integer deleteTrTransData(Long id, Integer operUser, String remark);
}