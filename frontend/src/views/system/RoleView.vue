<script setup lang="ts">
defineOptions({ name: 'RoleIndex' })

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
import { Badge } from '@/components/ui/badge'
import { Search, RotateCcw, Plus, Trash2, Pencil, Check, Minus, X } from '@lucide/vue'
import type { RoleDTO, RolePageParams } from '@/api/role'
import type { PageResponse } from '@/api/dict'
import { fetchRoles, createRole, updateRole, deleteRole, deleteRoles } from '@/api/role'

const roles = ref<RoleDTO[]>([])
const total = ref(0)
const page = ref(1)
const size = ref(10)
const searchName = ref('')
const searchCode = ref('')
const selected = ref<string[]>([])
const dialogOpen = ref(false)
const editingRole = ref<RoleDTO | null>(null)
const defaultRoleForm = () => ({ roleName: '', roleCode: '', sortOrder: 0, remark: '' })
const formData = ref(defaultRoleForm())
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

async function loadRoles() {
  const params: RolePageParams = { page: page.value, size: size.value }
  if (searchName.value) params.roleName = searchName.value
  if (searchCode.value) params.roleCode = searchCode.value
  const res: PageResponse<RoleDTO> = await fetchRoles(params)
  roles.value = res.records
  total.value = res.total
}

function handleSearch() {
  page.value = 1
  loadRoles()
}

function handleSizeChange(val: unknown) {
  size.value = Number(val)
  page.value = 1
  loadRoles()
}

function handleJump() {
  if (!jumpPage.value) return
  const p = Math.max(1, Math.min(jumpPage.value, totalPages.value))
  page.value = p
  jumpPage.value = undefined
  loadRoles()
}

function handleDelete(roleId: string) {
  openConfirm('确定要删除该角色吗？', async () => {
    await deleteRole(roleId)
    await loadRoles()
  })
}

function handleBatchDelete() {
  if (selected.value.length === 0) return
  openConfirm(`确定要删除选中的 ${selected.value.length} 个角色吗？`, async () => {
    await deleteRoles(selected.value)
    selected.value = []
    await loadRoles()
  })
}

function toggleSelect(id: string, checked: boolean) {
  if (checked) {
    selected.value = [...selected.value, id]
  } else {
    selected.value = selected.value.filter((s) => s !== id)
  }
}

const selectableRoles = computed(() => roles.value.filter((r) => r.builtIn !== 1))

const headerChecked = computed(() => {
  if (selectableRoles.value.length === 0) return false
  const selectedSet = new Set(selected.value)
  const allSelected = selectableRoles.value.every((r) => selectedSet.has(r.roleId))
  if (allSelected) return true
  if (selectableRoles.value.some((r) => selectedSet.has(r.roleId))) return 'indeterminate' as const
  return false
})

function toggleSelectAll() {
  const ids = selectableRoles.value.map((r) => r.roleId)
  if (ids.every((id) => selected.value.includes(id))) {
    selected.value = selected.value.filter((id) => !ids.includes(id))
  } else {
    selected.value = [...new Set([...selected.value, ...ids])]
  }
}

async function handleToggleStatus(role: RoleDTO, enabled: boolean) {
  await updateRole(role.roleId, { ...role, status: enabled ? 1 : 0 })
  await loadRoles()
}

function openCreate() {
  editingRole.value = null
  formData.value = { ...defaultRoleForm(), sortOrder: total.value + 1 }
  formErrors.value = {}
  dialogOpen.value = true
}

function openEdit(role: RoleDTO) {
  editingRole.value = role
  formData.value = {
    roleName: role.roleName,
    roleCode: role.roleCode,
    sortOrder: role.sortOrder,
    remark: role.remark || '',
  }
  formErrors.value = {}
  dialogOpen.value = true
}

function validateRoleForm(): boolean {
  const errors: Record<string, string> = {}
  if (!formData.value.roleName.trim()) errors.roleName = '角色名称不能为空'
  if (!formData.value.roleCode.trim()) errors.roleCode = '角色编码不能为空'
  formErrors.value = errors
  return Object.keys(errors).length === 0
}

async function handleSave() {
  if (!validateRoleForm()) return
  const payload = {
    roleName: formData.value.roleName,
    roleCode: formData.value.roleCode,
    sortOrder: formData.value.sortOrder,
    remark: formData.value.remark,
  }
  if (editingRole.value) {
    await updateRole(editingRole.value.roleId, payload)
  } else {
    await createRole(payload)
  }
  dialogOpen.value = false
  await loadRoles()
}

onMounted(loadRoles)
</script>

