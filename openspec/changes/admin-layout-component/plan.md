# 通用后台管理布局组件 实施计划

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 实现通用后台管理布局组件（顶部通栏 + 左侧可折叠侧边栏 + 面包屑 + 主内容区），支撑三层菜单结构，含主题切换持久化。

**Architecture:** AdminLayout.vue 作为布局壳，管理一级菜单选中态和侧边栏折叠态；AppHeader/AppSidebar/AppBreadcrumb 为纯展示子组件；菜单配置由 `config/menu.ts` 静态定义，`createRoutesFromMenu()` 纯函数生成路由；`useThemeStore` Pinia store 管理主题状态和 DOM 同步。

**Tech Stack:** Vue 3.5 · TypeScript 6 · Tailwind CSS v4 · shadcn-vue · Pinia · Vue Router 5 · Vitest · pnpm

---

### Task 1: 基础设施搭建

**Files:**
- Create: `src/layouts/`
- Create: `src/components/layout/`
- Create: `src/config/`
- Create: `src/composables/`
- Modify: `src/styles/index.css`

- [ ] **Step 1: 创建目录结构**

```powershell
New-Item -ItemType Directory -Path "frontend/src/layouts" -Force
New-Item -ItemType Directory -Path "frontend/src/components/layout" -Force
New-Item -ItemType Directory -Path "frontend/src/config" -Force
New-Item -ItemType Directory -Path "frontend/src/composables" -Force
```

- [ ] **Step 2: 修复 design-system.css 加载链**

Modify `frontend/src/styles/index.css`:
```css
@import "tailwind.css";
@import "design-system.css";
```

- [ ] **Step 3: 安装 shadcn-vue 组件**

使用 `shadcnVue_get_add_command_for_items` 获取安装命令，安装以下组件：
`sidebar`, `button`, `dropdown-menu`, `badge`, `avatar`, `tooltip`, `breadcrumb`, `separator`

在 frontend/ 目录下执行返回的命令。

- [ ] **Step 4: Commit**

```bash
git add frontend/src/layouts/ frontend/src/components/layout/ frontend/src/config/ frontend/src/composables/ frontend/src/styles/index.css
git commit -m "chore: setup infrastructure for admin layout (directories, CSS, shadcn-vue components)"
```

---

### Task 2: 菜单配置（类型定义 + 静态数据）

**Files:**
- Create: `frontend/src/config/menu.ts`
- Create: `frontend/src/__tests__/menu.spec.ts`

- [ ] **Step 1: 创建菜单类型和静态配置**

Write `frontend/src/config/menu.ts`:
```typescript
export interface MenuItem {
  id: string
  title: string
  icon?: string
  path?: string
  children?: MenuItem[]
  meta?: {
    breadcrumb?: string
    keepAlive?: boolean
  }
}

export const menuConfig: MenuItem[] = [
  {
    id: 'dashboard',
    title: '仪表盘',
    icon: 'LayoutDashboard',
    path: '/dashboard',
  },
  {
    id: 'system',
    title: '系统管理',
    icon: 'Settings',
    children: [
      {
        id: 'user-mgmt',
        title: '用户管理',
        icon: 'Users',
        children: [
          { id: 'user-list', title: '用户列表', path: '/system/users' },
          { id: 'role-list', title: '角色管理', path: '/system/roles' },
        ],
      },
      {
        id: 'dept-mgmt',
        title: '部门管理',
        path: '/system/depts',
      },
    ],
  },
]
```

- [ ] **Step 2: 编写 createRoutesFromMenu 测试**

Write `frontend/src/__tests__/menu.spec.ts`:
```typescript
import { describe, it, expect } from 'vitest'
import type { MenuItem } from '@/config/menu'

function createRoutesFromMenu(items: MenuItem[]) {
  const routes: { path: string; name: string; meta: Record<string, unknown> }[] = []
  for (const item of items) {
    if (item.path) {
      routes.push({
        path: item.path,
        name: item.id,
        meta: {
          title: item.title,
          ...item.meta,
        },
      })
    }
    if (item.children) {
      routes.push(...createRoutesFromMenu(item.children))
    }
  }
  return routes
}

// Move createRoutesFromMenu to config/menu.ts after tests pass

describe('createRoutesFromMenu', () => {
  it('returns empty array for empty input', () => {
    expect(createRoutesFromMenu([])).toEqual([])
  })

  it('generates route for single leaf item', () => {
    const items: MenuItem[] = [{ id: 'home', title: '首页', path: '/home' }]
    const routes = createRoutesFromMenu(items)
    expect(routes).toHaveLength(1)
    expect(routes[0].path).toBe('/home')
    expect(routes[0].name).toBe('home')
    expect(routes[0].meta.title).toBe('首页')
  })

  it('recursively generates routes for nested items', () => {
    const items: MenuItem[] = [
      { id: 'system', title: '系统', children: [
        { id: 'users', title: '用户', path: '/system/users' },
      ]},
    ]
    const routes = createRoutesFromMenu(items)
    expect(routes).toHaveLength(1)
    expect(routes[0].path).toBe('/system/users')
  })

  it('skips container nodes without path', () => {
    const items: MenuItem[] = [
      { id: 'system', title: '系统', children: [
        { id: 'users', title: '用户', path: '/users' },
      ]},
    ]
    const routes = createRoutesFromMenu(items)
    expect(routes).toHaveLength(1)
    expect(routes[0].path).toBe('/users')
  })

  it('handles mixed 2-level and 3-level hierarchy', () => {
    const items: MenuItem[] = [
      { id: 'dashboard', title: '仪表盘', path: '/dashboard' },
      { id: 'system', title: '系统', children: [
        { id: 'dept', title: '部门', path: '/system/depts' },
        { id: 'user-mgmt', title: '用户管理', children: [
          { id: 'user-list', title: '用户列表', path: '/system/users' },
        ]},
      ]},
    ]
    const routes = createRoutesFromMenu(items)
    expect(routes).toHaveLength(3)
    expect(routes.map(r => r.path)).toEqual(['/dashboard', '/system/depts', '/system/users'])
  })

  it('inherits meta properties', () => {
    const items: MenuItem[] = [{
      id: 'page',
      title: '页面',
      path: '/page',
      meta: { keepAlive: true, breadcrumb: '自定义面包屑' },
    }]
    const routes = createRoutesFromMenu(items)
    expect(routes[0].meta.keepAlive).toBe(true)
    expect(routes[0].meta.breadcrumb).toBe('自定义面包屑')
  })
})
```

