<script setup lang="ts">
defineOptions({ name: 'MenuTreeCheckbox' })

import { ref, computed, watch } from 'vue'
import { Checkbox } from '@/components/ui/checkbox'
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from '@/components/ui/table'
import { Check, Minus, ChevronRight, ChevronDown, ChevronsUpDown } from '@lucide/vue'
import type { MenuDTO } from '@/api/menu'

const props = defineProps<{
  items: MenuDTO[]
  checkedIds: Set<string>
}>()

const emit = defineEmits<{
  toggle: [menuId: string]
}>()

interface FlatMenuItem {
  id: string
  name: string
  type: string
  depth: number
  hasChildren: boolean
  buttons: MenuDTO[]
  allChildIds: string[]
}

const expandedIds = ref<Set<string>>(new Set())

const flatMenu = computed(() => {
  const result: FlatMenuItem[] = []
  function walk(nodes: MenuDTO[], depth: number) {
    for (const node of nodes) {
      if (node.menuType === 'B') continue
      const children = node.children ?? []
      const buttons = children.filter((c) => c.menuType === 'B')
      const subMenus = children.filter((c) => c.menuType !== 'B')
      result.push({
        id: node.menuId,
        name: node.menuName,
        type: node.menuType,
        depth,
        hasChildren: subMenus.length > 0,
        buttons,
        allChildIds: children.map((c) => c.menuId),
      })
      if (subMenus.length > 0 && expandedIds.value.has(node.menuId)) {
        walk(subMenus, depth + 1)
      }
    }
  }
  walk(props.items, 0)
  return result
})

function isIndeterminate(item: FlatMenuItem): boolean {
  if (props.checkedIds.has(item.id)) return false
  return item.allChildIds.some((id) => props.checkedIds.has(id))
}

function handleRowToggle(item: FlatMenuItem) {
  emit('toggle', item.id)
}

function handleButtonToggle(menuId: string) {
  emit('toggle', menuId)
}

function toggleExpand(menuId: string) {
  const next = new Set(expandedIds.value)
  if (next.has(menuId)) next.delete(menuId)
  else next.add(menuId)
  expandedIds.value = next
}

function collectExpandableIds(nodes: MenuDTO[]): string[] {
  const ids: string[] = []
  for (const node of nodes) {
    if (node.menuType === 'B') continue
    const subMenus = (node.children ?? []).filter((c) => c.menuType !== 'B')
    if (subMenus.length > 0) ids.push(node.menuId)
    ids.push(...collectExpandableIds(subMenus))
  }
  return ids
}

watch(() => props.items, (items) => {
  expandedIds.value = new Set(collectExpandableIds(items))
}, { immediate: true })

function toggleAllExpand() {
  const ids = collectExpandableIds(props.items)
  if (ids.length > 0 && ids.every((id) => expandedIds.value.has(id))) {
    expandedIds.value = new Set()
  } else {
    expandedIds.value = new Set(ids)
  }
}
</script>

<template>
  <Table>
    <TableHeader>
      <TableRow>
        <TableHead class="w-[30%] border-r">
          <div class="flex items-center">
            <span class="flex-1">菜单名称</span>
            <button class="mr-2 flex size-5 shrink-0 items-center justify-center rounded hover:bg-muted cursor-pointer" aria-label="批量展开折叠" @click="toggleAllExpand">
              <ChevronsUpDown class="size-4 text-muted-foreground" />
            </button>
          </div>
        </TableHead>
        <TableHead class="w-[70%] text-center">按钮权限</TableHead>
      </TableRow>
    </TableHeader>
    <TableBody>
      <TableRow v-for="row in flatMenu" :key="row.id" class="hover:bg-muted/50">
        <TableCell class="border-r">
          <div class="flex items-center gap-2" :style="{ paddingLeft: `${row.depth * 24}px` }">
            <Checkbox
              :model-value="checkedIds.has(row.id) ? true : isIndeterminate(row) ? 'indeterminate' : false"
              :aria-label="row.name"
              class="data-[state=indeterminate]:bg-primary data-[state=indeterminate]:text-primary-foreground data-[state=indeterminate]:border-primary"
              @update:model-value="handleRowToggle(row)"
            >
              <template v-if="isIndeterminate(row)" #default><Minus class="size-3.5" /></template>
              <template v-else #default><Check class="size-3.5" /></template>
            </Checkbox>
            <span class="flex-1 text-sm">{{ row.name }}</span>
            <button class="ml-auto mr-2 flex size-5 shrink-0 items-center justify-center rounded" :class="row.hasChildren ? 'hover:bg-muted cursor-pointer' : 'invisible'" :aria-label="expandedIds.has(row.id) ? '收起' : '展开'" @click="toggleExpand(row.id)">
              <ChevronDown v-if="expandedIds.has(row.id)" class="size-4" />
              <ChevronRight v-else class="size-4" />
            </button>
          </div>
        </TableCell>
        <TableCell>
          <div v-if="row.buttons.length > 0" class="flex flex-wrap gap-3">
            <label v-for="btn in row.buttons" :key="btn.menuId" class="flex items-center gap-1.5 cursor-pointer text-xs text-muted-foreground hover:text-foreground">
              <Checkbox :model-value="checkedIds.has(btn.menuId)" :aria-label="btn.menuName" @update:model-value="handleButtonToggle(btn.menuId)" />
              {{ btn.menuName }}
            </label>
          </div>
        </TableCell>
      </TableRow>
    </TableBody>
  </Table>
</template>
