<script setup lang="ts">
defineOptions({ name: 'PostIndex' })

import { ref, computed, onMounted } from 'vue'
import {
  Table,
  TableBody,
  TableCell,
  TableEmpty,
  TableHead,
  TableHeader,
  TableRow,
} from '@/components/ui/table'
import { Card, CardContent } from '@/components/ui/card'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Checkbox } from '@/components/ui/checkbox'
import { Label } from '@/components/ui/label'
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
} from '@/components/ui/dialog'
import {
  AlertDialog,
  AlertDialogAction,
  AlertDialogCancel,
  AlertDialogContent,
  AlertDialogDescription,
  AlertDialogFooter,
  AlertDialogHeader,
  AlertDialogTitle,
} from '@/components/ui/alert-dialog'
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select'
import {
  Pagination,
  PaginationContent,
  PaginationEllipsis,
  PaginationItem,
  PaginationNext,
  PaginationPrevious,
} from '@/components/ui/pagination'
import { Switch } from '@/components/ui/switch'

import { Search, RotateCcw, Plus, Trash2, Pencil, Check, Minus, X } from '@lucide/vue'
import type { PostDTO, PostPageParams } from '@/api/post'
import { fetchPosts, createPost, updatePost, deletePost, deletePosts } from '@/api/post'
import { dict } from '@/dict'

const posts = ref<PostDTO[]>([])
const total = ref(0)
const page = ref(1)
const size = ref(10)
const searchName = ref('')
const searchCode = ref('')
const searchLevel = ref('')
const selected = ref<string[]>([])
const dialogOpen = ref(false)
const editingPost = ref<PostDTO | null>(null)
const defaultPostForm = () => ({ postName: '', postCode: '', postLevel: '1', sortOrder: 0, remark: '' })
const formData = ref(defaultPostForm())
const jumpPage = ref<number | undefined>()
const totalPages = computed(() => Math.ceil(total.value / size.value) || 1)
const formErrors = ref<Record<string, string>>({})
const confirmOpen = ref(false)
const confirmMessage = ref('')
const confirmAction = ref<(() => Promise<void>) | null>(null)


function openConfirm(message: string, action: () => Promise<void>) {
  confirmMessage.value = message
  confirmAction.value = action
  confirmOpen.value = true
}

async function executeConfirm() {
  if (confirmAction.value) await confirmAction.value()
  confirmOpen.value = false
}

async function loadPosts() {
  const params: PostPageParams = { page: page.value, size: size.value }
  if (searchName.value) params.postName = searchName.value
  if (searchCode.value) params.postCode = searchCode.value
  if (searchLevel.value) params.postLevel = Number(searchLevel.value)
  const res: PageResponse<PostDTO> = await fetchPosts(params)
  posts.value = res.records
  total.value = res.total
}

function handleSearch() {
  page.value = 1
  loadPosts()
}

function handleSizeChange(val: unknown) {
  size.value = Number(val)
  page.value = 1
  loadPosts()
}

function handleJump() {
  if (!jumpPage.value) return
  const p = Math.max(1, Math.min(jumpPage.value, totalPages.value))
  page.value = p
  jumpPage.value = undefined
  loadPosts()
}

function handleDelete(postId: string) {
  openConfirm('确定要删除该岗位吗？', async () => {
    await deletePost(postId)
    await loadPosts()
  })
}

function handleBatchDelete() {
  if (selected.value.length === 0) return
  openConfirm(`确定要删除选中的 ${selected.value.length} 个岗位吗？`, async () => {
    await deletePosts(selected.value)
    selected.value = []
    await loadPosts()
  })
}

function toggleSelect(id: string, checked: boolean) {
  if (checked) {
    selected.value = [...selected.value, id]
  } else {
    selected.value = selected.value.filter((s) => s !== id)
  }
}

const selectablePosts = computed(() => posts.value.filter((p) => p.builtIn !== 1))

const headerChecked = computed(() => {
  if (selectablePosts.value.length === 0) return false
  const selectedSet = new Set(selected.value)
  const allSelected = selectablePosts.value.every((p) => selectedSet.has(p.postId))
  if (allSelected) return true
  if (selectablePosts.value.some((p) => selectedSet.has(p.postId))) return 'indeterminate' as const
  return false
})

