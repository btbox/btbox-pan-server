-- ----------------------------
-- 1、操作日志记录
-- ----------------------------
drop table if exists sys_oper_log;
create table sys_oper_log (
    oper_id           bigint(20)      not null                   comment '日志主键',
    tenant_id         varchar2(20)     default '000000'           comment '租户编号',
    title             varchar2(50)     default ''                 comment '模块标题',
    business_type     int(2)          default 0                  comment '业务类型（0其它 1新增 2修改 3删除）',
    method            varchar2(100)    default ''                 comment '方法名称',
    request_method    varchar2(10)     default ''                 comment '请求方式',
    operator_type     int(1)          default 0                  comment '操作类别（0其它 1后台用户 2手机端用户）',
    oper_name         varchar2(50)     default ''                 comment '操作人员',
    dept_name         varchar2(50)     default ''                 comment '部门名称',
    oper_url          varchar2(255)    default ''                 comment '请求URL',
    oper_ip           varchar2(128)    default ''                 comment '主机地址',
    oper_location     varchar2(255)    default ''                 comment '操作地点',
    oper_param        varchar2(2000)   default ''                 comment '请求参数',
    json_result       varchar2(2000)   default ''                 comment '返回参数',
    status            int(1)          default 0                  comment '操作状态（0正常 1异常）',
    error_msg         varchar2(2000)   default ''                 comment '错误消息',
    oper_time         timestamp                                   comment '操作时间',
    cost_time         bigint(20)      default 0                  comment '消耗时间',
    primary key (oper_id),
    key idx_sys_oper_log_bt (business_type),
    key idx_sys_oper_log_s  (status),
    key idx_sys_oper_log_ot (oper_time)
)  comment = '操作日志记录';


-- ----------------------------
-- 2、字典类型表
-- ----------------------------
drop table if exists sys_dict_type;
create table sys_dict_type
(
    dict_id          bigint(20)      not null                   comment '字典主键',
    tenant_id        varchar2(20)     default '000000'           comment '租户编号',
    dict_name        varchar2(100)    default ''                 comment '字典名称',
    dict_type        varchar2(100)    default ''                 comment '字典类型',
    create_dept      bigint(20)      default null               comment '创建部门',
    create_by        bigint(20)      default null               comment '创建者',
    create_time      timestamp                                   comment '创建时间',
    update_by        bigint(20)      default null               comment '更新者',
    update_time      timestamp                                   comment '更新时间',
    remark           varchar2(500)    default null               comment '备注',
    primary key (dict_id),
    unique (tenant_id, dict_type)
)  comment = '字典类型表';

insert into sys_dict_type values(1, '000000', '用户性别', 'sys_user_sex',        103, 1, current_timestamp(), null, null, '用户性别列表');
insert into sys_dict_type values(2, '000000', '菜单状态', 'sys_show_hide',       103, 1, current_timestamp(), null, null, '菜单状态列表');
insert into sys_dict_type values(3, '000000', '系统开关', 'sys_normal_disable',  103, 1, current_timestamp(), null, null, '系统开关列表');
insert into sys_dict_type values(6, '000000', '系统是否', 'sys_yes_no',          103, 1, current_timestamp(), null, null, '系统是否列表');
insert into sys_dict_type values(7, '000000', '通知类型', 'sys_notice_type',     103, 1, current_timestamp(), null, null, '通知类型列表');
insert into sys_dict_type values(8, '000000', '通知状态', 'sys_notice_status',   103, 1, current_timestamp(), null, null, '通知状态列表');
insert into sys_dict_type values(9, '000000', '操作类型', 'sys_oper_type',       103, 1, current_timestamp(), null, null, '操作类型列表');
insert into sys_dict_type values(10, '000000', '系统状态', 'sys_common_status',  103, 1, current_timestamp(), null, null, '登录状态列表');
insert into sys_dict_type values(11, '000000', '授权类型', 'sys_grant_type',     103, 1, current_timestamp(), null, null, '认证授权类型');
insert into sys_dict_type values(12, '000000', '设备类型', 'sys_device_type',    103, 1, current_timestamp(), null, null, '客户端设备类型');


-- ----------------------------
-- 3、字典数据表
-- ----------------------------
drop table if exists sys_dict_data;
create table sys_dict_data
(
    dict_code        bigint(20)      not null                   comment '字典编码',
    tenant_id        varchar2(20)     default '000000'           comment '租户编号',
    dict_sort        int(4)          default 0                  comment '字典排序',
    dict_label       varchar2(100)    default ''                 comment '字典标签',
    dict_value       varchar2(100)    default ''                 comment '字典键值',
    dict_type        varchar2(100)    default ''                 comment '字典类型',
    css_class        varchar2(100)    default null               comment '样式属性（其他样式扩展）',
    list_class       varchar2(100)    default null               comment '表格回显样式',
    is_default       char(1)         default 'N'                comment '是否默认（Y是 N否）',
    create_dept      bigint(20)      default null               comment '创建部门',
    create_by        bigint(20)      default null               comment '创建者',
    create_time      timestamp                                   comment '创建时间',
    update_by        bigint(20)      default null               comment '更新者',
    update_time      timestamp                                   comment '更新时间',
    remark           varchar2(500)    default null               comment '备注',
    primary key (dict_code)
)  comment = '字典数据表';

