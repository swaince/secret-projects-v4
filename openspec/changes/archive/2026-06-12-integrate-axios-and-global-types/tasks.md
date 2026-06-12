## 1. 安装依赖

- [x] 1.1 安装 axios：`pnpm add axios`
- [x] 1.2 安装 shadcn-vue sonner 组件

## 2. 全局类型

- [x] 2.1 创建 `src/types/api.d.ts`（declare global R<T>、PageResponse<T>）
- [x] 2.2 确认 tsconfig.json include 包含 `src/types/`

## 3. Toast 基础设施

- [x] 3.1 在 App.vue 中挂载 Toaster 组件

## 4. Axios 实例

- [x] 4.1 创建 `src/utils/request.ts`（axios 实例 + baseURL=/api + 响应拦截器）

## 5. 重构 API 文件

- [x] 5.1 重构 `api/dict.ts`（使用 axios，移除 R/PageResponse 定义和 request 函数）
- [x] 5.2 重构 `api/post.ts`（使用 axios，移除本地 request 函数）
- [x] 5.3 重构 `api/role.ts`（使用 axios，移除本地 request 函数）
- [x] 5.4 重构 `api/dept.ts`（使用 axios，移除本地 request 函数）

## 6. 文档更新

- [x] 6.1 更新 `frontend/AGENTS.md`：禁止 fetch，标注 request.ts 路径

## 7. 验证

- [x] 7.1 运行 `pnpm type-check`
- [x] 7.2 运行 `pnpm lint`
