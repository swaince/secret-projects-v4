CREATE TABLE sys_role (
    role_id    VARCHAR(32) PRIMARY KEY,
    role_name  VARCHAR(50) NOT NULL,
    role_code  VARCHAR(50) NOT NULL,
    sort_order INT DEFAULT 0,
    remark     VARCHAR(500),
    status     INT DEFAULT 1,
    built_in   INT DEFAULT 0,
    deleted    INT DEFAULT 0,
    create_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    create_by  VARCHAR(32),
    update_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_by  VARCHAR(32)
);

COMMENT ON TABLE sys_role IS '角色表';
COMMENT ON COLUMN sys_role.role_id IS '角色ID';
COMMENT ON COLUMN sys_role.role_name IS '角色名称';
COMMENT ON COLUMN sys_role.role_code IS '角色编码';
COMMENT ON COLUMN sys_role.sort_order IS '排序';
COMMENT ON COLUMN sys_role.remark IS '备注';
COMMENT ON COLUMN sys_role.status IS '状态 1-启用 0-禁用';
COMMENT ON COLUMN sys_role.built_in IS '是否内置 1-是 0-否';
COMMENT ON COLUMN sys_role.deleted IS '是否删除 1-是 0-否';
COMMENT ON COLUMN sys_role.create_at IS '创建时间';
COMMENT ON COLUMN sys_role.create_by IS '创建人';
COMMENT ON COLUMN sys_role.update_at IS '更新时间';
COMMENT ON COLUMN sys_role.update_by IS '更新人';
