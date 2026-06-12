## Why

前端 API 层各文件使用 fetch 分散封装，无统一错误处理，通用类型（R、PageResponse）定义在业务模块中位置不合理。需要集中化 HTTP 请求管理，为后续 token 注入、请求重试等提供基础设施。

## What Changes

**新增基础设施**
- From: 各 API 文件各自定义 `request()` 函数使用 fetch
- To: 统一 axios 实例 + 响应拦截器自动解包和错误 toast
- Impact: non-breaking，API 函数签名不变

**类型全局化**
- From: `R<T>`、`PageResponse<T>` 定义在 `api/dict.ts` 中
- To: `src/types/api.d.ts` 全局声明，无需 import
- Impact: non-breaking，现有 import 保持兼容或移除

**Toast 组件**
- 新增 sonner 组件用于全局 toast 通知

## Capabilities

### New Capabilities
- `http-client-infra`: 统一 HTTP 请求基础设施（axios 实例 + 拦截器 + 全局类型 + toast 错误处理）

### Modified Capabilities
（无 spec 级别的行为变更，仅内部重构）

## Impact

- 依赖：新增 axios、vue-sonner
- 文件：新增 `src/types/api.d.ts`、`src/utils/request.ts`；修改所有 `api/*.ts`
- 行为：API 调用方无感知变化（函数签名不变），错误由 toast 展示
- 破坏性：无
