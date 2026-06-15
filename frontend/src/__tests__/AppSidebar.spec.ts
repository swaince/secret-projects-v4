import { describe, it, expect, vi } from 'vitest'
import { mount } from '@vue/test-utils'
import { createRouter, createWebHistory } from 'vue-router'
import { ref, computed } from 'vue'
import type { MenuItem } from '@/stores/menu'
import AppSidebar from '@/components/layout/AppSidebar.vue'

vi.mock('@/components/ui/sidebar/utils', async (importOriginal) => {
  const actual = await importOriginal<typeof import('@/components/ui/sidebar/utils')>()
  return {
    ...actual,
    useSidebar: () => ({
      state: computed(() => 'expanded' as const),
      open: ref(true),
      setOpen: vi.fn<(v: boolean) => void>(),
      isMobile: ref(false),
      openMobile: ref(false),
      setOpenMobile: vi.fn<(v: boolean) => void>(),
      toggleSidebar: vi.fn<() => void>(),
    }),
  }
})

const menu: MenuItem[] = [
  { id: 'user', title: '用户管理', children: [{ id: 'list', title: '用户列表', path: '/sys/users' }] },
  { id: 'dept', title: '部门管理', path: '/sys/depts' },
]

describe('AppSidebar', () => {
  function wrap(m: MenuItem[]) {
    const r = createRouter({ history: createWebHistory(), routes: [
      { path: '/sys/users', component: {} as never },
      { path: '/sys/depts', component: {} as never },
    ]})
    r.push('/sys/users')
    return mount(AppSidebar, {
      props: { menuItems: m },
      global: {
        plugins: [r],
        stubs: {
          SidebarProvider: { template: '<div><slot /></div>' },
          Sidebar: { template: '<div><slot /></div>' },
          SidebarContent: { template: '<div><slot /></div>' },
          SidebarFooter: { template: '<div><slot /></div>' },
          SidebarGroup: { template: '<div><slot /></div>' },
          SidebarGroupContent: { template: '<div><slot /></div>' },
          SidebarMenu: { template: '<div><slot /></div>' },
          SidebarMenuItem: { template: '<div><slot /></div>' },
          SidebarMenuButton: { template: '<button><slot /></button>', inheritAttrs: false },
          SidebarMenuSub: { template: '<div><slot /></div>' },
          SidebarMenuSubItem: { template: '<div><slot /></div>' },
          SidebarMenuSubButton: { template: '<button><slot /></button>' },
          Tooltip: { template: '<div><slot /></div>' },
          TooltipTrigger: { template: '<div><slot /></div>' },
          TooltipContent: { template: '<div><slot /></div>' },
        },
      },
    })
  }

  it('renders menu items', () => {
    const w = wrap(menu)
    expect(w.text()).toContain('用户管理')
    expect(w.text()).toContain('部门管理')
  })

  it('hides when empty', () => {
    const w = wrap([])
    expect(w.text()).not.toContain('用户管理')
  })
})
