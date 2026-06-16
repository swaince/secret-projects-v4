<script setup lang="ts">
defineOptions({ name: 'UserAuthView' })

import { ref, computed, watch, onMounted } from 'vue'
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs'
import { Button } from '@/components/ui/button'
import { Checkbox } from '@/components/ui/checkbox'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { Input } from '@/components/ui/input'
import { toast } from 'vue-sonner'
import { Save, Search, ArrowRight } from '@lucide/vue'
import { fetchUsers } from '@/api/user'
import type { UserDTO } from '@/api/user'
import { fetchMenuTree } from '@/api/menu'
import type { MenuDTO } from '@/api/menu'
import { fetchDeptTree } from '@/api/dept'
import type { DeptDTO } from '@/api/dept'
import { fetchPosts } from '@/api/post'
import type { PostDTO } from '@/api/post'
import { fetchRoles } from '@/api/role'
import type { RoleDTO } from '@/api/role'
import { fetchAuthMenuIds, saveAuthMenu, fetchUserRelations, saveUserRelations } from '@/api/auth'
import MenuTreeCheckbox from './MenuTreeCheckbox.vue'

type TabKey = 'menu' | 'dept' | 'post' | 'role'

const activeTab = ref<TabKey>('menu')

const users = ref<UserDTO[]>([])
const userSearch = ref('')
const selectedUsers = ref<string[]>([])

const menuTree = ref<MenuDTO[]>([])
const checkedMenuIds = ref(new Set<string>())

const deptList = ref<DeptDTO[]>([])
const checkedDeptIds = ref(new Set<string>())
const deptSearch = ref('')

const postList = ref<PostDTO[]>([])
const checkedPostIds = ref(new Set<string>())
const postSearch = ref('')

const roleList = ref<RoleDTO[]>([])
const checkedRoleIds = ref(new Set<string>())
const roleSearch = ref('')

const filteredUsers = computed(() => {
  if (!userSearch.value) return users.value
  const q = userSearch.value.toLowerCase()
  return users.value.filter((u) => (u.displayName || u.username).toLowerCase().includes(q))
})

const allUsersSelected = computed(
  () =>
    filteredUsers.value.length > 0 &&
    filteredUsers.value.every((u) => selectedUsers.value.includes(u.userId)),
)

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
  if (!deptSearch.value) return deptList.value
  const q = deptSearch.value.toLowerCase()
  return deptList.value.filter((d) => d.deptName.toLowerCase().includes(q))
})

const allDeptsSelected = computed(
  () =>
    filteredDepts.value.length > 0 &&
    filteredDepts.value.every((d) => checkedDeptIds.value.has(d.deptId)),
)

const filteredPosts = computed(() => {
  if (!postSearch.value) return postList.value
  const q = postSearch.value.toLowerCase()
  return postList.value.filter((p) => p.postName.toLowerCase().includes(q))
})

const allPostsSelected = computed(
  () =>
    filteredPosts.value.length > 0 &&
    filteredPosts.value.every((p) => checkedPostIds.value.has(p.postId)),
)

const filteredRoles = computed(() => {
  if (!roleSearch.value) return roleList.value
  const q = roleSearch.value.toLowerCase()
  return roleList.value.filter((r) => r.roleName.toLowerCase().includes(q))
})

const allRolesSelected = computed(
  () =>
    filteredRoles.value.length > 0 &&
    filteredRoles.value.every((r) => checkedRoleIds.value.has(r.roleId)),
)

const saveButtonText = computed(() => {
  switch (activeTab.value) {
    case 'menu':
      return '授权菜单'
    case 'dept':
      return '绑定部门'
    case 'post':
      return '绑定岗位'
    case 'role':
      return '绑定角色'
    default:
      return '保存'
  }
})

function toggleAllUsers() {
  if (allUsersSelected.value) {
    selectedUsers.value = selectedUsers.value.filter(
      (id) => !filteredUsers.value.some((u) => u.userId === id),
    )
  } else {
    const idsToAdd = filteredUsers.value
      .filter((u) => !selectedUsers.value.includes(u.userId))
      .map((u) => u.userId)
    selectedUsers.value = [...selectedUsers.value, ...idsToAdd]
  }
}

function toggleUser(userId: string) {
  if (selectedUsers.value.includes(userId)) {
    selectedUsers.value = selectedUsers.value.filter((id) => id !== userId)
  } else {
    selectedUsers.value = [...selectedUsers.value, userId]
  }
}

