package cn.org.opendfl.translate.dflsystem.biz.impl;

import cn.org.opendfl.translate.base.BaseService;
import cn.org.opendfl.translate.base.BeanUtils;
import cn.org.opendfl.translate.base.MyPageInfo;
import cn.org.opendfl.translate.dflsystem.biz.ITrTransTypeBiz;
import cn.org.opendfl.translate.dflsystem.mapper.TrTransTypeMapper;
import cn.org.opendfl.translate.dflsystem.po.TrTransTypePo;
import cn.org.opendfl.translate.dflsystem.vo.TransTypeCountVo;
import com.github.pagehelper.PageHelper;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.util.StringUtil;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 翻译类型 业务实现
 *
 * @author chenjh
 * @Copyright: 2022 opendfl Inc. All rights reserved.
 */
@Service(value = "trTransTypeBiz")
public class TrTransTypeBiz extends BaseService<TrTransTypePo> implements ITrTransTypeBiz {
    @Resource
    private TrTransTypeMapper mapper;
    @Resource(name = "redisTemplateString")
    private RedisTemplate<String, String> redisTemplateString;

    static Logger logger = LoggerFactory.getLogger(TrTransTypeBiz.class);

    @Override
    public Mapper<TrTransTypePo> getMapper() {
        return mapper;
    }

    public String getTypeCode(Integer id) {
        String redisKey = "transTypeCode:" + id;
        String code = redisTemplateString.opsForValue().get("transTypeCode:" + id);
        if (code == null) {
            TrTransTypePo trTransTypePo = this.getDataById(id);
            if (trTransTypePo != null) {
                code = trTransTypePo.getCode();
                redisTemplateString.opsForValue().set(redisKey, code, 1, TimeUnit.HOURS);
            } else {
                logger.warn("-----getTypeCode id={} unexist", id);
            }
        }
        return code;
    }

    public void getTypeCode_evict(Integer id) {
        String redisKey = "transTypeCode:" + id;
        redisTemplateString.delete(redisKey);
    }


    public TrTransTypePo getDataById(Integer id) {
        return getDataById(id, "ifDel,createTime,createUser,updateTime,updateUser");
    }

    /**
     * 按ID查数据
     *
     * @param id           数据id
     * @param ignoreFields 支持忽略属性，例如：ignoreFields=ifDel,createTime,createUser将不返回这些属性
     * @return 数据
     */
    public TrTransTypePo getDataById(Integer id, String ignoreFields) {
        if (id == null || id == 0) {
            return null;
        }
        Example example = new Example(TrTransTypePo.class);
        if (StringUtils.isNotBlank(ignoreFields)) {
            String props = BeanUtils.getAllProperties(TrTransTypePo.class, ignoreFields);
            example.selectProperties(props.split(","));
        }
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("id", id);
        return this.mapper.selectOneByExample(example);
    }

    private static Cache<String, Integer> transCodeIdCache = CacheBuilder.newBuilder().expireAfterWrite(10, TimeUnit.MINUTES)
            .maximumSize(1000).build();

    public Integer getTransTypeId(String code) {
        if (StringUtils.isBlank(code)) {
            return null;
        }
        Integer id = transCodeIdCache.getIfPresent(code);
        if (id == null) {
            TrTransTypePo trTransTypePo = this.getDataByByCode(code, null, "code,id");
            if (trTransTypePo != null) {
                id = trTransTypePo.getId();
                transCodeIdCache.put(code, id);
            }
        }
        return id;
    }

    public TrTransTypePo getDataByByCode(String code, String ignoreFields, String includeFields) {
        if (StringUtils.isBlank(code)) {
            return null;
        }
        Example example = new Example(TrTransTypePo.class);
        if (StringUtils.isNotBlank(ignoreFields)) {
            String props = BeanUtils.getAllProperties(TrTransTypePo.class, ignoreFields);
            example.selectProperties(props.split(","));
        }
        if (StringUtils.isNotBlank(includeFields)) {
            example.selectProperties(includeFields.split(","));
        }
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("code", code);
        criteria.andEqualTo("ifDel", 0);
        return this.mapper.selectOneByExample(example);
    }

    @Override
    public Example createConditions(TrTransTypePo entity, Map<String, Object> otherParams) {
        Example example = new Example(entity.getClass());
        Example.Criteria criteria = example.createCriteria();
        searchCondition(entity, otherParams, criteria);
        addFilters(criteria, otherParams);
        return example;
    }

    private void searchCondition(TrTransTypePo entity, Map<String, Object> otherParams, Example.Criteria criteria) {
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
    }

    @Override
    public MyPageInfo<TrTransTypePo> findPageBy(TrTransTypePo entity, MyPageInfo<TrTransTypePo> pageInfo, Map<String, Object> otherParams) {
        if (entity == null) {
            entity = new TrTransTypePo();
        }
        Example example = createConditions(entity, otherParams);
        if (StringUtil.isNotEmpty(pageInfo.getOrderBy()) && StringUtil.isNotEmpty(pageInfo.getOrder())) {
            example.setOrderByClause(StringUtil.camelhumpToUnderline(pageInfo.getOrderBy()) + " " + pageInfo.getOrder());
        }
        PageHelper.startPage(pageInfo.getPageNum(), pageInfo.getPageSize());
        List<TrTransTypePo> list = this.getMapper().selectByExample(example);
        return new MyPageInfo<>(list);
    }

    @Override
    public Integer saveTrTransType(TrTransTypePo entity) {
        if (entity.getCreateTime() == null) {
            entity.setCreateTime(new Date());
        }
        entity.setUpdateTime(new Date());
        if (entity.getIfDel() == null) {
            entity.setIfDel(0);
        }
        return this.mapper.insertUseGeneratedKeys(entity);
    }

    @Override
    public Integer updateTrTransType(TrTransTypePo entity) {
        entity.setUpdateTime(new Date());
        if (entity.getIfDel() == null) {
            entity.setIfDel(0);
        }
        int v = this.updateByPrimaryKeySelective(entity);
        getTypeCode_evict(entity.getId());
        return v;
    }

    @Override
    public Integer deleteTrTransType(Integer id, Integer operUser, String remark) {
        TrTransTypePo po = new TrTransTypePo();
        po.setId(id);
        po.setIfDel(1); // 0未删除,1已删除
        po.setUpdateUser(operUser);
        po.setRemark(remark);
        po.setUpdateTime(new Date());
        return this.updateByPrimaryKeySelective(po);
    }

    public List<TransTypeCountVo> findTransTypeCount() {
        return mapper.findTransTypeCount();
    }
}