# 日志审计 — 头脑风暴记录

## 背景

系统缺少请求级日志审计能力。需要记录所有访问和操作，便于安全审计、问题排查和行为分析。

## 决议链

### Q1：日志类型与分层？

**决定**：三种类型，本次实现前两种。
- **访问日志**：Servlet Filter 层（OncePerRequestFilter），捕获所有 HTTP 请求，含 404、静态资源、未进入 Controller 的请求
- **操作日志**：Controller 层（HandlerInterceptor），仅记录进入 Controller 的请求，包含方法级信息
- **登录日志**：暂不实现，仅预留菜单入口

### Q2：记录哪些字段？

**决定**：尽可能详细。

**访问日志字段**：
- logId, requestId(UUID), requestUrl, requestMethod, requestHeaders(JSON), requestParams, requestBody, responseStatus, durationMs, clientIp, userAgent, userId, username, exceptionStack, createAt

**操作日志字段**（在访问日志基础上增加）：
- logId, requestId(UUID), requestUrl, requestMethod, requestHeaders(JSON), requestParams, requestBody, responseStatus, durationMs, clientIp, userAgent, userId, username, exceptionStack, controllerClass, controllerMethod, createAt

### Q6：请求关联？

**决定**：Filter 层生成 UUID 作为 requestId 存入 `request.setAttribute("requestId", uuid)`，操作日志和访问日志都记录同一个 requestId。通过 requestId 可以关联同一个请求的两条记录。

### Q3：性能影响？

**决定**：异步写入。使用 `@Async` + 线程池，Filter/Interceptor 中收集数据后异步入库，不阻塞请求。

### Q4：请求体/响应体记录？

**决定**：
- 请求体：使用 ContentCachingRequestWrapper 缓存后读取，限制最大长度（如 4KB）
- 响应体：不记录（体积大、敏感数据多）
- 请求头：完整记录为 JSON 字符串

### Q5：前端页面？

**决定**：
- 两个查看页面（访问日志 + 操作日志），只读不可编辑/删除
- 支持按时间范围、URL、用户名、响应码搜索
- 表格展示 + 点击行查看详情（弹窗显示完整请求头/请求体/异常堆栈）