function toggleAllDepts() {
  const newSet = new Set(checkedDeptIds.value)
  if (allDeptsSelected.value) {
    for (const d of filteredDepts.value) newSet.delete(d.deptId)
  } else {
    for (const d of filteredDepts.value) newSet.add(d.deptId)
  }
  checkedDeptIds.value = newSet
}

function toggleDept(deptId: string) {
  const newSet = new Set(checkedDeptIds.value)
  if (newSet.has(deptId)) newSet.delete(deptId)
  else newSet.add(deptId)
  checkedDeptIds.value = newSet
}

function toggleAllPosts() {
  const newSet = new Set(checkedPostIds.value)
  if (allPostsSelected.value) {
    for (const p of filteredPosts.value) newSet.delete(p.postId)
  } else {
    for (const p of filteredPosts.value) newSet.add(p.postId)
  }
  checkedPostIds.value = newSet
}

function togglePost(postId: string) {
  const newSet = new Set(checkedPostIds.value)
  if (newSet.has(postId)) newSet.delete(postId)
  else newSet.add(postId)
  checkedPostIds.value = newSet
}

function toggleAllRoles() {
  const newSet = new Set(checkedRoleIds.value)
  if (allRolesSelected.value) {
    for (const r of filteredRoles.value) newSet.delete(r.roleId)
  } else {
    for (const r of filteredRoles.value) newSet.add(r.roleId)
  }
  checkedRoleIds.value = newSet
}

function toggleRole(roleId: string) {
  const newSet = new Set(checkedRoleIds.value)
  if (newSet.has(roleId)) newSet.delete(roleId)
  else newSet.add(roleId)
  checkedRoleIds.value = newSet
}

function intersect<T>(arrays: T[][]): Set<T> {
  if (arrays.length === 0) return new Set()
  const first = arrays[0]!
  return new Set(first.filter((item) => arrays.every((arr) => arr.includes(item))))
}

async function loadUsers() {
  const res = await fetchUsers({ page: 1, size: 9999 })
  users.value = res.records
}

async function loadMenuTree() {
  menuTree.value = await fetchMenuTree()
}

async function loadDeptTree() {
  const tree = await fetchDeptTree()
  deptList.value = flattenDepts(tree)
}

async function loadPosts() {
  const res = await fetchPosts({ page: 1, size: 9999 })
  postList.value = res.records
}

async function loadRoles() {
  const res = await fetchRoles({ page: 1, size: 9999 })
  roleList.value = res.records
}

async function loadTabData() {
  if (selectedUsers.value.length === 0) {
    checkedMenuIds.value = new Set()
    checkedDeptIds.value = new Set()
    checkedPostIds.value = new Set()
    checkedRoleIds.value = new Set()
    return
  }

  switch (activeTab.value) {
    case 'menu': {
      const results = await Promise.all(
        selectedUsers.value.map((uid) => fetchAuthMenuIds('user', uid)),
      )
      checkedMenuIds.value = intersect(results)
      break
    }
    case 'dept': {
      const results = await Promise.all(
        selectedUsers.value.map((uid) => fetchUserRelations('dept', uid)),
      )
      checkedDeptIds.value = intersect(results)
      break
    }
    case 'post': {
      const results = await Promise.all(
        selectedUsers.value.map((uid) => fetchUserRelations('post', uid)),
      )
      checkedPostIds.value = intersect(results)
      break
    }
    case 'role': {
      const results = await Promise.all(
        selectedUsers.value.map((uid) => fetchUserRelations('role', uid)),
      )
      checkedRoleIds.value = intersect(results)
      break
    }
  }
}

watch(selectedUsers, loadTabData, { deep: true })

watch(activeTab, () => {
  if (selectedUsers.value.length > 0) loadTabData()
})

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
  if (selectedUsers.value.length === 0) return
  switch (activeTab.value) {
    case 'menu': {
      await saveAuthMenu('user', selectedUsers.value, Array.from(checkedMenuIds.value))
      toast.success('菜单授权保存成功')
      break
    }
    case 'dept': {
      for (const deptId of checkedDeptIds.value) {
        await saveUserRelations('dept', selectedUsers.value, deptId)
      }
      toast.success('部门关联保存成功')
      break
    }
    case 'post': {
      for (const postId of checkedPostIds.value) {
        await saveUserRelations('post', selectedUsers.value, postId)
      }
      toast.success('岗位关联保存成功')
      break
    }
    case 'role': {
      for (const roleId of checkedRoleIds.value) {
        await saveUserRelations('role', selectedUsers.value, roleId)
      }
      toast.success('角色关联保存成功')
      break
    }
  }
}

