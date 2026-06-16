<script setup lang="ts">
defineOptions({ name: 'RoleAuthView' })

import { ref, computed, watch, onMounted } from 'vue'
import { Button } from '@/components/ui/button'
import { Checkbox } from '@/components/ui/checkbox'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { Input } from '@/components/ui/input'
import { toast } from 'vue-sonner'
import { Save, Search, ArrowRight } from '@lucide/vue'
import { fetchRoles } from '@/api/role'
import type { RoleDTO } from '@/api/role'
import { fetchMenuTree } from '@/api/menu'
import type { MenuDTO } from '@/api/menu'
import { fetchAuthMenuIds, saveAuthMenu } from '@/api/auth'
import MenuTreeCheckbox from './MenuTreeCheckbox.vue'

const roles = ref<RoleDTO[]>([])
const roleSearch = ref('')
const selectedRoles = ref<string[]>([])
const menuTree = ref<MenuDTO[]>([])
const checkedMenuIds = ref(new Set<string>())

const filteredRoles = computed(() => {
  if (!roleSearch.value) return roles.value
  const q = roleSearch.value.toLowerCase()
  return roles.value.filter((r) => r.roleName.toLowerCase().includes(q))
})

const allRolesSelected = computed(
  () =>
    filteredRoles.value.length > 0 &&
    filteredRoles.value.every((r) => selectedRoles.value.includes(r.roleId)),
)

function toggleAllRoles() {
  if (allRolesSelected.value) {
    selectedRoles.value = selectedRoles.value.filter(
      (id) => !filteredRoles.value.some((r) => r.roleId === id),
    )
  } else {
    const idsToAdd = filteredRoles.value
      .filter((r) => !selectedRoles.value.includes(r.roleId))
      .map((r) => r.roleId)
    selectedRoles.value = [...selectedRoles.value, ...idsToAdd]
  }
}

function toggleRole(roleId: string) {
  if (selectedRoles.value.includes(roleId)) {
    selectedRoles.value = selectedRoles.value.filter((id) => id !== roleId)
  } else {
    selectedRoles.value = [...selectedRoles.value, roleId]
  }
}

function intersect<T>(arrays: T[][]): Set<T> {
  if (arrays.length === 0) return new Set()
  const first = arrays[0]!
  return new Set(first.filter((item) => arrays.every((arr) => arr.includes(item))))
}

async function loadData() {
  const [roleRes, tree] = await Promise.all([
    fetchRoles({ page: 1, size: 9999 }),
    fetchMenuTree(),
  ])
  roles.value = roleRes.records
  menuTree.value = tree
}

async function loadMenuAuth() {
  if (selectedRoles.value.length === 0) {
    checkedMenuIds.value = new Set()
    return
  }
  const results = await Promise.all(
    selectedRoles.value.map((roleId) => fetchAuthMenuIds('role', roleId)),
  )
  checkedMenuIds.value = intersect(results)
}

watch(selectedRoles, loadMenuAuth, { deep: true })

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
  if (selectedRoles.value.length === 0) return
  await saveAuthMenu('role', selectedRoles.value, Array.from(checkedMenuIds.value))
  toast.success('角色授权保存成功')
}

onMounted(loadData)
</script>

<template>
  <Card>
    <CardHeader class="pb-2">
      <CardTitle>角色授权</CardTitle>
    </CardHeader>
    <CardContent>
      <div class="grid grid-cols-[1fr_auto_2fr] gap-4 items-start">
        <div class="rounded-lg border">
          <div class="px-3 py-2 border-b">
            <div class="flex items-center gap-2">
              <Search class="size-4 text-muted-foreground shrink-0" />
              <Input v-model="roleSearch" placeholder="搜索角色..." class="h-8 text-sm" />
            </div>
          </div>
          <div class="px-3 py-1.5 border-b">
            <div class="flex items-center gap-2">
              <Checkbox
                :model-value="allRolesSelected"
                aria-label="全选角色"
                @update:model-value="toggleAllRoles"
              />
              <span class="text-xs text-muted-foreground"
                >已选 {{ selectedRoles.length }} 个角色</span
              >
            </div>
          </div>
          <div class="overflow-auto max-h-[420px]">
            <div
              v-if="filteredRoles.length === 0"
              class="px-3 py-6 text-center text-sm text-muted-foreground"
            >
              暂无角色
            </div>
            <label
              v-for="role in filteredRoles"
              :key="role.roleId"
              :for="`role-item-${role.roleId}`"
              class="flex items-center gap-2 px-3 py-1.5 hover:bg-accent cursor-pointer"
            >
              <Checkbox
                :id="`role-item-${role.roleId}`"
                :model-value="selectedRoles.includes(role.roleId)"
                :aria-label="role.roleName"
                @update:model-value="toggleRole(role.roleId)"
              />
              <span class="text-sm truncate">{{ role.roleName }}</span>
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
            <template v-if="selectedRoles.length === 0">
              <div class="px-3 py-6 text-center text-sm text-muted-foreground">
                请先选择角色
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
        <Button :disabled="selectedRoles.length === 0" @click="handleSave">
          <Save class="size-4" />授权
        </Button>
      </div>
    </CardContent>
  </Card>
</template>
