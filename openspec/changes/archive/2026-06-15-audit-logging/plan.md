# 日志审计 Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 实现请求级日志审计（访问日志 + 操作日志），Filter/Interceptor 异步采集，前端只读查看。

**Architecture:** AccessLogFilter（OncePerRequestFilter）捕获所有请求 → OperationLogInterceptor（HandlerInterceptor）捕获 Controller 请求 → AsyncLogService 异步写入 → 独立查询 Controller 提供分页/详情。

**Tech Stack:** Spring Boot 3.5 / MyBatis-Plus / @Async / ContentCachingRequestWrapper / Vue 3.5 / shadcn-vue

---

## Task 1: Flyway 建表

**Files:**
- Create: `backend/src/main/resources/db/migration/V1.0.1.2__create_log_tables.sql`

- [ ] **Step 1: 建表脚本**

```sql
CREATE TABLE sys_access_log (
    log_id          VARCHAR(32)   PRIMARY KEY,
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

CREATE TABLE sys_operation_log (
    log_id            VARCHAR(32)   PRIMARY KEY,
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

CREATE INDEX idx_access_log_create_at ON sys_access_log(create_at DESC);
CREATE INDEX idx_operation_log_create_at ON sys_operation_log(create_at DESC);

COMMENT ON TABLE sys_access_log IS '访问日志';
COMMENT ON TABLE sys_operation_log IS '操作日志';
```

---

## Task 2: 实体 + DTO

**Files:**
- Create: SysAccessLog.java, SysOperationLog.java, AccessLogDTO.java, OperationLogDTO.java, AccessLogPageRequest.java, OperationLogPageRequest.java

- [ ] **Step 1: 实体** — @TableName, @TableId(type=INPUT), 所有字段
- [ ] **Step 2: DTO** — 响应 DTO（所有字段）
- [ ] **Step 3: PageRequest** — extends QueryRequest, 含搜索字段（requestUrl, username, responseStatus, startTime, endTime; 操作日志额外含 controllerMethod）

---

## Task 3: Mapper

- [ ] **Step 1:** AccessLogMapper extends BaseMapper<SysAccessLog>
- [ ] **Step 2:** OperationLogMapper extends BaseMapper<SysOperationLog>

---

## Task 4: 异步日志服务

**Files:**
- Create: AsyncLogService.java, AsyncConfig.java

- [ ] **Step 1: AsyncConfig** — @EnableAsync + @Bean TaskExecutor（核心线程 2, 最大 10, 队列 500, CallerRunsPolicy）
- [ ] **Step 2: AsyncLogService** — @Async saveAccessLog(SysAccessLog), @Async saveOperationLog(SysOperationLog)

---

## Task 5: Filter + Interceptor

**Files:**
- Create: AccessLogFilter.java, OperationLogInterceptor.java

- [ ] **Step 1: AccessLogFilter**
  - extends OncePerRequestFilter
  - doFilterInternal: 包装请求为 ContentCachingRequestWrapper → 执行 filterChain → 采集数据 → asyncLogService.saveAccessLog()
  - 采集：URL, method, headers(遍历转JSON), params, body(从wrapper读取,限4KB), status, duration, IP(X-Forwarded-For), userAgent, userId/username(从Token或SecurityContext)
  - 异常：通过 request.getAttribute(DispatcherServlet.EXCEPTION_ATTRIBUTE) 或 try-catch

- [ ] **Step 2: OperationLogInterceptor**
  - implements HandlerInterceptor
  - preHandle: 记录开始时间到 request attribute
  - afterCompletion: 采集数据 + controllerClass/method(从 HandlerMethod) + exception → asyncLogService.saveOperationLog()

- [ ] **Step 3: 注册**
  - Filter: @Component + @Order(Ordered.HIGHEST_PRECEDENCE + 10)
  - Interceptor: WebMvcConfigurer.addInterceptors()

---

## Task 6: 查询 Service

- [ ] **Step 1: AccessLogService/Impl** — page(PageRequest), getById(logId)
- [ ] **Step 2: OperationLogService/Impl** — page(PageRequest), getById(logId)

分页查询按条件过滤 + createAt 降序。

---

## Task 7: 查询 Controller

- [ ] **Step 1: AccessLogController** — @RequestMapping("/logs/access")
  - GET / — 分页查询
  - GET /{logId} — 详情

- [ ] **Step 2: OperationLogController** — @RequestMapping("/logs/operation")
  - GET / — 分页查询
  - GET /{logId} — 详情

---

## Task 8: 测试

- [ ] **Step 1:** 集成测试 — 发请求后查询日志表验证记录存在

---

## Task 9: 前端 API 层

**Files:**
- Create: `frontend/src/api/log.ts`

- [ ] **Step 1:** 接口定义 + fetchAccessLogs/fetchOperationLogs/fetchAccessLogDetail/fetchOperationLogDetail

---

## Task 10: 前端页面

**Files:**
- Create: `frontend/src/views/audit/AccessLogView.vue`
- Create: `frontend/src/views/audit/OperationLogView.vue`

- [ ] **Step 1: AccessLogView** — 只读表格（URL/方法/响应码/耗时/IP/用户名/时间）+ 搜索 + 点击行弹详情 Dialog
- [ ] **Step 2: OperationLogView** — 同上 + Controller 方法列

详情 Dialog 展示完整请求头（<pre> 格式化 JSON）、请求体、异常堆栈。

---

## Task 11: 菜单数据

- [ ] **Step 1:** Flyway 迁移插入菜单：日志审计(D) → 访问日志(M) + 操作日志(M) + 登录日志(M,占位)

---

## Task 12: 验证

- [ ] **Step 1:** `./mvnw test -pl backend`
- [ ] **Step 2:** `pnpm type-check`
- [ ] **Step 3:** `pnpm lint:oxlint`
