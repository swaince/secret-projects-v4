<script setup lang="ts">
defineOptions({ name: 'DeptAuthView' })

import { ref, computed, watch, onMounted } from 'vue'
import { Button } from '@/components/ui/button'
import { Checkbox } from '@/components/ui/checkbox'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { Input } from '@/components/ui/input'
import { toast } from 'vue-sonner'
import { Save, Search, ArrowRight } from '@lucide/vue'
import { fetchDeptTree } from '@/api/dept'
import type { DeptDTO } from '@/api/dept'
import { fetchMenuTree } from '@/api/menu'
import type { MenuDTO } from '@/api/menu'
import { fetchAuthMenuIds, saveAuthMenu } from '@/api/auth'
import MenuTreeCheckbox from './MenuTreeCheckbox.vue'

const depts = ref<DeptDTO[]>([])
const deptSearch = ref('')
const selectedDepts = ref<string[]>([])
const menuTree = ref<MenuDTO[]>([])
const checkedMenuIds = ref(new Set<string>())

function flattenDepts(items: DeptDTO[]): DeptDTO[] {
  let result: DeptDTO[] = []
  for (const item of items) {
    result.push(item)
    if (item.children && item.children.length > 0) {
      result = result.concat(flattenDepts(item.children))
    }
  }
  return result
}

const filteredDepts = computed(() => {
  if (!deptSearch.value) return depts.value
  const q = deptSearch.value.toLowerCase()
  return depts.value.filter((d) => d.deptName.toLowerCase().includes(q))
})

const allDeptsSelected = computed(
  () =>
    filteredDepts.value.length > 0 &&
    filteredDepts.value.every((d) => selectedDepts.value.includes(d.deptId)),
)

function toggleAllDepts() {
  if (allDeptsSelected.value) {
    selectedDepts.value = selectedDepts.value.filter(
      (id) => !filteredDepts.value.some((d) => d.deptId === id),
    )
  } else {
    const idsToAdd = filteredDepts.value
      .filter((d) => !selectedDepts.value.includes(d.deptId))
      .map((d) => d.deptId)
    selectedDepts.value = [...selectedDepts.value, ...idsToAdd]
  }
}

function toggleDept(deptId: string) {
  if (selectedDepts.value.includes(deptId)) {
    selectedDepts.value = selectedDepts.value.filter((id) => id !== deptId)
  } else {
    selectedDepts.value = [...selectedDepts.value, deptId]
  }
}

function intersect<T>(arrays: T[][]): Set<T> {
  if (arrays.length === 0) return new Set()
  const first = arrays[0]!
  return new Set(first.filter((item) => arrays.every((arr) => arr.includes(item))))
}

async function loadData() {
  const [tree, menuTreeData] = await Promise.all([fetchDeptTree(), fetchMenuTree()])
  depts.value = flattenDepts(tree)
  menuTree.value = menuTreeData
}

async function loadMenuAuth() {
  if (selectedDepts.value.length === 0) {
    checkedMenuIds.value = new Set()
    return
  }
  const results = await Promise.all(
    selectedDepts.value.map((deptId) => fetchAuthMenuIds('dept', deptId)),
  )
  checkedMenuIds.value = intersect(results)
}

watch(selectedDepts, loadMenuAuth, { deep: true })

function findMenuItem(id: string, items: MenuDTO[]): MenuDTO | null {
  for (const item of items) {
    if (item.menuId === id) return item
    if (item.children) {
      const found = findMenuItem(id, item.children)
      if (found) return found
    }
  }
  return null
}

function collectDescendantIds(item: MenuDTO): string[] {
  const ids: string[] = []
  if (item.children) {
    for (const child of item.children) {
      ids.push(child.menuId)
      ids.push(...collectDescendantIds(child))
    }
  }
  return ids
}

function collectAncestorIds(menuId: string, items: MenuDTO[]): string[] {
  for (const item of items) {
    if (item.menuId === menuId) return [item.menuId]
    if (item.children) {
      const path = collectAncestorIds(menuId, item.children)
      if (path.length > 0) return [item.menuId, ...path]
    }
  }
  return []
}