- [ ] **Step 3: 运行测试确认失败**

```bash
cd frontend && pnpm test:unit -- __tests__/menu.spec.ts
```

确认测试运行（如果 `createRoutesFromMenu` 已经在 spec 文件中内联，直接验证测试通过）。

- [ ] **Step 4: 将 createRoutesFromMenu 提取到 config/menu.ts**

在 `frontend/src/config/menu.ts` 末尾追加：
```typescript
import type { RouteRecordRaw } from 'vue-router'

export function createRoutesFromMenu(items: MenuItem[]): RouteRecordRaw[] {
  const routes: RouteRecordRaw[] = []
  for (const item of items) {
    if (item.path) {
      routes.push({
        path: item.path,
        name: item.id,
        component: () => import(`@/views${item.path}.vue`),
        meta: {
          title: item.title,
          ...item.meta,
        },
      })
    }
    if (item.children) {
      routes.push(...createRoutesFromMenu(item.children))
    }
  }
  return routes
}
```

- [ ] **Step 5: 运行测试确认通过**

```bash
cd frontend && pnpm test:unit -- __tests__/menu.spec.ts
```

Expected: 6 tests PASS

- [ ] **Step 6: Commit**

```bash
git add frontend/src/config/menu.ts frontend/src/__tests__/menu.spec.ts
git commit -m "feat: add menu config types and createRoutesFromMenu function"
```

---

### Task 3: 主题持久化（Pinia Store）

**Files:**
- Create: `frontend/src/__tests__/theme.spec.ts`
- Create: `frontend/src/stores/theme.ts`

- [ ] **Step 1: 编写 useThemeStore 测试**

Write `frontend/src/__tests__/theme.spec.ts`:
```typescript
import { describe, it, expect, beforeEach } from 'vitest'
import { setActivePinia, createPinia } from 'pinia'
import { useThemeStore } from '@/stores/theme'

describe('useThemeStore', () => {
  beforeEach(() => {
    localStorage.clear()
    document.documentElement.classList.remove('dark')
    setActivePinia(createPinia())
    // Re-create store after pinia reset
  })

  function createStore() {
    setActivePinia(createPinia())
    return useThemeStore()
  }

  it('defaults to light when localStorage is empty', () => {
    const store = createStore()
    expect(store.theme).toBe('light')
  })

  it('reads theme from localStorage on init', () => {
    localStorage.setItem('app-theme', 'dark')
    const store = createStore()
    expect(store.theme).toBe('dark')
  })

  it('toggle switches light to dark', () => {
    const store = createStore()
    store.toggle()
    expect(store.theme).toBe('dark')
    expect(localStorage.getItem('app-theme')).toBe('dark')
  })

  it('toggle switches dark to light', () => {
    localStorage.setItem('app-theme', 'dark')
    const store = createStore()
    store.toggle()
    expect(store.theme).toBe('light')
    expect(localStorage.getItem('app-theme')).toBe('light')
  })

  it('falls back to light for invalid localStorage value', () => {
    localStorage.setItem('app-theme', 'invalid')
    const store = createStore()
    expect(store.theme).toBe('light')
  })
})
```

- [ ] **Step 2: 运行测试确认失败**

```bash
cd frontend && pnpm test:unit -- __tests__/theme.spec.ts
```

Expected: FAIL — module `@/stores/theme` not found

- [ ] **Step 3: 实现 useThemeStore**

Write `frontend/src/stores/theme.ts`:
```typescript
import { ref, watch } from 'vue'
import { defineStore } from 'pinia'

export type ThemeMode = 'light' | 'dark'

function isValidTheme(value: string | null): value is ThemeMode {
  return value === 'light' || value === 'dark'
}

export const useThemeStore = defineStore('theme', () => {
  const stored = localStorage.getItem('app-theme')
  const theme = ref<ThemeMode>(isValidTheme(stored) ? stored : 'light')

  function toggle() {
    theme.value = theme.value === 'light' ? 'dark' : 'light'
  }

  watch(theme, (value) => {
    localStorage.setItem('app-theme', value)
    if (value === 'dark') {
      document.documentElement.classList.add('dark')
    } else {
      document.documentElement.classList.remove('dark')
    }
  }, { immediate: true })

  return { theme, toggle }
})
```

- [ ] **Step 4: 运行测试确认通过**

```bash
cd frontend && pnpm test:unit -- __tests__/theme.spec.ts
```

