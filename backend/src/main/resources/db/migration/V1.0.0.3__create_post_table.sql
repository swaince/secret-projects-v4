CREATE TABLE sys_post (
    post_id    VARCHAR(32) PRIMARY KEY,
    post_name  VARCHAR(50) NOT NULL,
    post_code  VARCHAR(50) NOT NULL,
    post_level INT DEFAULT 1,
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

COMMENT ON TABLE sys_post IS '岗位表';
COMMENT ON COLUMN sys_post.post_id IS '岗位ID';
COMMENT ON COLUMN sys_post.post_name IS '岗位名称';
COMMENT ON COLUMN sys_post.post_code IS '岗位编码';
COMMENT ON COLUMN sys_post.post_level IS '岗位级别';
COMMENT ON COLUMN sys_post.sort_order IS '排序';
COMMENT ON COLUMN sys_post.remark IS '备注';
COMMENT ON COLUMN sys_post.status IS '状态 1-启用 0-禁用';
COMMENT ON COLUMN sys_post.built_in IS '是否内置 1-是 0-否';
COMMENT ON COLUMN sys_post.deleted IS '是否删除 1-是 0-否';
COMMENT ON COLUMN sys_post.create_at IS '创建时间';
COMMENT ON COLUMN sys_post.create_by IS '创建人';
COMMENT ON COLUMN sys_post.update_at IS '更新时间';
COMMENT ON COLUMN sys_post.update_by IS '更新人';

