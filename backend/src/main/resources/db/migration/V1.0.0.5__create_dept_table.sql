CREATE TABLE sys_dept (
    dept_id    VARCHAR(32) PRIMARY KEY,
    dept_name  VARCHAR(50) NOT NULL,
    dept_code  VARCHAR(50) NOT NULL,
    parent_id  VARCHAR(32),
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

COMMENT ON TABLE sys_dept IS '部门表';
COMMENT ON COLUMN sys_dept.dept_id IS '部门ID';
COMMENT ON COLUMN sys_dept.dept_name IS '部门名称';
COMMENT ON COLUMN sys_dept.dept_code IS '部门编码';
COMMENT ON COLUMN sys_dept.parent_id IS '父部门ID';
COMMENT ON COLUMN sys_dept.sort_order IS '排序';
COMMENT ON COLUMN sys_dept.remark IS '备注';
COMMENT ON COLUMN sys_dept.status IS '状态 1-启用 0-禁用';
COMMENT ON COLUMN sys_dept.built_in IS '是否内置 1-是 0-否';
COMMENT ON COLUMN sys_dept.deleted IS '是否删除 1-是 0-否';
COMMENT ON COLUMN sys_dept.create_at IS '创建时间';
COMMENT ON COLUMN sys_dept.create_by IS '创建人';
COMMENT ON COLUMN sys_dept.update_at IS '更新时间';
COMMENT ON COLUMN sys_dept.update_by IS '更新人';
