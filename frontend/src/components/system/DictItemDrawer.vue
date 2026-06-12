<script setup lang="ts">
import { ref, computed, watch } from 'vue'
import {
  Sheet,
  SheetContent,
  SheetHeader,
  SheetTitle,
  SheetDescription,
} from '@/components/ui/sheet'
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
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import { Checkbox } from '@/components/ui/checkbox'
import { Switch } from '@/components/ui/switch'
import { Plus, Pencil, Trash2, Check, Minus, X } from '@lucide/vue'
import type { DictDTO, DictItemDTO } from '@/api/dict'
import {
  fetchDictItems,
  createDictItem,
  updateDictItem,
  deleteDictItem,
  deleteDictItems,
} from '@/api/dict'

const props = defineProps<{
  open: boolean
  dict: DictDTO | null
}>()

const emit = defineEmits<{
  'update:open': [value: boolean]
}>()

const isBuiltIn = computed(() => props.dict?.builtIn === 1)

const items = ref<DictItemDTO[]>([])
const selected = ref<string[]>([])
const dialogOpen = ref(false)
const editing = ref<DictItemDTO | null>(null)
const defaultItemForm = () => ({ itemKey: '', itemValue: '', sortOrder: 0 })
const formData = ref(defaultItemForm())
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

async function loadItems() {
  if (!props.dict) return
  items.value = await fetchDictItems(props.dict.dictId)
}

watch(
  () => props.dict?.dictId,
  (id) => {
    if (id) loadItems()
    else items.value = []
  },
)

watch(
  () => props.open,
  (open) => {
    if (open && props.dict) loadItems()
  },
)

function openCreate() {
  editing.value = null
  formData.value = { ...defaultItemForm(), sortOrder: items.value.length + 1 }
  formErrors.value = {}
  dialogOpen.value = true
}

function openEdit(item: DictItemDTO) {
  editing.value = item
  formData.value = {
    itemKey: item.itemKey,
    itemValue: item.itemValue,
    sortOrder: item.sortOrder ?? 0,
  }
  formErrors.value = {}
  dialogOpen.value = true
}

function validateItemForm(): boolean {
  const errors: Record<string, string> = {}
  if (!formData.value.itemKey.trim()) errors.itemKey = '字典项键不能为空'
  if (!formData.value.itemValue.trim()) errors.itemValue = '字典项值不能为空'
  formErrors.value = errors
  return Object.keys(errors).length === 0
}

async function handleSave() {
  if (!props.dict || !validateItemForm()) return
  if (editing.value?.dictItemId) {
    await updateDictItem(props.dict.dictId, editing.value.dictItemId, formData.value)
  } else {
    await createDictItem(props.dict.dictId, formData.value)
  }
  dialogOpen.value = false
  await loadItems()
}

async function handleToggleItemStatus(item: DictItemDTO, enabled: boolean) {
  if (!props.dict) return
  await updateDictItem(props.dict.dictId, item.dictItemId, { ...item, status: enabled ? 1 : 0 })
  await loadItems()
}

function handleDelete(itemId: string) {
  if (!props.dict) return
  openConfirm('确定要删除该字典项吗？', async () => {
    await deleteDictItem(props.dict!.dictId, itemId)
    await loadItems()
  })
}