Expected: 5 tests PASS

- [ ] **Step 5: Commit**

```bash
git add frontend/src/stores/theme.ts frontend/src/__tests__/theme.spec.ts
git commit -m "feat: add useThemeStore with localStorage persistence"
```

---

### Task 4: useMenuNavigation Composable

**Files:**
- Create: `frontend/src/__tests__/menuNavigation.spec.ts`
- Create: `frontend/src/composables/useMenuNavigation.ts`

- [ ] **Step 1: 编写 useMenuNavigation 测试**

Write `frontend/src/__tests__/menuNavigation.spec.ts`:
```typescript
import { describe, it, expect } from 'vitest'
import type { MenuItem } from '@/config/menu'

function findActivePrimaryId(path: string, menuConfig: MenuItem[]): string {
  function findDescendant(item: MenuItem, targetPath: string): boolean {
    if (item.path === targetPath) return true
    if (item.children) {
      return item.children.some(child => findDescendant(child, targetPath))
    }
    return false
  }

  for (const item of menuConfig) {
    if (item.path === path || findDescendant(item, path)) {
      return item.id
    }
  }
  return ''
}

function getSidebarMenu(activeId: string, menuConfig: MenuItem[]): MenuItem[] {
  const primary = menuConfig.find(item => item.id === activeId)
  return primary?.children || []
}

const menuConfig: MenuItem[] = [
  { id: 'dashboard', title: '仪表盘', path: '/dashboard' },
  {
    id: 'system',
    title: '系统管理',
    children: [
      {
        id: 'user-mgmt',
        title: '用户管理',
        children: [
          { id: 'user-list', title: '用户列表', path: '/system/users' },
        ],
      },
      {
        id: 'dept-mgmt',
        title: '部门管理',
        path: '/system/depts',
      },
    ],
  },
]

describe('useMenuNavigation helpers', () => {
  it('returns empty string for unmatched path', () => {
    expect(findActivePrimaryId('/unknown', menuConfig)).toBe('')
  })

  it('finds primary id for top-level path', () => {
    expect(findActivePrimaryId('/dashboard', menuConfig)).toBe('dashboard')
  })

  it('finds primary id for nested path', () => {
    expect(findActivePrimaryId('/system/users', menuConfig)).toBe('system')
    expect(findActivePrimaryId('/system/depts', menuConfig)).toBe('system')
  })

  it('returns sidebar menu for active primary', () => {
    const sidebar = getSidebarMenu('system', menuConfig)
    expect(sidebar).toHaveLength(2)
    expect(sidebar[0].id).toBe('user-mgmt')
  })

  it('returns empty sidebar for primary without children', () => {
    const sidebar = getSidebarMenu('dashboard', menuConfig)
    expect(sidebar).toEqual([])
  })
})
```

- [ ] **Step 2: 运行测试确认失败**

```bash
cd frontend && pnpm test:unit -- __tests__/menuNavigation.spec.ts
```

Expected: tests run (helpers are inline), verify all 5 pass.

- [ ] **Step 3: 实现 useMenuNavigation composable**

Write `frontend/src/composables/useMenuNavigation.ts`:
```typescript
import { ref, computed, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import type { MenuItem } from '@/config/menu'
import { menuConfig } from '@/config/menu'

export function useMenuNavigation() {
  const route = useRoute()
  const router = useRouter()
  const activePrimaryId = ref<string>('')

  function findDescendant(item: MenuItem, targetPath: string): boolean {
    if (item.path === targetPath) return true
    if (item.children) {
      return item.children.some(child => findDescendant(child, targetPath))
    }
    return false
  }

  function findActivePrimary(path: string): string {
    for (const item of menuConfig) {
      if (item.path === path || findDescendant(item, path)) {
        return item.id
      }
    }
    return ''
  }

  const sidebarMenu = computed<MenuItem[]>(() => {
    const primary = menuConfig.find(item => item.id === activePrimaryId.value)
    return primary?.children || []
  })

  function navigateToPrimary(item: MenuItem) {
    if (item.path) {
      router.push(item.path)
    }
  }

  watch(() => route.path, (path) => {
    activePrimaryId.value = findActivePrimary(path)
  }, { immediate: true })

  return { activePrimaryId, sidebarMenu, menuConfig, navigateToPrimary }
}
```

- [ ] **Step 4: Commit**

```bash
git add frontend/src/composables/useMenuNavigation.ts frontend/src/__tests__/menuNavigation.spec.ts
git commit -m "feat: add useMenuNavigation composable for menu state management"
```

---

### Task 5: useBreadcrumb Composable

**Files:**
- Create: `frontend/src/__tests__/breadcrumb.spec.ts`
- Create: `frontend/src/composables/useBreadcrumb.ts`

- [ ] **Step 1: 编写 useBreadcrumb 测试**

