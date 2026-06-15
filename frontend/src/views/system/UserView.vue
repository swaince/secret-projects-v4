<script setup lang="ts">
defineOptions({ name: 'UserIndex' })

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
import { Popover, PopoverContent, PopoverTrigger } from '@/components/ui/popover'
import { Calendar } from '@/components/ui/calendar'

import { parseCalendarDate, formatCalendarDate } from '@/utils/date'
import { Search, RotateCcw, Plus, Trash2, Pencil, Check, Minus, X, Eye, EyeOff, CalendarIcon } from '@lucide/vue'
import type { UserDTO, UserPageParams } from '@/api/user'
import { fetchUsers, createUser, updateUser, deleteUser, deleteUsers } from '@/api/user'
import { dict } from '@/dict'

const users = ref<UserDTO[]>([])
const total = ref(0)
const page = ref(1)
const size = ref(10)
const searchForm = ref({ username: '', displayName: '', gender: '', status: '' })
const selected = ref<string[]>([])
const dialogOpen = ref(false)
const editingUser = ref<UserDTO | null>(null)
const defaultUserForm = () => ({ username: '', displayName: '', password: '', gender: '', status: 1, accountExpireTime: '' })
const showPassword = ref(false)
const calendarOpen = ref(false)
const calendarValue = ref<import('@internationalized/date').CalendarDate | undefined>()
const formData = ref(defaultUserForm())
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

async function loadUsers() {
  const params: UserPageParams = { page: page.value, size: size.value }
  if (searchForm.value.username) params.username = searchForm.value.username
  if (searchForm.value.displayName) params.displayName = searchForm.value.displayName
  if (searchForm.value.gender) params.gender = searchForm.value.gender
  if (searchForm.value.status) params.status = Number(searchForm.value.status)
  const res: PageResponse<UserDTO> = await fetchUsers(params)
  users.value = res.records
  total.value = res.total
}

function handleSearch() {
  page.value = 1
  loadUsers()
}

function resetSearch() {
  searchForm.value = { username: '', displayName: '', gender: '', status: '' }
  handleSearch()
}

function handleSizeChange(val: unknown) {
  size.value = Number(val)
  page.value = 1
  loadUsers()
}

function handleJump() {
  if (!jumpPage.value) return
  const p = Math.max(1, Math.min(jumpPage.value, totalPages.value))
  page.value = p
  jumpPage.value = undefined
  loadUsers()
}

function handleDelete(userId: string) {
  openConfirm('确定要删除该用户吗？', async () => {
    await deleteUser(userId)
    await loadUsers()
  })
}

function handleBatchDelete() {
  if (selected.value.length === 0) return
  openConfirm(`确定要删除选中的 ${selected.value.length} 个用户吗？`, async () => {
    await deleteUsers(selected.value)
    selected.value = []
    await loadUsers()
  })
}

function toggleSelect(id: string, checked: boolean) {
  if (checked) {
    selected.value = [...selected.value, id]
  } else {
    selected.value = selected.value.filter((s) => s !== id)
  }
}

const headerChecked = computed(() => {
  if (users.value.length === 0) return false
  const selectedSet = new Set(selected.value)
  const allSelected = users.value.every((u) => selectedSet.has(u.userId))
  if (allSelected) return true
  if (users.value.some((u) => selectedSet.has(u.userId))) return 'indeterminate' as const
  return false
})

function toggleSelectAll() {
  const ids = users.value.map((u) => u.userId)
  if (ids.every((id) => selected.value.includes(id))) {
    selected.value = selected.value.filter((id) => !ids.includes(id))
  } else {
    selected.value = [...new Set([...selected.value, ...ids])]
  }
}

async function handleToggleStatus(user: UserDTO, enabled: boolean) {
  await updateUser(user.userId, { ...user, status: enabled ? 1 : 0 })
  await loadUsers()
}

function openCreate() {
  editingUser.value = null
  formData.value = defaultUserForm()
  formErrors.value = {}
  calendarValue.value = undefined
  dialogOpen.value = true
}

function openEdit(user: UserDTO) {
  editingUser.value = user
  formData.value = {
    username: user.username,
    displayName: user.displayName ?? '',
    password: '',
    gender: user.gender,
    status: user.status,
    accountExpireTime: user.accountExpireTime ?? '',
  }
  calendarValue.value = parseCalendarDate(user.accountExpireTime)
  formErrors.value = {}
  dialogOpen.value = true
}

function validateUserForm(): boolean {
  const errors: Record<string, string> = {}
  if (!formData.value.username.trim()) errors.username = '用户名不能为空'
  if (!editingUser.value && !formData.value.password.trim()) errors.password = '密码不能为空'
  formErrors.value = errors
  return Object.keys(errors).length === 0
}