function handleMenuToggle(menuId: string) {
  const newSet = new Set(checkedMenuIds.value)
  if (newSet.has(menuId)) {
    newSet.delete(menuId)
    const item = findMenuItem(menuId, menuTree.value)
    if (item) {
      for (const childId of collectDescendantIds(item)) {
        newSet.delete(childId)
      }
    }
    for (const ancestorId of collectAncestorIds(menuId, menuTree.value)) {
      newSet.delete(ancestorId)
    }
  } else {
    newSet.add(menuId)
    const item = findMenuItem(menuId, menuTree.value)
    if (item) {
      for (const childId of collectDescendantIds(item)) {
        newSet.add(childId)
      }
    }
  }
  checkedMenuIds.value = newSet
}

async function handleSave() {
  if (selectedDepts.value.length === 0) return
  await saveAuthMenu('dept', selectedDepts.value, Array.from(checkedMenuIds.value))
  toast.success('部门授权保存成功')
}

onMounted(loadData)
</script>

<template>
  <Card>
    <CardHeader class="pb-2">
      <CardTitle>部门授权</CardTitle>
    </CardHeader>
    <CardContent>
      <div class="grid grid-cols-[1fr_auto_2fr] gap-4 items-start">
        <div class="rounded-lg border">
          <div class="px-3 py-2 border-b">
            <div class="flex items-center gap-2">
              <Search class="size-4 text-muted-foreground shrink-0" />
              <Input v-model="deptSearch" placeholder="搜索部门..." class="h-8 text-sm" />
            </div>
          </div>
          <div class="px-3 py-1.5 border-b">
            <div class="flex items-center gap-2">
              <Checkbox
                :model-value="allDeptsSelected"
                aria-label="全选部门"
                @update:model-value="toggleAllDepts"
              />
              <span class="text-xs text-muted-foreground"
                >已选 {{ selectedDepts.length }} 个部门</span
              >
            </div>
          </div>
          <div class="overflow-auto max-h-[420px]">
            <div
              v-if="filteredDepts.length === 0"
              class="px-3 py-6 text-center text-sm text-muted-foreground"
            >
              暂无部门
            </div>
            <label
              v-for="dept in filteredDepts"
              :key="dept.deptId"
              :for="`dept-item-${dept.deptId}`"
              class="flex items-center gap-2 px-3 py-1.5 hover:bg-accent cursor-pointer"
            >
              <Checkbox
                :id="`dept-item-${dept.deptId}`"
                :model-value="selectedDepts.includes(dept.deptId)"
                :aria-label="dept.deptName"
                @update:model-value="toggleDept(dept.deptId)"
              />
              <span class="text-sm truncate">{{ dept.deptName }}</span>
            </label>
          </div>
        </div>

        <div class="flex items-center justify-center pt-24">
          <ArrowRight class="size-5 text-muted-foreground" />
        </div>

        <div class="rounded-lg border">
          <div class="px-3 py-2 border-b">
            <span class="text-sm font-medium">菜单树</span>
          </div>
          <div class="px-3 py-1.5 border-b">
            <span class="text-xs text-muted-foreground"
              >已选 {{ checkedMenuIds.size }} 项</span
            >
          </div>
          <div class="overflow-auto max-h-[420px] px-2 py-1">
            <template v-if="selectedDepts.length === 0">
              <div class="px-3 py-6 text-center text-sm text-muted-foreground">
                请先选择部门
              </div>
            </template>
            <MenuTreeCheckbox
              v-else
              :items="menuTree"
              :checked-ids="checkedMenuIds"
              @toggle="handleMenuToggle"
            />
          </div>
        </div>
      </div>
      <div class="flex justify-end mt-4">
        <Button :disabled="selectedDepts.length === 0" @click="handleSave">
          <Save class="size-4" />授权
        </Button>
      </div>
    </CardContent>
  </Card>
</template>
