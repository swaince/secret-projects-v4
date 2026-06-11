CREATE TABLE sys_dict (
    dict_id VARCHAR(32) PRIMARY KEY,
    dict_name VARCHAR(50) NOT NULL,
    dict_code VARCHAR(50) NOT NULL,
    data_value_type VARCHAR(20) NOT NULL DEFAULT 'STRING',
    remark VARCHAR(500),
    create_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    create_by VARCHAR(32),
    update_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_by VARCHAR(32),
    status INT DEFAULT 1,
    built_in INT DEFAULT 0,
    deleted INT DEFAULT 0
);

COMMENT ON TABLE sys_dict IS '字典表';
COMMENT ON COLUMN sys_dict.dict_id IS '字典ID';
COMMENT ON COLUMN sys_dict.dict_name IS '字典名称';
COMMENT ON COLUMN sys_dict.dict_code IS '字典编码';
COMMENT ON COLUMN sys_dict.data_value_type IS '数据类型';
COMMENT ON COLUMN sys_dict.remark IS '备注';
COMMENT ON COLUMN sys_dict.create_at IS '创建时间';
COMMENT ON COLUMN sys_dict.create_by IS '创建人';
COMMENT ON COLUMN sys_dict.update_at IS '更新时间';
COMMENT ON COLUMN sys_dict.update_by IS '更新人';
COMMENT ON COLUMN sys_dict.status IS '状态 1-启用 0-禁用';
COMMENT ON COLUMN sys_dict.built_in IS '是否内置 1-是 0-否';
COMMENT ON COLUMN sys_dict.deleted IS '是否删除 1-是 0-否';

CREATE TABLE sys_dict_item (
    dict_item_id VARCHAR(32) PRIMARY KEY,
    dict_id VARCHAR(32) NOT NULL,
    item_key VARCHAR(50) NOT NULL,
    item_value VARCHAR(200) NOT NULL,
    sort_order INT DEFAULT 0,
    remark VARCHAR(500),
    create_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    create_by VARCHAR(32),
    update_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_by VARCHAR(32),
    status INT DEFAULT 1,
    built_in INT DEFAULT 0,
    deleted INT DEFAULT 0
);

COMMENT ON TABLE sys_dict_item IS '字典项表';
COMMENT ON COLUMN sys_dict_item.dict_item_id IS '字典项ID';
COMMENT ON COLUMN sys_dict_item.dict_id IS '字典ID';
COMMENT ON COLUMN sys_dict_item.item_key IS '字典项键';
COMMENT ON COLUMN sys_dict_item.item_value IS '字典项值';
COMMENT ON COLUMN sys_dict_item.sort_order IS '排序';
COMMENT ON COLUMN sys_dict_item.remark IS '备注';
COMMENT ON COLUMN sys_dict_item.create_at IS '创建时间';
COMMENT ON COLUMN sys_dict_item.create_by IS '创建人';
COMMENT ON COLUMN sys_dict_item.update_at IS '更新时间';
COMMENT ON COLUMN sys_dict_item.update_by IS '更新人';
COMMENT ON COLUMN sys_dict_item.status IS '状态 1-启用 0-禁用';
COMMENT ON COLUMN sys_dict_item.built_in IS '是否内置 1-是 0-否';
COMMENT ON COLUMN sys_dict_item.deleted IS '是否删除 1-是 0-否';
