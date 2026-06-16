<script setup lang="ts">
defineOptions({ name: 'RoleAuthIndex' })

import { ref, computed, onMounted } from 'vue'
import { Input } from '@/components/ui/input'
import { Checkbox } from '@/components/ui/checkbox'
import { Button } from '@/components/ui/button'
import { Search, Save, Eye, Check, Minus } from '@lucide/vue'
import { fetchRoles } from '@/api/role'
import { fetchMenuTree } from '@/api/menu'
import { fetchAuthMenuIds, saveAuthMenu } from '@/api/auth'
import MenuTreeCheckbox from './MenuTreeCheckbox.vue'
import type { MenuDTO } from '@/api/menu'
import type { RoleDTO } from '@/api/role'

const roles = ref<RoleDTO[]>([])
const searchQuery = ref('')
const selectedRoles = ref<string[]>([])
const menuTree = ref<MenuDTO[]>([])
const checkedMenuIds = ref(new Set<string>())
const saving = ref(false)
const viewingId = ref<string | null>(null)

const filteredRoles = computed(() => {
  if (!searchQuery.value) return roles.value
  const q = searchQuery.value.toLowerCase()
  return roles.value.filter((r) => r.roleName?.toLowerCase().includes(q))
})

const allRolesSelected = computed<boolean | 'indeterminate'>(() => {
  if (filteredRoles.value.length === 0) return false
  const n = filteredRoles.value.filter((r) => selectedRoles.value.includes(r.roleId)).length
  if (n === filteredRoles.value.length) return true
  if (n > 0) return 'indeterminate'
  return false
})

function toggleAllRoles() {
  if (allRolesSelected.value) { selectedRoles.value = [] }
  else { selectedRoles.value = filteredRoles.value.map((r) => r.roleId) }
}

function toggleRole(roleId: string) {
  const idx = selectedRoles.value.indexOf(roleId)
  if (idx >= 0) { selectedRoles.value.splice(idx, 1) } else { selectedRoles.value.push(roleId) }
}

function findMenuById(nodes: MenuDTO[], id: string): MenuDTO | null {
  for (const node of nodes) {
    if (node.menuId === id) return node
    if (node.children) { const f = findMenuById(node.children, id); if (f) return f }
  }
  return null
}

function collectMenuDescendants(node: MenuDTO): string[] {
  const ids = [node.menuId]
  if (node.children) { for (const c of node.children) ids.push(...collectMenuDescendants(c)) }
  return ids
}

function autoSelectParentMenus() {
  let changed = true
  while (changed) {
    changed = false
    const walk = (nodes: MenuDTO[]) => {
      for (const node of nodes) {
        if (node.children?.length) {
          walk(node.children)
          const all = node.children.every((c) => checkedMenuIds.value.has(c.menuId))
          if (all && !checkedMenuIds.value.has(node.menuId)) { checkedMenuIds.value = new Set([...checkedMenuIds.value, node.menuId]); changed = true }
          else if (!all && checkedMenuIds.value.has(node.menuId)) { const next = new Set(checkedMenuIds.value); next.delete(node.menuId); checkedMenuIds.value = next; changed = true }
        }
      }
    }
    walk(menuTree.value)
  }
}

function handleMenuToggle(menuId: string) {
  const menu = findMenuById(menuTree.value, menuId)
  const affected = menu ? collectMenuDescendants(menu) : [menuId]
  if (checkedMenuIds.value.has(menuId)) {
    const next = new Set(checkedMenuIds.value); for (const id of affected) next.delete(id); checkedMenuIds.value = next
  } else {
    const next = new Set(checkedMenuIds.value); for (const id of affected) next.add(id); checkedMenuIds.value = next
  }
  autoSelectParentMenus()
}

async function handleViewRole(roleId: string) {
  viewingId.value = roleId
  checkedMenuIds.value = new Set(await fetchAuthMenuIds('role', roleId))
}

async function handleSave() {
  if (saving.value) return
  const lastRole = selectedRoles.value[selectedRoles.value.length - 1]
  saving.value = true
  try {
    await saveAuthMenu('role', selectedRoles.value, Array.from(checkedMenuIds.value))
    selectedRoles.value = []
    if (lastRole) { viewingId.value = lastRole; await handleViewRole(lastRole) }
  } finally { saving.value = false }
}

onMounted(async () => {
  const [roleData, menuData] = await Promise.all([fetchRoles({ page: 1, size: 1000 }), fetchMenuTree()])
  roles.value = roleData.records ?? roleData ?? []
  menuTree.value = menuData
})
</script>

<template>
  <div class="flex h-full gap-4">
    <div class="w-80 shrink-0 rounded-lg border flex flex-col overflow-hidden">
      <div class="px-3 py-2 border-b shrink-0">
        <div class="flex items-center gap-2">
          <Search class="size-4 text-muted-foreground shrink-0" />
          <Input v-model="searchQuery" placeholder="搜索角色..." class="h-8 text-sm" />
        </div>
      </div>
      <div class="px-3 py-1.5 border-b shrink-0">
        <div class="flex items-center gap-2">
          <Checkbox :model-value="allRolesSelected" aria-label="全选角色" @update:model-value="toggleAllRoles" class="shrink-0 data-[state=indeterminate]:bg-primary data-[state=indeterminate]:text-primary-foreground data-[state=indeterminate]:border-primary">
            <template #default><Check v-if="allRolesSelected === true" class="size-3.5" /><Minus v-else-if="allRolesSelected === 'indeterminate'" class="size-3.5" /><template v-else /></template>
          </Checkbox>
          <span class="text-xs text-muted-foreground">已选 {{ selectedRoles.length }} 个角色</span>
        </div>
      </div>
      <div class="flex-1 overflow-auto">
        <div v-if="filteredRoles.length === 0" class="px-3 py-6 text-center text-sm text-muted-foreground">{{ searchQuery ? '无匹配角色' : '暂无角色数据' }}</div>
        <div v-for="role in filteredRoles" :key="role.roleId" class="flex items-center gap-2 px-3 py-2 text-sm hover:bg-accent cursor-pointer border-b last:border-b-0" @click="toggleRole(role.roleId)">
          <Checkbox :model-value="selectedRoles.includes(role.roleId)" :aria-label="role.roleName" class="shrink-0" />
          <span class="flex-1 truncate">{{ role.roleName }}</span>
          <button class="flex size-5 shrink-0 items-center justify-center rounded transition-colors hover:bg-muted" :class="viewingId === role.roleId ? 'text-primary' : 'text-muted-foreground'" :aria-label="`查看 ${role.roleName} 授权`" @click.stop="handleViewRole(role.roleId)"><Eye class="size-3.5" /></button>
        </div>
      </div>
    </div>
    <div class="flex-1 min-w-0 rounded-lg border flex flex-col overflow-hidden">
      <div class="px-3 py-2 border-b shrink-0 flex items-center justify-between">
        <span class="text-sm font-medium">菜单授权</span>
        <span class="text-xs text-muted-foreground">已选 {{ checkedMenuIds.size }} 项</span>
      </div>
      <div class="flex-1 overflow-auto p-3">
        <MenuTreeCheckbox :items="menuTree" :checked-ids="checkedMenuIds" @toggle="handleMenuToggle" />
      </div>
      <div class="px-3 py-2 border-t shrink-0 flex justify-end">
        <Button size="sm" :disabled="selectedRoles.length === 0 || saving" @click="handleSave"><Save class="size-4 mr-1.5" />保存授权</Button>
      </div>
    </div>
  </div>
</template>
