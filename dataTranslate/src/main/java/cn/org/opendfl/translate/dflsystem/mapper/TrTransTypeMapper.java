package cn.org.opendfl.translate.dflsystem.mapper;

import cn.org.opendfl.translate.dflsystem.po.TrTransTypePo;
import tk.mybatis.mapper.common.MapperById;

/**
 * 翻译类型 Mapper
 *
 * @author chenjh
 * @Company: opendfl
 * @Copyright: 2022 opendfl Inc. All rights reserved.
 */
public interface TrTransTypeMapper extends MapperById<TrTransTypePo> {
    public int insertXML(TrTransTypePo entity);
}