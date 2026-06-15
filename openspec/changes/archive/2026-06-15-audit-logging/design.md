## Context

系统需要请求级日志审计能力。当前无任何请求日志记录机制。需要在不影响业务性能的前提下，记录所有访问和控制器操作。

约束：
- 后端：Spring Boot 3.5 + MyBatis-Plus + Flyway
- 需异步写入避免阻塞请求
- 请求体缓存读取需兼容 JSON 和表单
- 前端：Vue 3.5 + shadcn-vue，只读查看页面

## Goals / Non-Goals

**Goals:**
- 访问日志：Filter 层记录所有 HTTP 请求（含 404、拦截器拒绝的请求）
- 操作日志：Interceptor 层记录所有进入 Controller 的请求
- 异步入库：不阻塞业务请求
- 详细记录：URL、方法、请求头、参数、请求体、响应码、耗时、IP、User-Agent、用户、异常堆栈
- 前端：两个只读查看页面 + 详情弹窗

**Non-Goals:**
- 不做登录日志（暂不实现）
- 不做响应体记录（体积大、隐私风险）
- 不做日志清理/归档策略（后续做）
- 不做日志导出

## Decisions

### D0：请求关联标识

- **选择**：Filter 层生成 UUID 作为 requestId，存入 `request.setAttribute("requestId", uuid)`，操作日志和访问日志共用
- **理由**：同一请求经过 Filter 和 Interceptor 各生成一条记录，需要 requestId 关联

### D1：访问日志采集层

- **选择**：`OncePerRequestFilter` + `ContentCachingRequestWrapper`
- **理由**：Filter 是最早的拦截点，能捕获所有请求（含未匹配 Controller 的）；ContentCachingRequestWrapper 允许重复读取请求体
- **已考虑 alternative**：Tomcat AccessLog — 格式固定，无法记录请求体和用户信息 → 拒绝

### D2：操作日志采集层

- **选择**：`HandlerInterceptor`（preHandle + afterCompletion）
- **理由**：能获取 HandlerMethod 信息（Controller 类名/方法名），afterCompletion 能获取异常
- **已考虑 alternative**：AOP @Around — 需要切面表达式，灵活性差 → 拒绝

### D3：异步写入

- **选择**：`@Async` + Spring TaskExecutor 线程池
- **理由**：Spring 原生支持，配置简单，与项目技术栈一致
- **已考虑 alternative**：消息队列（Kafka/RabbitMQ）— 项目无 MQ 依赖，过重 → 拒绝

### D4：请求体读取

- **选择**：`ContentCachingRequestWrapper` 包装原始请求，afterCompletion 时读取缓存内容
- **理由**：Spring 原生提供，兼容 JSON 和 FormData
- **限制**：请求体超过 4KB 截断存储

### D5：存储

- **选择**：两张独立表 `sys_access_log` + `sys_operation_log`
- **理由**：访问日志量远大于操作日志，分表便于独立管理和清理

### D6：前端页面

- **选择**：只读表格 + 详情弹窗（Dialog 显示完整数据）
- **理由**：日志不可编辑/删除，仅查看

## Risks / Trade-offs

[Risk] 高并发下日志写入线程池满 → Mitigation: 配置合理线程池大小 + 拒绝策略为 CallerRunsPolicy

[Risk] 请求体中含敏感数据（密码等）→ Mitigation: 后续可加敏感字段脱敏逻辑（本次不做）

[Trade-off] 记录所有请求头可能含 Token → 接受：审计需要完整信息，访问控制保护日志页面

## Migration Plan

1. Flyway 建表（sys_access_log + sys_operation_log）
2. 后端实体 + DTO + Mapper + Service
3. AccessLogFilter + OperationLogInterceptor
4. 异步日志写入服务
5. Controller（只读分页查询 + 详情）
6. 前端页面（访问日志 + 操作日志）
7. 菜单注册（含登录日志占位）

## Open Questions

无