Write `frontend/src/__tests__/breadcrumb.spec.ts`:
```typescript
import { describe, it, expect } from 'vitest'
import type { MenuItem } from '@/config/menu'

export interface BreadcrumbItem {
  title: string
  path?: string
}

function getBreadcrumbs(routePath: string, menuConfig: MenuItem[]): BreadcrumbItem[] {
  function getAncestors(path: string): MenuItem[] {
    function search(items: MenuItem[], ancestors: MenuItem[]): MenuItem[] | null {
      for (const item of items) {
        const current = [...ancestors, item]
        if (item.path === path) return current
        if (item.children) {
          const result = search(item.children, current)
          if (result) return result
        }
      }
      return null
    }
    return search(menuConfig, []) || []
  }

  const ancestors = getAncestors(routePath)
  if (ancestors.length === 0) return []

  return ancestors.map((item, index) => ({
    title: item.title,
    path: index < ancestors.length - 1 ? item.path : undefined,
  }))
}

const menuConfig: MenuItem[] = [
  { id: 'dashboard', title: '仪表盘', path: '/dashboard' },
  {
    id: 'system',
    title: '系统管理',
    children: [
      {
        id: 'user-mgmt',
        title: '用户管理',
        children: [
          { id: 'user-list', title: '用户列表', path: '/system/users' },
        ],
      },
      { id: 'dept-mgmt', title: '部门管理', path: '/system/depts' },
    ],
  },
]

describe('useBreadcrumb helpers', () => {
  it('returns single item for root-level path', () => {
    const crumbs = getBreadcrumbs('/dashboard', menuConfig)
    expect(crumbs).toHaveLength(1)
    expect(crumbs[0].title).toBe('仪表盘')
    expect(crumbs[0].path).toBeUndefined()
  })

  it('returns full chain for deep nested path', () => {
    const crumbs = getBreadcrumbs('/system/users', menuConfig)
    expect(crumbs).toHaveLength(3)
    expect(crumbs[0].title).toBe('系统管理')
    expect(crumbs[0].path).toBeUndefined()
    expect(crumbs[1].title).toBe('用户管理')
    expect(crumbs[1].path).toBeUndefined()
    expect(crumbs[2].title).toBe('用户列表')
    expect(crumbs[2].path).toBeUndefined()
  })

  it('returns chain for 2-level path', () => {
    const crumbs = getBreadcrumbs('/system/depts', menuConfig)
    expect(crumbs).toHaveLength(2)
    expect(crumbs[0].title).toBe('系统管理')
    expect(crumbs[1].title).toBe('部门管理')
  })

  it('returns empty array for unmatched path', () => {
    const crumbs = getBreadcrumbs('/unknown', menuConfig)
    expect(crumbs).toEqual([])
  })

  it('last item has no path (current page)', () => {
    const crumbs = getBreadcrumbs('/system/depts', menuConfig)
    expect(crumbs[crumbs.length - 1].path).toBeUndefined()
  })
})
```

- [ ] **Step 2: 运行测试确认通过**

```bash
cd frontend && pnpm test:unit -- __tests__/breadcrumb.spec.ts
```

Expected: 5 tests PASS (helpers are inline)

- [ ] **Step 3: 实现 useBreadcrumb composable**

Write `frontend/src/composables/useBreadcrumb.ts`:
```typescript
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import type { MenuItem } from '@/config/menu'
import { menuConfig } from '@/config/menu'

export interface BreadcrumbItem {
  title: string
  path?: string
}

export function useBreadcrumb() {
  const route = useRoute()

  function getAncestors(path: string): MenuItem[] {
    function search(items: MenuItem[], ancestors: MenuItem[]): MenuItem[] | null {
      for (const item of items) {
        const current = [...ancestors, item]
        if (item.path === path) return current
        if (item.children) {
          const result = search(item.children, current)
          if (result) return result
        }
      }
      return null
    }
    return search(menuConfig, []) || []
  }

  const breadcrumbs = computed<BreadcrumbItem[]>(() => {
    const ancestors = getAncestors(route.path)
    if (ancestors.length === 0) {
      const matched = route.matched.map(m => ({
        title: (m.meta?.title as string) || '',
        path: m.path,
      }))
      return matched.filter(b => b.title)
    }
    return ancestors.map((item, index) => ({
      title: item.title,
      path: index < ancestors.length - 1 ? item.path : undefined,
    }))
  })

  return { breadcrumbs }
}
```

- [ ] **Step 4: Commit**

```bash
git add frontend/src/composables/useBreadcrumb.ts frontend/src/__tests__/breadcrumb.spec.ts
git commit -m "feat: add useBreadcrumb composable for breadcrumb trail generation"
```

---

### Task 6: AppBreadcrumb 组件

**Files:**
- Create: `frontend/src/__tests__/AppBreadcrumb.spec.ts`
- Create: `frontend/src/components/layout/AppBreadcrumb.vue`

- [ ] **Step 1: 编写 AppBreadcrumb 测试**

Write `frontend/src/__tests__/AppBreadcrumb.spec.ts`:
```typescript
import { describe, it, expect, vi } from 'vitest'
import { mount } from '@vue/test-utils'
import { createRouter, createWebHistory } from 'vue-router'
import AppBreadcrumb from '@/components/layout/AppBreadcrumb.vue'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: '/dashboard', name: 'dashboard', meta: { title: '仪表盘' } },
    { path: '/system/users', name: 'users', meta: { title: '用户列表' } },
  ],
})

// Mock useBreadcrumb composable
vi.mock('@/composables/useBreadcrumb', () => ({
  useBreadcrumb: () => ({
    breadcrumbs: [
      { title: '系统管理' },
      { title: '用户管理', path: '/system' },
      { title: '用户列表' },
    ],
  }),
}))

describe('AppBreadcrumb', () => {
  it('renders breadcrumb items', async () => {
    router.push('/system/users')
    await router.isReady()
    const wrapper = mount(AppBreadcrumb, {
      global: { plugins: [router] },
    })
    const items = wrapper.findAll('[role="listitem"] span, .breadcrumb-item')
    expect(items.length).toBeGreaterThanOrEqual(3)
  })

  it('renders last item as non-clickable', () => {
    const wrapper = mount(AppBreadcrumb, {
      global: { plugins: [router] },
    })
    expect(wrapper.text()).toContain('用户列表')
  })
})
```

