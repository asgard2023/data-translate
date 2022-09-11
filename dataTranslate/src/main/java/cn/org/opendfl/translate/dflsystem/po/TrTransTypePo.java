package cn.org.opendfl.translate.dflsystem.po;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.Date;

/**
 * 翻译类型 实体
 *
 * @author chenjh
 * @Copyright: 2022 opendfl Inc. All rights reserved.
 */
@Data
@Table(name = "tr_trans_type")
@JsonInclude(Include.ALWAYS)
public class TrTransTypePo implements Serializable {

    /**
     * 主键
     */
    @Id
    @Column(name = "id")
    private Integer id;
    /**
     * 类型
     */
    @Column(name = "code")
    @NotBlank(message = "code不能为空")
    @Length(message = "code超出最大长度32限制", max = 32)
    private String code;
    /**
     * 编码
     */
    @Column(name = "name")
    @Length(message = "name超出最大长度100限制", max = 100)
    private String name;

    /**
     * 仅做分类用，比如模块分类，微服务分类
     */
    @Column(name = "type_code")
    @Length(message = "typeCode超出最大长度32限制", max = 32)
    private String typeCode;
    /**
     * id属性名
     */
    @Column(name = "id_field")
    @Length(message = "idField超出最大长度32限制", max = 32)
    private String idField;
    /**
     * id属性类型
     */
    @Column(name = "id_type")
    private Integer idType;
    /**
     * 备注
     */
    @Column(name = "remark")
    @Length(message = "remark超出最大长度255限制", max = 255)
    private String remark;
    /**
     * 状态
     */
    @Column(name = "status")
    private Integer status;
    /**
     * 是否删除
     */
    @Column(name = "if_del")
    private Integer ifDel;
    /**
     * 创建时间
     */
    @Column(name = "create_time")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;
    /**
     * 更新时间
     */
    @Column(name = "update_time")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;
    /**
     * 创建人
     */
    @Column(name = "create_user")
    private Integer createUser;
    /**
     * 修改人
     */
    @Column(name = "update_user")
    private Integer updateUser;
}