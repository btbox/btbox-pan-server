-- ----------------------------
-- 1、操作日志记录
-- ----------------------------
drop table if exists sys_oper_log;
create table if not exists sys_oper_log
(
    oper_id        int8,
    tenant_id      varchar(20)   default '000000'::varchar,
    title          varchar(50)   default ''::varchar,
    business_type  int4          default 0,
    method         varchar(100)  default ''::varchar,
    request_method varchar(10)   default ''::varchar,
    operator_type  int4          default 0,
    oper_name      varchar(50)   default ''::varchar,
    dept_name      varchar(50)   default ''::varchar,
    oper_url       varchar(255)  default ''::varchar,
    oper_ip        varchar(128)  default ''::varchar,
    oper_location  varchar(255)  default ''::varchar,
    oper_param     varchar(2000) default ''::varchar,
    json_result    varchar(2000) default ''::varchar,
    status         int4          default 0,
    error_msg      varchar(2000) default ''::varchar,
    oper_time      timestamp,
    cost_time      int8          default 0,
    constraint sys_oper_log_pk primary key (oper_id)
    );

create index idx_sys_oper_log_bt ON sys_oper_log (business_type);
create index idx_sys_oper_log_s ON sys_oper_log (status);
create index idx_sys_oper_log_ot ON sys_oper_log (oper_time);

comment on table sys_oper_log                   is '操作日志记录';
comment on column sys_oper_log.oper_id          is '日志主键';
comment on column sys_oper_log.tenant_id        is '租户编号';
comment on column sys_oper_log.title            is '模块标题';
comment on column sys_oper_log.business_type    is '业务类型（0其它 1新增 2修改 3删除）';
comment on column sys_oper_log.method           is '方法名称';
comment on column sys_oper_log.request_method   is '请求方式';
comment on column sys_oper_log.operator_type    is '操作类别（0其它 1后台用户 2手机端用户）';
comment on column sys_oper_log.oper_name        is '操作人员';
comment on column sys_oper_log.dept_name        is '部门名称';
comment on column sys_oper_log.oper_url         is '请求URL';
comment on column sys_oper_log.oper_ip          is '主机地址';
comment on column sys_oper_log.oper_location    is '操作地点';
comment on column sys_oper_log.oper_param       is '请求参数';
comment on column sys_oper_log.json_result      is '返回参数';
comment on column sys_oper_log.status           is '操作状态（0正常 1异常）';
comment on column sys_oper_log.error_msg        is '错误消息';
comment on column sys_oper_log.oper_time        is '操作时间';
comment on column sys_oper_log.cost_time        is '消耗时间';

-- ----------------------------
-- 2、字典类型表
-- ----------------------------
drop table if exists sys_dict_type;
create table if not exists sys_dict_type
(
    dict_id     int8,
    tenant_id   varchar(20)  default '000000'::varchar,
    dict_name   varchar(100) default ''::varchar,
    dict_type   varchar(100) default ''::varchar,
    create_dept int8,
    create_by   int8,
    create_time timestamp,
    update_by   int8,
    update_time timestamp,
    remark      varchar(500) default null::varchar,
    constraint sys_dict_type_pk primary key (dict_id)
    );

create unique index sys_dict_type_index1 ON sys_dict_type (tenant_id, dict_type);

comment on table sys_dict_type                  is '字典类型表';
comment on column sys_dict_type.dict_id         is '字典主键';
comment on column sys_dict_type.tenant_id       is '租户编号';
comment on column sys_dict_type.dict_name       is '字典名称';
comment on column sys_dict_type.dict_type       is '字典类型';
comment on column sys_dict_type.create_dept     is '创建部门';
comment on column sys_dict_type.create_by       is '创建者';
comment on column sys_dict_type.create_time     is '创建时间';
comment on column sys_dict_type.update_by       is '更新者';
comment on column sys_dict_type.update_time     is '更新时间';
comment on column sys_dict_type.remark          is '备注';