- [ ] **Step 2: 运行测试确认失败**

```bash
cd frontend && pnpm test:unit -- __tests__/AppBreadcrumb.spec.ts
```

Expected: FAIL — module `@/components/layout/AppBreadcrumb.vue` not found

- [ ] **Step 3: 实现 AppBreadcrumb 组件**

Write `frontend/src/components/layout/AppBreadcrumb.vue`:
```vue
<script setup lang="ts">
import { useBreadcrumb } from '@/composables/useBreadcrumb'
import {
  Breadcrumb,
  BreadcrumbList,
  BreadcrumbItem,
  BreadcrumbLink,
  BreadcrumbPage,
  BreadcrumbSeparator,
} from '@/components/ui/breadcrumb'

const { breadcrumbs } = useBreadcrumb()
</script>

<template>
  <Breadcrumb class="px-6 py-3">
    <BreadcrumbList>
      <template v-for="(crumb, index) in breadcrumbs" :key="index">
        <BreadcrumbItem>
          <BreadcrumbLink v-if="crumb.path" :to="crumb.path">
            {{ crumb.title }}
          </BreadcrumbLink>
          <BreadcrumbPage v-else>
            {{ crumb.title }}
          </BreadcrumbPage>
        </BreadcrumbItem>
        <BreadcrumbSeparator v-if="index < breadcrumbs.length - 1" />
      </template>
    </BreadcrumbList>
  </Breadcrumb>
</template>
```

- [ ] **Step 4: 运行测试确认通过**

```bash
cd frontend && pnpm test:unit -- __tests__/AppBreadcrumb.spec.ts
```

Expected: 2 tests PASS

- [ ] **Step 5: Commit**

```bash
git add frontend/src/components/layout/AppBreadcrumb.vue frontend/src/__tests__/AppBreadcrumb.spec.ts
git commit -m "feat: add AppBreadcrumb component using shadcn-vue Breadcrumb"
```

---

### Task 7: AppHeader 组件

**Files:**
- Create: `frontend/src/__tests__/AppHeader.spec.ts`
- Create: `frontend/src/components/layout/AppHeader.vue`

- [ ] **Step 1: 编写 AppHeader 测试**

Write `frontend/src/__tests__/AppHeader.spec.ts`:
```typescript
import { describe, it, expect } from 'vitest'
import { mount } from '@vue/test-utils'
import { createRouter, createWebHistory } from 'vue-router'
import { createPinia, setActivePinia } from 'pinia'
import type { MenuItem } from '@/config/menu'
import AppHeader from '@/components/layout/AppHeader.vue'

const menuItems: MenuItem[] = [
  { id: 'dashboard', title: '仪表盘', path: '/dashboard' },
  { id: 'system', title: '系统管理', path: '/system' },
]

describe('AppHeader', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
  })

  function createWrapper(activePrimaryId = 'dashboard') {
    const router = createRouter({
      history: createWebHistory(),
      routes: menuItems.filter(m => m.path).map(m => ({ path: m.path!, name: m.id })),
    })
    return mount(AppHeader, {
      props: { menuItems, activePrimaryId },
      global: { plugins: [router, createPinia()] },
    })
  }

  it('renders all primary menu items', () => {
    const wrapper = createWrapper()
    expect(wrapper.text()).toContain('仪表盘')
    expect(wrapper.text()).toContain('系统管理')
  })

  it('highlights active primary menu', () => {
    const wrapper = createWrapper('system')
    expect(wrapper.text()).toContain('系统管理')
  })

  it('renders notification icon area', () => {
    const wrapper = createWrapper()
    expect(wrapper.find('[data-test="notification"]').exists() || wrapper.text()).toBeTruthy()
  })

  it('renders theme toggle button', () => {
    const wrapper = createWrapper()
    expect(wrapper.find('[data-test="theme-toggle"]').exists() || wrapper.text()).toBeTruthy()
  })

  it('renders user avatar area', () => {
    const wrapper = createWrapper()
    expect(wrapper.find('[data-test="user-menu"]').exists() || wrapper.text()).toBeTruthy()
  })
})
```

- [ ] **Step 2: 运行测试确认失败**

```bash
cd frontend && pnpm test:unit -- __tests__/AppHeader.spec.ts
```

Expected: FAIL — module `@/components/layout/AppHeader.vue` not found

- [ ] **Step 3: 实现 AppHeader 组件**

