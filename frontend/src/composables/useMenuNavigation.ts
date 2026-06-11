import { ref, computed, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import type { MenuItem } from '@/config/menu'
import { menuConfig } from '@/config/menu'

export function useMenuNavigation() {
  const route = useRoute()
  const router = useRouter()
  const activePrimaryId = ref('')

  function findDescendant(item: MenuItem, p: string): boolean {
    if (item.path === p) return true
    return item.children?.some(c => findDescendant(c, p)) ?? false
  }

  function findPrimary(path: string): string {
    for (const item of menuConfig) {
      if (item.path === path || findDescendant(item, path)) return item.id
    }
    return ''
  }

  const sidebarMenu = computed<MenuItem[]>(() =>
    menuConfig.find(i => i.id === activePrimaryId.value)?.children || [],
  )

  function firstPath(item: MenuItem): string | undefined {
    if (item.path) return item.path
    for (const child of item.children || []) {
      const p = firstPath(child)
      if (p) return p
    }
    return undefined
  }

  function navigate(item: MenuItem) {
    const p = item.path || firstPath(item)
    if (p) router.push(p)
  }

  watch(() => route.path, p => { activePrimaryId.value = findPrimary(p) }, { immediate: true })

  return { activePrimaryId, sidebarMenu, menuConfig, navigate }
}
