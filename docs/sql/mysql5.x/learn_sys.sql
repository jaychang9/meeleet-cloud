/*
* 系统管理表
* MySQL5.x版本
*/
use learn_sys;

SET NAMES utf8;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for sys_dept
-- ----------------------------
DROP TABLE IF EXISTS `sys_dept`;
CREATE TABLE `sys_dept` (
                            `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
                            `name` varchar(64) DEFAULT '' COMMENT '部门名称',
                            `parent_id` bigint(20) DEFAULT '0' COMMENT '父节点id',
                            `tree_path` varchar(255) DEFAULT '' COMMENT '父节点id路径',
                            `sort` int(11) DEFAULT '0' COMMENT '显示顺序',
                            `status` tinyint(1) DEFAULT '1' COMMENT '状态：1-正常 0-禁用',
                            `is_deleted` tinyint(1) DEFAULT '0' COMMENT '删除状态：1-删除 0-未删除',
                            `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                            `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                            PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='部门表';

-- ----------------------------
-- Records of sys_dept
-- ----------------------------
INSERT INTO `sys_dept` VALUES ('1', '阿来技术', '0', '0', '1', '1', '0', null, null);
INSERT INTO `sys_dept` VALUES ('2', '研发部门', '1', '0,1', '1', '1', '0', null, '2022-04-19 12:46:37');
INSERT INTO `sys_dept` VALUES ('3', '测试部门', '1', '0,1', '2', '1', '0', null, null);

-- ----------------------------
-- Table structure for sys_dict
-- ----------------------------
DROP TABLE IF EXISTS `sys_dict`;
CREATE TABLE `sys_dict` (
                            `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键 ',
                            `name` varchar(50) DEFAULT '' COMMENT '类型名称',
                            `code` varchar(50) DEFAULT '' COMMENT '类型编码',
                            `status` tinyint(1) DEFAULT '0' COMMENT '状态（0-正常 ,1-停用）',
                            `remark` varchar(255) DEFAULT NULL COMMENT '备注',
                            `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                            `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                            PRIMARY KEY (`id`) USING BTREE,
                            UNIQUE KEY `type_code` (`code`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='字典类型表';

-- ----------------------------
-- Records of sys_dict
-- ----------------------------
INSERT INTO `sys_dict` VALUES ('1', '性别', 'gender', '1', null, '2019-12-06 19:03:32', '2021-02-08 14:58:01');
INSERT INTO `sys_dict` VALUES ('2', '授权方式', 'grant_type', '1', null, '2020-10-17 08:09:50', '2021-01-31 09:48:24');
INSERT INTO `sys_dict` VALUES ('3', '微服务列表', 'micro_service', '1', null, '2021-06-17 00:13:43', '2021-06-17 00:17:22');
INSERT INTO `sys_dict` VALUES ('4', '请求方式', 'request_method', '1', null, '2021-06-17 00:18:07', '2021-06-17 00:18:07');

-- ----------------------------
-- Table structure for sys_dict_item
-- ----------------------------
DROP TABLE IF EXISTS `sys_dict_item`;
CREATE TABLE `sys_dict_item` (
                                 `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
                                 `name` varchar(50) DEFAULT '' COMMENT '字典项名称',
                                 `value` varchar(50) DEFAULT '' COMMENT '字典项值',
                                 `dict_code` varchar(50) DEFAULT '' COMMENT '字典编码',
                                 `sort` int(11) DEFAULT '0' COMMENT '排序',
                                 `status` tinyint(1) DEFAULT '0' COMMENT '状态（0 停用 1正常）',
                                 `is_default` tinyint(1) DEFAULT '0' COMMENT '是否默认（0否 1是）',
                                 `remark` varchar(255) DEFAULT '' COMMENT '备注',
                                 `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                 `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                 PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=22 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='字典数据表';

-- ----------------------------
-- Records of sys_dict_item
-- ----------------------------
INSERT INTO `sys_dict_item` VALUES ('1', '男', '1', 'gender', '1', '1', '0', null, '2019-05-05 13:07:52', '2019-07-02 14:23:05');
INSERT INTO `sys_dict_item` VALUES ('2', '女', '2', 'gender', '2', '1', '0', null, '2019-04-19 11:33:00', '2019-07-02 14:23:05');
INSERT INTO `sys_dict_item` VALUES ('3', '未知', '0', 'gender', '1', '1', '0', null, '2020-10-17 08:09:31', '2020-10-17 08:09:31');
INSERT INTO `sys_dict_item` VALUES ('6', '密码模式', 'password', 'grant_type', '1', '1', '0', null, '2020-10-17 09:11:52', '2021-01-31 09:48:18');
INSERT INTO `sys_dict_item` VALUES ('7', '授权码模式', 'authorization_code', 'grant_type', '1', '1', '0', null, '2020-10-17 09:12:15', '2020-12-14 10:11:00');
INSERT INTO `sys_dict_item` VALUES ('8', '客户端模式', 'client_credentials', 'grant_type', '1', '1', '0', null, '2020-10-17 09:12:36', '2020-12-14 10:11:00');
INSERT INTO `sys_dict_item` VALUES ('9', '刷新模式', 'refresh_token', 'grant_type', '1', '1', '0', null, '2020-10-17 09:12:57', '2021-01-08 17:33:12');
INSERT INTO `sys_dict_item` VALUES ('10', '简化模式', 'implicit', 'grant_type', '1', '1', '0', null, '2020-10-17 09:13:23', '2020-12-14 10:11:00');
INSERT INTO `sys_dict_item` VALUES ('11', '系统服务', 'youlai-admin', 'micro_service', '1', '1', '0', null, '2021-06-17 00:14:12', '2021-06-17 00:14:12');
INSERT INTO `sys_dict_item` VALUES ('12', '会员服务', 'youlai-ums', 'micro_service', '2', '1', '0', null, '2021-06-17 00:15:06', '2021-06-17 00:15:06');
INSERT INTO `sys_dict_item` VALUES ('13', '商品服务', 'youlai-pms', 'micro_service', '3', '1', '0', null, '2021-06-17 00:15:26', '2021-06-17 00:16:18');
INSERT INTO `sys_dict_item` VALUES ('14', '订单服务', 'youlai-oms', 'micro_service', '4', '1', '0', null, '2021-06-17 00:15:40', '2021-06-17 00:16:10');
INSERT INTO `sys_dict_item` VALUES ('15', '营销服务', 'youlai-sms', 'micro_service', '5', '1', '0', null, '2021-06-17 00:16:01', '2021-06-17 00:16:01');
INSERT INTO `sys_dict_item` VALUES ('16', '不限', '*', 'request_method', '1', '1', '0', null, '2021-06-17 00:18:34', '2021-06-17 00:18:34');
INSERT INTO `sys_dict_item` VALUES ('17', 'GET', 'GET', 'request_method', '2', '1', '0', null, '2021-06-17 00:18:55', '2021-06-17 00:18:55');
INSERT INTO `sys_dict_item` VALUES ('18', 'POST', 'POST', 'request_method', '3', '1', '0', null, '2021-06-17 00:19:06', '2021-06-17 00:19:06');
INSERT INTO `sys_dict_item` VALUES ('19', 'PUT', 'PUT', 'request_method', '4', '1', '0', null, '2021-06-17 00:19:17', '2021-06-17 00:19:17');
INSERT INTO `sys_dict_item` VALUES ('20', 'DELETE', 'DELETE', 'request_method', '5', '1', '0', null, '2021-06-17 00:19:30', '2021-06-17 00:19:30');
INSERT INTO `sys_dict_item` VALUES ('21', 'PATCH', 'PATCH', 'request_method', '6', '1', '0', null, '2021-06-17 00:19:42', '2021-06-17 00:19:42');

-- ----------------------------
-- Table structure for sys_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_menu`;
CREATE TABLE `sys_menu` (
                            `id` bigint(20) NOT NULL AUTO_INCREMENT,
                            `parent_id` bigint(20) DEFAULT NULL COMMENT '父菜单ID',
                            `name` varchar(64) DEFAULT '' COMMENT '菜单名称',
                            `path` varchar(128) DEFAULT '' COMMENT '路由路径(浏览器地址栏路径)',
                            `component` varchar(128) DEFAULT NULL COMMENT '组件路径(vue页面完整路径，省略.vue后缀)',
                            `icon` varchar(64) DEFAULT '' COMMENT '菜单图标',
                            `sort` int(11) DEFAULT '0' COMMENT '排序',
                            `is_visible` tinyint(1) DEFAULT '1' COMMENT '状态(0:禁用;1:开启)',
                            `redirect` varchar(128) DEFAULT '' COMMENT '跳转路径',
                            `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                            `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                            `type` tinyint(4) DEFAULT NULL COMMENT '菜单类型(1:菜单;2:目录;3:外链)',
                            PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=35 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='菜单管理';

-- ----------------------------
-- Records of sys_menu
-- ----------------------------
INSERT INTO `sys_menu` VALUES ('1', '0', '系统管理', '/system', 'Layout', 'system', '1', '1', '/system/user', '2021-08-28 09:12:21', '2021-08-28 09:12:21', '2');
INSERT INTO `sys_menu` VALUES ('2', '1', '用户管理', 'user', 'system/user/index', 'user', '1', '1', null, '2021-08-28 09:12:21', '2021-08-28 09:12:21', '1');
INSERT INTO `sys_menu` VALUES ('3', '1', '角色管理', 'role', 'system/role/index', 'role', '2', '1', null, '2021-08-28 09:12:21', '2021-08-28 09:12:21', '1');
INSERT INTO `sys_menu` VALUES ('4', '1', '菜单管理', 'cmenu', 'system/menu/index', 'menu', '3', '1', null, '2021-08-28 09:12:21', '2021-08-28 09:12:21', '1');
INSERT INTO `sys_menu` VALUES ('5', '1', '部门管理', 'dept', 'system/dept/index', 'tree', '4', '1', null, '2021-08-28 09:12:21', '2021-08-28 09:12:21', '1');
INSERT INTO `sys_menu` VALUES ('6', '1', '字典管理', 'dict', 'system/dict/index', 'education', '5', '1', null, '2021-08-28 09:12:21', '2021-08-28 09:12:21', '1');
INSERT INTO `sys_menu` VALUES ('7', '1', '客户端管理', 'client', 'system/client/index', 'client', '6', '1', null, '2021-08-28 09:12:21', '2021-08-28 09:12:21', '1');
INSERT INTO `sys_menu` VALUES ('9', '0', '营销管理', '/sms', 'Layout', 'number', '5', '1', null, '2021-08-28 09:12:21', '2021-08-28 09:12:21', '2');
INSERT INTO `sys_menu` VALUES ('10', '9', '广告管理', 'advert', 'sms/advert/index', 'advert', '1', '1', null, '2021-08-28 09:12:21', '2021-08-28 09:12:21', '1');
INSERT INTO `sys_menu` VALUES ('11', '0', '商品管理', '/pms', 'Layout', 'goods', '2', '1', '/pms/goods', '2021-08-28 09:12:21', '2021-08-28 09:12:21', '1');
INSERT INTO `sys_menu` VALUES ('12', '11', '商品列表', 'goods', 'pms/goods/index', 'goods-list', '1', '1', null, '2021-08-28 09:12:21', '2021-08-28 09:12:21', '1');
INSERT INTO `sys_menu` VALUES ('13', '0', '订单管理', '/oms', 'Layout', 'shopping', '3', '1', '/oms/order', '2021-08-28 09:12:21', '2021-08-28 09:12:21', '1');
INSERT INTO `sys_menu` VALUES ('14', '13', '订单列表', 'order', 'oms/order/index', 'order', '1', '1', null, '2021-08-28 09:12:21', '2021-08-28 09:12:21', '1');
INSERT INTO `sys_menu` VALUES ('15', '0', '会员管理', '/ums', 'Layout', 'user', '4', '1', '/ums/member', '2021-08-28 09:12:21', '2021-08-28 09:12:21', '1');
INSERT INTO `sys_menu` VALUES ('16', '15', '会员列表', 'member', 'ums/member/index', 'peoples', '1', '1', null, '2021-08-28 09:12:21', '2021-08-28 09:12:21', '1');
INSERT INTO `sys_menu` VALUES ('17', '11', '品牌管理', 'brand', 'pms/brand/index', 'brand', '5', '1', null, '2021-08-28 09:12:21', '2021-08-28 09:12:21', '1');
INSERT INTO `sys_menu` VALUES ('18', '11', '商品分类', 'category', 'pms/category/index', 'menu', '3', '1', null, '2021-08-28 09:12:21', '2021-08-28 09:12:21', '1');
INSERT INTO `sys_menu` VALUES ('19', '11', '商品上架', 'goods-detail', 'pms/goods/detail', 'publish', '2', '1', null, '2021-08-28 09:12:21', '2021-08-28 09:12:21', '1');
INSERT INTO `sys_menu` VALUES ('20', '0', '多级菜单', '/multi-level-menu', 'Layout', 'nested', '7', '1', '/nested/level1/level2', '2022-02-16 23:11:00', '2022-02-16 23:11:00', '2');
INSERT INTO `sys_menu` VALUES ('21', '20', '菜单一级', 'nested_level1_index', 'nested/level1/index', '', '1', '1', '/nested/level1/level2', '2022-02-16 23:13:38', '2022-02-16 23:13:38', '1');
INSERT INTO `sys_menu` VALUES ('22', '21', '菜单二级', 'nested_level1_level2_index', 'nested/level1/level2/index', '', '1', '1', '/nested/level1/level2/level3', '2022-02-16 23:14:23', '2022-02-16 23:14:23', '1');
INSERT INTO `sys_menu` VALUES ('23', '22', '菜单三级-1', 'nested_level1_level2_level3_index1', 'nested/level1/level2/level3/index1', '', '1', '1', '', '2022-02-16 23:14:51', '2022-02-16 23:14:51', '1');
INSERT INTO `sys_menu` VALUES ('24', '22', '菜单三级-2', 'nested_level1_level2_level3_index2', 'nested/level1/level2/level3/index2', '', '2', '1', '', '2022-02-16 23:15:08', '2022-02-16 23:15:08', '1');
INSERT INTO `sys_menu` VALUES ('26', '0', '外部链接', '/external-link', 'Layout', 'link', '9', '1', 'noredirect', '2022-02-17 22:51:20', '2022-02-17 22:51:20', '1');
INSERT INTO `sys_menu` VALUES ('30', '26', 'document', 'https://www.cnblogs.com/haoxianrui/', '', 'link', '1', '1', '', '2022-02-18 00:01:40', '2022-02-18 00:01:40', '3');
INSERT INTO `sys_menu` VALUES ('32', '0', '实验室', '/lab', 'Layout', 'lab', '8', '1', 'noredirect', '2022-04-19 09:35:38', '2022-04-19 09:35:38', '2');
INSERT INTO `sys_menu` VALUES ('33', '32', 'Seata', 'seata', 'lab/seata/index', 'security', '1', '1', '', '2022-04-19 09:35:38', '2022-04-19 09:35:38', '1');
INSERT INTO `sys_menu` VALUES ('34', '32', 'RabbitMQ', 'rabbitmq', 'lab/rabbit/index', 'rabbitmq', '2', '1', '', '2022-04-19 09:38:25', '2022-04-19 09:38:25', '1');

-- ----------------------------
-- Table structure for sys_oauth_client
-- ----------------------------
DROP TABLE IF EXISTS `sys_oauth_client`;
CREATE TABLE `sys_oauth_client` (
                                    `client_id` varchar(256) NOT NULL,
                                    `resource_ids` varchar(256) DEFAULT NULL,
                                    `client_secret` varchar(256) DEFAULT NULL,
                                    `scope` varchar(256) DEFAULT NULL,
                                    `authorized_grant_types` varchar(256) DEFAULT NULL,
                                    `web_server_redirect_uri` varchar(256) DEFAULT NULL,
                                    `authorities` varchar(256) DEFAULT NULL,
                                    `access_token_validity` int(11) DEFAULT NULL,
                                    `refresh_token_validity` int(11) DEFAULT NULL,
                                    `additional_information` varchar(4096) DEFAULT NULL,
                                    `autoapprove` varchar(256) DEFAULT NULL,
                                    PRIMARY KEY (`client_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of sys_oauth_client
-- ----------------------------
INSERT INTO `sys_oauth_client` VALUES ('client', '', '123456', 'all', 'password,refresh_token', null, null, '3600', '7200', null, 'true');
INSERT INTO `sys_oauth_client` VALUES ('mall-admin-web', '', '123456', 'all', 'password,refresh_token,captcha', null, '', '3600', '7200', null, 'true');
INSERT INTO `sys_oauth_client` VALUES ('mall-app', '', '123456', 'all', 'sms_code,refresh_token', null, null, '3600', '7200', null, 'true');
INSERT INTO `sys_oauth_client` VALUES ('mall-weapp', '', '123456', 'all', 'wechat,refresh_token', null, null, '3600', '7200', null, 'true');
INSERT INTO `sys_oauth_client` VALUES ('youlai-admin', '', '123456', 'all', 'password,client_credentials,refresh_token,authorization_code', null, '', '3600', '7200', null, 'true');
INSERT INTO `sys_oauth_client` VALUES ('youlai-mall-app', '', '123456', 'all', 'authorization_code,password,refresh_token,implicit,wechat,refresh_token', null, null, '3600', '7200', null, 'true');

-- ----------------------------
-- Table structure for sys_permission
-- ----------------------------
DROP TABLE IF EXISTS `sys_permission`;
CREATE TABLE `sys_permission` (
                                  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
                                  `name` varchar(64) DEFAULT NULL COMMENT '权限名称',
                                  `menu_id` int(11) DEFAULT NULL COMMENT '菜单模块ID\r\n',
                                  `url_perm` varchar(128) DEFAULT NULL COMMENT 'URL权限标识',
                                  `btn_perm` varchar(64) DEFAULT NULL COMMENT '按钮权限标识',
                                  `create_time` datetime DEFAULT NULL,
                                  `update_time` datetime DEFAULT NULL,
                                  PRIMARY KEY (`id`) USING BTREE,
                                  KEY `id` (`id`,`name`) USING BTREE,
                                  KEY `idx_menu_id` (`menu_id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='权限表';

-- ----------------------------
-- Records of sys_permission
-- ----------------------------
INSERT INTO `sys_permission` VALUES ('1', '查看用户', '2', 'GET:/learn-admin/api/v1/users/*', 'sys:user:view', '2021-02-02 14:16:07', '2021-06-16 22:25:24');
INSERT INTO `sys_permission` VALUES ('2', '编辑用户', '2', 'PUT:/learn-admin/users/*', 'sys:user:edit', '2021-06-16 16:19:44', '2021-06-16 23:36:53');
INSERT INTO `sys_permission` VALUES ('3', '新增用户', '2', 'POST:/learn-admin/api/v1/users', 'sys:user:add', '2021-06-16 23:36:37', '2021-06-16 23:37:03');
INSERT INTO `sys_permission` VALUES ('4', '删除用户', '2', 'DELETE:/learn-admin/api/v1/users/*', 'sys:user:delete', '2021-06-16 23:43:54', '2021-06-16 23:43:54');
INSERT INTO `sys_permission` VALUES ('5', '路由列表', '4', 'GET:/learn-admin/api/v1/menus/route', 'sys:route:query', null, null);
INSERT INTO `sys_permission` VALUES ('6', '我的信息', null, 'GET:/learn-ops/sys/user/me', 'sys:user:me', null, null);

-- ----------------------------
-- Table structure for sys_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role` (
                            `id` bigint(20) NOT NULL AUTO_INCREMENT,
                            `name` varchar(64) NOT NULL DEFAULT '' COMMENT '角色名称',
                            `code` varchar(32) DEFAULT NULL COMMENT '角色编码',
                            `sort` int(11) DEFAULT NULL COMMENT '显示顺序',
                            `status` tinyint(1) DEFAULT '1' COMMENT '角色状态：0-正常；1-停用',
                            `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '逻辑删除标识：0-未删除；1-已删除',
                            `create_time` datetime DEFAULT NULL COMMENT '更新时间',
                            `update_time` datetime DEFAULT NULL COMMENT '创建时间',
                            PRIMARY KEY (`id`) USING BTREE,
                            UNIQUE KEY `name` (`name`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='角色表';

-- ----------------------------
-- Records of sys_role
-- ----------------------------
INSERT INTO `sys_role` VALUES ('1', '超级管理员', 'ROOT', '1', '1', '0', '2021-05-21 14:56:51', '2018-12-23 16:00:00');
INSERT INTO `sys_role` VALUES ('2', '系统管理员', 'ADMIN', '2', '1', '0', '2021-03-25 12:39:54', '2018-12-23 16:00:00');
INSERT INTO `sys_role` VALUES ('3', '访问游客', 'GUEST', '3', '1', '0', '2021-05-26 15:49:05', '2019-05-05 16:00:00');

-- ----------------------------
-- Table structure for sys_role_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_menu`;
CREATE TABLE `sys_role_menu` (
                                 `role_id` bigint(20) NOT NULL COMMENT '角色ID',
                                 `menu_id` bigint(20) NOT NULL COMMENT '菜单ID'
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='角色和菜单关联表';

-- ----------------------------
-- Records of sys_role_menu
-- ----------------------------
INSERT INTO `sys_role_menu` VALUES ('2', '1');
INSERT INTO `sys_role_menu` VALUES ('2', '2');
INSERT INTO `sys_role_menu` VALUES ('2', '3');
INSERT INTO `sys_role_menu` VALUES ('2', '4');
INSERT INTO `sys_role_menu` VALUES ('2', '5');
INSERT INTO `sys_role_menu` VALUES ('2', '6');
INSERT INTO `sys_role_menu` VALUES ('2', '7');
INSERT INTO `sys_role_menu` VALUES ('2', '11');
INSERT INTO `sys_role_menu` VALUES ('2', '19');
INSERT INTO `sys_role_menu` VALUES ('2', '18');
INSERT INTO `sys_role_menu` VALUES ('2', '17');
INSERT INTO `sys_role_menu` VALUES ('2', '12');
INSERT INTO `sys_role_menu` VALUES ('2', '13');
INSERT INTO `sys_role_menu` VALUES ('2', '14');
INSERT INTO `sys_role_menu` VALUES ('2', '15');
INSERT INTO `sys_role_menu` VALUES ('2', '16');
INSERT INTO `sys_role_menu` VALUES ('2', '9');
INSERT INTO `sys_role_menu` VALUES ('2', '10');
INSERT INTO `sys_role_menu` VALUES ('2', '20');
INSERT INTO `sys_role_menu` VALUES ('2', '21');
INSERT INTO `sys_role_menu` VALUES ('2', '22');
INSERT INTO `sys_role_menu` VALUES ('2', '24');
INSERT INTO `sys_role_menu` VALUES ('2', '26');
INSERT INTO `sys_role_menu` VALUES ('2', '30');
INSERT INTO `sys_role_menu` VALUES ('2', '32');
INSERT INTO `sys_role_menu` VALUES ('2', '33');
INSERT INTO `sys_role_menu` VALUES ('2', '34');

-- ----------------------------
-- Table structure for sys_role_permission
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_permission`;
CREATE TABLE `sys_role_permission` (
                                       `role_id` int(11) DEFAULT NULL COMMENT '角色id',
                                       `permission_id` int(11) DEFAULT NULL COMMENT '资源id',
                                       KEY `role_id` (`role_id`) USING BTREE,
                                       KEY `permission_id` (`permission_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='角色权限表';

-- ----------------------------
-- Records of sys_role_permission
-- ----------------------------
INSERT INTO `sys_role_permission` VALUES ('2', '1');
INSERT INTO `sys_role_permission` VALUES ('2', '2');
INSERT INTO `sys_role_permission` VALUES ('2', '3');
INSERT INTO `sys_role_permission` VALUES ('2', '5');
INSERT INTO `sys_role_permission` VALUES ('2', '4');
INSERT INTO `sys_role_permission` VALUES ('2', '6');

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user` (
                            `id` int(11) NOT NULL AUTO_INCREMENT,
                            `username` varchar(64) DEFAULT NULL COMMENT '用户名',
                            `nickname` varchar(64) DEFAULT NULL COMMENT '昵称',
                            `gender` tinyint(1) DEFAULT '1' COMMENT '性别：1-男 2-女',
                            `password` varchar(100) DEFAULT NULL COMMENT '密码',
                            `dept_id` int(11) DEFAULT NULL COMMENT '部门ID',
                            `avatar` varchar(255) DEFAULT '' COMMENT '用户头像',
                            `mobile` varchar(20) DEFAULT NULL COMMENT '手机号',
                            `status` tinyint(1) DEFAULT '1' COMMENT '用户状态：1-正常 0-禁用',
                            `email` varchar(128) DEFAULT NULL COMMENT '用户邮箱',
                            `is_deleted` tinyint(1) DEFAULT '0' COMMENT '逻辑删除标识：0-未删除；1-已删除',
                            `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                            `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                            PRIMARY KEY (`id`) USING BTREE,
                            UNIQUE KEY `uk_login_name` (`username`) USING BTREE,
                            UNIQUE KEY `uk_mobile` (`mobile`) USING BTREE,
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='用户信息表';

-- ----------------------------
-- Records of sys_user
-- ----------------------------
INSERT INTO `sys_user` VALUES ('1', 'root', '有来技术', '0', '$2a$10$xVWsNOhHrCxh5UbpCE7/HuJ.PAOKcYAqRxD2CO2nVnJS.IAXkr5aq', null, '', '17621590365', '1', 'youlaitech@163.com', '0', null, null);
INSERT INTO `sys_user` VALUES ('2', 'admin', '系统管理员', '1', '$2a$10$yJSqqr6sTxNuYtA6EKcVUe2I4USFCzJ29sNcRrBvtAkSYcNg5ydQ6', '2', 'https://s2.loli.net/2022/04/07/gw1L2Z5sPtS8GIl.gif', '17621210366', '1', 'youlaitech@163.com', '0', '2019-10-10 13:41:22', '2021-06-06 23:41:35');
INSERT INTO `sys_user` VALUES ('3', 'test', '测试小用户', '1', '$2a$10$MPJkNw.hKT/fZOgwYP8q9eu/rFJJDsNov697AmdkHNJkpjIpVSw2q', '3', 'https://s2.loli.net/2022/04/07/gw1L2Z5sPtS8GIl.gif', '17621210367', '1', 'youlaitech@163.com', '0', '2021-06-05 01:31:29', '2021-06-05 01:31:29');

-- ----------------------------
-- Table structure for sys_user_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_role`;
CREATE TABLE `sys_user_role` (
                                 `user_id` int(11) NOT NULL COMMENT '用户ID',
                                 `role_id` int(11) NOT NULL COMMENT '角色ID',
                                 PRIMARY KEY (`user_id`,`role_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='用户和角色关联表';

-- ----------------------------
-- Records of sys_user_role
-- ----------------------------
INSERT INTO `sys_user_role` VALUES ('1', '1');
INSERT INTO `sys_user_role` VALUES ('2', '2');
INSERT INTO `sys_user_role` VALUES ('3', '3');
