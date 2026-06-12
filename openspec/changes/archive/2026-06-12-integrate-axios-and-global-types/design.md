## Context

前端 API 层使用原生 fetch 分散封装，通用类型定义在业务模块中。需要统一为 axios + 全局类型 + 集中错误处理。

## Goals / Non-Goals

**Goals:**
- 安装 axios 替代 fetch，提供统一请求实例
- 创建 `src/types/` 目录，全局注册 `R<T>`、`PageResponse<T>` 等通用类型
- 响应拦截器自动解包 `R<T>.data`，非 200 时 toast 提示
- 重构所有现有 API 文件使用 axios
- 项目级禁止 fetch

**Non-Goals:**
- Token 注入（后续迭代）
- 请求重试、取消机制
- API mock/测试环境切换

## Decisions

### D1：axios 实例封装位置
- **选择**：`src/utils/request.ts`
- **理由**：utils 目录放工具函数，request 是基础工具

### D2：全局类型注册方式
- **选择**：`src/types/api.d.ts` 使用 `declare global` + tsconfig include
- **理由**：`.d.ts` 文件中的全局声明无需 import 即可使用
- **已考虑**：普通 `.ts` 导出 → 每处需要 import，不够便捷

### D3：响应拦截器行为
- **选择**：
  - 成功（code=200）：直接返回 `response.data.data`（解包两层：axios.data → R.data）
  - 失败（code≠200 或网络错误）：toast 显示错误信息，reject Promise
- **理由**：调用方无需重复处理通用错误，但仍可 catch 处理特殊场景

### D4：Toast 组件选型
- **选择**：shadcn-vue 的 sonner（基于 vue-sonner）
- **理由**：API 简洁（`toast.error(msg)` 即可），无需 Provider 包裹

### D5：禁止 fetch 的执行方式
- **选择**：在 `frontend/AGENTS.md` 中明确禁止，ESLint 规则可选
- **理由**：AI 开发为主，AGENTS.md 是最直接的约束

## Risks / Trade-offs

- [Trade-off] 全局类型可能与其他库类型冲突 → 使用特定命名（`ApiResponse`/`PageResult` 而非 `R`/`Response`）减少冲突。但用户明确要求用 `R`，保持简短。
- [Risk] 响应拦截器统一解包后，调用方拿到的类型是 `T` 而非 `AxiosResponse` → 需要正确定义 request 函数的泛型返回类型

## Migration Plan

1. 安装依赖：`pnpm add axios` + shadcn-vue sonner
2. 创建类型文件 + request 工具
3. 逐个重构 API 文件（dict → post → role → dept）
4. 移除旧 fetch 代码
5. 验证所有页面功能正常

## Open Questions

无。
