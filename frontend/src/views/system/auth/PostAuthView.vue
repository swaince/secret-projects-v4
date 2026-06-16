<script setup lang="ts">
defineOptions({ name: 'PostAuthView' })

import { ref, computed, watch, onMounted } from 'vue'
import { Button } from '@/components/ui/button'
import { Checkbox } from '@/components/ui/checkbox'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { Input } from '@/components/ui/input'
import { toast } from 'vue-sonner'
import { Save, Search, ArrowRight } from '@lucide/vue'
import { fetchPosts } from '@/api/post'
import type { PostDTO } from '@/api/post'
import { fetchMenuTree } from '@/api/menu'
import type { MenuDTO } from '@/api/menu'
import { fetchAuthMenuIds, saveAuthMenu } from '@/api/auth'
import MenuTreeCheckbox from './MenuTreeCheckbox.vue'

const posts = ref<PostDTO[]>([])
const postSearch = ref('')
const selectedPosts = ref<string[]>([])
const menuTree = ref<MenuDTO[]>([])
const checkedMenuIds = ref(new Set<string>())

const filteredPosts = computed(() => {
  if (!postSearch.value) return posts.value
  const q = postSearch.value.toLowerCase()
  return posts.value.filter((p) => p.postName.toLowerCase().includes(q))
})

const allPostsSelected = computed(
  () =>
    filteredPosts.value.length > 0 &&
    filteredPosts.value.every((p) => selectedPosts.value.includes(p.postId)),
)

function toggleAllPosts() {
  if (allPostsSelected.value) {
    selectedPosts.value = selectedPosts.value.filter(
      (id) => !filteredPosts.value.some((p) => p.postId === id),
    )
  } else {
    const idsToAdd = filteredPosts.value
      .filter((p) => !selectedPosts.value.includes(p.postId))
      .map((p) => p.postId)
    selectedPosts.value = [...selectedPosts.value, ...idsToAdd]
  }
}

function togglePost(postId: string) {
  if (selectedPosts.value.includes(postId)) {
    selectedPosts.value = selectedPosts.value.filter((id) => id !== postId)
  } else {
    selectedPosts.value = [...selectedPosts.value, postId]
  }
}

function intersect<T>(arrays: T[][]): Set<T> {
  if (arrays.length === 0) return new Set()
  const first = arrays[0]!
  return new Set(first.filter((item) => arrays.every((arr) => arr.includes(item))))
}

async function loadData() {
  const [postRes, tree] = await Promise.all([
    fetchPosts({ page: 1, size: 9999 }),
    fetchMenuTree(),
  ])
  posts.value = postRes.records
  menuTree.value = tree
}

async function loadMenuAuth() {
  if (selectedPosts.value.length === 0) {
    checkedMenuIds.value = new Set()
    return
  }
  const results = await Promise.all(
    selectedPosts.value.map((postId) => fetchAuthMenuIds('post', postId)),
  )
  checkedMenuIds.value = intersect(results)
}

watch(selectedPosts, loadMenuAuth, { deep: true })

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
  if (selectedPosts.value.length === 0) return
  await saveAuthMenu('post', selectedPosts.value, Array.from(checkedMenuIds.value))
  toast.success('岗位授权保存成功')
}

onMounted(loadData)
</script>

<template>
  <Card>
    <CardHeader class="pb-2">
      <CardTitle>岗位授权</CardTitle>
    </CardHeader>
    <CardContent>
      <div class="grid grid-cols-[1fr_auto_2fr] gap-4 items-start">
        <div class="rounded-lg border">
          <div class="px-3 py-2 border-b">
            <div class="flex items-center gap-2">
              <Search class="size-4 text-muted-foreground shrink-0" />
              <Input v-model="postSearch" placeholder="搜索岗位..." class="h-8 text-sm" />
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
                >已选 {{ selectedPosts.length }} 个岗位</span
              >
            </div>
          </div>
          <div class="overflow-auto max-h-[420px]">
            <div
              v-if="filteredPosts.length === 0"
              class="px-3 py-6 text-center text-sm text-muted-foreground"
            >
              暂无岗位
            </div>
            <label
              v-for="post in filteredPosts"
              :key="post.postId"
              :for="`post-item-${post.postId}`"
              class="flex items-center gap-2 px-3 py-1.5 hover:bg-accent cursor-pointer"
            >
              <Checkbox
                :id="`post-item-${post.postId}`"
                :model-value="selectedPosts.includes(post.postId)"
                :aria-label="post.postName"
                @update:model-value="togglePost(post.postId)"
              />
              <span class="text-sm truncate">{{ post.postName }}</span>
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
            <template v-if="selectedPosts.length === 0">
              <div class="px-3 py-6 text-center text-sm text-muted-foreground">
                请先选择岗位
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
        <Button :disabled="selectedPosts.length === 0" @click="handleSave">
          <Save class="size-4" />授权
        </Button>
      </div>
    </CardContent>
  </Card>
</template>
