import { describe, it, expect, beforeEach } from 'vitest'
import { mount } from '@vue/test-utils'
import { createRouter, createWebHistory } from 'vue-router'
import { createPinia, setActivePinia } from 'pinia'
import type { MenuItem } from '@/stores/menu'
import AppHeader from '@/components/layout/AppHeader.vue'

const items: MenuItem[] = [
  { id: 'dashboard', title: '仪表盘', path: '/dashboard' },
  { id: 'system', title: '系统管理', path: '/system' },
]

describe('AppHeader', () => {
  beforeEach(() => { localStorage.clear(); setActivePinia(createPinia()) })

  function wrap(activeId = 'dashboard') {
    const r = createRouter({ history: createWebHistory(), routes: [
      { path: '/dashboard', component: {} as never },
      { path: '/system', component: {} as never },
    ]})
    return mount(AppHeader, { props: { menuItems: items, activePrimaryId: activeId }, global: { plugins: [r, createPinia()] } })
  }

  it('renders primary menu items', () => {
    const w = wrap()
    expect(w.text()).toContain('仪表盘')
    expect(w.text()).toContain('系统管理')
  })

  it('emits navigate on menu click', async () => {
    const w = wrap()
    const btn = w.findAll('button').find(b => b.text().includes('系统管理'))
    expect(btn).toBeDefined()
    await btn!.trigger('click')
    expect(w.emitted('navigate')).toBeTruthy()
  })

  it('has notification button', () => { expect(wrap().find('[data-test="notification"]').exists()).toBe(true) })
  it('has theme toggle', () => { expect(wrap().find('[data-test="theme-toggle"]').exists()).toBe(true) })
  it('has user menu', () => { expect(wrap().find('[data-test="user-menu"]').exists()).toBe(true) })
})