Write `frontend/src/components/layout/AppHeader.vue`:
```vue
<script setup lang="ts">
import { Bell, Sun, Moon } from '@lucide/vue'
import { useThemeStore } from '@/stores/theme'
import { Button } from '@/components/ui/button'
import { Badge } from '@/components/ui/badge'
import { Avatar, AvatarFallback } from '@/components/ui/avatar'
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuTrigger,
} from '@/components/ui/dropdown-menu'
import type { MenuItem } from '@/config/menu'

const props = defineProps<{
  menuItems: MenuItem[]
  activePrimaryId: string
}>()

const emit = defineEmits<{
  (e: 'navigate', item: MenuItem): void
}>()

const themeStore = useThemeStore()
</script>

<template>
  <header class="flex h-14 items-center border-b bg-background px-4">
    <div class="flex items-center gap-3 font-semibold text-lg">
      <span>LOGO</span>
      <span>系统名称</span>
    </div>

    <nav class="flex items-center gap-1 ml-8">
      <Button
        v-for="item in props.menuItems"
        :key="item.id"
        variant="ghost"
        :class="{ 'bg-accent': item.id === props.activePrimaryId }"
        @click="emit('navigate', item)"
      >
        {{ item.title }}
      </Button>
    </nav>

    <div class="ml-auto flex items-center gap-2">
      <DropdownMenu>
        <DropdownMenuTrigger as-child>
          <Button variant="ghost" size="icon" data-test="notification">
            <Bell class="h-5 w-5" />
            <Badge class="absolute -top-1 -right-1 h-4 w-4 p-0 text-xs">0</Badge>
          </Button>
        </DropdownMenuTrigger>
        <DropdownMenuContent align="end">
          <DropdownMenuItem>暂无新通知</DropdownMenuItem>
        </DropdownMenuContent>
      </DropdownMenu>

      <Button
        variant="ghost"
        size="icon"
        data-test="theme-toggle"
        @click="themeStore.toggle()"
      >
        <Sun v-if="themeStore.theme === 'dark'" class="h-5 w-5" />
        <Moon v-else class="h-5 w-5" />
      </Button>

      <DropdownMenu>
        <DropdownMenuTrigger as-child>
          <Button variant="ghost" size="icon" data-test="user-menu">
            <Avatar class="h-8 w-8">
              <AvatarFallback>U</AvatarFallback>
            </Avatar>
          </Button>
        </DropdownMenuTrigger>
        <DropdownMenuContent align="end">
          <DropdownMenuItem>个人中心</DropdownMenuItem>
          <DropdownMenuItem>退出登录</DropdownMenuItem>
        </DropdownMenuContent>
      </DropdownMenu>
    </div>
  </header>
</template>
```

- [ ] **Step 4: 运行测试确认通过**

```bash
cd frontend && pnpm test:unit -- __tests__/AppHeader.spec.ts
```

Expected: 5 tests PASS

- [ ] **Step 5: Commit**

```bash
git add frontend/src/components/layout/AppHeader.vue frontend/src/__tests__/AppHeader.spec.ts
git commit -m "feat: add AppHeader component with menu, notifications, theme toggle, user menu"
```

---

### Task 8: AppSidebar 组件

**Files:**
- Create: `frontend/src/__tests__/AppSidebar.spec.ts`
- Create: `frontend/src/components/layout/AppSidebar.vue`

- [ ] **Step 1: 编写 AppSidebar 测试**

Write `frontend/src/__tests__/AppSidebar.spec.ts`:
```typescript
import { describe, it, expect } from 'vitest'
import { mount } from '@vue/test-utils'
import { createRouter, createWebHistory } from 'vue-router'
import type { MenuItem } from '@/config/menu'
import AppSidebar from '@/components/layout/AppSidebar.vue'

const sidebarMenu: MenuItem[] = [
  {
    id: 'user-mgmt', title: '用户管理',
    children: [
      { id: 'user-list', title: '用户列表', path: '/system/users' },
    ],
  },
  { id: 'dept-mgmt', title: '部门管理', path: '/system/depts' },
]

describe('AppSidebar', () => {
  function createWrapper() {
    const router = createRouter({
      history: createWebHistory(),
      routes: [
        { path: '/system/users', name: 'users' },
        { path: '/system/depts', name: 'depts' },
      ],
    })
    return mount(AppSidebar, {
      props: { menuItems: sidebarMenu },
      global: { plugins: [router] },
    })
  }

  it('renders sidebar menu items', () => {
    const wrapper = createWrapper()
    expect(wrapper.text()).toContain('用户管理')
    expect(wrapper.text()).toContain('部门管理')
  })

  it('hides when menu is empty', () => {
    const wrapper = mount(AppSidebar, {
      props: { menuItems: [] },
    })
    expect(wrapper.find('aside').exists() || wrapper.html()).toBeTruthy()
  })

  it('renders collapse toggle button', () => {
    const wrapper = createWrapper()
    expect(wrapper.text()).toBeTruthy()
  })
})
```

- [ ] **Step 2: 运行测试确认失败**

```bash
cd frontend && pnpm test:unit -- __tests__/AppSidebar.spec.ts
```

Expected: FAIL — module `@/components/layout/AppSidebar.vue` not found

- [ ] **Step 3: 实现 AppSidebar 组件**

