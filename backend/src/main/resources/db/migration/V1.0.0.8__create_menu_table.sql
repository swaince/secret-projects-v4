CREATE TABLE sys_menu (
    menu_id       VARCHAR(32)   PRIMARY KEY,
    parent_id     VARCHAR(32),
    menu_name     VARCHAR(50)   NOT NULL,
    menu_type     VARCHAR(1)    NOT NULL,
    path          VARCHAR(200),
    component     VARCHAR(200),
    icon          VARCHAR(50),
    sort_order    INT           DEFAULT 0,
    status        INT           DEFAULT 1,
    permission    VARCHAR(100),
    visible       INT           DEFAULT 1,
    redirect      VARCHAR(200),
    require_auth  INT           DEFAULT 1,
    create_at     TIMESTAMP     DEFAULT CURRENT_TIMESTAMP,
    create_by     VARCHAR(32),
    update_at     TIMESTAMP     DEFAULT CURRENT_TIMESTAMP,
    update_by     VARCHAR(32),
    deleted       INT           DEFAULT 0
);

COMMENT ON TABLE sys_menu IS '系统菜单表';
COMMENT ON COLUMN sys_menu.menu_id IS '菜单ID';
COMMENT ON COLUMN sys_menu.parent_id IS '父菜单ID';
COMMENT ON COLUMN sys_menu.menu_name IS '菜单名称';
COMMENT ON COLUMN sys_menu.menu_type IS '菜单类型 D=目录 M=菜单 B=按钮';
COMMENT ON COLUMN sys_menu.path IS '路由路径';
COMMENT ON COLUMN sys_menu.component IS '组件路径';
COMMENT ON COLUMN sys_menu.icon IS '图标';
COMMENT ON COLUMN sys_menu.sort_order IS '排序';
COMMENT ON COLUMN sys_menu.status IS '状态 1=启用 0=禁用';
COMMENT ON COLUMN sys_menu.permission IS '权限标识';
COMMENT ON COLUMN sys_menu.visible IS '是否可见 1=可见 0=隐藏';
COMMENT ON COLUMN sys_menu.redirect IS '重定向路径';
COMMENT ON COLUMN sys_menu.require_auth IS '是否需要认证 1=需要 0=不需要';
COMMENT ON COLUMN sys_menu.create_at IS '创建时间';
COMMENT ON COLUMN sys_menu.create_by IS '创建人';
COMMENT ON COLUMN sys_menu.update_at IS '更新时间';
COMMENT ON COLUMN sys_menu.update_by IS '更新人';
COMMENT ON COLUMN sys_menu.deleted IS '是否删除 1=是 0=否';
