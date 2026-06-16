<script setup lang="ts">
defineOptions({ name: 'PostAuthIndex' })

import { ref, computed, onMounted } from 'vue'
import { Input } from '@/components/ui/input'
import { Checkbox } from '@/components/ui/checkbox'
import { Button } from '@/components/ui/button'
import { Search, Save, Eye, Check, Minus } from '@lucide/vue'
import { fetchPosts } from '@/api/post'
import { fetchMenuTree } from '@/api/menu'
import { fetchAuthMenuIds, saveAuthMenu } from '@/api/auth'
import MenuTreeCheckbox from './MenuTreeCheckbox.vue'
import type { MenuDTO } from '@/api/menu'
import type { PostDTO } from '@/api/post'

const posts = ref<PostDTO[]>([])
const searchQuery = ref('')
const selectedPosts = ref<string[]>([])
const menuTree = ref<MenuDTO[]>([])
const checkedMenuIds = ref(new Set<string>())
const saving = ref(false)
const viewingId = ref<string | null>(null)

const filteredPosts = computed(() => {
  if (!searchQuery.value) return posts.value
  const q = searchQuery.value.toLowerCase()
  return posts.value.filter((p) => p.postName?.toLowerCase().includes(q) || p.postCode?.toLowerCase().includes(q))
})

const allPostsSelected = computed<boolean | 'indeterminate'>(() => {
  if (filteredPosts.value.length === 0) return false
  const n = filteredPosts.value.filter((p) => selectedPosts.value.includes(p.postId)).length
  if (n === filteredPosts.value.length) return true
  if (n > 0) return 'indeterminate'
  return false
})

function toggleAllPosts() {
  if (allPostsSelected.value) { selectedPosts.value = [] }
  else { selectedPosts.value = filteredPosts.value.map((p) => p.postId) }
}

function togglePost(postId: string) {
  const idx = selectedPosts.value.indexOf(postId)
  if (idx >= 0) { selectedPosts.value.splice(idx, 1) } else { selectedPosts.value.push(postId) }
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

async function handleViewPost(postId: string) {
  viewingId.value = postId
  checkedMenuIds.value = new Set(await fetchAuthMenuIds('post', postId))
}

async function handleSave() {
  if (saving.value) return
  const lastPost = selectedPosts.value[selectedPosts.value.length - 1]
  saving.value = true
  try {
    await saveAuthMenu('post', selectedPosts.value, Array.from(checkedMenuIds.value))
    selectedPosts.value = []
    if (lastPost) { viewingId.value = lastPost; await handleViewPost(lastPost) }
  } finally { saving.value = false }
}

onMounted(async () => {
  const [postData, menuData] = await Promise.all([fetchPosts({ page: 1, size: 1000 }), fetchMenuTree()])
  posts.value = postData.records ?? postData ?? []
  menuTree.value = menuData
})
</script>

<template>
  <div class="flex h-full gap-4">
    <div class="w-80 shrink-0 rounded-lg border flex flex-col overflow-hidden">
      <div class="px-3 py-2 border-b shrink-0">
        <div class="flex items-center gap-2">
          <Search class="size-4 text-muted-foreground shrink-0" />
          <Input v-model="searchQuery" placeholder="搜索岗位..." class="h-8 text-sm" />
        </div>
      </div>
      <div class="px-3 py-1.5 border-b shrink-0">
        <div class="flex items-center gap-2">
          <Checkbox :model-value="allPostsSelected" aria-label="全选岗位" @update:model-value="toggleAllPosts" class="shrink-0 data-[state=indeterminate]:bg-primary data-[state=indeterminate]:text-primary-foreground data-[state=indeterminate]:border-primary">
            <template #default><Check v-if="allPostsSelected === true" class="size-3.5" /><Minus v-else-if="allPostsSelected === 'indeterminate'" class="size-3.5" /><template v-else /></template>
          </Checkbox>
          <span class="text-xs text-muted-foreground">已选 {{ selectedPosts.length }} 个岗位</span>
        </div>
      </div>
      <div class="flex-1 overflow-auto">
        <div v-if="filteredPosts.length === 0" class="px-3 py-6 text-center text-sm text-muted-foreground">{{ searchQuery ? '无匹配岗位' : '暂无岗位数据' }}</div>
        <div v-for="post in filteredPosts" :key="post.postId" class="flex items-center gap-2 px-3 py-2 text-sm hover:bg-accent cursor-pointer border-b last:border-b-0" :class="viewingId === post.postId && 'bg-accent'" @click="togglePost(post.postId)">
          <Checkbox :model-value="selectedPosts.includes(post.postId)" :aria-label="post.postName" class="shrink-0" />
          <span class="flex-1 truncate">{{ post.postName }}</span>
          <button class="flex size-5 shrink-0 items-center justify-center rounded transition-colors hover:bg-muted" :class="viewingId === post.postId ? 'text-primary' : 'text-muted-foreground'" :aria-label="`查看 ${post.postName} 授权`" @click.stop="handleViewPost(post.postId)"><Eye class="size-3.5" /></button>
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
        <Button size="sm" :disabled="selectedPosts.length === 0 || saving" @click="handleSave"><Save class="size-4 mr-1.5" />保存授权</Button>
      </div>
    </div>
  </div>
</template>
