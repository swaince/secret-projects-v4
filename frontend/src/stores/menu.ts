import { ref } from 'vue'
import { defineStore } from 'pinia'
import type { RouteRecordRaw } from 'vue-router'
import { fetchMenuTree } from '@/api/menu'
import type { MenuDTO } from '@/api/menu'

export interface MenuItem {
  id: string
  title: string
  icon?: string
  path?: string
  children?: MenuItem[]
  meta?: Record<string, unknown>
}

const modules = import.meta.glob('@/views/**/*.vue')

function toMenuItems(dtos: MenuDTO[]): MenuItem[] {
  return dtos
    .filter((d) => d.visible === 1 && d.menuType !== 'B')
    .map((d) => ({
      id: d.menuId,
      title: d.menuName,
      icon: d.icon || undefined,
      path: d.path || undefined,
      children: d.children?.length ? toMenuItems(d.children) : undefined,
    }))
}

function generateRoutes(menuTree: MenuDTO[]): RouteRecordRaw[] {
  const result: RouteRecordRaw[] = []
  for (const menu of menuTree) {
    if (menu.menuType === 'B') continue

    const route: RouteRecordRaw = {
      path: menu.path || '',
      name: menu.menuId,
      meta: {
        title: menu.menuName,
        icon: menu.icon,
        permission: menu.permission,
        visible: menu.visible === 1,
        requireAuth: menu.requireAuth === 1,
      },
      children: [],
    }

    if (menu.menuType === 'M' && menu.component) {
      const componentPath = `/src/views/${menu.component}.vue`
      route.component = modules[componentPath] || (() => import('@/views/PlaceholderView.vue'))
    }

    if (menu.redirect) {
      route.redirect = menu.redirect
    }

    if (menu.children && menu.children.length > 0) {
      route.children = generateRoutes(menu.children)
    }

    result.push(route)
  }
  return result
}

export const useMenuStore = defineStore('menu', () => {
  const menus = ref<MenuDTO[]>([])
  const menuItems = ref<MenuItem[]>([])
  const routes = ref<RouteRecordRaw[]>([])
  const loaded = ref(false)

  async function loadMenus() {
    if (loaded.value) return
    const tree = await fetchMenuTree()
    menus.value = tree
    menuItems.value = toMenuItems(tree)
    routes.value = generateRoutes(tree)
    loaded.value = true
  }

  function reset() {
    menus.value = []
    menuItems.value = []
    routes.value = []
    loaded.value = false
  }

  return { menus, menuItems, routes, loaded, loadMenus, reset }
})