insert into sys_dict_type values(1, '000000', '用户性别', 'sys_user_sex',        103, 1, now(), null, null, '用户性别列表');
insert into sys_dict_type values(2, '000000', '菜单状态', 'sys_show_hide',       103, 1, now(), null, null, '菜单状态列表');
insert into sys_dict_type values(3, '000000', '系统开关', 'sys_normal_disable',  103, 1, now(), null, null, '系统开关列表');
insert into sys_dict_type values(6, '000000', '系统是否', 'sys_yes_no',          103, 1, now(), null, null, '系统是否列表');
insert into sys_dict_type values(7, '000000', '通知类型', 'sys_notice_type',     103, 1, now(), null, null, '通知类型列表');
insert into sys_dict_type values(8, '000000', '通知状态', 'sys_notice_status',   103, 1, now(), null, null, '通知状态列表');
insert into sys_dict_type values(9, '000000', '操作类型', 'sys_oper_type',       103, 1, now(), null, null, '操作类型列表');
insert into sys_dict_type values(10, '000000', '系统状态', 'sys_common_status',  103, 1, now(), null, null, '登录状态列表');
insert into sys_dict_type values(11, '000000', '授权类型', 'sys_grant_type',     103, 1, now(), null, null, '认证授权类型');
insert into sys_dict_type values(12, '000000', '设备类型', 'sys_device_type',    103, 1, now(), null, null, '客户端设备类型');

-- ----------------------------
-- 3、字典数据表
-- ----------------------------
drop table if exists sys_dict_data;
create table if not exists sys_dict_data
(
    dict_code   int8,
    tenant_id   varchar(20)  default '000000'::varchar,
    dict_sort   int4         default 0,
    dict_label  varchar(100) default ''::varchar,
    dict_value  varchar(100) default ''::varchar,
    dict_type   varchar(100) default ''::varchar,
    css_class   varchar(100) default null::varchar,
    list_class  varchar(100) default null::varchar,
    is_default  char         default 'N'::bpchar,
    create_dept int8,
    create_by   int8,
    create_time timestamp,
    update_by   int8,
    update_time timestamp,
    remark      varchar(500) default null::varchar,
    constraint sys_dict_data_pk primary key (dict_code)
    );

comment on table sys_dict_data                  is '字典数据表';
comment on column sys_dict_data.dict_code       is '字典编码';
comment on column sys_dict_type.tenant_id       is '租户编号';
comment on column sys_dict_data.dict_sort       is '字典排序';
comment on column sys_dict_data.dict_label      is '字典标签';
comment on column sys_dict_data.dict_value      is '字典键值';
comment on column sys_dict_data.dict_type       is '字典类型';
comment on column sys_dict_data.css_class       is '样式属性（其他样式扩展）';
comment on column sys_dict_data.list_class      is '表格回显样式';
comment on column sys_dict_data.is_default      is '是否默认（Y是 N否）';
comment on column sys_dict_data.create_dept     is '创建部门';
comment on column sys_dict_data.create_by       is '创建者';
comment on column sys_dict_data.create_time     is '创建时间';
comment on column sys_dict_data.update_by       is '更新者';
comment on column sys_dict_data.update_time     is '更新时间';
comment on column sys_dict_data.remark          is '备注';

