# 集成 Axios 并封装统一 API — Brainstorm

## 背景

前端各 API 文件（dict.ts, post.ts, role.ts, dept.ts）各自使用 `fetch` + 自定义 `request()` 函数封装。通用类型（`R<T>`, `PageResponse<T>`）定义在 dict.ts 中被其他文件导入。

问题：
- fetch 封装分散、重复
- 无统一错误处理（当前仅 throw Error）
- 无拦截器机制（后续需要 token 注入）
- 类型定义位置不合理（通用类型不应在业务模块中）

## 决议链

### Q1: HTTP 客户端选型？
**决议**：axios。生态成熟，拦截器机制完善，TypeScript 支持好。项目禁止使用 fetch。

### Q2: 类型文件位置？
**决议**：`src/types/` 目录，通过 tsconfig 注册为全局类型（无需每处 import）。

### Q3: 错误处理方式？
**决议**：axios 响应拦截器统一处理，非 200 状态通过 toast 提示用户，同时 reject Promise 供调用方按需处理。

### Q4: Token 注入？
**决议**：暂不实现，预留请求拦截器位置，后续添加。

### Q5: Toast 组件？
**决议**：安装 shadcn-vue 的 sonner 组件（基于 vue-sonner），轻量且 API 简洁。

## 实施范围

1. `pnpm add axios`
2. 安装 shadcn-vue sonner 组件
3. 创建 `src/types/api.d.ts` — 全局类型 `R<T>`, `PageResponse<T>`
4. 创建 `src/utils/request.ts` — axios 实例 + 响应拦截器（解包 R<T>.data + toast 错误）
5. 重构所有 `api/*.ts` — 使用 axios 实例替代 fetch
6. 从 `api/dict.ts` 移除 `R<T>`, `PageResponse<T>` 定义和 `request()` 函数
7. ESLint 规则或 AGENTS.md 注明禁止使用 fetch
