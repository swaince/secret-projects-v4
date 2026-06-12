# Retrospective: integrate-axios-and-global-types

## 概要

将前端 HTTP 请求从分散的 fetch 封装统一为 axios 实例 + 响应拦截器 + 全局类型 + toast 错误提示。

## 完成情况

- 新增：axios、vue-sonner 依赖
- 新增：`src/types/api.ts`（全局 R<T>、PageResponse<T>）
- 新增：`src/utils/request.ts`（axios 实例 + 拦截器）
- 重构：4 个 API 文件 + 3 个 View 文件（移除旧 import）
- 更新：App.vue（Toaster）、AGENTS.md（禁止 fetch）

## 经验

- 响应拦截器解包两层（axios.data → R.data）使调用方代码更简洁
- 全局类型声明需要确保 tsconfig include 覆盖 types 目录
- shadcn-vue sonner 安装后可能需要手动调整 v-bind 顺序修复 TS2783
