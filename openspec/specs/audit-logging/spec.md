# audit-logging Specification

## Purpose
TBD - created by archiving change audit-logging. Update Purpose after archive.
## Requirements
### Requirement: 访问日志 Filter 采集
AccessLogFilter SHALL 作为 OncePerRequestFilter 记录所有 HTTP 请求。

#### Scenario: 正常请求记录
- **WHEN** 任意 HTTP 请求到达应用端口
- **THEN** SHALL 异步记录：requestUrl, requestMethod, requestHeaders(JSON), requestParams, requestBody(最大4KB), responseStatus, durationMs, clientIp, userAgent, userId, username, createAt

#### Scenario: 404 请求记录
- **WHEN** 请求路径不匹配任何 Controller
- **THEN** SHALL 仍记录访问日志（responseStatus=404）

#### Scenario: 异常请求记录
- **WHEN** 请求处理过程中发生异常
- **THEN** SHALL 记录异常堆栈到 exceptionStack 字段

### Requirement: 操作日志 Interceptor 采集
OperationLogInterceptor SHALL 作为 HandlerInterceptor 记录所有进入 Controller 的请求。

#### Scenario: Controller 请求记录
- **WHEN** 请求匹配到 Controller 方法
- **THEN** SHALL 异步记录所有访问日志字段 + controllerClass + controllerMethod

#### Scenario: Controller 异常记录
- **WHEN** Controller 方法抛出异常
- **THEN** SHALL 在 afterCompletion 中记录完整异常堆栈

### Requirement: 异步写入
日志写入 SHALL 异步执行，不阻塞业务请求。

#### Scenario: 异步入库
- **WHEN** Filter/Interceptor 采集到日志数据
- **THEN** SHALL 通过 @Async 线程池异步写入数据库

### Requirement: 访问日志分页查询
AccessLogController SHALL 提供 `GET /logs/access` 接口，支持分页查询。

#### Scenario: 分页查询
- **WHEN** 请求 `GET /logs/access?page=1&size=20`
- **THEN** 返回 `PageResponse<AccessLogDTO>`，按 createAt 降序

#### Scenario: 条件搜索
- **WHEN** 传入 requestUrl、username、responseStatus、startTime、endTime 参数
- **THEN** SHALL 按条件过滤结果

### Requirement: 操作日志分页查询
OperationLogController SHALL 提供 `GET /logs/operation` 接口，支持分页查询。

#### Scenario: 分页查询
- **WHEN** 请求 `GET /logs/operation?page=1&size=20`
- **THEN** 返回 `PageResponse<OperationLogDTO>`，按 createAt 降序

#### Scenario: 条件搜索
- **WHEN** 传入 requestUrl、username、controllerMethod、responseStatus、startTime、endTime 参数
- **THEN** SHALL 按条件过滤结果

### Requirement: 访问日志详情
AccessLogController SHALL 提供 `GET /logs/access/{logId}` 接口。

#### Scenario: 查看详情
- **WHEN** 请求详情
- **THEN** 返回完整日志记录（含 requestHeaders、requestBody、exceptionStack）

### Requirement: Flyway 建表
SHALL 提供 Flyway 迁移脚本创建 sys_access_log 和 sys_operation_log 表。

#### Scenario: 访问日志表
- **WHEN** 迁移执行
- **THEN** sys_access_log SHALL 包含：log_id(VARCHAR 32 PK), request_id(VARCHAR 32 NOT NULL), request_url(VARCHAR 500), request_method(VARCHAR 10), request_headers(TEXT), request_params(VARCHAR 2000), request_body(TEXT), response_status(INT), duration_ms(BIGINT), client_ip(VARCHAR 50), user_agent(VARCHAR 500), user_id(VARCHAR 32), username(VARCHAR 50), exception_stack(TEXT), create_at(TIMESTAMP)

#### Scenario: 操作日志表
- **WHEN** 迁移执行
- **THEN** sys_operation_log SHALL 包含访问日志所有字段（含 request_id）+ controller_class(VARCHAR 200), controller_method(VARCHAR 100)

### Requirement: 前端访问日志页面
AccessLogView.vue SHALL 提供访问日志只读查看。

#### Scenario: 列表展示
- **WHEN** 进入访问日志页面
- **THEN** SHALL 以表格形式展示：请求URL、方法、响应码、耗时、IP、用户名、时间

#### Scenario: 详情弹窗
- **WHEN** 点击某行
- **THEN** SHALL 弹出 Dialog 展示完整信息（含请求头、请求体、异常堆栈）

### Requirement: 前端操作日志页面
OperationLogView.vue SHALL 提供操作日志只读查看。

#### Scenario: 列表展示
- **WHEN** 进入操作日志页面
- **THEN** SHALL 以表格形式展示：请求URL、方法、Controller方法、响应码、耗时、用户名、时间

#### Scenario: 详情弹窗
- **WHEN** 点击某行
- **THEN** SHALL 弹出 Dialog 展示完整信息

