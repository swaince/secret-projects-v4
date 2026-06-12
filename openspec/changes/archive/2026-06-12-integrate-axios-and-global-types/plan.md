# Axios 集成 + 全局类型 Implementation Plan

**Goal:** 用 axios 替代 fetch，统一 API 请求封装，全局类型注册，toast 错误提示。

**Tech Stack:** axios, vue-sonner (shadcn-vue sonner), TypeScript

---

## Task 1: 安装依赖

- [ ] **Step 1:** `pnpm add axios`
- [ ] **Step 2:** 使用 shadcn-vue CLI 安装 sonner：`npx shadcn-vue@latest add sonner`

**Commit point:** `chore(frontend): add axios and sonner dependencies`

---

## Task 2: 全局类型

- [ ] **Step 1:** 创建 `frontend/src/types/api.d.ts`：
  ```typescript
  export {}
  declare global {
    interface R<T> {
      code: number
      message: string
      data: T
    }
    interface PageResponse<T> {
      records: T[]
      total: number
      page: number
      size: number
    }
  }
  ```
- [ ] **Step 2:** 确认 `tsconfig.app.json` 的 include 包含 `src/types/**/*.d.ts`（通常 `src/**/*` 已覆盖）

---

## Task 3: Toast 基础设施

- [ ] **Step 1:** 在 `App.vue` 中导入并挂载 `<Toaster />` 组件（来自 shadcn-vue sonner）

---

## Task 4: Axios 实例

- [ ] **Step 1:** 创建 `frontend/src/utils/request.ts`：
  ```typescript
  import axios from 'axios'
  import { toast } from 'vue-sonner'

  const request = axios.create({
    baseURL: '/api',
    headers: { 'Content-Type': 'application/json' },
  })

  request.interceptors.response.use(
    (response) => {
      const body: R<unknown> = response.data
      if (body.code === 200) return body.data as any
      toast.error(body.message || '请求失败')
      return Promise.reject(new Error(body.message))
    },
    (error) => {
      toast.error(error.message || '网络错误')
      return Promise.reject(error)
    },
  )

  export default request
  ```

---

## Task 5: 重构 API 文件

逐个重构，保持函数签名不变：

- [ ] **Step 1:** 重构 `api/dict.ts`
  - 移除 `R<T>`, `PageResponse<T>` 接口定义
  - 移除 `request()` 函数和 `BASE` 常量
  - 导入 `request from '@/utils/request'`
  - 改为 `request.get<any, PageResponse<DictDTO>>('/dicts', { params })` 等

- [ ] **Step 2:** 重构 `api/post.ts` — 同上模式
- [ ] **Step 3:** 重构 `api/role.ts` — 同上模式
- [ ] **Step 4:** 重构 `api/dept.ts` — 同上模式

注意：由于响应拦截器已解包 `R<T>.data`，axios 调用的返回值直接是 `T`。泛型签名用 `request.get<any, T>(url)` 形式。

---

## Task 6: 文档更新

- [ ] **Step 1:** 在 `frontend/AGENTS.md` 技术要点中添加：
  - 禁止使用原生 fetch，所有 HTTP 请求通过 `src/utils/request.ts` 的 axios 实例
  - 通用类型 `R<T>`, `PageResponse<T>` 在 `src/types/api.d.ts` 全局声明

---

## Task 7: 验证

- [ ] **Step 1:** `pnpm type-check`
- [ ] **Step 2:** `pnpm lint`

**Commit point:** `refactor(frontend): integrate axios, global types, sonner toast`
