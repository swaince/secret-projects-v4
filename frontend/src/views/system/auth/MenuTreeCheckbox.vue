<script setup lang="ts">
defineOptions({ name: 'MenuTreeCheckbox' })

import { computed } from 'vue'
import { Checkbox } from '@/components/ui/checkbox'
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from '@/components/ui/table'
import { Check, Minus } from '@lucide/vue'
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
  buttons: MenuDTO[]
}

const flatMenu = computed(() => {
  const result: FlatMenuItem[] = []
  function walk(nodes: MenuDTO[], depth: number) {
    for (const node of nodes) {
      if (node.menuType === 'B') continue
      const buttons = node.children?.filter((c) => c.menuType === 'B') ?? []
      const subMenus = node.children?.filter((c) => c.menuType !== 'B') ?? []
      result.push({
        id: node.menuId,
        name: node.menuName,
        type: node.menuType,
        depth,
        buttons,
      })
      if (subMenus.length > 0) walk(subMenus, depth + 1)
    }
  }
  walk(props.items, 0)
  return result
})

function allButtonChecked(buttons: MenuDTO[]): boolean {
  if (buttons.length === 0) return false
  return buttons.every((b) => props.checkedIds.has(b.menuId))
}

function someButtonChecked(buttons: MenuDTO[]): boolean {
  return buttons.some((b) => props.checkedIds.has(b.menuId))
}

function isIndeterminate(item: FlatMenuItem): boolean {
  if (props.checkedIds.has(item.id)) return false
  return someButtonChecked(item.buttons)
}

function handleRowToggle(item: FlatMenuItem) {
  emit('toggle', item.id)
}

function handleButtonToggle(menuId: string) {
  emit('toggle', menuId)
}
</script>

<template>
  <Table>
    <TableHeader>
      <TableRow>
        <TableHead class="w-full">菜单名称</TableHead>
        <TableHead class="min-w-48 text-center">按钮权限</TableHead>
      </TableRow>
    </TableHeader>
    <TableBody>
      <TableRow v-for="row in flatMenu" :key="row.id" class="hover:bg-muted/50">
        <TableCell>
          <div class="flex items-center gap-2" :style="{ paddingLeft: `${row.depth * 24}px` }">
            <Checkbox
              :model-value="checkedIds.has(row.id) ? true : isIndeterminate(row) ? 'indeterminate' : false"
              :aria-label="row.name"
              @update:model-value="handleRowToggle(row)"
            >
              <template v-if="isIndeterminate(row)" #default>
                <Minus class="size-3.5" />
              </template>
              <template v-else #default>
                <Check class="size-3.5" />
              </template>
            </Checkbox>
            <span class="text-sm">{{ row.name }}</span>
          </div>
        </TableCell>
        <TableCell>
          <div v-if="row.buttons.length > 0" class="flex flex-wrap gap-3">
            <label
              v-for="btn in row.buttons"
              :key="btn.menuId"
              class="flex items-center gap-1.5 cursor-pointer text-xs text-muted-foreground hover:text-foreground"
            >
              <Checkbox
                :model-value="checkedIds.has(btn.menuId)"
                :aria-label="btn.menuName"
                @update:model-value="handleButtonToggle(btn.menuId)"
              />
              {{ btn.menuName }}
            </label>
          </div>
          <span v-else class="text-xs text-muted-foreground">—</span>
        </TableCell>
      </TableRow>
    </TableBody>
  </Table>
</template>
