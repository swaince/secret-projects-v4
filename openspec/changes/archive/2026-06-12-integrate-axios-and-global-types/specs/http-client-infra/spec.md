## ADDED Requirements

### Requirement: 全局类型定义

系统 SHALL 在 `src/types/api.d.ts` 中全局声明 `R<T>` 和 `PageResponse<T>` 类型，项目中任何文件无需 import 即可使用。

#### Scenario: 全局类型可用
- **WHEN** 任意 `.ts` 或 `.vue` 文件引用 `R<T>` 或 `PageResponse<T>`
- **THEN** TypeScript 无需 import 语句即可识别类型，type-check 通过

#### Scenario: 类型定义正确
- **WHEN** 后端返回 `{ code: 200, message: "ok", data: {...} }` 格式
- **THEN** `R<T>` 类型 MUST 包含 `code: number`、`message: string`、`data: T` 字段

---

### Requirement: Axios 统一请求实例

系统 SHALL 在 `src/utils/request.ts` 中创建 axios 实例，配置 baseURL 为 `/api`，设置默认 Content-Type 为 `application/json`。

#### Scenario: 请求自动添加 baseURL
- **WHEN** 调用 `request.get('/posts')` 
- **THEN** 实际请求 URL 为 `/api/posts`

#### Scenario: 响应成功自动解包
- **WHEN** 后端返回 `{ code: 200, message: "ok", data: { ... } }`
- **THEN** 调用方直接获得 `data` 部分（自动解包 axios response.data 和 R.data 两层）

#### Scenario: 业务错误 toast 提示
- **WHEN** 后端返回 `{ code: 非200, message: "岗位编码已存在" }`
- **THEN** 自动显示 toast 错误提示（message 内容），同时 reject Promise

#### Scenario: 网络错误 toast 提示
- **WHEN** 请求发生网络错误（超时、断连等）
- **THEN** 自动显示 toast 错误提示，reject Promise

---

### Requirement: API 文件重构

所有 API 文件（dict.ts、post.ts、role.ts、dept.ts）MUST 使用 axios 实例发送请求，MUST NOT 使用 fetch。

#### Scenario: API 函数签名不变
- **WHEN** 重构后调用 `fetchPosts(params)`
- **THEN** 返回类型和参数签名与重构前一致，调用方无需修改

#### Scenario: 移除旧代码
- **WHEN** 重构完成
- **THEN** `api/dict.ts` 中不再包含 `R<T>`、`PageResponse<T>` 定义和 `request()` 函数

---

### Requirement: 禁止使用 fetch

项目 MUST 在 `frontend/AGENTS.md` 中明确禁止使用原生 fetch，所有 HTTP 请求 MUST 通过 `src/utils/request.ts` 的 axios 实例发送。

#### Scenario: 文档约束
- **WHEN** 开发者或 AI 查阅 `frontend/AGENTS.md`
- **THEN** 能看到明确的"禁止使用 fetch"规则和推荐的 request 工具路径

---

### Requirement: Toast 通知组件

系统 SHALL 安装 shadcn-vue 的 sonner 组件，并在 App.vue 中挂载 Toaster，使全局 toast 可用。

#### Scenario: toast 可在任意位置调用
- **WHEN** 在任意 `.ts` 或 `.vue` 文件中调用 `toast.error('msg')`
- **THEN** 页面显示错误 toast 通知
