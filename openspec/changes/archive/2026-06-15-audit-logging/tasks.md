## 1. 后端：数据库

- [x] 1.1 创建 Flyway 迁移脚本建 `sys_access_log` 和 `sys_operation_log` 表

## 2. 后端：实体与 DTO

- [x] 2.1 创建 `SysAccessLog` 实体类
- [x] 2.2 创建 `SysOperationLog` 实体类
- [x] 2.3 创建 `AccessLogDTO` 和 `OperationLogDTO`
- [x] 2.4 创建 `AccessLogPageRequest` 和 `OperationLogPageRequest`

## 3. 后端：Mapper

- [x] 3.1 创建 `AccessLogMapper` 和 `OperationLogMapper`

## 4. 后端：异步日志服务

- [x] 4.1 创建 `AsyncLogService`（@Async 方法：saveAccessLog、saveOperationLog）
- [x] 4.2 配置异步线程池（TaskExecutor Bean）

## 5. 后端：Filter + Interceptor

- [x] 5.1 创建 `AccessLogFilter`（OncePerRequestFilter + ContentCachingRequestWrapper）
- [x] 5.2 创建 `OperationLogInterceptor`（HandlerInterceptor，preHandle 记录开始时间，afterCompletion 记录结果）
- [x] 5.3 注册 Filter 和 Interceptor（WebMvcConfigurer + @Component/@Bean）

## 6. 后端：查询 Controller

- [x] 6.1 创建 `AccessLogController`（GET /logs/access 分页 + GET /logs/access/{logId} 详情）
- [x] 6.2 创建 `OperationLogController`（GET /logs/operation 分页 + GET /logs/operation/{logId} 详情）

## 7. 后端：查询 Service

- [x] 7.1 创建 `AccessLogService` + `AccessLogServiceImpl`（分页查询 + 详情）
- [x] 7.2 创建 `OperationLogService` + `OperationLogServiceImpl`

## 8. 后端：测试

- [x] 8.1 编写集成测试验证 Filter 和 Interceptor 正确记录日志

## 9. 前端：API 层

- [x] 9.1 创建 `src/api/log.ts`（AccessLogDTO, OperationLogDTO, fetchAccessLogs, fetchOperationLogs, fetchAccessLogDetail, fetchOperationLogDetail）

## 10. 前端：页面

- [x] 10.1 创建 `src/views/audit/AccessLogView.vue`（只读表格 + 搜索 + 详情弹窗）
- [x] 10.2 创建 `src/views/audit/OperationLogView.vue`（只读表格 + 搜索 + 详情弹窗）

## 11. 菜单数据

- [x] 11.1 创建 Flyway 迁移插入日志审计菜单（访问日志 + 操作日志 + 登录日志占位）

## 12. 验证

- [x] 12.1 运行 `./mvnw test -pl backend` 确认后端测试通过
- [x] 12.2 运行 `pnpm type-check` 和 `pnpm lint` 确认前端无错误