function toggleSelectAll() {
  const ids = selectablePosts.value.map((p) => p.postId)
  if (ids.every((id) => selected.value.includes(id))) {
    selected.value = selected.value.filter((id) => !ids.includes(id))
  } else {
    selected.value = [...new Set([...selected.value, ...ids])]
  }
}

async function handleToggleStatus(post: PostDTO, enabled: boolean) {
  await updatePost(post.postId, { ...post, status: enabled ? 1 : 0 })
  await loadPosts()
}

function openCreate() {
  editingPost.value = null
  formData.value = { ...defaultPostForm(), sortOrder: total.value + 1 }
  formErrors.value = {}
  dialogOpen.value = true
}

function openEdit(post: PostDTO) {
  editingPost.value = post
  formData.value = {
    postName: post.postName,
    postCode: post.postCode,
    postLevel: String(post.postLevel),
    sortOrder: post.sortOrder,
    remark: post.remark || '',
  }
  formErrors.value = {}
  dialogOpen.value = true
}

function validatePostForm(): boolean {
  const errors: Record<string, string> = {}
  if (!formData.value.postName.trim()) errors.postName = '岗位名称不能为空'
  if (!formData.value.postCode.trim()) errors.postCode = '岗位编码不能为空'
  formErrors.value = errors
  return Object.keys(errors).length === 0
}

async function handleSave() {
  if (!validatePostForm()) return
  const payload = {
    postName: formData.value.postName,
    postCode: formData.value.postCode,
    postLevel: Number(formData.value.postLevel),
    sortOrder: formData.value.sortOrder,
    remark: formData.value.remark,
  }
  if (editingPost.value) {
    await updatePost(editingPost.value.postId, payload)
  } else {
    await createPost(payload)
  }
  dialogOpen.value = false
  await loadPosts()
}

onMounted(loadPosts)
</script>