insert into sys_dict_data values(1, '000000', 1,  '男',       '0',       'sys_user_sex',        '',   '',        'Y', 103, 1, current_timestamp(), null, null, '性别男');
insert into sys_dict_data values(2, '000000', 2,  '女',       '1',       'sys_user_sex',        '',   '',        'N', 103, 1, current_timestamp(), null, null, '性别女');
insert into sys_dict_data values(3, '000000', 3,  '未知',     '2',       'sys_user_sex',        '',   '',        'N', 103, 1, current_timestamp(), null, null, '性别未知');
insert into sys_dict_data values(4, '000000', 1,  '显示',     '0',       'sys_show_hide',       '',   'primary', 'Y', 103, 1, current_timestamp(), null, null, '显示菜单');
insert into sys_dict_data values(5, '000000', 2,  '隐藏',     '1',       'sys_show_hide',       '',   'danger',  'N', 103, 1, current_timestamp(), null, null, '隐藏菜单');
insert into sys_dict_data values(6, '000000', 1,  '正常',     '0',       'sys_normal_disable',  '',   'primary', 'Y', 103, 1, current_timestamp(), null, null, '正常状态');
insert into sys_dict_data values(7, '000000', 2,  '停用',     '1',       'sys_normal_disable',  '',   'danger',  'N', 103, 1, current_timestamp(), null, null, '停用状态');
insert into sys_dict_data values(12, '000000', 1,  '是',       'Y',       'sys_yes_no',          '',   'primary', 'Y', 103, 1, current_timestamp(), null, null, '系统默认是');
insert into sys_dict_data values(13, '000000', 2,  '否',       'N',       'sys_yes_no',          '',   'danger',  'N', 103, 1, current_timestamp(), null, null, '系统默认否');
insert into sys_dict_data values(14, '000000', 1,  '通知',     '1',       'sys_notice_type',     '',   'warning', 'Y', 103, 1, current_timestamp(), null, null, '通知');
insert into sys_dict_data values(15, '000000', 2,  '公告',     '2',       'sys_notice_type',     '',   'success', 'N', 103, 1, current_timestamp(), null, null, '公告');
insert into sys_dict_data values(16, '000000', 1,  '正常',     '0',       'sys_notice_status',   '',   'primary', 'Y', 103, 1, current_timestamp(), null, null, '正常状态');
insert into sys_dict_data values(17, '000000', 2,  '关闭',     '1',       'sys_notice_status',   '',   'danger',  'N', 103, 1, current_timestamp(), null, null, '关闭状态');
insert into sys_dict_data values(29, '000000', 99, '其他',     '0',       'sys_oper_type',       '',   'info',    'N', 103, 1, current_timestamp(), null, null, '其他操作');
insert into sys_dict_data values(18, '000000', 1,  '新增',     '1',       'sys_oper_type',       '',   'info',    'N', 103, 1, current_timestamp(), null, null, '新增操作');
insert into sys_dict_data values(19, '000000', 2,  '修改',     '2',       'sys_oper_type',       '',   'info',    'N', 103, 1, current_timestamp(), null, null, '修改操作');
insert into sys_dict_data values(20, '000000', 3,  '删除',     '3',       'sys_oper_type',       '',   'danger',  'N', 103, 1, current_timestamp(), null, null, '删除操作');
insert into sys_dict_data values(21, '000000', 4,  '授权',     '4',       'sys_oper_type',       '',   'primary', 'N', 103, 1, current_timestamp(), null, null, '授权操作');
insert into sys_dict_data values(22, '000000', 5,  '导出',     '5',       'sys_oper_type',       '',   'warning', 'N', 103, 1, current_timestamp(), null, null, '导出操作');
insert into sys_dict_data values(23, '000000', 6,  '导入',     '6',       'sys_oper_type',       '',   'warning', 'N', 103, 1, current_timestamp(), null, null, '导入操作');
insert into sys_dict_data values(24, '000000', 7,  '强退',     '7',       'sys_oper_type',       '',   'danger',  'N', 103, 1, current_timestamp(), null, null, '强退操作');
insert into sys_dict_data values(25, '000000', 8,  '生成代码', '8',       'sys_oper_type',       '',   'warning', 'N', 103, 1, current_timestamp(), null, null, '生成操作');
insert into sys_dict_data values(26, '000000', 9,  '清空数据', '9',       'sys_oper_type',       '',   'danger',  'N', 103, 1, current_timestamp(), null, null, '清空操作');
insert into sys_dict_data values(27, '000000', 1,  '成功',     '0',       'sys_common_status',   '',   'primary', 'N', 103, 1, current_timestamp(), null, null, '正常状态');
insert into sys_dict_data values(28, '000000', 2,  '失败',     '1',       'sys_common_status',   '',   'danger',  'N', 103, 1, current_timestamp(), null, null, '停用状态');
insert into sys_dict_data values(30, '000000', 0,  '密码认证', 'password',   'sys_grant_type',   'el-check-tag',   'default', 'N', 103, 1, current_timestamp(), null, null, '密码认证');
insert into sys_dict_data values(31, '000000', 0,  '短信认证', 'sms',        'sys_grant_type',   'el-check-tag',   'default', 'N', 103, 1, current_timestamp(), null, null, '短信认证');
insert into sys_dict_data values(32, '000000', 0,  '邮件认证', 'email',      'sys_grant_type',   'el-check-tag',   'default', 'N', 103, 1, current_timestamp(), null, null, '邮件认证');
insert into sys_dict_data values(33, '000000', 0,  '小程序认证', 'xcx',      'sys_grant_type',   'el-check-tag',   'default', 'N', 103, 1, current_timestamp(), null, null, '小程序认证');
insert into sys_dict_data values(34, '000000', 0,  '三方登录认证', 'social', 'sys_grant_type',   'el-check-tag',   'default', 'N', 103, 1, current_timestamp(), null, null, '三方登录认证');
insert into sys_dict_data values(35, '000000', 0,  'PC',    'pc',         'sys_device_type',     '',   'default', 'N', 103, 1, current_timestamp(), null, null, 'PC');
insert into sys_dict_data values(36, '000000', 0,  '安卓', 'android',     'sys_device_type',     '',   'default', 'N', 103, 1, current_timestamp(), null, null, '安卓');
insert into sys_dict_data values(37, '000000', 0,  'iOS', 'ios',          'sys_device_type',     '',   'default', 'N', 103, 1, current_timestamp(), null, null, 'iOS');
insert into sys_dict_data values(38, '000000', 0,  '小程序', 'xcx',       'sys_device_type',     '',   'default', 'N', 103, 1, current_timestamp(), null, null, '小程序');


