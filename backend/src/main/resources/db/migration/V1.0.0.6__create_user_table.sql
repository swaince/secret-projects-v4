CREATE TABLE sys_user (
    user_id              VARCHAR(32)   PRIMARY KEY,
    username             VARCHAR(50)   NOT NULL,
    password             VARCHAR(200)  NOT NULL,
    gender               VARCHAR(10),
    status               INT           DEFAULT 1,
    account_expire_time  TIMESTAMP,
    password_expire_time TIMESTAMP,
    last_login_time      TIMESTAMP,
    create_at            TIMESTAMP     DEFAULT CURRENT_TIMESTAMP,
    create_by            VARCHAR(32),
    update_at            TIMESTAMP     DEFAULT CURRENT_TIMESTAMP,
    update_by            VARCHAR(32),
    deleted              INT           DEFAULT 0
);

CREATE UNIQUE INDEX uk_sys_user_username ON sys_user(username);

COMMENT ON TABLE sys_user IS '系统用户表';
COMMENT ON COLUMN sys_user.user_id IS '用户ID';
COMMENT ON COLUMN sys_user.username IS '用户名';
COMMENT ON COLUMN sys_user.password IS '密码(BCrypt加密)';
COMMENT ON COLUMN sys_user.gender IS '性别';
COMMENT ON COLUMN sys_user.status IS '状态 1=启用 0=禁用';
COMMENT ON COLUMN sys_user.account_expire_time IS '账号过期时间';
COMMENT ON COLUMN sys_user.password_expire_time IS '密码过期时间';
COMMENT ON COLUMN sys_user.last_login_time IS '最近登录时间';
COMMENT ON COLUMN sys_user.create_at IS '创建时间';
COMMENT ON COLUMN sys_user.create_by IS '创建人';
COMMENT ON COLUMN sys_user.update_at IS '更新时间';
COMMENT ON COLUMN sys_user.update_by IS '更新人';
COMMENT ON COLUMN sys_user.deleted IS '是否删除 1-是 0-否';
