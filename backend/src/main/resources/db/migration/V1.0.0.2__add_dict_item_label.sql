ALTER TABLE sys_dict_item
    ADD COLUMN item_label VARCHAR(100) DEFAULT '' NOT NULL;

COMMENT ON COLUMN sys_dict_item.item_label IS '字典项标签';
