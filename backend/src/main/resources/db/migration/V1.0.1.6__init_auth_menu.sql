INSERT INTO sys_menu (menu_id, parent_id, menu_name, menu_type, component, icon, sort_order, status, visible, require_auth, deleted)
VALUES ('a0000000000000000000000000000016', 'a0000000000000000000000000000006', '授权管理', 'D', NULL, 'Shield', 9, 1, 1, 1, 0);

INSERT INTO sys_menu (menu_id, parent_id, menu_name, menu_type, path, component, icon, sort_order, status, visible, require_auth, deleted)
VALUES ('a0000000000000000000000000000017', 'a0000000000000000000000000000016', '用户授权', 'M', '/system/auth/user', 'system/auth/UserAuthView', 'UserCog', 1, 1, 1, 1, 0);

INSERT INTO sys_menu (menu_id, parent_id, menu_name, menu_type, path, component, icon, sort_order, status, visible, require_auth, deleted)
VALUES ('a0000000000000000000000000000018', 'a0000000000000000000000000000016', '角色授权', 'M', '/system/auth/role', 'system/auth/RoleAuthView', 'ShieldCheck', 2, 1, 1, 1, 0);

INSERT INTO sys_menu (menu_id, parent_id, menu_name, menu_type, path, component, icon, sort_order, status, visible, require_auth, deleted)
VALUES ('a0000000000000000000000000000019', 'a0000000000000000000000000000016', '部门授权', 'M', '/system/auth/dept', 'system/auth/DeptAuthView', 'Building', 3, 1, 1, 1, 0);

INSERT INTO sys_menu (menu_id, parent_id, menu_name, menu_type, path, component, icon, sort_order, status, visible, require_auth, deleted)
VALUES ('a000000000000000000000000000001a', 'a0000000000000000000000000000016', '岗位授权', 'M', '/system/auth/post', 'system/auth/PostAuthView', 'BriefcaseBusiness', 4, 1, 1, 1, 0);
