import { createRouter, createWebHistory } from 'vue-router'
import { menuConfig, createRoutesFromMenu } from '@/config/menu'
import AdminLayout from '@/layouts/AdminLayout.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [{ path: '/', component: AdminLayout, children: createRoutesFromMenu(menuConfig) }],
})

export default router
