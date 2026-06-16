CREATE TABLE sys_user_relation (
    relation_id   VARCHAR(32) PRIMARY KEY,
    user_id       VARCHAR(32) NOT NULL,
    relation_type VARCHAR(10) NOT NULL,
    target_id     VARCHAR(32) NOT NULL,
    create_at     TIMESTAMP   DEFAULT CURRENT_TIMESTAMP,
    create_by     VARCHAR(32)
);
CREATE INDEX idx_relation_user ON sys_user_relation(user_id, relation_type);

CREATE TABLE sys_authorization (
    auth_id      VARCHAR(32) PRIMARY KEY,
    subject_type VARCHAR(10) NOT NULL,
    subject_id   VARCHAR(32) NOT NULL,
    menu_id      VARCHAR(32) NOT NULL,
    create_at    TIMESTAMP   DEFAULT CURRENT_TIMESTAMP,
    create_by    VARCHAR(32)
);
CREATE INDEX idx_auth_subject ON sys_authorization(subject_type, subject_id);

COMMENT ON TABLE sys_user_relation IS '用户关系表';
COMMENT ON TABLE sys_authorization IS '菜单授权表';