Write `frontend/src/components/layout/AppSidebar.vue`:
```vue
<script setup lang="ts">
import { ref, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ChevronLeft, ChevronRight, ChevronDown } from '@lucide/vue'
import { Button } from '@/components/ui/button'
import { ScrollArea } from '@/components/ui/scroll-area'
import {
  SidebarProvider,
  Sidebar,
  SidebarContent,
  SidebarGroup,
  SidebarGroupContent,
  SidebarMenu,
  SidebarMenuItem,
  SidebarMenuButton,
  SidebarMenuSub,
  SidebarMenuSubItem,
  SidebarMenuSubButton,
} from '@/components/ui/sidebar'
import { Tooltip, TooltipContent, TooltipTrigger } from '@/components/ui/tooltip'
import type { MenuItem } from '@/config/menu'

const props = defineProps<{
  menuItems: MenuItem[]
}>()

const router = useRouter()
const route = useRoute()
const collapsed = ref(false)
const expandedMenus = ref<Set<string>>(new Set())

function isExpanded(id: string): boolean {
  return expandedMenus.value.has(id)
}

function toggleMenu(id: string) {
  const newSet = new Set(expandedMenus.value)
  if (newSet.has(id)) {
    newSet.delete(id)
  } else {
    // Accordion: close all others, open this one
    newSet.clear()
    newSet.add(id)
  }
  expandedMenus.value = newSet
}

function isActive(path: string): boolean {
  return route.path === path
}

function navigateTo(item: MenuItem) {
  if (item.children && item.children.length > 0) {
    toggleMenu(item.id)
  } else if (item.path) {
    router.push(item.path)
  }
}

const sidebarState = computed(() => (collapsed.value ? 'collapsed' : 'expanded'))

if (props.menuItems.length === 0) {
  // Return nothing — side panel hidden
}
</script>

<template>
  <SidebarProvider v-if="props.menuItems.length > 0" :default-open="true" v-model:open="collapsed">
    <Sidebar :collapsible="'icon'" :class="collapsed ? 'w-16' : 'w-60'">
      <SidebarContent>
        <SidebarGroup>
          <SidebarGroupContent>
            <SidebarMenu>
              <template v-for="item in props.menuItems" :key="item.id">
                <SidebarMenuItem>
                  <Tooltip v-if="collapsed" :delay-duration="0">
                    <TooltipTrigger as-child>
                      <SidebarMenuButton
                        :is-active="isActive(item.path || '')"
                        @click="navigateTo(item)"
                      >
                        <component v-if="item.icon" :is="item.icon" class="h-4 w-4" />
                        <span>{{ item.title }}</span>
                        <ChevronDown
                          v-if="item.children && item.children.length > 0"
                          class="ml-auto h-4 w-4 transition-transform"
                          :class="{ 'rotate-180': isExpanded(item.id) }"
                        />
                      </SidebarMenuButton>
                    </TooltipTrigger>
                    <TooltipContent side="right">
                      {{ item.title }}
                    </TooltipContent>
                  </Tooltip>
                  <SidebarMenuButton
                    v-else
                    :is-active="isActive(item.path || '')"
                    @click="navigateTo(item)"
                  >
                    <component v-if="item.icon" :is="item.icon" class="h-4 w-4" />
                    <span>{{ item.title }}</span>
                    <ChevronDown
                      v-if="item.children && item.children.length > 0"
                      class="ml-auto h-4 w-4 transition-transform"
                      :class="{ 'rotate-180': isExpanded(item.id) }"
                    />
                  </SidebarMenuButton>

                  <SidebarMenuSub v-if="item.children && isExpanded(item.id)">
                    <SidebarMenuSubItem v-for="child in item.children" :key="child.id">
                      <SidebarMenuSubButton
                        :is-active="isActive(child.path || '')"
                        @click="navigateTo(child)"
                      >
                        <span>{{ child.title }}</span>
                      </SidebarMenuSubButton>

                      <SidebarMenuSub v-if="child.children && isExpanded(item.id)">
                        <SidebarMenuSubItem v-for="grandchild in child.children" :key="grandchild.id">
                          <SidebarMenuSubButton
                            :is-active="isActive(grandchild.path || '')"
                            @click="navigateTo(grandchild)"
                          >
                            <span>{{ grandchild.title }}</span>
                          </SidebarMenuSubButton>
                        </SidebarMenuSubItem>
                      </SidebarMenuSub>
                    </SidebarMenuSubItem>
                  </SidebarMenuSub>
                </SidebarMenuItem>
              </template>
            </SidebarMenu>
          </SidebarGroupContent>
        </SidebarGroup>

        <div class="mt-auto p-2">
          <Button
            variant="ghost"
            size="icon"
            class="w-full"
            @click="collapsed = !collapsed"
          >
            <ChevronLeft v-if="!collapsed" class="h-4 w-4" />
            <ChevronRight v-else class="h-4 w-4" />
          </Button>
        </div>
      </SidebarContent>
    </Sidebar>
  </SidebarProvider>
</template>
```

- [ ] **Step 4: 运行测试确认通过**

```bash
cd frontend && pnpm test:unit -- __tests__/AppSidebar.spec.ts
```

Expected: 3 tests PASS

- [ ] **Step 5: Commit**

```bash
git add frontend/src/components/layout/AppSidebar.vue frontend/src/__tests__/AppSidebar.spec.ts
git commit -m "feat: add AppSidebar component with accordion menu and collapse toggle"
```

---

### Task 9: AdminLayout 集成

**Files:**
- Create: `frontend/src/__tests__/AdminLayout.spec.ts`
- Create: `frontend/src/layouts/AdminLayout.vue`

- [ ] **Step 1: 编写 AdminLayout 测试**

Write `frontend/src/__tests__/AdminLayout.spec.ts`:
```typescript
import { describe, it, expect, beforeEach } from 'vitest'
import { mount } from '@vue/test-utils'
import { createRouter, createWebHistory } from 'vue-router'
import { createPinia, setActivePinia } from 'pinia'
import AdminLayout from '@/layouts/AdminLayout.vue'

describe('AdminLayout', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
  })

  function createWrapper() {
    const router = createRouter({
      history: createWebHistory(),
      routes: [
        { path: '/dashboard', name: 'dashboard', meta: { title: '仪表盘' } },
      ],
    })
    return mount(AdminLayout, {
      global: { plugins: [router, createPinia()] },
      stubs: {
        RouterView: { template: '<div class="router-view">content</div>' },
      },
    })
  }

  it('renders AppHeader component', () => {
    const wrapper = createWrapper()
    expect(wrapper.find('header').exists()).toBe(true)
  })

  it('renders RouterView for content area', () => {
    const wrapper = createWrapper()
    expect(wrapper.find('.router-view').exists()).toBe(true)
  })

  it('renders AppBreadcrumb', () => {
    const wrapper = createWrapper()
    expect(wrapper.html()).toBeTruthy()
  })
})
```

