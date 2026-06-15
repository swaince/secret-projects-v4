CREATE TABLE sys_access_log (
    log_id          VARCHAR(32)   PRIMARY KEY,
    request_id      VARCHAR(32)   NOT NULL,
    request_url     VARCHAR(500)  NOT NULL,
    request_method  VARCHAR(10)   NOT NULL,
    request_headers TEXT,
    request_params  VARCHAR(2000),
    request_body    TEXT,
    response_status INT,
    duration_ms     BIGINT,
    client_ip       VARCHAR(50),
    user_agent      VARCHAR(500),
    user_id         VARCHAR(32),
    username        VARCHAR(50),
    exception_stack TEXT,
    create_at       TIMESTAMP     DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE sys_access_log IS '访问日志';
COMMENT ON COLUMN sys_access_log.log_id IS '日志ID';
COMMENT ON COLUMN sys_access_log.request_id IS '请求ID';
COMMENT ON COLUMN sys_access_log.request_url IS '请求URL';
COMMENT ON COLUMN sys_access_log.request_method IS '请求方法';
COMMENT ON COLUMN sys_access_log.request_headers IS '请求头';
COMMENT ON COLUMN sys_access_log.request_params IS '请求参数';
COMMENT ON COLUMN sys_access_log.request_body IS '请求体';
COMMENT ON COLUMN sys_access_log.response_status IS '响应状态码';
COMMENT ON COLUMN sys_access_log.duration_ms IS '耗时(毫秒)';
COMMENT ON COLUMN sys_access_log.client_ip IS '客户端IP';
COMMENT ON COLUMN sys_access_log.user_agent IS 'User-Agent';
COMMENT ON COLUMN sys_access_log.user_id IS '用户ID';
COMMENT ON COLUMN sys_access_log.username IS '用户名';
COMMENT ON COLUMN sys_access_log.exception_stack IS '异常堆栈';
COMMENT ON COLUMN sys_access_log.create_at IS '创建时间';

CREATE TABLE sys_operation_log (
    log_id            VARCHAR(32)   PRIMARY KEY,
    request_id        VARCHAR(32)   NOT NULL,
    request_url       VARCHAR(500)  NOT NULL,
    request_method    VARCHAR(10)   NOT NULL,
    request_headers   TEXT,
    request_params    VARCHAR(2000),
    request_body      TEXT,
    response_status   INT,
    duration_ms       BIGINT,
    client_ip         VARCHAR(50),
    user_agent        VARCHAR(500),
    user_id           VARCHAR(32),
    username          VARCHAR(50),
    exception_stack   TEXT,
    controller_class  VARCHAR(200),
    controller_method VARCHAR(100),
    create_at         TIMESTAMP     DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE sys_operation_log IS '操作日志';
COMMENT ON COLUMN sys_operation_log.log_id IS '日志ID';
COMMENT ON COLUMN sys_operation_log.request_id IS '请求ID';
COMMENT ON COLUMN sys_operation_log.request_url IS '请求URL';
COMMENT ON COLUMN sys_operation_log.request_method IS '请求方法';
COMMENT ON COLUMN sys_operation_log.request_headers IS '请求头';
COMMENT ON COLUMN sys_operation_log.request_params IS '请求参数';
COMMENT ON COLUMN sys_operation_log.request_body IS '请求体';
COMMENT ON COLUMN sys_operation_log.response_status IS '响应状态码';
COMMENT ON COLUMN sys_operation_log.duration_ms IS '耗时(毫秒)';
COMMENT ON COLUMN sys_operation_log.client_ip IS '客户端IP';
COMMENT ON COLUMN sys_operation_log.user_agent IS 'User-Agent';
COMMENT ON COLUMN sys_operation_log.user_id IS '用户ID';
COMMENT ON COLUMN sys_operation_log.username IS '用户名';
COMMENT ON COLUMN sys_operation_log.exception_stack IS '异常堆栈';
COMMENT ON COLUMN sys_operation_log.controller_class IS '控制器类';
COMMENT ON COLUMN sys_operation_log.controller_method IS '控制器方法';
COMMENT ON COLUMN sys_operation_log.create_at IS '创建时间';

CREATE INDEX idx_access_log_create_at ON sys_access_log(create_at);
CREATE INDEX idx_operation_log_create_at ON sys_operation_log(create_at);
CREATE INDEX idx_access_log_request_id ON sys_access_log(request_id);
CREATE INDEX idx_operation_log_request_id ON sys_operation_log(request_id);