<template>
  <div class="space-y-4">
    <Card>
      <CardContent class="py-4">
          <div class="flex items-end gap-4">
          <div class="flex items-center gap-2">
            <Label class="shrink-0">角色名称</Label>
            <Input v-model="searchName" name="roleName" autocomplete="off" placeholder="请输入角色名称…" class="w-48" />
          </div>
          <div class="flex items-center gap-2">
            <Label class="shrink-0">角色编码</Label>
            <Input v-model="searchCode" name="roleCode" autocomplete="off" placeholder="请输入角色编码…" class="w-48" />
          </div>
          <div class="flex gap-2">
            <Button size="sm" data-testid="search-btn" @click="handleSearch">
              <Search class="size-4" />搜索
            </Button>
            <Button size="sm" variant="outline" @click="searchName = ''; searchCode = ''; handleSearch()">
              <RotateCcw class="size-4" />重置
            </Button>
          </div>
        </div>
      </CardContent>
    </Card>

    <div class="flex items-center gap-2">
      <Button size="sm" @click="openCreate">
        <Plus class="size-4" />新增角色
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
              <TableHead>角色名称</TableHead>
              <TableHead>角色编码</TableHead>
              <TableHead>排序</TableHead>
              <TableHead>状态</TableHead>
              <TableHead>内置</TableHead>
              <TableHead class="text-center">创建时间</TableHead>
              <TableHead>备注</TableHead>
              <TableHead class="w-52 text-center">操作</TableHead>
            </TableRow>
          </TableHeader>
          <TableBody>
            <TableRow v-for="role in roles" :key="role.roleId">
              <TableCell>
                <Checkbox
                  :model-value="selected.includes(role.roleId)"
                  :disabled="role.builtIn === 1"
                  @update:model-value="toggleSelect(role.roleId, !!$event)"
                />
              </TableCell>
              <TableCell class="truncate">{{ role.roleName }}</TableCell>
              <TableCell class="truncate">{{ role.roleCode }}</TableCell>
              <TableCell class="tabular-nums">{{ role.sortOrder }}</TableCell>
              <TableCell>
                <Switch
                  size="sm"
                  :model-value="role.status === 1"
                  :disabled="role.builtIn === 1"
                  @update:model-value="(val: boolean) => handleToggleStatus(role, val)"
                />
              </TableCell>
              <TableCell>
                <Badge v-if="role.builtIn === 1" variant="secondary">是</Badge>
                <Badge v-else variant="outline">否</Badge>
              </TableCell>
              <TableCell class="tabular-nums text-center">{{ role.createdAt }}</TableCell>
              <TableCell class="truncate">{{ role.remark }}</TableCell>
              <TableCell class="text-center">
                <div class="flex justify-center gap-1">
                  <Button variant="ghost" size="sm" class="text-primary" :disabled="role.builtIn === 1" @click="openEdit(role)">
                    <Pencil class="size-4" />编辑
                  </Button>
                  <Button variant="ghost" size="sm" class="text-destructive" :disabled="role.builtIn === 1" @click="handleDelete(role.roleId)">
                    <Trash2 class="size-4" />删除
                  </Button>
                </div>
              </TableCell>
            </TableRow>
            <TableEmpty v-if="roles.length === 0" :colspan="9">暂无数据</TableEmpty>
          </TableBody>
        </Table>

        <div class="flex items-center justify-end gap-4 border-t px-4 py-3">
          <Pagination
            v-model:page="page"
            :total="total"
            :items-per-page="size"
            class="mx-0 w-auto justify-end"
            @update:page="loadRoles"
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
          <DialogTitle>{{ editingRole ? '编辑角色' : '新增角色' }}</DialogTitle>
          <DialogDescription>{{ editingRole ? '修改角色信息' : '创建一个新的角色' }}</DialogDescription>
        </DialogHeader>
        <div class="grid gap-4 py-4">
          <div class="grid grid-cols-4 items-center gap-x-4 gap-y-1">
            <Label class="justify-end">角色名称</Label>
            <Input v-model="formData.roleName" name="roleName" autocomplete="off" placeholder="请输入角色名称…" class="col-span-3" :aria-invalid="!!formErrors.roleName" />
            <template v-if="formErrors.roleName">
              <span />
              <p class="col-span-3 text-destructive text-xs">{{ formErrors.roleName }}</p>
            </template>
          </div>
          <div class="grid grid-cols-4 items-center gap-x-4 gap-y-1">
            <Label class="justify-end">角色编码</Label>
            <Input
              v-model="formData.roleCode"
              name="roleCode"
              autocomplete="off"
              placeholder="请输入角色编码…"
              class="col-span-3"
              :disabled="!!editingRole"
              :aria-invalid="!!formErrors.roleCode"
            />
            <template v-if="formErrors.roleCode">
              <span />
              <p class="col-span-3 text-destructive text-xs">{{ formErrors.roleCode }}</p>
            </template>
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
