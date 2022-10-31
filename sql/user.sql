use yupi;
-- 用户表
create table user
(
    id            bigint auto_increment comment 'id' primary key,
    user_account  varchar(30)                        null comment '账号',
    username      varchar(30)                        null comment '用户昵称',
    avatar_url    varchar(255)                       null comment '用户头像',
    gender        tinyint                            null comment '性别',
    user_password varchar(255)                       not null comment '密码',
    phone         varchar(128)                       null comment '电话',
    email         varchar(255)                       null comment '邮箱',
    user_status   int      default 0                 not null comment '用户状态',
    create_time   datetime default CURRENT_TIMESTAMP null comment '创建时间',
    update_time   datetime default CURRENT_TIMESTAMP null comment '更新时间',
    is_delete     tinyint  default 0                 not null comment '逻辑删除',
    role          int      default 0                 not null comment '角色 0普通用户 1 管理员 		 ',
    planet_code   varchar(20)                        null comment '星球id',
    tags          varchar(1024)                      null comment '标签列表',
    profile       varchar(521)                       null comment '个人简介'
)
    comment '用户';
-- 队伍表
create table team
(
    id          bigint auto_increment comment 'id' primary key,
    name        varchar(256)                       null comment '队伍名称',
    description varchar(1024)                      null comment '队伍描述',
    max_num     int      default 1                 not null comment '最大人数',
    expire_time datetime                           null comment '过期时间',
    user_id     bigint comment '用户id',
    status      int      default 0                 not null comment '0-公开 1-私有 2-加密',
    password    varchar(512)                       null comment '房间密码',
    create_time datetime default CURRENT_TIMESTAMP null comment '创建时间',
    update_time datetime default CURRENT_TIMESTAMP null comment '更新时间',
    is_delete   tinyint  default 0                 not null comment '逻辑删除'
)
    comment '队伍表';
-- 用户队伍关系
create table user_team
(
    id          bigint auto_increment comment 'id' primary key,
    user_id     bigint comment '用户id',
    team_id     bigint comment '队伍id',
    join_time   datetime                           null comment '加入时间',
    create_time datetime default CURRENT_TIMESTAMP null comment '创建时间',
    update_time datetime default CURRENT_TIMESTAMP null comment '更新时间',
    is_delete   tinyint  default 0                 not null comment '逻辑删除'
)
    comment '用户队伍关系';
