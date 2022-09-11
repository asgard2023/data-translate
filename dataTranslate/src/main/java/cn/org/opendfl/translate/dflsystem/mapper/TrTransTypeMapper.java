package cn.org.opendfl.translate.dflsystem.mapper;

import cn.org.opendfl.translate.dflsystem.po.TrTransTypePo;
import cn.org.opendfl.translate.dflsystem.vo.TransTypeCountVo;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.MapperById;

import java.util.List;

/**
 * 翻译类型 Mapper
 *
 * @author chenjh
 * @Company: opendfl
 * @Copyright: 2022 opendfl Inc. All rights reserved.
 */
public interface TrTransTypeMapper extends MapperById<TrTransTypePo> {
    public int insertXML(TrTransTypePo entity);


    /**
     * typeCode翻译类型统计
     *
     * @return
     */
    @Select("select type_code typeCode, max(create_time) maxCreateTime,max(update_time) maxUpdateTime, count(*) cout" +
            " from tr_trans_type where if_del=0" +
            " group by type_code")
    List<TransTypeCountVo> findTransTypeCount();
}