-- ----------------------------
-- 4、系统授权表
-- ----------------------------
drop table if exists sys_client;
create table sys_client (
    id                  bigint(20)    not null            comment 'id',
    client_id           varchar2(64)   default null        comment '客户端id',
    client_key          varchar2(32)   default null        comment '客户端key',
    client_secret       varchar2(255)  default null        comment '客户端秘钥',
    grant_type          varchar2(255)  default null        comment '授权类型',
    device_type         varchar2(32)   default null        comment '设备类型',
    active_timeout      int(11)       default 1800        comment 'token活跃超时时间',
    timeout             int(11)       default 604800      comment 'token固定超时',
    status              char(1)       default '0'         comment '状态（0正常 1停用）',
    del_flag            char(1)       default '0'         comment '删除标志（0代表存在 2代表删除）',
    create_dept         bigint(20)    default null        comment '创建部门',
    create_by           bigint(20)    default null        comment '创建者',
    create_time         timestamp      default null        comment '创建时间',
    update_by           bigint(20)    default null        comment '更新者',
    update_time         timestamp      default null        comment '更新时间',
    primary key (id)
)  comment='系统授权表';

insert into sys_client values (1, 'e5cd7e4891bf95d1d19206ce24a7b32e', 'pc', 'pc123', 'password,social', 'pc', 1800, 604800, 0, 0, 103, 1, current_timestamp(), 1, current_timestamp());
insert into sys_client values (2, '428a8310cd442757ae699df5d894f051', 'app', 'app123', 'password,sms,social', 'android', 1800, 604800, 0, 0, 103, 1, current_timestamp(), 1, current_timestamp());

-- ----------------------------
-- 5、文件数据表
-- ----------------------------
drop table if exists `file_detail`;
create table `file_detail`
(
    `id`                varchar2(32)  not null COMMENT '文件id',
    `url`               varchar2(512) not null COMMENT '文件访问地址',
    `size`              bigint(20)   default null COMMENT '文件大小，单位字节',
    `filename`          varchar2(256) default null COMMENT '文件名称',
    `original_filename` varchar2(256) default null COMMENT '原始文件名',
    `base_path`         varchar2(256) default null COMMENT '基础存储路径',
    `path`              varchar2(256) default null COMMENT '存储路径',
    `ext`               varchar2(32)  default null COMMENT '文件扩展名',
    `content_type`      varchar2(128)  default null COMMENT 'MIME类型',
    `platform`          varchar2(32)  default null COMMENT '存储平台',
    `th_url`            varchar2(512) default null COMMENT '缩略图访问路径',
    `th_filename`       varchar2(256) default null COMMENT '缩略图名称',
    `th_size`           bigint(20)   default null COMMENT '缩略图大小，单位字节',
    `th_content_type`   varchar2(128)  default null COMMENT '缩略图MIME类型',
    `object_id`         varchar2(32)  default null COMMENT '文件所属对象id',
    `object_type`       varchar2(32)  default null COMMENT '文件所属对象类型，例如用户头像，评价图片',
    `attr`              text COMMENT '附加属性',
    `file_acl`          varchar2(32)  default null COMMENT '文件ACL',
    `th_file_acl`       varchar2(32)  default null COMMENT '缩略图文件ACL',
    `create_time`       timestamp     default null COMMENT '创建时间',
    PRIMARY KEY (`id`)
) COMMENT ='文件记录表';