async function handleSave() {
  if (!validateUserForm()) return
  const payload: Record<string, unknown> = {
    username: formData.value.username,
    displayName: formData.value.displayName || null,
    gender: formData.value.gender,
    status: formData.value.status,
    accountExpireTime: formData.value.accountExpireTime || null,
  }
  if (formData.value.password) payload.password = formData.value.password
  if (editingUser.value) {
    await updateUser(editingUser.value.userId, payload)
  } else {
    await createUser(payload as Partial<UserDTO> & { password: string })
  }
  dialogOpen.value = false
  await loadUsers()
}

onMounted(loadUsers)
</script>

<template>
  <div class="space-y-4">
    <Card>
      <CardContent class="py-4">
        <div class="flex flex-wrap items-end gap-4">
          <div class="flex items-center gap-2">
            <Label class="shrink-0">用户名</Label>
            <Input v-model="searchForm.username" name="username" autocomplete="off" placeholder="请输入用户名…" class="w-40" />
          </div>
          <div class="flex items-center gap-2">
            <Label class="shrink-0">显示名</Label>
            <Input v-model="searchForm.displayName" name="displayName" autocomplete="off" placeholder="请输入显示名…" class="w-40" />
          </div>
          <div class="flex items-center gap-2">
            <Label class="shrink-0">性别</Label>
            <Select :model-value="searchForm.gender || '__all__'" @update:model-value="(v: any) => { searchForm.gender = v === '__all__' ? '' : String(v) }">
              <SelectTrigger class="w-40">
                <SelectValue placeholder="全部" />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="__all__">全部</SelectItem>
                <SelectItem v-for="item in dict.gender.items.value" :key="item.itemKey" :value="item.itemKey">
                  {{ item.itemLabel }}
                </SelectItem>
              </SelectContent>
            </Select>
          </div>
          <div class="flex items-center gap-2">
            <Label class="shrink-0">状态</Label>
            <Select :model-value="searchForm.status || '__all__'" @update:model-value="(v: any) => { searchForm.status = v === '__all__' ? '' : String(v) }">
              <SelectTrigger class="w-40">
                <SelectValue placeholder="全部" />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="__all__">全部</SelectItem>
                <SelectItem value="1">启用</SelectItem>
                <SelectItem value="0">禁用</SelectItem>
              </SelectContent>
            </Select>
          </div>
          <div class="flex gap-2">
            <Button size="sm" data-testid="search-btn" @click="handleSearch">
              <Search class="size-4" />搜索
            </Button>
            <Button size="sm" variant="outline" @click="resetSearch()">
              <RotateCcw class="size-4" />重置
            </Button>
          </div>
        </div>
      </CardContent>
    </Card>

    <div class="flex items-center gap-2">
      <Button size="sm" @click="openCreate">
        <Plus class="size-4" />新增用户
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
              <TableHead>用户名</TableHead>
              <TableHead>显示名</TableHead>
              <TableHead class="text-center">性别</TableHead>
              <TableHead>状态</TableHead>
              <TableHead class="text-center">账号过期时间</TableHead>
              <TableHead class="text-center">最近登录</TableHead>
              <TableHead class="text-center">创建时间</TableHead>
              <TableHead class="w-52 text-center">操作</TableHead>
            </TableRow>
          </TableHeader>
          <TableBody>
            <TableRow v-for="user in users" :key="user.userId">
              <TableCell>
                <Checkbox
                  :model-value="selected.includes(user.userId)"
                  :aria-label="`选择用户 ${user.username}`"
                  @update:model-value="toggleSelect(user.userId, !!$event)"
                />
              </TableCell>
              <TableCell class="truncate">{{ user.username }}</TableCell>
              <TableCell class="truncate">{{ user.displayName }}</TableCell>
              <TableCell class="text-center">{{ dict.gender.getLabel(user.gender) }}</TableCell>
              <TableCell>
                <Switch
                  size="sm"
                  :model-value="user.status === 1"
                  :aria-label="`${user.username} 启用状态`"
                  @update:model-value="(val: boolean) => handleToggleStatus(user, val)"
                />
              </TableCell>
              <TableCell class="tabular-nums text-center">{{ user.accountExpireTime }}</TableCell>
              <TableCell class="tabular-nums text-center">{{ user.lastLoginTime }}</TableCell>
              <TableCell class="tabular-nums text-center">{{ user.createdAt }}</TableCell>
              <TableCell class="text-center">
                <div class="flex justify-center gap-1">
                  <Button variant="ghost" size="sm" class="text-primary" @click="openEdit(user)">
                    <Pencil class="size-4" />编辑
                  </Button>
                  <Button variant="ghost" size="sm" class="text-destructive" @click="handleDelete(user.userId)">
                    <Trash2 class="size-4" />删除
                  </Button>
                </div>
              </TableCell>
            </TableRow>
            <TableEmpty v-if="users.length === 0" :colspan="9">暂无数据</TableEmpty>
          </TableBody>
        </Table>

        <div class="flex items-center justify-end gap-4 border-t px-4 py-3">
          <Pagination
            v-model:page="page"
            :total="total"
            :items-per-page="size"
            class="mx-0 w-auto justify-end"
            @update:page="loadUsers"
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
          <DialogTitle>{{ editingUser ? '编辑用户' : '新增用户' }}</DialogTitle>
          <DialogDescription>{{ editingUser ? '修改用户信息' : '创建一个新的用户' }}</DialogDescription>
        </DialogHeader>
        <div class="grid gap-4 py-4">
          <div class="grid grid-cols-4 items-center gap-x-4 gap-y-1">
            <Label class="justify-end">用户名</Label>
            <Input v-model="formData.username" name="username" autocomplete="off" placeholder="请输入用户名…" class="col-span-3" :aria-invalid="!!formErrors.username" />
            <template v-if="formErrors.username">
              <span />
              <p class="col-span-3 text-destructive text-xs">{{ formErrors.username }}</p>
            </template>
          </div>
          <div class="grid grid-cols-4 items-center gap-4">
            <Label class="justify-end">显示名</Label>
            <Input v-model="formData.displayName" name="displayName" autocomplete="off" placeholder="请输入显示名…" class="col-span-3" />
          </div>
          <div class="grid grid-cols-4 items-center gap-x-4 gap-y-1">
            <Label class="justify-end">密码</Label>
            <div class="col-span-3 relative">
              <Input
                v-model="formData.password"
                :type="showPassword ? 'text' : 'password'"
                name="password"
                autocomplete="off"
                :placeholder="editingUser ? '留空则不修改密码' : '请输入密码…'"
                class="pr-9"
                :aria-invalid="!!formErrors.password"
              />
              <button
                type="button"
                :aria-label="showPassword ? '隐藏密码' : '显示密码'"
                class="absolute right-2 top-1/2 -translate-y-1/2 text-muted-foreground hover:text-foreground"
                @click="showPassword = !showPassword"
              >
                <Eye v-if="showPassword" class="size-4" />
                <EyeOff v-else class="size-4" />
              </button>
            </div>
            <template v-if="formErrors.password">
              <span />
              <p class="col-span-3 text-destructive text-xs">{{ formErrors.password }}</p>
            </template>
          </div>
          <div class="grid grid-cols-4 items-center gap-4">
            <Label class="justify-end">性别</Label>
            <Select v-model="formData.gender">
              <SelectTrigger class="col-span-3 w-full">
                <SelectValue placeholder="选择性别" />
              </SelectTrigger>
              <SelectContent>
                <SelectItem v-for="item in dict.gender.items.value" :key="item.itemKey" :value="item.itemKey">
                  {{ item.itemLabel }}
                </SelectItem>
              </SelectContent>
            </Select>
          </div>
          <div class="grid grid-cols-4 items-center gap-4">
            <Label class="justify-end">账号过期</Label>
            <Popover v-model:open="calendarOpen">
              <PopoverTrigger as-child>
                <Button variant="outline" class="col-span-3 justify-start text-left font-normal" :class="{ 'text-muted-foreground': !formData.accountExpireTime }">
                  <CalendarIcon class="mr-2 size-4" />
                  {{ formData.accountExpireTime || '选择日期（可留空）' }}
                </Button>
              </PopoverTrigger>
              <PopoverContent class="w-auto p-0" align="start">
                <Calendar v-model="calendarValue" locale="zh-CN" />
                <div class="flex justify-end gap-2 border-t p-2">
                  <Button size="sm" variant="ghost" @click="calendarValue = undefined; formData.accountExpireTime = ''; calendarOpen = false">
                    清除
                  </Button>
                  <Button size="sm" @click="formData.accountExpireTime = formatCalendarDate(calendarValue); calendarOpen = false">
                    确认
                  </Button>
                </div>
              </PopoverContent>
            </Popover>
          </div>
          <div class="grid grid-cols-4 items-center gap-4">
            <Label class="justify-end">状态</Label>
            <Switch
              :model-value="formData.status === 1"
              @update:model-value="(val: boolean) => { formData.status = val ? 1 : 0 }"
            />
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
