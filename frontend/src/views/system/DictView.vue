<script setup lang="ts">
defineOptions({ name: 'DictIndex' })

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
import {
  Card,
  CardContent,
} from '@/components/ui/card'
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
import { Search, RotateCcw, Plus, Trash2, Pencil, Eye, Check, Minus, X } from '@lucide/vue'
import type { DictDTO, DictPageParams } from '@/api/dict'
import { fetchDicts, createDict, updateDict, deleteDict, deleteDicts } from '@/api/dict'
import DictItemDrawer from '@/components/system/DictItemDrawer.vue'

const dicts = ref<DictDTO[]>([])
const total = ref(0)
const page = ref(1)
const size = ref(10)
const searchName = ref('')
const searchType = ref('')
const selected = ref<string[]>([])
const drawerOpen = ref(false)
const currentDict = ref<DictDTO | null>(null)
const dialogOpen = ref(false)
const editingDict = ref<DictDTO | null>(null)
const defaultDictForm = () => ({ dictName: '', dictCode: '', dataValueType: 'STRING', remark: '' })
const formData = ref(defaultDictForm())
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

async function loadDicts() {
  const params: DictPageParams = { page: page.value, size: size.value }
  if (searchName.value) params.dictName = searchName.value
  if (searchType.value) params.dataValueType = searchType.value
  const res: PageResponse<DictDTO> = await fetchDicts(params)
  dicts.value = res.records
  total.value = res.total
}

function handleSearch() {
  page.value = 1
  loadDicts()
}

function handleSizeChange(val: unknown) {
  size.value = Number(val)
  page.value = 1
  loadDicts()
}

function handleJump() {
  if (!jumpPage.value) return
  const p = Math.max(1, Math.min(jumpPage.value, totalPages.value))
  page.value = p
  jumpPage.value = undefined
  loadDicts()
}

function handleRowClick(dict: DictDTO) {
  currentDict.value = dict
  drawerOpen.value = true
}

function handleDelete(dictId: string) {
  openConfirm('确定要删除该字典及其所有字典项吗？', async () => {
    await deleteDict(dictId)
    await loadDicts()
  })
}

