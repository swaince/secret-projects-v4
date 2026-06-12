---
name: Presentational-Container-Pattern
description: Use when creating Vue components that mix data fetching with rendering, when refactoring bloated components, or when deciding how to split a feature into composables and display components. Triggers on symptoms like components doing too much, hard-to-reuse UI, or tangled state/view logic.
---

# Presentational / Container Pattern (Vue 3)

## Overview

将组件拆分为**展示组件**（负责 UI 渲染）和**容器组件**（负责数据/逻辑），实现关注点分离。Vue 3 Composition API 中，composable 函数替代了传统容器组件的角色。

## Core Principle

| 角色 | 职责 | 特征 |
|------|------|------|
| **展示组件** | 如何展示数据 | 接收 props，emit 事件，无副作用，纯渲染 |
| **容器组件** | 数据从哪来、如何变 | 调用 API/composable，管理状态，编排子组件 |
| **Composable** | 可复用逻辑封装 | 替代容器组件，返回响应式数据 + 操作函数 |

## When to Use

- 组件同时处理数据获取和复杂 UI 渲染（> 200 行）
- 相同 UI 需要在不同数据源下复用
- 需要独立测试 UI 和逻辑
- 多个组件共享同一数据获取逻辑

## When NOT to Use

- 小型组件（< 100 行），拆分反而增加复杂度
- 逻辑与视图天然耦合（如表单验证 + 表单渲染）
- 一次性页面组件，无复用需求

## Vue 3 实现模式

### 模式 A：Composable 替代容器（推荐）

```typescript
// composables/useDepts.ts — 逻辑层
export function useDepts() {
  const tree = ref<DeptDTO[]>([])
  const loading = ref(false)

  async function load() {
    loading.value = true
    tree.value = await fetchDeptTree()
    loading.value = false
  }

  async function remove(id: string) {
    await deleteDept(id)
    await load()
  }

  return { tree, loading, load, remove }
}
```

```vue
<!-- DeptView.vue — 容器层（轻量编排） -->
<script setup lang="ts">
const { tree, loading, load, remove } = useDepts()
onMounted(load)
</script>

<template>
  <DeptTree :nodes="tree" @delete="remove" />
</template>
```

```vue
<!-- DeptTree.vue — 展示层（纯渲染） -->
<script setup lang="ts">
defineProps<{ nodes: DeptDTO[] }>()
defineEmits<{ delete: [id: string] }>()
</script>
```

### 模式 B：页面即容器（本项目当前模式）

```vue
<!-- PostView.vue 同时承担容器 + 编排角色 -->
<script setup lang="ts">
// 状态管理（容器职责）
const posts = ref<PostDTO[]>([])
async function loadPosts() { ... }

// 展示逻辑（通过 shadcn-vue 组件委托）
</script>

<template>
  <!-- shadcn-vue 组件 = 展示组件 -->
  <Table>...</Table>
  <Dialog>...</Dialog>
</template>
```

本项目中 shadcn-vue 组件（Button、Table、Dialog）天然是展示组件，页面级 View 承担容器角色。当页面超过 300 行时考虑抽取 composable。

## 判断边界

```
组件是否超过 200 行？
├─ 否 → 保持现状，不拆分
└─ 是 → 数据逻辑是否可复用？
    ├─ 是 → 抽取 composable（模式 A）
    └─ 否 → 页面内 script 区域是否过长？
        ├─ 是 → 抽取 composable 降低认知负担
        └─ 否 → 保持现状
```

## 本项目约定

- **展示组件**：`src/components/ui/`（shadcn-vue，禁止修改）+ `src/components/<module>/`（业务展示组件）
- **容器组件**：`src/views/<module>/XxxView.vue`（页面级，负责状态和 API 调用）
- **Composable**：`src/composables/useXxx.ts`（可复用逻辑）
- 展示组件只接收 props + emit 事件，不直接调用 API
- 容器组件可以内联逻辑（< 300 行时），超出则抽取 composable

## 常见错误

| 错误 | 修复 |
|------|------|
| 展示组件内调用 fetch/API | 移至容器或 composable，通过 props 传入数据 |
| 容器组件包含大量模板样式 | 将 UI 片段抽取为展示子组件 |
| Composable 返回 JSX/模板 | Composable 只返回数据和函数，不返回 UI |
| 过度拆分简单组件 | 遵循 200 行阈值，小组件不拆 |

## 参考

- [patterns.dev: Container/Presentational Pattern](https://www.patterns.dev/react/presentational-container-pattern/)
- Vue 3 Composition API 中 composable 等价于 React Custom Hooks
- Dan Abramov 原文已标注此模式在 Hooks 时代可选但原则仍有效