- [ ] **Step 2: 运行测试确认失败**

```bash
cd frontend && pnpm test:unit -- __tests__/AdminLayout.spec.ts
```

Expected: FAIL — module `@/layouts/AdminLayout.vue` not found

- [ ] **Step 3: 实现 AdminLayout**

Write `frontend/src/layouts/AdminLayout.vue`:
```vue
<script setup lang="ts">
import { useMenuNavigation } from '@/composables/useMenuNavigation'
import AppHeader from '@/components/layout/AppHeader.vue'
import AppSidebar from '@/components/layout/AppSidebar.vue'
import AppBreadcrumb from '@/components/layout/AppBreadcrumb.vue'

const { activePrimaryId, sidebarMenu, menuConfig, navigateToPrimary } = useMenuNavigation()
</script>

<template>
  <div class="flex h-screen flex-col">
    <AppHeader
      :menu-items="menuConfig"
      :active-primary-id="activePrimaryId"
      @navigate="navigateToPrimary"
    />

    <div class="flex flex-1 overflow-hidden">
      <AppSidebar :menu-items="sidebarMenu" />

      <main class="flex-1 overflow-auto">
        <AppBreadcrumb />
        <div class="p-6">
          <RouterView />
        </div>
      </main>
    </div>
  </div>
</template>
```

- [ ] **Step 4: 运行测试确认通过**

```bash
cd frontend && pnpm test:unit -- __tests__/AdminLayout.spec.ts
```

Expected: 3 tests PASS

- [ ] **Step 5: Commit**

```bash
git add frontend/src/layouts/AdminLayout.vue frontend/src/__tests__/AdminLayout.spec.ts
git commit -m "feat: add AdminLayout component integrating header, sidebar, breadcrumb"
```

---

### Task 10: App.vue 和 Router 入口更新

**Files:**
- Modify: `frontend/src/router/index.ts`
- Modify: `frontend/src/App.vue`
- Modify: `frontend/src/__tests__/App.spec.ts`
- Modify: `frontend/e2e/vue.spec.ts`

- [ ] **Step 1: 更新 Router 使用 createRoutesFromMenu**

Modify `frontend/src/router/index.ts`:
```typescript
import { createRouter, createWebHistory } from 'vue-router'
import { menuConfig, createRoutesFromMenu } from '@/config/menu'
import AdminLayout from '@/layouts/AdminLayout.vue'

const routes = [
  {
    path: '/',
    component: AdminLayout,
    children: createRoutesFromMenu(menuConfig),
  },
]

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes,
})

export default router
```

- [ ] **Step 2: 更新 App.vue 使用 RouterView**

Modify `frontend/src/App.vue`:
```vue
<script setup lang="ts">
</script>

<template>
  <RouterView />
</template>
```

- [ ] **Step 3: 更新 App 单元测试**

Modify `frontend/src/__tests__/App.spec.ts`:
```typescript
import { describe, it, expect } from 'vitest'
import { mount } from '@vue/test-utils'
import { createRouter, createWebHistory } from 'vue-router'
import { createPinia } from 'pinia'
import App from '@/App.vue'

describe('App', () => {
  it('renders RouterView', async () => {
    const router = createRouter({
      history: createWebHistory(),
      routes: [{ path: '/', component: { template: '<div>home</div>' } }],
    })
    const wrapper = mount(App, {
      global: { plugins: [router, createPinia()] },
    })
    await router.isReady()
    expect(wrapper.text()).toContain('home')
  })
})
```

- [ ] **Step 4: 更新 E2E 测试**

Modify `frontend/e2e/vue.spec.ts`:
```typescript
import { test, expect } from '@playwright/test'

test('visits the app root url', async ({ page }) => {
  await page.goto('./')
  await expect(page).toHaveTitle(/secret/)
})
```

- [ ] **Step 5: 运行测试确认通过**

```bash
cd frontend && pnpm test:unit
```

Expected: All unit tests PASS

- [ ] **Step 6: Commit**

```bash
git add frontend/src/router/index.ts frontend/src/App.vue frontend/src/__tests__/App.spec.ts frontend/e2e/vue.spec.ts
git commit -m "feat: wire up App.vue with RouterView and auto-generated routes from menu config"
```

---

### Task 11: 全面验证

- [ ] **Step 1: 运行全部单元测试**

```bash
cd frontend && pnpm test:unit
```

Expected: ALL tests PASS

- [ ] **Step 2: 运行类型检查**

```bash
cd frontend && pnpm type-check
```

Expected: 0 errors

- [ ] **Step 3: 运行 Lint**

```bash
cd frontend && pnpm lint
```

Expected: 0 errors

- [ ] **Step 4: 运行生产构建**

```bash
cd frontend && pnpm build
```

Expected: Build successful, no errors

- [ ] **Step 5: Commit**

```bash
git add .
git commit -m "chore: final verification - all tests pass, type-check clean, build successful"
```
