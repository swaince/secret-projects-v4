<script setup lang="ts">
defineOptions({ name: 'UserAuthIndex' })

import { ref, computed, onMounted, watch } from 'vue'
import { Input } from '@/components/ui/input'
import { Checkbox } from '@/components/ui/checkbox'
import { Button } from '@/components/ui/button'
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs'
import { Search, Save, Eye, Check, Minus } from '@lucide/vue'
import { toast } from 'vue-sonner'
import { fetchUsers } from '@/api/user'
import { fetchMenuTree } from '@/api/menu'
import { fetchDeptTree } from '@/api/dept'
import { fetchPosts } from '@/api/post'
import { fetchRoles } from '@/api/role'
import { fetchAuthMenuIds, saveAuthMenu, fetchUserRelations, saveUserRelations, fetchAllUserMenuIds } from '@/api/auth'
import MenuTreeCheckbox from './MenuTreeCheckbox.vue'
import DeptTreeItem from './DeptTreeItem.vue'
import type { MenuDTO } from '@/api/menu'
import type { RoleDTO } from '@/api/role'
import type { DeptDTO } from '@/api/dept'

interface PostDTO { postId: string; postName: string; postCode: string }

const users = ref<any[]>([])
const userSearch = ref('')
const selectedUsers = ref<string[]>([])

const menuTree = ref<MenuDTO[]>([])
const checkedMenuIds = ref(new Set<string>())

const roles = ref<RoleDTO[]>([])
const selectedUserRoles = ref<Set<string>>(new Set())

const posts = ref<PostDTO[]>([])
const selectedUserPosts = ref<Set<string>>(new Set())

const deptTree = ref<DeptDTO[]>([])
const selectedUserDepts = ref<Set<string>>(new Set())

const allUserMenuIds = ref<Set<string>>(new Set())
const viewingId = ref<string | null>(null)
const tabValue = ref('menu')

const saving = ref(false)

const filteredUsers = computed(() => {
  if (!userSearch.value) return users.value
  const q = userSearch.value.toLowerCase()
  return users.value.filter((u: any) => u.username?.toLowerCase().includes(q) || u.displayName?.toLowerCase().includes(q))
})

const allUsersSelected = computed<boolean | 'indeterminate'>(() => {
  if (filteredUsers.value.length === 0) return false
  const n = filteredUsers.value.filter((u: any) => selectedUsers.value.includes(u.userId)).length
  if (n === filteredUsers.value.length) return true
  if (n > 0) return 'indeterminate'
  return false
})

function toggleAllUsers() {
  if (allUsersSelected.value) { selectedUsers.value = [] }
  else { selectedUsers.value = filteredUsers.value.map((u: any) => u.userId) }
}

