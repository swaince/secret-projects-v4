import { describe, it, expect } from 'vitest'
import { mount } from '@vue/test-utils'
import { createRouter, createWebHistory } from 'vue-router'
import { createPinia } from 'pinia'
import App from '@/App.vue'

describe('App', () => {
  it('renders RouterView', async () => {
    const r = createRouter({ history: createWebHistory(), routes: [{ path: '/', component: { template: '<div>home</div>' } }] })
    const w = mount(App, { global: { plugins: [r, createPinia()] } })
    await r.isReady()
    expect(w.text()).toContain('home')
  })
})
