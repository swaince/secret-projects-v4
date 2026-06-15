ALTER TABLE sys_user ADD COLUMN display_name VARCHAR(50);

COMMENT ON COLUMN sys_user.display_name IS '显示名';
