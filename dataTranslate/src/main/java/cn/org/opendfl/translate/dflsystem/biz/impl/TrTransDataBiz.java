package cn.org.opendfl.translate.dflsystem.biz.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import cn.org.opendfl.translate.base.BaseService;
import cn.org.opendfl.translate.base.BeanUtils;
import cn.org.opendfl.translate.base.MyPageInfo;
import cn.org.opendfl.translate.dflsystem.biz.ITrTransDataBiz;
import cn.org.opendfl.translate.dflsystem.biz.ITrTransTypeBiz;
import cn.org.opendfl.translate.dflsystem.mapper.TrTransDataMapper;
import cn.org.opendfl.translate.dflsystem.po.TrTransDataPo;
import cn.org.opendfl.translate.dflsystem.translate.IdType;
import cn.org.opendfl.translate.dflsystem.translate.TransDto;
import cn.org.opendfl.translate.dflsystem.translate.TranslateTrans;
import cn.org.opendfl.translate.dflsystem.vo.TransDataCountVo;
import cn.org.opendfl.translate.dflsystem.vo.TransRepeatVo;
import com.github.pagehelper.PageHelper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.util.StringUtil;

import javax.annotation.Resource;
import java.util.*;

/**
 * 数据翻译 业务实现
 *
 * @author chenjh
 * @Copyright: 2022 opendfl Inc. All rights reserved.
 */
@Service(value = "trTransDataBiz")
public class TrTransDataBiz extends BaseService<TrTransDataPo> implements ITrTransDataBiz {
    @Resource
    private TrTransDataMapper mapper;

    static Logger logger = LoggerFactory.getLogger(TrTransDataBiz.class);

    @Override
    public Mapper<TrTransDataPo> getMapper() {
        return mapper;
    }

    @Resource
    private ITrTransTypeBiz trTransTypeBiz;

    public TrTransDataPo getDataById(Long id) {
        return getDataById(id, null);
    }

    /**
     * 按ID查数据
     *
     * @param id           数据id
     * @param ignoreFields 支持忽略属性，例如：ignoreFields=ifDel,createTime,createUser将不返回这些属性
     * @return 数据
     */
    public TrTransDataPo getDataById(Long id, String ignoreFields) {
        if (id == null || id == 0) {
            return null;
        }
        Example example = new Example(TrTransDataPo.class);
        if (StringUtils.isNotBlank(ignoreFields)) {
            String props = BeanUtils.getAllProperties(TrTransDataPo.class, ignoreFields);
            example.selectProperties(props.split(","));
        }
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("id", id);
        return this.mapper.selectOneByExample(example);
    }

    @Override
    public Example createConditions(TrTransDataPo entity, Map<String, Object> otherParams) {
        Example example = new Example(TrTransDataPo.class);
        Example.Criteria criteria = example.createCriteria();
        searchCondition(entity, otherParams, criteria);
        addFilters(criteria, otherParams);
        return example;
    }

    private void searchCondition(TrTransDataPo entity, Map<String, Object> otherParams, Example.Criteria criteria) {
        String startTime = (String) otherParams.get("startTime");
        if (StringUtil.isNotEmpty(startTime)) {
            criteria.andGreaterThanOrEqualTo("createTime", startTime);
        }
        String endTime = (String) otherParams.get("endTime");
        if (StringUtil.isNotEmpty(endTime)) {
            criteria.andLessThanOrEqualTo("createTime", endTime);
        }

        if (entity.getIfDel() != null) {
            criteria.andEqualTo("ifDel", entity.getIfDel());
        }
        this.addEqualByKey(criteria, "id", otherParams);
        this.addEqualByKey(criteria, "transTypeId", otherParams);
        this.addEqualByKey(criteria, "status", otherParams);
        this.addEqualByKey(criteria, "dataSid", otherParams);
        this.addEqualByKey(criteria, "dataNid", otherParams);


        if (entity instanceof TransDto) {
            TransDto transDto = (TransDto) entity;
            this.searchCondition(transDto, criteria);
        }

    }

    private void searchCondition(TransDto transDto, Example.Criteria criteria) {
        String transTypeCode = transDto.getTransTypeCode();
        if (StringUtils.isNotBlank(transTypeCode)) {
            Integer transTypeId = this.trTransTypeBiz.getTransTypeId(transTypeCode);
            if (transTypeId != null) {
                criteria.andEqualTo("transTypeId", transTypeId);
            }
        }

        String dataNids = transDto.getDataNids();
        if (StringUtils.isNotBlank(dataNids)) {
            String[] nids = dataNids.split(",");
            List<Long> nidList = new ArrayList<>(dataNids.length());
            for (String nid : nids) {
                if (StringUtils.isNotBlank(nid)) {
                    nidList.add(Long.parseLong(nid));
                }
            }
            criteria.andIn("dataNid", nidList);
        }
        String dataSids = transDto.getDataSids();
        if (StringUtils.isNotBlank(dataSids)) {
            criteria.andIn("dataSid", Arrays.asList(dataSids.split(",")));
        }
    }