onMounted(async () => {
  await Promise.all([loadUsers(), loadMenuTree(), loadDeptTree(), loadPosts(), loadRoles()])
})
</script>

<template>
  <Card>
    <CardHeader class="pb-2">
      <CardTitle>用户授权</CardTitle>
    </CardHeader>
    <CardContent>
      <Tabs v-model="activeTab">
        <div class="mb-4">
          <TabsList>
            <TabsTrigger value="menu">菜单授权</TabsTrigger>
            <TabsTrigger value="dept">部门</TabsTrigger>
            <TabsTrigger value="post">岗位</TabsTrigger>
            <TabsTrigger value="role">角色</TabsTrigger>
          </TabsList>
        </div>

        <TabsContent value="menu">
          <div class="grid grid-cols-[1fr_auto_2fr] gap-4 items-start">
            <div class="rounded-lg border">
              <div class="px-3 py-2 border-b">
                <div class="flex items-center gap-2">
                  <Search class="size-4 text-muted-foreground shrink-0" />
                  <Input
                    v-model="userSearch"
                    placeholder="搜索用户..."
                    class="h-8 text-sm"
                  />
                </div>
              </div>
              <div class="px-3 py-1.5 border-b">
                <div class="flex items-center gap-2">
                  <Checkbox
                    :model-value="allUsersSelected"
                    aria-label="全选用户"
                    @update:model-value="toggleAllUsers"
                  />
                  <span class="text-xs text-muted-foreground"
                    >已选 {{ selectedUsers.length }} 个用户</span
                  >
                </div>
              </div>
              <div class="overflow-auto max-h-[420px]">
                <div
                  v-if="filteredUsers.length === 0"
                  class="px-3 py-6 text-center text-sm text-muted-foreground"
                >
                  暂无用户
                </div>
                <label
                  v-for="user in filteredUsers"
                  :key="user.userId"
                  :for="`menu-tab-user-${user.userId}`"
                  class="flex items-center gap-2 px-3 py-1.5 hover:bg-accent cursor-pointer"
                >
                  <Checkbox
                    :id="`menu-tab-user-${user.userId}`"
                    :model-value="selectedUsers.includes(user.userId)"
                    :aria-label="user.displayName || user.username"
                    @update:model-value="toggleUser(user.userId)"
                  />
                  <span class="text-sm truncate">{{ user.displayName || user.username }}</span>
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
                <span class="text-xs text-muted-foreground">已选 {{ checkedMenuIds.size }} 项</span>
              </div>
              <div class="overflow-auto max-h-[420px] px-2 py-1">
                <template v-if="selectedUsers.length === 0">
                  <div class="px-3 py-6 text-center text-sm text-muted-foreground">
                    请先选择用户
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
            <Button :disabled="selectedUsers.length === 0" @click="handleSave">
              <Save class="size-4" />{{ saveButtonText }}
            </Button>
          </div>
        </TabsContent>

        <TabsContent value="dept">
          <div class="grid grid-cols-[1fr_auto_2fr] gap-4 items-start">
            <div class="rounded-lg border">
              <div class="px-3 py-2 border-b">
                <div class="flex items-center gap-2">
                  <Search class="size-4 text-muted-foreground shrink-0" />
                  <Input
                    v-model="userSearch"
                    placeholder="搜索用户..."
                    class="h-8 text-sm"
                  />
                </div>
              </div>
              <div class="px-3 py-1.5 border-b">
                <div class="flex items-center gap-2">
                  <Checkbox
                    :model-value="allUsersSelected"
                    aria-label="全选用户"
                    @update:model-value="toggleAllUsers"
                  />
                  <span class="text-xs text-muted-foreground"
                    >已选 {{ selectedUsers.length }} 个用户</span
                  >
                </div>
              </div>
              <div class="overflow-auto max-h-[420px]">
                <div
                  v-if="filteredUsers.length === 0"
                  class="px-3 py-6 text-center text-sm text-muted-foreground"
                >
                  暂无用户
                </div>
                <label
                  v-for="user in filteredUsers"
                  :key="user.userId"
                  :for="`dept-tab-user-${user.userId}`"
                  class="flex items-center gap-2 px-3 py-1.5 hover:bg-accent cursor-pointer"
                >
                  <Checkbox
                    :id="`dept-tab-user-${user.userId}`"
                    :model-value="selectedUsers.includes(user.userId)"
                    :aria-label="user.displayName || user.username"
                    @update:model-value="toggleUser(user.userId)"
                  />
                  <span class="text-sm truncate">{{ user.displayName || user.username }}</span>
                </label>
              </div>
            </div>

            <div class="flex items-center justify-center pt-24">
              <ArrowRight class="size-5 text-muted-foreground" />
            </div>

            <div class="rounded-lg border">
              <div class="px-3 py-2 border-b">
                <div class="flex items-center gap-2">
                  <Search class="size-4 text-muted-foreground shrink-0" />
                  <Input
                    v-model="deptSearch"
                    placeholder="搜索部门..."
                    class="h-8 text-sm"
                  />
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
                    >已选 {{ checkedDeptIds.size }} 项</span
                  >
                </div>
              </div>
              <div class="overflow-auto max-h-[420px]">
                <template v-if="selectedUsers.length === 0">
                  <div class="px-3 py-6 text-center text-sm text-muted-foreground">
                    请先选择用户
                  </div>
                </template>
                <template v-else-if="filteredDepts.length === 0">
                  <div class="px-3 py-6 text-center text-sm text-muted-foreground">
                    暂无部门
                  </div>
                </template>
                <label
                  v-for="dept in filteredDepts"
                  :key="dept.deptId"
                  :for="`user-dept-${dept.deptId}`"
                  class="flex items-center gap-2 px-3 py-1.5 hover:bg-accent cursor-pointer"
                >
                  <Checkbox
                    :id="`user-dept-${dept.deptId}`"
                    :model-value="checkedDeptIds.has(dept.deptId)"
                    :aria-label="dept.deptName"
                    @update:model-value="toggleDept(dept.deptId)"
                  />
                  <span class="text-sm truncate">{{ dept.deptName }}</span>
                </label>
              </div>
            </div>
          </div>
          <div class="flex justify-end mt-4">
            <Button :disabled="selectedUsers.length === 0" @click="handleSave">
              <Save class="size-4" />{{ saveButtonText }}
            </Button>
          </div>
        </TabsContent>

        <TabsContent value="post">
          <div class="grid grid-cols-[1fr_auto_2fr] gap-4 items-start">
            <div class="rounded-lg border">
              <div class="px-3 py-2 border-b">
                <div class="flex items-center gap-2">
                  <Search class="size-4 text-muted-foreground shrink-0" />
                  <Input
                    v-model="userSearch"
                    placeholder="搜索用户..."
                    class="h-8 text-sm"
                  />
                </div>
              </div>
              <div class="px-3 py-1.5 border-b">
                <div class="flex items-center gap-2">
                  <Checkbox
                    :model-value="allUsersSelected"
                    aria-label="全选用户"
                    @update:model-value="toggleAllUsers"
                  />
                  <span class="text-xs text-muted-foreground"
                    >已选 {{ selectedUsers.length }} 个用户</span
                  >
                </div>
              </div>
              <div class="overflow-auto max-h-[420px]">
                <div
                  v-if="filteredUsers.length === 0"
                  class="px-3 py-6 text-center text-sm text-muted-foreground"
                >
                  暂无用户
                </div>
                <label
                  v-for="user in filteredUsers"
                  :key="user.userId"
                  :for="`post-tab-user-${user.userId}`"
                  class="flex items-center gap-2 px-3 py-1.5 hover:bg-accent cursor-pointer"
                >
                  <Checkbox
                    :id="`post-tab-user-${user.userId}`"
                    :model-value="selectedUsers.includes(user.userId)"
                    :aria-label="user.displayName || user.username"
                    @update:model-value="toggleUser(user.userId)"
                  />
                  <span class="text-sm truncate">{{ user.displayName || user.username }}</span>
                </label>
              </div>
            </div>

            <div class="flex items-center justify-center pt-24">
              <ArrowRight class="size-5 text-muted-foreground" />
            </div>

            <div class="rounded-lg border">
              <div class="px-3 py-2 border-b">
                <div class="flex items-center gap-2">
                  <Search class="size-4 text-muted-foreground shrink-0" />
                  <Input
                    v-model="postSearch"
                    placeholder="搜索岗位..."
                    class="h-8 text-sm"
                  />
                </div>
              </div>
              <div class="px-3 py-1.5 border-b">
                <div class="flex items-center gap-2">
                  <Checkbox
                    :model-value="allPostsSelected"
                    aria-label="全选岗位"
                    @update:model-value="toggleAllPosts"
                  />
                  <span class="text-xs text-muted-foreground"
                    >已选 {{ checkedPostIds.size }} 项</span
                  >
                </div>
              </div>
              <div class="overflow-auto max-h-[420px]">
                <template v-if="selectedUsers.length === 0">
                  <div class="px-3 py-6 text-center text-sm text-muted-foreground">
                    请先选择用户
                  </div>
                </template>
                <template v-else-if="filteredPosts.length === 0">
                  <div class="px-3 py-6 text-center text-sm text-muted-foreground">
                    暂无岗位
                  </div>
                </template>
                <label
                  v-for="post in filteredPosts"
                  :key="post.postId"
                  :for="`user-post-${post.postId}`"
                  class="flex items-center gap-2 px-3 py-1.5 hover:bg-accent cursor-pointer"
                >
                  <Checkbox
                    :id="`user-post-${post.postId}`"
                    :model-value="checkedPostIds.has(post.postId)"
                    :aria-label="post.postName"
                    @update:model-value="togglePost(post.postId)"
                  />
                  <span class="text-sm truncate">{{ post.postName }}</span>
                </label>
              </div>
            </div>
          </div>
          <div class="flex justify-end mt-4">
            <Button :disabled="selectedUsers.length === 0" @click="handleSave">
              <Save class="size-4" />{{ saveButtonText }}
            </Button>
          </div>
        </TabsContent>

        <TabsContent value="role">
          <div class="grid grid-cols-[1fr_auto_2fr] gap-4 items-start">
            <div class="rounded-lg border">
              <div class="px-3 py-2 border-b">
                <div class="flex items-center gap-2">
                  <Search class="size-4 text-muted-foreground shrink-0" />
                  <Input
                    v-model="userSearch"
                    placeholder="搜索用户..."
                    class="h-8 text-sm"
                  />
                </div>
              </div>
              <div class="px-3 py-1.5 border-b">
                <div class="flex items-center gap-2">
                  <Checkbox
                    :model-value="allUsersSelected"
                    aria-label="全选用户"
                    @update:model-value="toggleAllUsers"
                  />
                  <span class="text-xs text-muted-foreground"
                    >已选 {{ selectedUsers.length }} 个用户</span
                  >
                </div>
              </div>
              <div class="overflow-auto max-h-[420px]">
                <div
                  v-if="filteredUsers.length === 0"
                  class="px-3 py-6 text-center text-sm text-muted-foreground"
                >
                  暂无用户
                </div>
                <label
                  v-for="user in filteredUsers"
                  :key="user.userId"
                  :for="`role-tab-user-${user.userId}`"
                  class="flex items-center gap-2 px-3 py-1.5 hover:bg-accent cursor-pointer"
                >
                  <Checkbox
                    :id="`role-tab-user-${user.userId}`"
                    :model-value="selectedUsers.includes(user.userId)"
                    :aria-label="user.displayName || user.username"
                    @update:model-value="toggleUser(user.userId)"
                  />
                  <span class="text-sm truncate">{{ user.displayName || user.username }}</span>
                </label>
              </div>
            </div>

            <div class="flex items-center justify-center pt-24">
              <ArrowRight class="size-5 text-muted-foreground" />
            </div>

            <div class="rounded-lg border">
              <div class="px-3 py-2 border-b">
                <div class="flex items-center gap-2">
                  <Search class="size-4 text-muted-foreground shrink-0" />
                  <Input
                    v-model="roleSearch"
                    placeholder="搜索角色..."
                    class="h-8 text-sm"
                  />
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
                    >已选 {{ checkedRoleIds.size }} 项</span
                  >
                </div>
              </div>
              <div class="overflow-auto max-h-[420px]">
                <template v-if="selectedUsers.length === 0">
                  <div class="px-3 py-6 text-center text-sm text-muted-foreground">
                    请先选择用户
                  </div>
                </template>
                <template v-else-if="filteredRoles.length === 0">
                  <div class="px-3 py-6 text-center text-sm text-muted-foreground">
                    暂无角色
                  </div>
                </template>
                <label
                  v-for="role in filteredRoles"
                  :key="role.roleId"
                  :for="`user-role-${role.roleId}`"
                  class="flex items-center gap-2 px-3 py-1.5 hover:bg-accent cursor-pointer"
                >
                  <Checkbox
                    :id="`user-role-${role.roleId}`"
                    :model-value="checkedRoleIds.has(role.roleId)"
                    :aria-label="role.roleName"
                    @update:model-value="toggleRole(role.roleId)"
                  />
                  <span class="text-sm truncate">{{ role.roleName }}</span>
                </label>
              </div>
            </div>
          </div>
          <div class="flex justify-end mt-4">
            <Button :disabled="selectedUsers.length === 0" @click="handleSave">
              <Save class="size-4" />{{ saveButtonText }}
            </Button>
          </div>
        </TabsContent>
      </Tabs>
    </CardContent>
  </Card>
</template>
