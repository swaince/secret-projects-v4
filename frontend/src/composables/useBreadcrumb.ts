import { computed } from 'vue'
import { useRoute } from 'vue-router'
import type { MenuItem } from '@/config/menu'
import { menuConfig } from '@/config/menu'

export interface BreadcrumbItem { title: string; path?: string }

export function useBreadcrumb() {
  const route = useRoute()

  function getAncestors(path: string): MenuItem[] {
    function search(items: MenuItem[], ancestors: MenuItem[]): MenuItem[] | null {
      for (const item of items) {
        const cur = [...ancestors, item]
        if (item.path === path) return cur
        if (item.children) {
          const r = search(item.children, cur)
          if (r) return r
        }
      }
      return null
    }
    return search(menuConfig, []) || []
  }

  const breadcrumbs = computed<BreadcrumbItem[]>(() => {
    const ancestors = getAncestors(route.path)
    if (!ancestors.length) {
      return route.matched
        .map(m => ({ title: (m.meta?.title as string) || '', path: m.path }))
        .filter(b => b.title)
    }
    return ancestors.map((item, i) => ({
      title: item.title,
      path: i < ancestors.length - 1 ? item.path : undefined,
    }))
  })

  return { breadcrumbs }
}