    @Override
    public MyPageInfo<TrTransDataPo> findPageBy(TrTransDataPo entity, MyPageInfo<TrTransDataPo> pageInfo, Map<String, Object> otherParams) {
        if (entity == null) {
            entity = new TrTransDataPo();
        }
        Example example = createConditions(entity, otherParams);
        if (StringUtil.isNotEmpty(pageInfo.getOrderBy()) && StringUtil.isNotEmpty(pageInfo.getOrder())) {
            example.setOrderByClause(StringUtil.camelhumpToUnderline(pageInfo.getOrderBy()) + " " + pageInfo.getOrder());
        }
        PageHelper.startPage(pageInfo.getPageNum(), pageInfo.getPageSize());
        List<TrTransDataPo> list = this.getMapper().selectByExample(example);
        return new MyPageInfo<>(list);
    }

    @Override
    public Integer saveTrTransData(TrTransDataPo entity) {
        if (entity.getCreateTime() == null) {
            entity.setCreateTime(new Date());
        }
        entity.setUpdateTime(new Date());
        if (entity.getIfDel() == null) {
            entity.setIfDel(0);
        }
        return this.mapper.insert(entity);
    }

    @Override
    public Integer updateTrTransData(TrTransDataPo entity) {
        TrTransDataPo exist = this.findById(entity.getId());
        entity.setUpdateTime(new Date());
        if (entity.getIfDel() == null) {
            entity.setIfDel(0);
        }
        int v = this.updateByPrimaryKeySelective(entity);
        String transTypeCode = trTransTypeBiz.getTypeCode(entity.getTransTypeId());
        TranslateTrans.evictDataIdFieldCache(transTypeCode, exist.getCode(), exist.getLang(), entity.getId());
        return v;

    }

    @Override
    public Integer deleteTrTransData(Long id, Integer operUser, String remark) {
        TrTransDataPo po = new TrTransDataPo();
        po.setId(id);
        po.setIfDel(1); // 0未删除,1已删除
        po.setUpdateUser(operUser);
        po.setRemark(remark);
        po.setUpdateTime(new Date());
        return this.updateByPrimaryKeySelective(po);
    }

    public Map<String, Map<String, String>> getValueMapCacheByIdNum(final Integer dataTypeId, final String lang, final List<String> fields, final List<Object> idList) {
        if (CollUtil.isEmpty(idList)) {
            return MapUtil.empty();
        }
        List<TrTransDataPo> list = findDataTransListByIds(dataTypeId, lang, IdType.NUM, fields, idList);
        int fieldSie = fields.size();
        Map<String, Map<String, String>> dataIdMap = new HashMap<>(list.size() / fieldSie);
        for (TrTransDataPo trTransDataPo : list) {
            String key = trTransDataPo.getDataNid() + "_" + trTransDataPo.getLang();
            Map<String, String> fieldValueMap = dataIdMap.computeIfAbsent(key, k -> new HashMap<>(fieldSie));
            fieldValueMap.put(trTransDataPo.getCode(), trTransDataPo.getContent());
        }
        return dataIdMap;
    }

    public Map<String, Map<String, String>> getValueMapCacheByIdStr(final Integer dataTypeId, final String lang, final List<String> fields, final List<Object> idList) {
        if (CollUtil.isEmpty(fields) || CollUtil.isEmpty(idList)) {
            return new HashMap<>();
        }
        List<TrTransDataPo> list = findDataTransListByIds(dataTypeId, lang, IdType.STRING, fields, idList);
        int fieldSie = fields.size();
        Map<String, Map<String, String>> dataIdMap = new HashMap<>(list.size() / fieldSie);
        for (TrTransDataPo trTransDataPo : list) {
            String key = trTransDataPo.getDataSid() + "_" + trTransDataPo.getLang();
            Map<String, String> fieldValueMap = dataIdMap.computeIfAbsent(key, k -> new HashMap<>(fieldSie));
            fieldValueMap.put(trTransDataPo.getCode(), trTransDataPo.getContent());
        }
        return dataIdMap;
    }

    private List<TrTransDataPo> findDataTransListByIds(final Integer dataTypeId, String lang, final IdType idType, final List<String> fields, final List<Object> idList) {
        Example example = new Example(TrTransDataPo.class);
        String selectFields = "code,content,lang";
        Example.Criteria criteria = example.createCriteria();
        if (idType == IdType.NUM) {
            criteria.andIn("dataNid", idList);
            selectFields += ",dataNid";
        } else {
            criteria.andIn("dataSid", idList);
            selectFields += ",dataSid";
        }
        criteria.andIn("code", fields);
        if (StringUtils.isNotBlank(lang)) {
            criteria.andEqualTo("lang", lang);
        }
        criteria.andEqualTo("transTypeId", dataTypeId);
        criteria.andEqualTo("ifDel", 0);

        example.selectProperties(selectFields.split(","));
        return this.mapper.selectByExample(example);
    }

    public List<TransDataCountVo> findTransCount(Integer transTypeId) {
        return mapper.findTransCount(transTypeId);
    }

    public List<TransRepeatVo> findRepeatData(Integer transTypeId) {
        return mapper.findRepeatData(transTypeId);
    }
}