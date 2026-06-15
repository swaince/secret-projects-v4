-- 用户管理按钮
INSERT INTO sys_menu (menu_id, parent_id, menu_name, menu_type, permission, sort_order, status, visible, require_auth, deleted)
VALUES ('b0000000000000000000000000000001', 'a0000000000000000000000000000007', '新增', 'B', 'system:user:add', 1, 1, 1, 1, 0);
INSERT INTO sys_menu (menu_id, parent_id, menu_name, menu_type, permission, sort_order, status, visible, require_auth, deleted)
VALUES ('b0000000000000000000000000000002', 'a0000000000000000000000000000007', '编辑', 'B', 'system:user:edit', 2, 1, 1, 1, 0);
INSERT INTO sys_menu (menu_id, parent_id, menu_name, menu_type, permission, sort_order, status, visible, require_auth, deleted)
VALUES ('b0000000000000000000000000000003', 'a0000000000000000000000000000007', '删除', 'B', 'system:user:delete', 3, 1, 1, 1, 0);
INSERT INTO sys_menu (menu_id, parent_id, menu_name, menu_type, permission, sort_order, status, visible, require_auth, deleted)
VALUES ('b0000000000000000000000000000004', 'a0000000000000000000000000000007', '批量删除', 'B', 'system:user:batch-delete', 4, 1, 1, 1, 0);

-- 角色管理按钮
INSERT INTO sys_menu (menu_id, parent_id, menu_name, menu_type, permission, sort_order, status, visible, require_auth, deleted)
VALUES ('b0000000000000000000000000000005', 'a0000000000000000000000000000008', '新增', 'B', 'system:role:add', 1, 1, 1, 1, 0);
INSERT INTO sys_menu (menu_id, parent_id, menu_name, menu_type, permission, sort_order, status, visible, require_auth, deleted)
VALUES ('b0000000000000000000000000000006', 'a0000000000000000000000000000008', '编辑', 'B', 'system:role:edit', 2, 1, 1, 1, 0);
INSERT INTO sys_menu (menu_id, parent_id, menu_name, menu_type, permission, sort_order, status, visible, require_auth, deleted)
VALUES ('b0000000000000000000000000000007', 'a0000000000000000000000000000008', '删除', 'B', 'system:role:delete', 3, 1, 1, 1, 0);
INSERT INTO sys_menu (menu_id, parent_id, menu_name, menu_type, permission, sort_order, status, visible, require_auth, deleted)
VALUES ('b0000000000000000000000000000008', 'a0000000000000000000000000000008', '批量删除', 'B', 'system:role:batch-delete', 4, 1, 1, 1, 0);

-- 部门管理按钮
INSERT INTO sys_menu (menu_id, parent_id, menu_name, menu_type, permission, sort_order, status, visible, require_auth, deleted)
VALUES ('b0000000000000000000000000000009', 'a0000000000000000000000000000009', '新增', 'B', 'system:dept:add', 1, 1, 1, 1, 0);
INSERT INTO sys_menu (menu_id, parent_id, menu_name, menu_type, permission, sort_order, status, visible, require_auth, deleted)
VALUES ('b000000000000000000000000000000a', 'a0000000000000000000000000000009', '编辑', 'B', 'system:dept:edit', 2, 1, 1, 1, 0);
INSERT INTO sys_menu (menu_id, parent_id, menu_name, menu_type, permission, sort_order, status, visible, require_auth, deleted)
VALUES ('b000000000000000000000000000000b', 'a0000000000000000000000000000009', '删除', 'B', 'system:dept:delete', 3, 1, 1, 1, 0);

-- 岗位管理按钮
INSERT INTO sys_menu (menu_id, parent_id, menu_name, menu_type, permission, sort_order, status, visible, require_auth, deleted)
VALUES ('b000000000000000000000000000000c', 'a000000000000000000000000000000a', '新增', 'B', 'system:post:add', 1, 1, 1, 1, 0);
INSERT INTO sys_menu (menu_id, parent_id, menu_name, menu_type, permission, sort_order, status, visible, require_auth, deleted)
VALUES ('b000000000000000000000000000000d', 'a000000000000000000000000000000a', '编辑', 'B', 'system:post:edit', 2, 1, 1, 1, 0);
INSERT INTO sys_menu (menu_id, parent_id, menu_name, menu_type, permission, sort_order, status, visible, require_auth, deleted)
VALUES ('b000000000000000000000000000000e', 'a000000000000000000000000000000a', '删除', 'B', 'system:post:delete', 3, 1, 1, 1, 0);
INSERT INTO sys_menu (menu_id, parent_id, menu_name, menu_type, permission, sort_order, status, visible, require_auth, deleted)
VALUES ('b000000000000000000000000000000f', 'a000000000000000000000000000000a', '批量删除', 'B', 'system:post:batch-delete', 4, 1, 1, 1, 0);

-- 菜单管理按钮
INSERT INTO sys_menu (menu_id, parent_id, menu_name, menu_type, permission, sort_order, status, visible, require_auth, deleted)
VALUES ('b0000000000000000000000000000010', 'a000000000000000000000000000000b', '新增', 'B', 'system:menu:add', 1, 1, 1, 1, 0);
INSERT INTO sys_menu (menu_id, parent_id, menu_name, menu_type, permission, sort_order, status, visible, require_auth, deleted)
VALUES ('b0000000000000000000000000000011', 'a000000000000000000000000000000b', '编辑', 'B', 'system:menu:edit', 2, 1, 1, 1, 0);
INSERT INTO sys_menu (menu_id, parent_id, menu_name, menu_type, permission, sort_order, status, visible, require_auth, deleted)
VALUES ('b0000000000000000000000000000012', 'a000000000000000000000000000000b', '删除', 'B', 'system:menu:delete', 3, 1, 1, 1, 0);

-- 字典管理按钮
INSERT INTO sys_menu (menu_id, parent_id, menu_name, menu_type, permission, sort_order, status, visible, require_auth, deleted)
VALUES ('b0000000000000000000000000000013', 'a000000000000000000000000000000c', '新增', 'B', 'system:dict:add', 1, 1, 1, 1, 0);
INSERT INTO sys_menu (menu_id, parent_id, menu_name, menu_type, permission, sort_order, status, visible, require_auth, deleted)
VALUES ('b0000000000000000000000000000014', 'a000000000000000000000000000000c', '编辑', 'B', 'system:dict:edit', 2, 1, 1, 1, 0);
INSERT INTO sys_menu (menu_id, parent_id, menu_name, menu_type, permission, sort_order, status, visible, require_auth, deleted)
VALUES ('b0000000000000000000000000000015', 'a000000000000000000000000000000c', '删除', 'B', 'system:dict:delete', 3, 1, 1, 1, 0);
INSERT INTO sys_menu (menu_id, parent_id, menu_name, menu_type, permission, sort_order, status, visible, require_auth, deleted)
VALUES ('b0000000000000000000000000000016', 'a000000000000000000000000000000c', '批量删除', 'B', 'system:dict:batch-delete', 4, 1, 1, 1, 0);
