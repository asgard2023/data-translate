CREATE TABLE `tr_trans_data`
(
    `id`            bigint                                                 NOT NULL AUTO_INCREMENT COMMENT '主键',
    `trans_type_id` mediumint                                              NOT NULL COMMENT '数据类型或表名id',
    `data_nid`      bigint                                                  DEFAULT NULL COMMENT '数字id',
    `data_sid`      varchar(64)                                             DEFAULT NULL COMMENT '字符串id',
    `code`          varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '编码',
    `lang`          varchar(32)                                             DEFAULT NULL COMMENT '语言编码',
    `content`       varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '内容',
    `remark`        varchar(255)                                            DEFAULT NULL COMMENT '备注',
    `status`        tinyint                                                 DEFAULT NULL COMMENT '状态',
    `if_del`        int                                                    NOT NULL COMMENT '是否删除',
    `create_time`   datetime                                                DEFAULT NULL COMMENT '创建时间',
    `update_time`   datetime                                                DEFAULT NULL COMMENT '更新时间',
    `create_user`   int                                                     DEFAULT NULL COMMENT '创建人',
    `update_user`   int                                                     DEFAULT NULL COMMENT '修改人',
    PRIMARY KEY (`id`),
    KEY             `idx_ll_code` (`code`,`lang`),
    KEY             `idx_ll_time` (`create_time`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb3 COMMENT='多语言国际化数据';

CREATE TABLE `tr_trans_type`
(
    `id`          int                                                     NOT NULL AUTO_INCREMENT COMMENT '主键',
    `code`        varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '类型',
    `name`        varchar(100)                                           DEFAULT NULL COMMENT '编码',
    `id_field`    varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT 'id属性名',
    `id_type`     tinyint                                                DEFAULT NULL COMMENT 'id属性类型',
    `remark`      varchar(255)                                           DEFAULT NULL COMMENT '备注',
    `status`      tinyint                                                DEFAULT NULL COMMENT '状态',
    `if_del`      int                                                     NOT NULL COMMENT '是否删除',
    `create_time` datetime                                               DEFAULT NULL COMMENT '创建时间',
    `update_time` datetime                                               DEFAULT NULL COMMENT '更新时间',
    `create_user` int                                                    DEFAULT NULL COMMENT '创建人',
    `update_user` int                                                    DEFAULT NULL COMMENT '修改人',
    PRIMARY KEY (`id`),
    KEY           `idx_dt_code` (`code`),
    KEY           `idx_dt_time` (`create_time`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb3 COMMENT='多语言数据类型';