<template>
  <div class="space-y-4">
    <Card class="py-4">
      <CardContent>
          <div class="grid grid-cols-4 gap-4">
          <div class="flex items-center gap-2">
            <Label class="shrink-0">岗位名称</Label>
            <Input v-model="searchName" name="postName" autocomplete="off" placeholder="请输入岗位名称…" class="flex-1" />
          </div>
          <div class="flex items-center gap-2">
            <Label class="shrink-0">岗位编码</Label>
            <Input v-model="searchCode" name="postCode" autocomplete="off" placeholder="请输入岗位编码…" class="flex-1" />
          </div>
          <div class="flex items-center gap-2">
            <Label class="shrink-0">岗位级别</Label>
            <Select :model-value="searchLevel || '__all__'" @update:model-value="(v: any) => { searchLevel = v === '__all__' ? '' : String(v) }">
              <SelectTrigger class="flex-1">
                <SelectValue placeholder="全部" />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="__all__">全部</SelectItem>
                <SelectItem v-for="item in dict.postLevel.items.value" :key="item.itemKey" :value="item.itemKey">
                  {{ item.itemLabel }}
                </SelectItem>
              </SelectContent>
            </Select>
          </div>
          <div class="flex gap-2">
            <Button size="sm" data-testid="search-btn" @click="handleSearch">
              <Search class="size-4" />搜索
            </Button>
            <Button size="sm" variant="outline" @click="searchName = ''; searchCode = ''; searchLevel = ''; handleSearch()">
              <RotateCcw class="size-4" />重置
            </Button>
          </div>
        </div>
      </CardContent>
    </Card>

    <div class="flex items-center gap-2">
      <Button size="sm" @click="openCreate">
        <Plus class="size-4" />新增岗位
      </Button>
      <Button
        v-if="selected.length > 0"
        size="sm"
        variant="destructive"
        @click="handleBatchDelete"
      >
        <Trash2 class="size-4" />批量删除 ({{ selected.length }})
      </Button>
    </div>

    <Card>
      <CardContent class="p-0">
        <Table class="table-fixed">
          <TableHeader>
            <TableRow>
              <TableHead class="w-10">
                <Checkbox
                  :model-value="headerChecked"
                  class="data-[state=indeterminate]:bg-primary data-[state=indeterminate]:text-primary-foreground data-[state=indeterminate]:border-primary"
                  @update:model-value="toggleSelectAll"
                >
                  <template #default="{ state }">
                    <Minus v-if="state === 'indeterminate'" class="size-3.5" />
                    <Check v-else class="size-3.5" />
                  </template>
                </Checkbox>
              </TableHead>
              <TableHead>岗位名称</TableHead>
              <TableHead>岗位编码</TableHead>
              <TableHead class="text-center">岗位级别</TableHead>
              <TableHead>排序</TableHead>
              <TableHead>状态</TableHead>
              <TableHead>内置</TableHead>
              <TableHead class="text-center">创建时间</TableHead>
              <TableHead>备注</TableHead>
              <TableHead class="w-52 text-center">操作</TableHead>
            </TableRow>
          </TableHeader>
          <TableBody>
            <TableRow v-for="post in posts" :key="post.postId">
              <TableCell>
                <Checkbox
                  :model-value="selected.includes(post.postId)"
                  :disabled="post.builtIn === 1"
                  @update:model-value="toggleSelect(post.postId, !!$event)"
                />
              </TableCell>
              <TableCell class="truncate">{{ post.postName }}</TableCell>
              <TableCell class="truncate">{{ post.postCode }}</TableCell>
              <TableCell class="text-center">{{ dict.postLevel.getLabel(String(post.postLevel)) }}</TableCell>
              <TableCell class="tabular-nums">{{ post.sortOrder }}</TableCell>
              <TableCell>
                <Switch
                  size="sm"
                  :model-value="post.status === 1"
                  :disabled="post.builtIn === 1"
                  @update:model-value="(val: boolean) => handleToggleStatus(post, val)"
                />
              </TableCell>
              <TableCell>
                {{ dict.builtIn.getLabel(String(post.builtIn)) }}
              </TableCell>
              <TableCell class="tabular-nums text-center">{{ post.createdAt }}</TableCell>
              <TableCell class="truncate">{{ post.remark }}</TableCell>
              <TableCell class="text-center">
                <div class="flex justify-center gap-1">
                  <Button variant="ghost" size="sm" class="text-primary" :disabled="post.builtIn === 1" @click="openEdit(post)">
                    <Pencil class="size-4" />编辑
                  </Button>
                  <Button variant="ghost" size="sm" class="text-destructive" :disabled="post.builtIn === 1" @click="handleDelete(post.postId)">
                    <Trash2 class="size-4" />删除
                  </Button>
                </div>
              </TableCell>
            </TableRow>
            <TableEmpty v-if="posts.length === 0" :colspan="10">暂无数据</TableEmpty>
          </TableBody>
        </Table>

        <div class="flex items-center justify-end gap-4 border-t px-4 py-3">
          <Pagination
            v-model:page="page"
            :total="total"
            :items-per-page="size"
            class="mx-0 w-auto justify-end"
            @update:page="loadPosts"
          >
            <PaginationContent v-slot="{ items }">
              <PaginationPrevious />
              <template v-for="(item, index) in items" :key="index">
                <PaginationItem
                  v-if="item.type === 'page'"
                  :value="item.value"
                  :is-active="item.value === page"
                />
                <PaginationEllipsis v-else :index="index" />
              </template>
              <PaginationNext />
            </PaginationContent>
          </Pagination>
          <div class="flex items-center gap-1.5">
            <span class="shrink-0 text-sm">每页</span>
            <Select :model-value="String(size)" @update:model-value="handleSizeChange">
              <SelectTrigger class="h-8 w-[70px]">
                <SelectValue />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="10">10</SelectItem>
                <SelectItem value="20">20</SelectItem>
                <SelectItem value="50">50</SelectItem>
                <SelectItem value="100">100</SelectItem>
              </SelectContent>
            </Select>
            <span class="shrink-0 text-sm">条</span>
          </div>
          <div class="flex items-center gap-1.5">
            <span class="shrink-0 text-sm">跳至</span>
            <Input
              v-model.number="jumpPage"
              type="number"
              name="jumpPage"
              autocomplete="off"
              aria-label="跳转页码"
              class="h-8 w-16 text-center tabular-nums"
              :min="1"
              :max="totalPages"
              @keydown.enter="handleJump"
            />
            <span class="shrink-0 text-sm">页</span>
          </div>
          <span class="text-muted-foreground shrink-0 text-sm tabular-nums">共 {{ total }} 条</span>
        </div>
      </CardContent>
    </Card>

    <Dialog v-model:open="dialogOpen">
      <DialogContent class="sm:max-w-md">
        <DialogHeader>
          <DialogTitle>{{ editingPost ? '编辑岗位' : '新增岗位' }}</DialogTitle>
          <DialogDescription>{{ editingPost ? '修改岗位信息' : '创建一个新的岗位' }}</DialogDescription>
        </DialogHeader>
        <div class="grid gap-4 py-4">
          <div class="grid grid-cols-4 items-center gap-x-4 gap-y-1">
            <Label class="justify-end">岗位名称</Label>
            <Input v-model="formData.postName" name="postName" autocomplete="off" placeholder="请输入岗位名称…" class="col-span-3" :aria-invalid="!!formErrors.postName" />
            <template v-if="formErrors.postName">
              <span />
              <p class="col-span-3 text-destructive text-xs">{{ formErrors.postName }}</p>
            </template>
          </div>
          <div class="grid grid-cols-4 items-center gap-x-4 gap-y-1">
            <Label class="justify-end">岗位编码</Label>
            <Input
              v-model="formData.postCode"
              name="postCode"
              autocomplete="off"
              placeholder="请输入岗位编码…"
              class="col-span-3"
              :disabled="!!editingPost"
              :aria-invalid="!!formErrors.postCode"
            />
            <template v-if="formErrors.postCode">
              <span />
              <p class="col-span-3 text-destructive text-xs">{{ formErrors.postCode }}</p>
            </template>
          </div>
          <div class="grid grid-cols-4 items-center gap-4">
            <Label class="justify-end">岗位级别</Label>
            <Select v-model="formData.postLevel">
              <SelectTrigger class="col-span-3 w-full">
                <SelectValue placeholder="选择岗位级别" />
              </SelectTrigger>
              <SelectContent>
                <SelectItem v-for="item in dict.postLevel.items.value" :key="item.itemKey" :value="item.itemKey">
                  {{ item.itemLabel }}
                </SelectItem>
              </SelectContent>
            </Select>
          </div>
          <div class="grid grid-cols-4 items-center gap-4">
            <Label class="justify-end">排序</Label>
            <Input v-model.number="formData.sortOrder" type="number" name="sortOrder" autocomplete="off" placeholder="排序号" class="col-span-3" />
          </div>
          <div class="grid grid-cols-4 items-center gap-4">
            <Label class="justify-end">备注</Label>
            <Input v-model="formData.remark" name="remark" autocomplete="off" placeholder="请输入备注…" class="col-span-3" />
          </div>
        </div>
        <DialogFooter>
          <Button size="sm" variant="outline" @click="dialogOpen = false">
            <X class="size-4" />取消
          </Button>
          <Button size="sm" @click="handleSave">
            <Check class="size-4" />保存
          </Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>

    <AlertDialog v-model:open="confirmOpen">
      <AlertDialogContent :disable-outside-pointer-events="true">
        <AlertDialogHeader>
          <AlertDialogTitle>确认操作</AlertDialogTitle>
          <AlertDialogDescription>{{ confirmMessage }}</AlertDialogDescription>
        </AlertDialogHeader>
        <AlertDialogFooter>
          <AlertDialogCancel size="sm"><X class="size-4" />取消</AlertDialogCancel>
          <AlertDialogAction size="sm" @click="executeConfirm"><Check class="size-4" />确定</AlertDialogAction>
        </AlertDialogFooter>
      </AlertDialogContent>
    </AlertDialog>
  </div>
</template>
