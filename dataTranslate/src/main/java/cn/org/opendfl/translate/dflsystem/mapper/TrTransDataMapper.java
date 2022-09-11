package cn.org.opendfl.translate.dflsystem.mapper;

import cn.org.opendfl.translate.dflsystem.po.TrTransDataPo;
import cn.org.opendfl.translate.dflsystem.vo.TransDataCountVo;
import cn.org.opendfl.translate.dflsystem.vo.TransRepeatVo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.MapperById;

import java.util.List;

/**
 * 数据翻译 Mapper
 *
 * @author chenjh
 * @Copyright: 2022 opendfl Inc. All rights reserved.
 */
public interface TrTransDataMapper extends MapperById<TrTransDataPo> {
    @Select("select lang, code, max(create_time) maxCreateTime,max(update_time) maxUpdateTime, count(*) cout from tr_trans_data" +
            " where trans_type_id=#{transTypeId} and if_del=0  GROUP BY lang, code")
    List<TransDataCountVo> findTransCount(@Param("transTypeId") Integer transTypeId);

    @Select("select data_nid dataId, lang, code, count(*) cout from tr_trans_data" +
            " where trans_type_id=#{transTypeId} and if_del=0 GROUP BY lang, code, dataId HAVING cout>1")
    List<TransRepeatVo> findRepeatData(@Param("transTypeId") Integer transTypeId);
}