function handleBatchDelete() {
  if (selected.value.length === 0) return
  openConfirm(`确定要删除选中的 ${selected.value.length} 个字典吗？`, async () => {
    await deleteDicts(selected.value)
    selected.value = []
    await loadDicts()
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
  if (dicts.value.length === 0) return false
  if (selected.value.length === dicts.value.length) return true
  if (selected.value.length > 0) return 'indeterminate' as const
  return false
})

function toggleSelectAll() {
  if (selected.value.length === dicts.value.length) {
    selected.value = []
  } else {
    selected.value = dicts.value.map((d) => d.dictId)
  }
}

async function handleToggleStatus(dict: DictDTO, enabled: boolean) {
  await updateDict(dict.dictId, { ...dict, status: enabled ? 1 : 0 })
  await loadDicts()
}

function openCreate() {
  editingDict.value = null
  formData.value = defaultDictForm()
  formErrors.value = {}
  dialogOpen.value = true
}

function openEdit(dict: DictDTO) {
  editingDict.value = dict
  formData.value = {
    dictName: dict.dictName,
    dictCode: dict.dictCode,
    dataValueType: dict.dataValueType || 'STRING',
    remark: dict.remark || '',
  }
  formErrors.value = {}
  dialogOpen.value = true
}

function validateDictForm(): boolean {
  const errors: Record<string, string> = {}
  if (!formData.value.dictName.trim()) errors.dictName = '字典名称不能为空'
  if (!formData.value.dictCode.trim()) errors.dictCode = '字典编码不能为空'
  formErrors.value = errors
  return Object.keys(errors).length === 0
}

async function handleSave() {
  if (!validateDictForm()) return
  if (editingDict.value) {
    await updateDict(editingDict.value.dictId, formData.value)
  } else {
    await createDict(formData.value)
  }
  dialogOpen.value = false
  await loadDicts()
}

onMounted(loadDicts)
</script>

<template>
  <div class="space-y-4">
    <Card>
      <CardContent class="py-4">
        <div class="flex items-end gap-4">
          <div class="flex items-center gap-2">
            <Label class="shrink-0">字典名称</Label>
            <Input v-model="searchName" name="dictName" autocomplete="off" placeholder="请输入字典名称…" class="w-48" />
          </div>
          <div class="flex items-center gap-2">
            <Label class="shrink-0">数据类型</Label>
            <Select :model-value="searchType || '__all__'" @update:model-value="(v: any) => { searchType = v === '__all__' ? '' : String(v) }">
              <SelectTrigger class="w-48">
                <SelectValue placeholder="全部" />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="__all__">全部</SelectItem>
                <SelectItem value="STRING">STRING</SelectItem>
                <SelectItem value="NUMBER">NUMBER</SelectItem>
                <SelectItem value="BOOLEAN">BOOLEAN</SelectItem>
                <SelectItem value="OBJECT">OBJECT</SelectItem>
                <SelectItem value="ARRAY">ARRAY</SelectItem>
              </SelectContent>
            </Select>
          </div>
          <div class="flex gap-2">
            <Button size="sm" data-testid="search-btn" @click="handleSearch">
              <Search class="size-4" />搜索
            </Button>
            <Button size="sm" variant="outline" @click="searchName = ''; searchType = ''; handleSearch()">
              <RotateCcw class="size-4" />重置
            </Button>
          </div>
        </div>
      </CardContent>
    </Card>

    <div class="flex items-center gap-2">
      <Button size="sm" @click="openCreate">
        <Plus class="size-4" />新增字典
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
              <TableHead>字典名称</TableHead>
              <TableHead>字典编码</TableHead>
              <TableHead>数据类型</TableHead>
              <TableHead>状态</TableHead>
              <TableHead>内置</TableHead>
              <TableHead class="text-center">创建时间</TableHead>
              <TableHead>备注</TableHead>
              <TableHead class="w-72 text-center">操作</TableHead>
            </TableRow>
          </TableHeader>
          <TableBody>
            <TableRow
              v-for="dict in dicts"
              :key="dict.dictId"
              class="cursor-pointer"
              @click="handleRowClick(dict)"
            >
              <TableCell @click.stop>
                <Checkbox
                  :model-value="selected.includes(dict.dictId)"
                  :disabled="dict.builtIn === 1"
                  @update:model-value="toggleSelect(dict.dictId, !!$event)"
                />
              </TableCell>
              <TableCell class="truncate">{{ dict.dictName }}</TableCell>
              <TableCell class="truncate">{{ dict.dictCode }}</TableCell>
              <TableCell>{{ dict.dataValueType }}</TableCell>
              <TableCell @click.stop>
                <Switch
                  size="sm"
                  :model-value="dict.status === 1"
                  :disabled="dict.builtIn === 1"
                  @update:model-value="(val: boolean) => handleToggleStatus(dict, val)"
                />
              </TableCell>
              <TableCell>
                <Badge v-if="dict.builtIn === 1" variant="secondary">是</Badge>
                <Badge v-else variant="outline">否</Badge>
              </TableCell>
              <TableCell class="tabular-nums text-center">{{ dict.createdAt }}</TableCell>
              <TableCell class="truncate">{{ dict.remark }}</TableCell>
              <TableCell class="text-center" @click.stop>
                <div class="flex justify-center gap-1">
                  <Button variant="ghost" size="sm" class="text-primary" :disabled="dict.builtIn === 1" @click="openEdit(dict)">
                    <Pencil class="size-4" />编辑
                  </Button>
                  <Button variant="ghost" size="sm" class="text-amber-500" @click="handleRowClick(dict)">
                    <Eye class="size-4" />详情
                  </Button>
                  <Button variant="ghost" size="sm" class="text-destructive" :disabled="dict.builtIn === 1" @click="handleDelete(dict.dictId)">
                    <Trash2 class="size-4" />删除
                  </Button>
                </div>
              </TableCell>
            </TableRow>
            <TableEmpty v-if="dicts.length === 0" :colspan="9">暂无数据</TableEmpty>
          </TableBody>
        </Table>

        <div class="flex items-center justify-end gap-4 border-t px-4 py-3">
          <Pagination
            v-model:page="page"
            :total="total"
            :items-per-page="size"
            class="mx-0 w-auto justify-end"
            @update:page="loadDicts"
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

    <DictItemDrawer v-model:open="drawerOpen" :dict="currentDict" />

    <Dialog v-model:open="dialogOpen">
      <DialogContent class="sm:max-w-md">
        <DialogHeader>
          <DialogTitle>{{ editingDict ? '编辑字典' : '新增字典' }}</DialogTitle>
          <DialogDescription>{{ editingDict ? '修改字典信息' : '创建一个新的字典' }}</DialogDescription>
        </DialogHeader>
        <div class="grid gap-4 py-4">
          <div class="grid grid-cols-4 items-center gap-x-4 gap-y-1">
            <Label class="justify-end">字典名称</Label>
            <Input v-model="formData.dictName" name="dictName" autocomplete="off" placeholder="请输入字典名称…" class="col-span-3" :aria-invalid="!!formErrors.dictName" />
            <template v-if="formErrors.dictName">
              <span />
              <p class="col-span-3 text-destructive text-xs">{{ formErrors.dictName }}</p>
            </template>
          </div>
          <div class="grid grid-cols-4 items-center gap-x-4 gap-y-1">
            <Label class="justify-end">字典编码</Label>
            <Input
              v-model="formData.dictCode"
              name="dictCode"
              autocomplete="off"
              placeholder="请输入字典编码…"
              class="col-span-3"
              :disabled="!!editingDict"
              :aria-invalid="!!formErrors.dictCode"
            />
            <template v-if="formErrors.dictCode">
              <span />
              <p class="col-span-3 text-destructive text-xs">{{ formErrors.dictCode }}</p>
            </template>
          </div>
          <div class="grid grid-cols-4 items-center gap-4">
            <Label class="justify-end">数据类型</Label>
            <Select v-model="formData.dataValueType">
              <SelectTrigger class="col-span-3 w-full">
                <SelectValue placeholder="选择数据类型" />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="STRING">STRING</SelectItem>
                <SelectItem value="NUMBER">NUMBER</SelectItem>
                <SelectItem value="BOOLEAN">BOOLEAN</SelectItem>
                <SelectItem value="OBJECT">OBJECT</SelectItem>
                <SelectItem value="ARRAY">ARRAY</SelectItem>
              </SelectContent>
            </Select>
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