function toggleUser(userId: string) {
  const idx = selectedUsers.value.indexOf(userId)
  if (idx >= 0) { selectedUsers.value.splice(idx, 1) } else { selectedUsers.value.push(userId) }
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

function toggleSet(set: Set<string>, id: string) {
  const next = new Set(set)
  if (next.has(id)) { next.delete(id) } else { next.add(id) }
  return next
}

function handleDeptToggle(id: string) {
  const next = new Set(selectedUserDepts.value)
  if (next.has(id)) next.delete(id)
  else next.add(id)
  selectedUserDepts.value = next
}

async function handleViewUserMenu(userId: string) {
  viewingId.value = userId
  checkedMenuIds.value = new Set(await fetchAuthMenuIds('user', userId))
}

async function handleViewUserRole(userId: string) {
  viewingId.value = userId
  selectedUserRoles.value = new Set(await fetchUserRelations('role', userId))
}

async function handleViewUserPost(userId: string) {
  viewingId.value = userId
  selectedUserPosts.value = new Set(await fetchUserRelations('post', userId))
}

async function handleViewUserDept(userId: string) {
  viewingId.value = userId
  selectedUserDepts.value = new Set(await fetchUserRelations('dept', userId))
}

async function handleViewUserAll(userId: string) {
  viewingId.value = userId
  allUserMenuIds.value = new Set(await fetchAllUserMenuIds(userId))
}

async function handleSaveMenu() {
  if (saving.value) return
  const lastUser = selectedUsers.value[selectedUsers.value.length - 1]
  saving.value = true
  try {
    await saveAuthMenu('user', selectedUsers.value, Array.from(checkedMenuIds.value))
    selectedUsers.value = []
    if (lastUser) { viewingId.value = lastUser; await handleViewUserMenu(lastUser) }
  } finally { saving.value = false }
}

async function handleSaveRelation(type: string) {
  if (saving.value) return
  const lastUser = selectedUsers.value[selectedUsers.value.length - 1]
  saving.value = true
  try {
    const set = type === 'role' ? selectedUserRoles : type === 'post' ? selectedUserPosts : selectedUserDepts
    await saveUserRelations(type, selectedUsers.value, Array.from(set.value))
    if (type === 'role') selectedUserRoles.value = new Set()
    else if (type === 'post') selectedUserPosts.value = new Set()
    else selectedUserDepts.value = new Set()
    if (lastUser) { viewingId.value = lastUser; await handleViewUserAll(lastUser) }
  } finally { saving.value = false }
}

watch(tabValue, (val) => {
  if (!viewingId.value) return
  if (val === 'menu') handleViewUserMenu(viewingId.value)
  else if (val === 'role') handleViewUserRole(viewingId.value)
  else if (val === 'post') handleViewUserPost(viewingId.value)
  else if (val === 'dept') handleViewUserDept(viewingId.value)
  else if (val === 'all') handleViewUserAll(viewingId.value)
})

onMounted(async () => {
  const [userData, menuData, roleData, postData, deptData] = await Promise.all([
    fetchUsers({ page: 1, size: 1000 }),
    fetchMenuTree(),
    fetchRoles({ page: 1, size: 1000 }),
    fetchPosts({ page: 1, size: 1000 }),
    fetchDeptTree(),
  ])
  users.value = (userData as any).records ?? []
  menuTree.value = menuData
  roles.value = (roleData as any).records ?? []
  posts.value = (postData as any).records ?? []
  deptTree.value = deptData as DeptDTO[]
})
</script>

<template>
  <Tabs v-model="tabValue" class="flex h-full flex-col">
    <TabsList class="shrink-0 w-full">
      <TabsTrigger value="menu">菜单授权</TabsTrigger>
      <TabsTrigger value="role">角色</TabsTrigger>
      <TabsTrigger value="post">岗位</TabsTrigger>
      <TabsTrigger value="dept">部门</TabsTrigger>
      <TabsTrigger value="all">权限总览</TabsTrigger>
    </TabsList>

    <div class="flex-1 min-h-0 mt-4">
      <TabsContent value="menu" class="h-full mt-0">
        <div class="flex h-full gap-4">
          <div class="w-80 shrink-0 rounded-lg border flex flex-col overflow-hidden">
            <div class="px-3 py-2 border-b shrink-0">
              <div class="flex items-center gap-2">
                <Search class="size-4 text-muted-foreground shrink-0" />
                <Input v-model="userSearch" placeholder="搜索用户..." class="h-8 text-sm" />
              </div>
            </div>
            <div class="px-3 py-1.5 border-b shrink-0">
              <div class="flex items-center gap-2">
                <Checkbox :model-value="allUsersSelected" aria-label="全选用户" @update:model-value="toggleAllUsers" class="shrink-0 data-[state=indeterminate]:bg-primary data-[state=indeterminate]:text-primary-foreground data-[state=indeterminate]:border-primary">
                  <template #default><Check v-if="allUsersSelected === true" class="size-3.5" /><Minus v-else-if="allUsersSelected === 'indeterminate'" class="size-3.5" /><template v-else /></template>
                </Checkbox>
                <span class="text-xs text-muted-foreground">已选 {{ selectedUsers.length }} 个用户</span>
              </div>
            </div>
            <div class="flex-1 overflow-auto">
              <div v-if="filteredUsers.length === 0" class="px-3 py-6 text-center text-sm text-muted-foreground">{{ userSearch ? '无匹配用户' : '暂无用户' }}</div>
              <div v-for="user in filteredUsers" :key="user.userId" class="flex items-center gap-2 px-3 py-2 text-sm hover:bg-accent cursor-pointer border-b last:border-b-0" @click="toggleUser(user.userId)">
                <Checkbox :model-value="selectedUsers.includes(user.userId)" :aria-label="user.displayName || user.username" class="shrink-0" />
                <span class="flex-1 truncate">{{ user.displayName || user.username }}</span>
                <button class="flex size-5 shrink-0 items-center justify-center rounded transition-colors hover:bg-muted" :class="viewingId === user.userId ? 'text-primary' : 'text-muted-foreground'" :aria-label="`查看 ${user.displayName || user.username} 授权`" @click.stop="handleViewUserMenu(user.userId)"><Eye class="size-3.5" /></button>
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
              <Button size="sm" :disabled="selectedUsers.length === 0 || saving" @click="handleSaveMenu"><Save class="size-4 mr-1.5" />保存授权</Button>
            </div>
          </div>
        </div>
      </TabsContent>

      <TabsContent value="role" class="h-full mt-0">
        <div class="flex h-full gap-4">
          <div class="w-80 shrink-0 rounded-lg border flex flex-col overflow-hidden">
            <div class="px-3 py-2 border-b shrink-0"><div class="flex items-center gap-2"><Search class="size-4 text-muted-foreground shrink-0" /><Input v-model="userSearch" placeholder="搜索用户..." class="h-8 text-sm" /></div></div>
            <div class="px-3 py-1.5 border-b shrink-0"><div class="flex items-center gap-2"><Checkbox :model-value="allUsersSelected" aria-label="全选用户" @update:model-value="toggleAllUsers" class="shrink-0 data-[state=indeterminate]:bg-primary data-[state=indeterminate]:text-primary-foreground data-[state=indeterminate]:border-primary"><template #default><Check v-if="allUsersSelected === true" class="size-3.5" /><Minus v-else-if="allUsersSelected === 'indeterminate'" class="size-3.5" /><template v-else /></template></Checkbox><span class="text-xs text-muted-foreground">已选 {{ selectedUsers.length }} 个用户</span></div></div>
            <div class="flex-1 overflow-auto">
              <div v-if="filteredUsers.length === 0" class="px-3 py-6 text-center text-sm text-muted-foreground">{{ userSearch ? '无匹配用户' : '暂无用户' }}</div>
              <div v-for="user in filteredUsers" :key="user.userId" class="flex items-center gap-2 px-3 py-2 text-sm hover:bg-accent cursor-pointer border-b last:border-b-0" @click="toggleUser(user.userId)"><Checkbox :model-value="selectedUsers.includes(user.userId)" :aria-label="user.displayName || user.username" class="shrink-0" /><span class="flex-1 truncate">{{ user.displayName || user.username }}</span><button class="flex size-5 shrink-0 items-center justify-center rounded transition-colors hover:bg-muted" :class="viewingId === user.userId ? 'text-primary' : 'text-muted-foreground'" :aria-label="`查看 ${user.displayName || user.username} 角色`" @click.stop="handleViewUserRole(user.userId)"><Eye class="size-3.5" /></button></div>
            </div>
          </div>
          <div class="flex-1 min-w-0 rounded-lg border flex flex-col overflow-hidden">
            <div class="px-3 py-2 border-b shrink-0 flex items-center justify-between"><span class="text-sm font-medium">角色绑定</span><span class="text-xs text-muted-foreground">已选 {{ selectedUserRoles.size }} 个角色</span></div>
            <div class="flex-1 overflow-auto">
              <div v-for="role in roles" :key="role.roleId" class="flex items-center gap-2 px-3 py-2 text-sm hover:bg-accent cursor-pointer border-b last:border-b-0" @click="selectedUserRoles = toggleSet(selectedUserRoles, role.roleId)"><Checkbox :model-value="selectedUserRoles.has(role.roleId)" :aria-label="role.roleName" class="shrink-0" /><span>{{ role.roleName }}</span></div>
            </div>
            <div class="px-3 py-2 border-t shrink-0 flex justify-end"><Button size="sm" :disabled="selectedUsers.length === 0 || saving" @click="handleSaveRelation('role')"><Save class="size-4 mr-1.5" />保存绑定</Button></div>
          </div>
        </div>
      </TabsContent>

      <TabsContent value="post" class="h-full mt-0">
        <div class="flex h-full gap-4">
          <div class="w-80 shrink-0 rounded-lg border flex flex-col overflow-hidden">
            <div class="px-3 py-2 border-b shrink-0"><div class="flex items-center gap-2"><Search class="size-4 text-muted-foreground shrink-0" /><Input v-model="userSearch" placeholder="搜索用户..." class="h-8 text-sm" /></div></div>
            <div class="px-3 py-1.5 border-b shrink-0"><div class="flex items-center gap-2"><Checkbox :model-value="allUsersSelected" aria-label="全选用户" @update:model-value="toggleAllUsers" class="shrink-0 data-[state=indeterminate]:bg-primary data-[state=indeterminate]:text-primary-foreground data-[state=indeterminate]:border-primary"><template #default><Check v-if="allUsersSelected === true" class="size-3.5" /><Minus v-else-if="allUsersSelected === 'indeterminate'" class="size-3.5" /><template v-else /></template></Checkbox><span class="text-xs text-muted-foreground">已选 {{ selectedUsers.length }} 个用户</span></div></div>
            <div class="flex-1 overflow-auto">
              <div v-if="filteredUsers.length === 0" class="px-3 py-6 text-center text-sm text-muted-foreground">{{ userSearch ? '无匹配用户' : '暂无用户' }}</div>
              <div v-for="user in filteredUsers" :key="user.userId" class="flex items-center gap-2 px-3 py-2 text-sm hover:bg-accent cursor-pointer border-b last:border-b-0" @click="toggleUser(user.userId)"><Checkbox :model-value="selectedUsers.includes(user.userId)" :aria-label="user.displayName || user.username" class="shrink-0" /><span class="flex-1 truncate">{{ user.displayName || user.username }}</span><button class="flex size-5 shrink-0 items-center justify-center rounded transition-colors hover:bg-muted" :class="viewingId === user.userId ? 'text-primary' : 'text-muted-foreground'" :aria-label="`查看 ${user.displayName || user.username} 岗位`" @click.stop="handleViewUserPost(user.userId)"><Eye class="size-3.5" /></button></div>
            </div>
          </div>
          <div class="flex-1 min-w-0 rounded-lg border flex flex-col overflow-hidden">
            <div class="px-3 py-2 border-b shrink-0 flex items-center justify-between"><span class="text-sm font-medium">岗位绑定</span><span class="text-xs text-muted-foreground">已选 {{ selectedUserPosts.size }} 个岗位</span></div>
            <div class="flex-1 overflow-auto">
              <div v-for="post in posts" :key="post.postId" class="flex items-center gap-2 px-3 py-2 text-sm hover:bg-accent cursor-pointer border-b last:border-b-0" @click="selectedUserPosts = toggleSet(selectedUserPosts, post.postId)"><Checkbox :model-value="selectedUserPosts.has(post.postId)" :aria-label="post.postName" class="shrink-0" /><span>{{ post.postName }}</span></div>
            </div>
            <div class="px-3 py-2 border-t shrink-0 flex justify-end"><Button size="sm" :disabled="selectedUsers.length === 0 || saving" @click="handleSaveRelation('post')"><Save class="size-4 mr-1.5" />保存绑定</Button></div>
          </div>
        </div>
      </TabsContent>

      <TabsContent value="dept" class="h-full mt-0">
        <div class="flex h-full gap-4">
          <div class="w-80 shrink-0 rounded-lg border flex flex-col overflow-hidden">
            <div class="px-3 py-2 border-b shrink-0"><div class="flex items-center gap-2"><Search class="size-4 text-muted-foreground shrink-0" /><Input v-model="userSearch" placeholder="搜索用户..." class="h-8 text-sm" /></div></div>
            <div class="px-3 py-1.5 border-b shrink-0"><div class="flex items-center gap-2"><Checkbox :model-value="allUsersSelected" aria-label="全选用户" @update:model-value="toggleAllUsers" class="shrink-0 data-[state=indeterminate]:bg-primary data-[state=indeterminate]:text-primary-foreground data-[state=indeterminate]:border-primary"><template #default><Check v-if="allUsersSelected === true" class="size-3.5" /><Minus v-else-if="allUsersSelected === 'indeterminate'" class="size-3.5" /><template v-else /></template></Checkbox><span class="text-xs text-muted-foreground">已选 {{ selectedUsers.length }} 个用户</span></div></div>
            <div class="flex-1 overflow-auto">
              <div v-if="filteredUsers.length === 0" class="px-3 py-6 text-center text-sm text-muted-foreground">{{ userSearch ? '无匹配用户' : '暂无用户' }}</div>
              <div v-for="user in filteredUsers" :key="user.userId" class="flex items-center gap-2 px-3 py-2 text-sm hover:bg-accent cursor-pointer border-b last:border-b-0" @click="toggleUser(user.userId)"><Checkbox :model-value="selectedUsers.includes(user.userId)" :aria-label="user.displayName || user.username" class="shrink-0" /><span class="flex-1 truncate">{{ user.displayName || user.username }}</span><button class="flex size-5 shrink-0 items-center justify-center rounded transition-colors hover:bg-muted" :class="viewingId === user.userId ? 'text-primary' : 'text-muted-foreground'" :aria-label="`查看 ${user.displayName || user.username} 部门`" @click.stop="handleViewUserDept(user.userId)"><Eye class="size-3.5" /></button></div>
            </div>
          </div>
          <div class="flex-1 min-w-0 rounded-lg border flex flex-col overflow-hidden">
            <div class="px-3 py-2 border-b shrink-0 flex items-center justify-between"><span class="text-sm font-medium">部门绑定</span><span class="text-xs text-muted-foreground">已选 {{ selectedUserDepts.size }} 个部门</span></div>
            <div class="flex-1 overflow-auto p-3">
              <DeptTreeItem v-for="dept in deptTree" :key="dept.deptId" :dept="dept" :depth="0" :selected="Array.from(selectedUserDepts)" @toggle="handleDeptToggle" />
            </div>
            <div class="px-3 py-2 border-t shrink-0 flex justify-end"><Button size="sm" :disabled="selectedUsers.length === 0 || saving" @click="handleSaveRelation('dept')"><Save class="size-4 mr-1.5" />保存绑定</Button></div>
          </div>
        </div>
      </TabsContent>

      <TabsContent value="all" class="h-full mt-0">
        <div class="flex h-full gap-4">
          <div class="w-80 shrink-0 rounded-lg border flex flex-col overflow-hidden">
            <div class="px-3 py-2 border-b shrink-0"><div class="flex items-center gap-2"><Search class="size-4 text-muted-foreground shrink-0" /><Input v-model="userSearch" placeholder="搜索用户..." class="h-8 text-sm" /></div></div>
            <div class="flex-1 overflow-auto p-2">
              <div v-if="filteredUsers.length === 0" class="px-3 py-6 text-center text-sm text-muted-foreground">{{ userSearch ? '无匹配用户' : '暂无用户' }}</div>
              <Button v-for="user in filteredUsers" :key="user.userId" :variant="viewingId === user.userId ? 'default' : 'ghost'" class="w-full justify-center border-b last:border-b-0" @click="handleViewUserAll(user.userId)">{{ user.displayName || user.username }}</Button>
            </div>
          </div>
          <div class="flex-1 min-w-0 rounded-lg border flex flex-col overflow-hidden">
            <div class="px-3 py-2 border-b shrink-0 flex items-center justify-between"><span class="text-sm font-medium">权限总览</span><span class="text-xs text-muted-foreground">共 {{ allUserMenuIds.size }} 项</span></div>
            <div class="flex-1 overflow-auto p-3">
              <MenuTreeCheckbox :items="menuTree" :checked-ids="allUserMenuIds" @toggle="() => {}" />
            </div>
          </div>
        </div>
      </TabsContent>
    </div>
  </Tabs>
</template>