insert into sys_dict_data values(1, '000000', 1,  '男',       '0',       'sys_user_sex',        '',   '',        'Y', 103, 1, now(), null, null, '性别男');
insert into sys_dict_data values(2, '000000', 2,  '女',       '1',       'sys_user_sex',        '',   '',        'N', 103, 1, now(), null, null, '性别女');
insert into sys_dict_data values(3, '000000', 3,  '未知',     '2',       'sys_user_sex',        '',   '',        'N', 103, 1, now(), null, null, '性别未知');
insert into sys_dict_data values(4, '000000', 1,  '显示',     '0',       'sys_show_hide',       '',   'primary', 'Y', 103, 1, now(), null, null, '显示菜单');
insert into sys_dict_data values(5, '000000', 2,  '隐藏',     '1',       'sys_show_hide',       '',   'danger',  'N', 103, 1, now(), null, null, '隐藏菜单');
insert into sys_dict_data values(6, '000000', 1,  '正常',     '0',       'sys_normal_disable',  '',   'primary', 'Y', 103, 1, now(), null, null, '正常状态');
insert into sys_dict_data values(7, '000000', 2,  '停用',     '1',       'sys_normal_disable',  '',   'danger',  'N', 103, 1, now(), null, null, '停用状态');
insert into sys_dict_data values(12, '000000', 1,  '是',       'Y',       'sys_yes_no',          '',   'primary', 'Y', 103, 1, now(), null, null, '系统默认是');
insert into sys_dict_data values(13, '000000', 2,  '否',       'N',       'sys_yes_no',          '',   'danger',  'N', 103, 1, now(), null, null, '系统默认否');
insert into sys_dict_data values(14, '000000', 1,  '通知',     '1',       'sys_notice_type',     '',   'warning', 'Y', 103, 1, now(), null, null, '通知');
insert into sys_dict_data values(15, '000000', 2,  '公告',     '2',       'sys_notice_type',     '',   'success', 'N', 103, 1, now(), null, null, '公告');
insert into sys_dict_data values(16, '000000', 1,  '正常',     '0',       'sys_notice_status',   '',   'primary', 'Y', 103, 1, now(), null, null, '正常状态');
insert into sys_dict_data values(17, '000000', 2,  '关闭',     '1',       'sys_notice_status',   '',   'danger',  'N', 103, 1, now(), null, null, '关闭状态');
insert into sys_dict_data values(29, '000000', 99, '其他',     '0',       'sys_oper_type',       '',   'info',    'N', 103, 1, now(), null, null, '其他操作');
insert into sys_dict_data values(18, '000000', 1,  '新增',     '1',       'sys_oper_type',       '',   'info',    'N', 103, 1, now(), null, null, '新增操作');
insert into sys_dict_data values(19, '000000', 2,  '修改',     '2',       'sys_oper_type',       '',   'info',    'N', 103, 1, now(), null, null, '修改操作');
insert into sys_dict_data values(20, '000000', 3,  '删除',     '3',       'sys_oper_type',       '',   'danger',  'N', 103, 1, now(), null, null, '删除操作');
insert into sys_dict_data values(21, '000000', 4,  '授权',     '4',       'sys_oper_type',       '',   'primary', 'N', 103, 1, now(), null, null, '授权操作');
insert into sys_dict_data values(22, '000000', 5,  '导出',     '5',       'sys_oper_type',       '',   'warning', 'N', 103, 1, now(), null, null, '导出操作');
insert into sys_dict_data values(23, '000000', 6,  '导入',     '6',       'sys_oper_type',       '',   'warning', 'N', 103, 1, now(), null, null, '导入操作');
insert into sys_dict_data values(24, '000000', 7,  '强退',     '7',       'sys_oper_type',       '',   'danger',  'N', 103, 1, now(), null, null, '强退操作');
insert into sys_dict_data values(25, '000000', 8,  '生成代码', '8',       'sys_oper_type',       '',   'warning', 'N', 103, 1, now(), null, null, '生成操作');
insert into sys_dict_data values(26, '000000', 9,  '清空数据', '9',       'sys_oper_type',       '',   'danger',  'N', 103, 1, now(), null, null, '清空操作');
insert into sys_dict_data values(27, '000000', 1,  '成功',     '0',       'sys_common_status',   '',   'primary', 'N', 103, 1, now(), null, null, '正常状态');
insert into sys_dict_data values(28, '000000', 2,  '失败',     '1',       'sys_common_status',   '',   'danger',  'N', 103, 1, now(), null, null, '停用状态');
insert into sys_dict_data values(30, '000000', 0,  '密码认证', 'password',   'sys_grant_type',   '',   'default', 'N', 103, 1, now(), null, null, '密码认证');
insert into sys_dict_data values(31, '000000', 0,  '短信认证', 'sms',        'sys_grant_type',   '',   'default', 'N', 103, 1, now(), null, null, '短信认证');
insert into sys_dict_data values(32, '000000', 0,  '邮件认证', 'email',      'sys_grant_type',   '',   'default', 'N', 103, 1, now(), null, null, '邮件认证');
insert into sys_dict_data values(33, '000000', 0,  '小程序认证', 'xcx',      'sys_grant_type',   '',   'default', 'N', 103, 1, now(), null, null, '小程序认证');
insert into sys_dict_data values(34, '000000', 0,  '三方登录认证', 'social', 'sys_grant_type',   '',   'default', 'N', 103, 1, now(), null, null, '三方登录认证');
insert into sys_dict_data values(35, '000000', 0,  'PC', 'pc',              'sys_device_type',   '',   'default', 'N', 103, 1, now(), null, null, 'PC');
insert into sys_dict_data values(36, '000000', 0,  '安卓', 'android',       'sys_device_type',   '',   'default', 'N', 103, 1, now(), null, null, '安卓');
insert into sys_dict_data values(37, '000000', 0,  'iOS', 'ios',            'sys_device_type',   '',   'default', 'N', 103, 1, now(), null, null, 'iOS');
insert into sys_dict_data values(38, '000000', 0,  '小程序', 'xcx',         'sys_device_type',   '',   'default', 'N', 103, 1, now(), null, null, '小程序');


