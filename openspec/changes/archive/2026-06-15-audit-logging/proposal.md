## Why

系统缺少请求级审计能力，无法追溯谁在什么时间访问了什么接口、做了什么操作。安全审计、故障排查、合规要求都需要完整的请求日志记录。

## What Changes

**后端：新增访问日志 + 操作日志记录机制**
- From: 无请求日志
- To: Filter 记录所有访问 + Interceptor 记录 Controller 操作，异步入库
- Reason: 安全审计与问题排查
- Impact: non-breaking，透明拦截

**后端：新增日志查询接口**
- From: 无
- To: 分页查询 + 详情查看（只读）
- Reason: 提供日志查看能力
- Impact: non-breaking，纯新增

**前端：新增日志查看页面**
- From: 日志审计菜单无功能页面
- To: 访问日志 + 操作日志两个查看页面
- Reason: 可视化日志查看
- Impact: non-breaking，新增页面

## Capabilities

### New Capabilities

- `audit-logging`: 日志审计 — 包含 Filter/Interceptor 日志采集、异步写入、查询接口、前端查看页面

### Modified Capabilities

无

## Impact

- **后端新增**：sys_access_log/sys_operation_log 表、实体/DTO/Mapper/Service、AccessLogFilter、OperationLogInterceptor、AsyncLogService、AccessLogController/OperationLogController、WebMvc 配置注册拦截器
- **前端新增**：`src/api/log.ts`、`src/views/audit/AccessLogView.vue`、`src/views/audit/OperationLogView.vue`
- **菜单**：通过管理页面添加（日志审计下 3 个子菜单）
- **依赖**：无新外部依赖
- **测试**：后端集成测试
