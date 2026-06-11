import { describe, it, expect, beforeEach } from 'vitest'
import { mount } from '@vue/test-utils'
import { createRouter, createWebHistory } from 'vue-router'
import { createPinia, setActivePinia } from 'pinia'
import AdminLayout from '@/layouts/AdminLayout.vue'

describe('AdminLayout', () => {
  beforeEach(() => { localStorage.clear(); setActivePinia(createPinia()) })

  function wrap() {
    const r = createRouter({ history: createWebHistory(), routes: [
      { path: '/dashboard', component: {} as never, meta: { title: '仪表盘' } },
    ]})
    return mount(AdminLayout, {
      global: {
        plugins: [r, createPinia()],
        stubs: {
          RouterView: { template: '<div class="router-view">content</div>' },
          AppHeader: { template: '<header>Header</header>' },
          AppSidebar: { template: '<aside>Sidebar</aside>' },
          AppBreadcrumb: { template: '<nav>Breadcrumb</nav>' },
        },
      },
    })
  }

  it('renders header', () => { expect(wrap().find('header').exists()).toBe(true) })
  it('renders RouterView', () => { expect(wrap().find('.router-view').exists()).toBe(true) })
  it('renders breadcrumb', () => { expect(wrap().find('nav').exists()).toBe(true) })
})
