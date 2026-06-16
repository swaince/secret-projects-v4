<script setup lang="ts">
defineOptions({ name: 'DeptAuthIndex' })

import { ref, computed, onMounted } from 'vue'
import { Input } from '@/components/ui/input'
import { Checkbox } from '@/components/ui/checkbox'
import { Button } from '@/components/ui/button'
import { Search, Save, Eye, Check, Minus, ChevronRight, ChevronDown } from '@lucide/vue'
import { toast } from 'vue-sonner'
import { fetchDeptTree } from '@/api/dept'
import type { DeptDTO } from '@/api/dept'
import { fetchMenuTree } from '@/api/menu'
import type { MenuDTO } from '@/api/menu'
import { fetchAuthMenuIds, saveAuthMenu } from '@/api/auth'
import MenuTreeCheckbox from './MenuTreeCheckbox.vue'

const deptTree = ref<DeptDTO[]>([])
const menuTree = ref<MenuDTO[]>([])
const selectedDepts = ref<string[]>([])
const checkedMenuIds = ref<Set<string>>(new Set())
const searchQuery = ref('')
const saving = ref(false)
const expandedIds = ref<Set<string>>(new Set())
const viewingId = ref<string | null>(null)

interface FlatRow { id: string; name: string; depth: number; hasChildren: boolean }

const flatRows = computed(() => {
  const result: FlatRow[] = []
  const walk = (nodes: DeptDTO[], depth: number) => {
    for (const node of nodes) {
      const hasChildren = !!(node.children && node.children.length > 0)
      result.push({ id: node.deptId, name: node.deptName, depth, hasChildren })
      if (hasChildren && expandedIds.value.has(node.deptId)) { walk(node.children!, depth + 1) }
    }
  }
  walk(deptTree.value, 0)
  return result
})

const filteredRows = computed(() => {
  const q = searchQuery.value.trim().toLowerCase()
  if (!q) return flatRows.value
  return flatRows.value.filter((r) => r.name.toLowerCase().includes(q))
})

const filteredIds = computed(() => filteredRows.value.map((r) => r.id))

const selectAllState = computed<boolean | 'indeterminate'>(() => {
  const ids = filteredIds.value
  if (ids.length === 0) return false
  const n = ids.filter((id) => selectedDepts.value.includes(id)).length
  if (n === ids.length) return true
  if (n > 0) return 'indeterminate'
  return false
})

function toggleDept(id: string) {
  const i = selectedDepts.value.indexOf(id)
  if (i >= 0) selectedDepts.value.splice(i, 1)
  else selectedDepts.value.push(id)
}

function toggleSelectAll() {
  if (selectAllState.value) {
    const set = new Set(filteredIds.value)
    selectedDepts.value = selectedDepts.value.filter((id) => !set.has(id))
  } else {
    const existing = new Set(selectedDepts.value)
    for (const id of filteredIds.value) { if (!existing.has(id)) selectedDepts.value.push(id) }
  }
}

function toggleExpand(id: string) {
  const next = new Set(expandedIds.value)
  if (next.has(id)) next.delete(id)
  else next.add(id)
  expandedIds.value = next
}

function findMenuById(nodes: MenuDTO[], id: string): MenuDTO | null {
  for (const node of nodes) { if (node.menuId === id) return node; if (node.children) { const f = findMenuById(node.children, id); if (f) return f } }
  return null
}

function collectMenuDescendants(node: MenuDTO): string[] {
  const ids = [node.menuId]
  if (node.children) { for (const c of node.children) ids.push(...collectMenuDescendants(c)) }
  return ids
}

function autoSelectParentMenus() {
  let changed = true
  while (changed) { changed = false; (function walk(nodes: MenuDTO[]) { for (const node of nodes) { if (node.children?.length) { walk(node.children); const all = node.children.every((c) => checkedMenuIds.value.has(c.menuId)); if (all && !checkedMenuIds.value.has(node.menuId)) { checkedMenuIds.value = new Set([...checkedMenuIds.value, node.menuId]); changed = true } else if (!all && checkedMenuIds.value.has(node.menuId)) { const next = new Set(checkedMenuIds.value); next.delete(node.menuId); checkedMenuIds.value = next; changed = true } } } })(menuTree.value) }
}

