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
import javax.persistence.Transient;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.Date;

/**
 * 数据翻译 实体
 *
 * @author chenjh
 * @Copyright: 2022 opendfl Inc. All rights reserved.
 */
@Data
@Table(name = "tr_trans_data")
@JsonInclude(Include.ALWAYS)
public class TrTransDataPo implements Serializable {

    /**
     * 主键
     */
    @Id
    @Column(name = "id")
    private Long id;
    /**
     * 数据类型或表名id
     */
    @Column(name = "trans_type_id")
    private Integer transTypeId;
    @Transient
    private String transTypeCode;
    /**
     * 数字id
     */
    @Column(name = "data_nid")
    private Long dataNid;
    /**
     * 字符串id
     */
    @Column(name = "data_sid")
    @Length(message = "dataSid超出最大长度64限制", max = 64)
    private String dataSid;
    /**
     * 编码
     */
    @Column(name = "code")
    @NotBlank(message = "code不能为空")
    @Length(message = "code超出最大长度64限制", max = 64)
    private String code;
    /**
     * 语言编码
     */
    @Column(name = "lang")
    @Length(message = "lang超出最大长度32限制", max = 32)
    private String lang;
    /**
     * 内容
     */
    @Column(name = "content")
    @Length(message = "content超出最大长度255限制", max = 255)
    private String content;
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