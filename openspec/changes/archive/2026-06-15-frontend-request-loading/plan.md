# 前端请求 Loading Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 全局请求 loading 遮罩，自动触发、可排除、并发合并。

**Architecture:** Pinia store 计数器管理状态，axios 拦截器自动 start/end，LoadingOverlay 组件响应式渲染遮罩。

**Tech Stack:** Vue 3.5 / Pinia / TypeScript / Tailwind CSS v4 / Vitest

---

## Task 1: Loading Store

**Files:**
- Create: `frontend/src/stores/loading.ts`

- [ ] **Step 1: 创建 store**

```typescript
import { computed, ref } from 'vue'
import { defineStore } from 'pinia'

export const useLoadingStore = defineStore('loading', () => {
  const count = ref(0)
  const isLoading = computed(() => count.value > 0)

  function start() {
    count.value++
  }

  function end() {
    count.value = Math.max(0, count.value - 1)
  }

  function reset() {
    count.value = 0
  }

  return { count, isLoading, start, end, reset }
})
```

- [ ] **Step 2: 验证编译**

Run: `pnpm type-check` (in `frontend/`)

---

## Task 2: Axios 拦截器扩展

**Files:**
- Modify: `frontend/src/utils/request.ts`

- [ ] **Step 1: 定义 RequestOptions 接口并扩展方法签名**

在 HttpClient 类中：
- 新增 `interface RequestOptions { silent?: boolean }`
- `get<T, D=T>(url, params?, options?)` — options 传入 config
- `post<T, D=T>(url, data?, options?)` — 同上
- `put<T, D=T>(url, data?, options?)` — 同上
- `delete<T, D>(url, data?, options?)` — 同上

将 `options.silent` 存入 axios config（`config.metadata = { silent }`）。

- [ ] **Step 2: 添加请求拦截器**

```typescript
this.instance.interceptors.request.use((config) => {
  if (!config.metadata?.silent) {
    useLoadingStore().start()
  }
  return config
})
```

- [ ] **Step 3: 修改响应拦截器**

在现有响应拦截器的 fulfill 和 reject 中，判断 `config.metadata?.silent`，非 silent 则调用 `end()`。

- [ ] **Step 4: 验证编译**

Run: `pnpm type-check` (in `frontend/`)

---

## Task 3: LoadingOverlay 组件

**Files:**
- Create: `frontend/src/components/common/LoadingOverlay.vue`

- [ ] **Step 1: 创建组件**

```vue
<script setup lang="ts">
import { useLoadingStore } from '@/stores/loading'

const { isLoading } = useLoadingStore()
</script>

<template>
  <div v-if="isLoading" class="fixed inset-0 z-[9999] flex items-center justify-center bg-background/60">
    <svg class="size-8 animate-spin text-primary" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
      <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4" />
      <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4z" />
    </svg>
  </div>
</template>
```

---

## Task 4: App.vue 挂载

**Files:**
- Modify: `frontend/src/App.vue`

- [ ] **Step 1: 导入并挂载 LoadingOverlay**

在 template 中添加 `<LoadingOverlay />`。

---

## Task 5: 字典请求 silent

**Files:**
- Modify: `frontend/src/api/dict.ts`

- [ ] **Step 1: fetchDictByCode 传入 silent**

```typescript
export function fetchDictByCode(dictCode: string): Promise<DictWithItemsDTO | null> {
  return http.get<DictWithItemsDTO | null>('/dicts/items/by-code', { dictCode }, { silent: true })
}
```

---

## Task 6: 单元测试

**Files:**
- Create: `frontend/src/__tests__/stores/loading.spec.ts`

- [ ] **Step 1: 编写 store 测试**

```typescript
import { beforeEach, describe, expect, it } from 'vitest'
import { createPinia, setActivePinia } from 'pinia'
import { useLoadingStore } from '@/stores/loading'

describe('useLoadingStore', () => {
  beforeEach(() => setActivePinia(createPinia()))

  it('初始状态 isLoading 为 false', () => {
    const store = useLoadingStore()
    expect(store.isLoading).toBe(false)
  })

  it('start 后 isLoading 为 true', () => {
    const store = useLoadingStore()
    store.start()
    expect(store.isLoading).toBe(true)
  })

  it('start + end 后 isLoading 为 false', () => {
    const store = useLoadingStore()
    store.start()
    store.end()
    expect(store.isLoading).toBe(false)
  })

  it('并发：3 次 start + 2 次 end 仍为 loading', () => {
    const store = useLoadingStore()
    store.start()
    store.start()
    store.start()
    store.end()
    store.end()
    expect(store.isLoading).toBe(true)
  })

  it('end 不低于零', () => {
    const store = useLoadingStore()
    store.end()
    store.end()
    expect(store.count).toBe(0)
  })

  it('reset 强制归零', () => {
    const store = useLoadingStore()
    store.start()
    store.start()
    store.reset()
    expect(store.isLoading).toBe(false)
  })
})
```

- [ ] **Step 2: 运行测试**

Run: `pnpm test:unit -- src/__tests__/stores/loading.spec.ts` (in `frontend/`)

- [ ] **Step 3: 全量检查**

Run: `pnpm type-check && pnpm lint:oxlint` (in `frontend/`)