-- ----------------------------
-- 4、 系统授权表
-- ----------------------------
drop table if exists sys_client;
create table sys_client (
                            id                  int8,
                            client_id           varchar(64)   default ''::varchar,
                            client_key          varchar(32)   default ''::varchar,
                            client_secret       varchar(255)  default ''::varchar,
                            grant_type          varchar(255)  default ''::varchar,
                            device_type         varchar(32)   default ''::varchar,
                            active_timeout      int4          default 1800,
                            timeout             int4          default 604800,
                            status              char(1)       default '0'::bpchar,
                            del_flag            char(1)       default '0'::bpchar,
                            create_dept         int8,
                            create_by           int8,
                            create_time         timestamp,
                            update_by           int8,
                            update_time         timestamp,
                            constraint sys_client_pk primary key (id)
);

comment on table sys_client                         is '系统授权表';
comment on column sys_client.id                     is '主建';
comment on column sys_client.client_id              is '客户端id';
comment on column sys_client.client_key             is '客户端key';
comment on column sys_client.client_secret          is '客户端秘钥';
comment on column sys_client.grant_type             is '授权类型';
comment on column sys_client.device_type            is '设备类型';
comment on column sys_client.active_timeout         is 'token活跃超时时间';
comment on column sys_client.timeout                is 'token固定超时';
comment on column sys_client.status                 is '状态（0正常 1停用）';
comment on column sys_client.del_flag               is '删除标志（0代表存在 2代表删除）';
comment on column sys_client.create_dept            is '创建部门';
comment on column sys_client.create_by              is '创建者';
comment on column sys_client.create_time            is '创建时间';
comment on column sys_client.update_by              is '更新者';
comment on column sys_client.update_time            is '更新时间';

insert into sys_client values (1, 'e5cd7e4891bf95d1d19206ce24a7b32e', 'pc', 'pc123', 'password,social', 'pc', 1800, 604800, 0, 0, 103, 1, now(), 1, now());
insert into sys_client values (2, '428a8310cd442757ae699df5d894f051', 'app', 'app123', 'password,sms,social', 'android', 1800, 604800, 0, 0, 103, 1, now(), 1, now());

-- ----------------------------
-- 5、文件数据表
-- ----------------------------
drop table if exists file_detail;
create table file_detail
(
    id                varchar(32)  not null,
    url               varchar(512) not null,
    size              int8   default null,
    filename          varchar(256) default null,
    original_filename varchar(256) default null,
    base_path         varchar(256) default null,
    path              varchar(256) default null,
    ext               varchar(32)  default null,
    content_type      varchar(128) default null,
    platform          varchar(32)  default null,
    th_url            varchar(512) default null,
    th_filename       varchar(256) default null,
    th_size           int8         default null,
    th_content_type   varchar(128) default null,
    object_id         varchar(32)  default null,
    object_type       varchar(32)  default null,
    attr              text,
    file_acl          varchar(32)  default null,
    th_file_acl       varchar(32)  default null,
    create_time       timestamp    default null,
    constraint file_detail_pk primary key (id)
);

comment on table file_detail is '文件数据表';
comment on column file_detail.id is '文件id';
comment on column file_detail.url is '文件访问地址';
comment on column file_detail.size is '文件大小，单位字节';
comment on column file_detail.filename is '文件名称';
comment on column file_detail.original_filename is '原始文件名';
comment on column file_detail.base_path is '基础存储路径';
comment on column file_detail.path is '存储路径';
comment on column file_detail.ext is '文件扩展名';
comment on column file_detail.content_type is 'MIME类型';
comment on column file_detail.platform is '存储平台';
comment on column file_detail.th_url is '缩略图访问路径';
comment on column file_detail.th_filename is '缩略图名称';
comment on column file_detail.th_size is '缩略图大小，单位字节';
comment on column file_detail.th_content_type is '缩略图MIME类型';
comment on column file_detail.object_id is '文件所属对象id';
comment on column file_detail.object_type is '文件所属对象类型，例如用户头像，评价图片';
comment on column file_detail.attr is '附加属性';
comment on column file_detail.file_acl is '文件ACL';
comment on column file_detail.th_file_acl is '缩略图文件ACL';
comment on column file_detail.create_time is '创建时间';


-- 字符串自动转时间 避免框架时间查询报错问题
create or replace function cast_varchar_to_timestamp(varchar) returns timestamptz as $$
select to_timestamp($1, 'yyyy-mm-dd hh24:mi:ss');
$$ language sql strict ;

create cast (varchar as timestamptz) with function cast_varchar_to_timestamp as IMPLICIT;
