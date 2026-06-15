INSERT INTO sys_menu (menu_id, parent_id, menu_name, menu_type, path, component, icon, sort_order, status, visible, require_auth, deleted)
VALUES ('a0000000000000000000000000000001', NULL, '仪表盘', 'M', '/dashboard', NULL, 'LayoutDashboard', 1, 1, 1, 1, 0);

INSERT INTO sys_menu (menu_id, parent_id, menu_name, menu_type, path, component, icon, sort_order, status, visible, require_auth, deleted)
VALUES ('a0000000000000000000000000000002', NULL, '内容管理', 'D', NULL, NULL, 'FileText', 2, 1, 1, 1, 0);

INSERT INTO sys_menu (menu_id, parent_id, menu_name, menu_type, path, component, icon, sort_order, status, visible, require_auth, deleted)
VALUES ('a0000000000000000000000000000003', 'a0000000000000000000000000000002', '文章列表', 'M', '/content/articles', NULL, 'FileText', 1, 1, 1, 1, 0);

INSERT INTO sys_menu (menu_id, parent_id, menu_name, menu_type, path, component, icon, sort_order, status, visible, require_auth, deleted)
VALUES ('a0000000000000000000000000000004', 'a0000000000000000000000000000002', '分类管理', 'M', '/content/categories', NULL, 'FolderTree', 2, 1, 1, 1, 0);

INSERT INTO sys_menu (menu_id, parent_id, menu_name, menu_type, path, component, icon, sort_order, status, visible, require_auth, deleted)
VALUES ('a0000000000000000000000000000005', 'a0000000000000000000000000000002', '评论审核', 'M', '/content/comments', NULL, 'MessageSquareText', 3, 1, 1, 1, 0);

INSERT INTO sys_menu (menu_id, parent_id, menu_name, menu_type, path, component, icon, sort_order, status, visible, require_auth, deleted)
VALUES ('a0000000000000000000000000000006', NULL, '系统管理', 'D', NULL, NULL, 'Settings', 3, 1, 1, 1, 0);

INSERT INTO sys_menu (menu_id, parent_id, menu_name, menu_type, path, component, icon, sort_order, status, visible, require_auth, deleted)
VALUES ('a0000000000000000000000000000007', 'a0000000000000000000000000000006', '用户管理', 'M', '/system/users', 'system/UserView', 'Users', 1, 1, 1, 1, 0);

INSERT INTO sys_menu (menu_id, parent_id, menu_name, menu_type, path, component, icon, sort_order, status, visible, require_auth, deleted)
VALUES ('a0000000000000000000000000000008', 'a0000000000000000000000000000006', '角色管理', 'M', '/system/roles', 'system/RoleView', 'ShieldCheck', 2, 1, 1, 1, 0);

INSERT INTO sys_menu (menu_id, parent_id, menu_name, menu_type, path, component, icon, sort_order, status, visible, require_auth, deleted)
VALUES ('a0000000000000000000000000000009', 'a0000000000000000000000000000006', '部门管理', 'M', '/system/depts', 'system/DeptView', 'Building', 3, 1, 1, 1, 0);

INSERT INTO sys_menu (menu_id, parent_id, menu_name, menu_type, path, component, icon, sort_order, status, visible, require_auth, deleted)
VALUES ('a000000000000000000000000000000a', 'a0000000000000000000000000000006', '岗位管理', 'M', '/system/posts', 'system/PostView', 'BriefcaseBusiness', 4, 1, 1, 1, 0);

INSERT INTO sys_menu (menu_id, parent_id, menu_name, menu_type, path, component, icon, sort_order, status, visible, require_auth, deleted)
VALUES ('a000000000000000000000000000000b', 'a0000000000000000000000000000006', '菜单配置', 'M', '/system/menus', NULL, 'Menu', 5, 1, 1, 1, 0);

INSERT INTO sys_menu (menu_id, parent_id, menu_name, menu_type, path, component, icon, sort_order, status, visible, require_auth, deleted)
VALUES ('a000000000000000000000000000000c', 'a0000000000000000000000000000006', '字典管理', 'M', '/system/dict', 'system/DictView', 'BookType', 6, 1, 1, 1, 0);

INSERT INTO sys_menu (menu_id, parent_id, menu_name, menu_type, path, component, icon, sort_order, status, visible, require_auth, deleted)
VALUES ('a000000000000000000000000000000d', 'a0000000000000000000000000000006', '参数设置', 'M', '/system/params', NULL, 'SlidersHorizontal', 7, 1, 1, 1, 0);

INSERT INTO sys_menu (menu_id, parent_id, menu_name, menu_type, path, component, icon, sort_order, status, visible, require_auth, deleted)
VALUES ('a000000000000000000000000000000e', NULL, '运维监控', 'D', NULL, NULL, 'Activity', 4, 1, 1, 1, 0);

INSERT INTO sys_menu (menu_id, parent_id, menu_name, menu_type, path, component, icon, sort_order, status, visible, require_auth, deleted)
VALUES ('a000000000000000000000000000000f', 'a000000000000000000000000000000e', '在线用户', 'M', '/monitor/online', NULL, 'UserCheck', 1, 1, 1, 1, 0);

INSERT INTO sys_menu (menu_id, parent_id, menu_name, menu_type, path, component, icon, sort_order, status, visible, require_auth, deleted)
VALUES ('a0000000000000000000000000000010', 'a000000000000000000000000000000e', '服务器信息', 'M', '/monitor/server', NULL, 'Server', 2, 1, 1, 1, 0);

INSERT INTO sys_menu (menu_id, parent_id, menu_name, menu_type, path, component, icon, sort_order, status, visible, require_auth, deleted)
VALUES ('a0000000000000000000000000000011', 'a000000000000000000000000000000e', '缓存监控', 'M', '/monitor/cache', NULL, 'Database', 3, 1, 1, 1, 0);

INSERT INTO sys_menu (menu_id, parent_id, menu_name, menu_type, path, component, icon, sort_order, status, visible, require_auth, deleted)
VALUES ('a0000000000000000000000000000012', NULL, '日志审计', 'D', NULL, NULL, 'ScrollText', 5, 1, 1, 1, 0);

INSERT INTO sys_menu (menu_id, parent_id, menu_name, menu_type, path, component, icon, sort_order, status, visible, require_auth, deleted)
VALUES ('a0000000000000000000000000000013', 'a0000000000000000000000000000012', '登录日志', 'M', '/audit/login', NULL, 'LogIn', 1, 1, 1, 1, 0);

INSERT INTO sys_menu (menu_id, parent_id, menu_name, menu_type, path, component, icon, sort_order, status, visible, require_auth, deleted)
VALUES ('a0000000000000000000000000000014', 'a0000000000000000000000000000012', '操作日志', 'M', '/audit/operation', NULL, 'ClipboardList', 2, 1, 1, 1, 0);