function handleBatchDelete() {
  if (!props.dict || selected.value.length === 0) return
  openConfirm(`确定要删除选中的 ${selected.value.length} 个字典项吗？`, async () => {
    await deleteDictItems(props.dict!.dictId, selected.value)
    selected.value = []
    await loadItems()
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
  if (items.value.length === 0) return false
  if (selected.value.length === items.value.length) return true
  if (selected.value.length > 0) return 'indeterminate' as const
  return false
})

function toggleSelectAll() {
  if (selected.value.length === items.value.length) {
    selected.value = []
  } else {
    selected.value = items.value.map((i) => i.dictItemId)
  }
}
</script>

<template>
  <Sheet :open="open" @update:open="emit('update:open', $event)">
    <SheetContent side="right" class="data-[side=right]:w-[40%] min-w-[400px] data-[side=right]:sm:max-w-none overscroll-contain">
      <SheetHeader>
        <SheetTitle>{{ dict?.dictName }} - 字典项</SheetTitle>
        <SheetDescription>管理字典「{{ dict?.dictCode }}」下的字典项</SheetDescription>
      </SheetHeader>

      <div class="mt-4 space-y-4">
        <div class="flex items-center gap-2 px-4">
          <Button size="sm" :disabled="isBuiltIn" @click="openCreate">
            <Plus class="size-4" />新增字典项
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

        <Table class="table-fixed">
          <TableHeader>
            <TableRow>
              <TableHead class="w-10">
                <Checkbox
                  :model-value="headerChecked"
                  :disabled="isBuiltIn"
                  class="data-[state=indeterminate]:bg-primary data-[state=indeterminate]:text-primary-foreground data-[state=indeterminate]:border-primary"
                  @update:model-value="toggleSelectAll"
                >
                  <template #default="{ state }">
                    <Minus v-if="state === 'indeterminate'" class="size-3.5" />
                    <Check v-else class="size-3.5" />
                  </template>
                </Checkbox>
              </TableHead>
              <TableHead>键</TableHead>
              <TableHead>值</TableHead>
              <TableHead>排序</TableHead>
              <TableHead>状态</TableHead>
              <TableHead class="w-44 text-center">操作</TableHead>
            </TableRow>
          </TableHeader>
          <TableBody>
            <TableRow v-for="item in items" :key="item.dictItemId">
              <TableCell>
                <Checkbox
                  :model-value="selected.includes(item.dictItemId)"
                  :disabled="isBuiltIn"
                  @update:model-value="toggleSelect(item.dictItemId, !!$event)"
                />
              </TableCell>
              <TableCell class="truncate">{{ item.itemKey }}</TableCell>
              <TableCell class="truncate">{{ item.itemValue }}</TableCell>
              <TableCell class="tabular-nums">{{ item.sortOrder }}</TableCell>
              <TableCell>
                <Switch
                  size="sm"
                  :model-value="item.status === 1"
                  :disabled="isBuiltIn || item.builtIn === 1"
                  @update:model-value="(val: boolean) => handleToggleItemStatus(item, val)"
                />
              </TableCell>
              <TableCell class="text-center">
                <div class="flex justify-center gap-1">
                  <Button variant="ghost" size="sm" class="text-primary" :disabled="isBuiltIn" @click="openEdit(item)">
                    <Pencil class="size-4" />编辑
                  </Button>
                  <Button
                    variant="ghost"
                    size="sm"
                    class="text-destructive"
                    :disabled="isBuiltIn"
                    @click="handleDelete(item.dictItemId)"
                  >
                    <Trash2 class="size-4" />删除
                  </Button>
                </div>
              </TableCell>
            </TableRow>
            <TableEmpty v-if="items.length === 0" :colspan="6">暂无字典项</TableEmpty>
          </TableBody>
        </Table>
      </div>
    </SheetContent>
  </Sheet>

  <Dialog v-model:open="dialogOpen">
    <DialogContent class="sm:max-w-md">
      <DialogHeader>
        <DialogTitle>{{ editing ? '编辑字典项' : '新增字典项' }}</DialogTitle>
        <DialogDescription>{{ editing ? '修改字典项信息' : '为当前字典添加新的字典项' }}</DialogDescription>
      </DialogHeader>
      <div class="grid gap-4 py-4">
        <div class="grid grid-cols-4 items-center gap-x-4 gap-y-1">
          <Label class="justify-end">键</Label>
          <Input v-model="formData.itemKey" name="itemKey" autocomplete="off" placeholder="请输入键…" class="col-span-3" :aria-invalid="!!formErrors.itemKey" />
          <template v-if="formErrors.itemKey">
            <span />
            <p class="col-span-3 text-destructive text-xs">{{ formErrors.itemKey }}</p>
          </template>
        </div>
        <div class="grid grid-cols-4 items-center gap-x-4 gap-y-1">
          <Label class="justify-end">值</Label>
          <Input v-model="formData.itemValue" name="itemValue" autocomplete="off" placeholder="请输入值…" class="col-span-3" :aria-invalid="!!formErrors.itemValue" />
          <template v-if="formErrors.itemValue">
            <span />
            <p class="col-span-3 text-destructive text-xs">{{ formErrors.itemValue }}</p>
          </template>
        </div>
        <div class="grid grid-cols-4 items-center gap-4">
          <Label class="justify-end">排序</Label>
          <Input v-model.number="formData.sortOrder" type="number" name="sortOrder" autocomplete="off" placeholder="排序号…" class="col-span-3 tabular-nums" />
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
</template>