function handleMenuToggle(menuId: string) {
  const menu = findMenuById(menuTree.value, menuId)
  const affected = menu ? collectMenuDescendants(menu) : [menuId]
  if (checkedMenuIds.value.has(menuId)) { const next = new Set(checkedMenuIds.value); for (const id of affected) next.delete(id); checkedMenuIds.value = next }
  else { const next = new Set(checkedMenuIds.value); for (const id of affected) next.add(id); checkedMenuIds.value = next }
  autoSelectParentMenus()
}

async function handleViewDept(deptId: string) {
  viewingId.value = deptId
  checkedMenuIds.value = new Set(await fetchAuthMenuIds('dept', deptId))
}

async function handleSave() {
  const lastDept = selectedDepts.value[selectedDepts.value.length - 1]
  saving.value = true
  try {
    await saveAuthMenu('dept', selectedDepts.value, Array.from(checkedMenuIds.value))
    selectedDepts.value = []
    if (lastDept) { viewingId.value = lastDept; await handleViewDept(lastDept) }
    toast.success('保存成功')
  } finally { saving.value = false }
}

onMounted(async () => {
  const [depts, menus] = await Promise.all([fetchDeptTree(), fetchMenuTree()])
  deptTree.value = depts
  menuTree.value = menus
  const ids: string[] = []; (function walk(nodes: DeptDTO[]) { for (const n of nodes) { if (n.children?.length) { ids.push(n.deptId); walk(n.children) } } })(depts)
  expandedIds.value = new Set(ids)
})
</script>

<template>
  <div class="flex h-full gap-4">
    <div class="w-80 shrink-0 rounded-lg border flex flex-col overflow-hidden">
      <div class="px-3 py-2 border-b shrink-0">
        <div class="flex items-center gap-2">
          <Search class="size-4 text-muted-foreground shrink-0" />
          <Input v-model="searchQuery" placeholder="搜索部门…" class="h-8 text-sm" />
        </div>
      </div>
      <div class="px-3 py-1.5 border-b shrink-0">
        <div class="flex items-center gap-2">
          <Checkbox :model-value="selectAllState" aria-label="全选部门" @update:model-value="toggleSelectAll" class="shrink-0 data-[state=indeterminate]:bg-primary data-[state=indeterminate]:text-primary-foreground data-[state=indeterminate]:border-primary">
            <template #default><Check v-if="selectAllState === true" class="size-3.5" /><Minus v-else-if="selectAllState === 'indeterminate'" class="size-3.5" /><template v-else /></template>
          </Checkbox>
          <span class="text-xs text-muted-foreground">已选 {{ selectedDepts.length }} 个部门</span>
        </div>
      </div>
      <div class="flex-1 overflow-auto">
        <div v-if="filteredRows.length === 0" class="px-3 py-8 text-center text-xs text-muted-foreground">暂无匹配部门</div>
        <div v-for="row in filteredRows" :key="row.id" class="flex items-center gap-2 px-3 py-1.5 text-sm hover:bg-accent/50 cursor-pointer border-b last:border-b-0" :class="viewingId === row.id && 'bg-accent'" :style="{ paddingLeft: `${row.depth * 20 + 12}px` }" @click="toggleDept(row.id)">
          <button class="flex size-5 shrink-0 items-center justify-center rounded" :class="row.hasChildren ? 'hover:bg-muted cursor-pointer' : 'invisible'" :aria-label="expandedIds.has(row.id) ? '收起' : '展开'" @click.stop="toggleExpand(row.id)">
            <ChevronDown v-if="expandedIds.has(row.id)" class="size-4" /><ChevronRight v-else class="size-4" />
          </button>
          <Checkbox :model-value="selectedDepts.includes(row.id)" :aria-label="row.name" class="shrink-0" />
          <span class="flex-1 truncate">{{ row.name }}</span>
          <button class="flex size-5 shrink-0 items-center justify-center rounded transition-colors hover:bg-muted" :class="viewingId === row.id ? 'text-primary' : 'text-muted-foreground'" :aria-label="`查看 ${row.name} 授权`" @click.stop="handleViewDept(row.id)"><Eye class="size-3.5" /></button>
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
        <Button size="sm" :disabled="selectedDepts.length === 0 || saving" @click="handleSave"><Save class="size-4 mr-1.5" />保存授权</Button>
      </div>
    </div>
  </div>
</template>
