import { describe, it, expect, vi } from 'vitest'
import { mount } from '@vue/test-utils'
import { createRouter, createWebHistory } from 'vue-router'
import AppBreadcrumb from '@/components/layout/AppBreadcrumb.vue'

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
    const router = createRouter({ history: createWebHistory(), routes: [{ path: '/sys', component: {} as never }] })
    router.push('/sys')
    await router.isReady()
    const wrapper = mount(AppBreadcrumb, { global: { plugins: [router] } })
    expect(wrapper.text()).toContain('系统管理')
    expect(wrapper.text()).toContain('用户管理')
    expect(wrapper.text()).toContain('用户列表')
  })

  it('last item rendered as page', () => {
    const router = createRouter({ history: createWebHistory(), routes: [{ path: '/sys', component: {} as never }] })
    const wrapper = mount(AppBreadcrumb, { global: { plugins: [router] } })
    expect(wrapper.text()).toContain('用户列表')
  })
})
