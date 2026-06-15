import { createRouter, createWebHistory } from 'vue-router'
import AdminLayout from '@/layouts/AdminLayout.vue'
import { useMenuStore } from '@/stores/menu'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'layout',
      component: AdminLayout,
      children: [],
    },
  ],
})

router.beforeEach(async (to) => {
  const menuStore = useMenuStore()
  if (!menuStore.loaded) {
    await menuStore.loadMenus()
    for (const route of menuStore.routes) {
      router.addRoute('layout', route)
    }
    return to.fullPath
  }
})

export default router
