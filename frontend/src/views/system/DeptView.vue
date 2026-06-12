<script setup lang="ts">
defineOptions({ name: 'DeptIndex' })

import { ref, computed, onMounted } from 'vue'
import { Card, CardContent } from '@/components/ui/card'
import {
  Table,
  TableBody,
  TableCell,
  TableEmpty,
  TableHead,
  TableHeader,
  TableRow,
} from '@/components/ui/table'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
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
import { Switch } from '@/components/ui/switch'
import {
  Plus,
  Pencil,
  Trash2,
  ChevronRight,
  ChevronDown,
  Check,
  X,
} from '@lucide/vue'
import type { DeptDTO } from '@/api/dept'
import { fetchDeptTree, createDept, updateDept, deleteDept } from '@/api/dept'

interface FlatNode {
  dept: DeptDTO
  depth: number
  hasChildren: boolean
}

const tree = ref<DeptDTO[]>([])
const expanded = ref<Set<string>>(new Set())
const dialogOpen = ref(false)
const editingDept = ref<DeptDTO | null>(null)
const parentDeptName = ref('')
const parentDeptId = ref<string | null>(null)
const defaultForm = () => ({ deptName: '', deptCode: '', sortOrder: 0, remark: '' })
const formData = ref(defaultForm())
const formErrors = ref<Record<string, string>>({})
const confirmOpen = ref(false)
const confirmMessage = ref('')
const confirmAction = ref<(() => Promise<void>) | null>(null)

function flattenTree(nodes: DeptDTO[], depth: number): FlatNode[] {
  const result: FlatNode[] = []
  for (const node of nodes) {
    const hasChildren = node.children && node.children.length > 0
    result.push({ dept: node, depth, hasChildren })
    if (hasChildren && expanded.value.has(node.deptId)) {
      result.push(...flattenTree(node.children, depth + 1))
    }
  }
  return result
}

const flatNodes = computed(() => flattenTree(tree.value, 0))

function collectDescendantIds(nodes: DeptDTO[], result: Set<string>) {
  for (const node of nodes) {
    result.add(node.deptId)
    if (node.children?.length) collectDescendantIds(node.children, result)
  }
}

function findNode(nodes: DeptDTO[], id: string): DeptDTO | null {
  for (const node of nodes) {
    if (node.deptId === id) return node
    if (node.children?.length) {
      const found = findNode(node.children, id)
      if (found) return found
    }
  }
  return null
}

interface ParentOption {
  id: string
  label: string
  depth: number
}

const parentOptions = computed<ParentOption[]>(() => {
  const excludeIds = new Set<string>()
  if (editingDept.value) {
    excludeIds.add(editingDept.value.deptId)
    const node = findNode(tree.value, editingDept.value.deptId)
    if (node?.children?.length) collectDescendantIds(node.children, excludeIds)
  }
  const options: ParentOption[] = []
  function walk(nodes: DeptDTO[], depth: number) {
    for (const node of nodes) {
      if (!excludeIds.has(node.deptId)) {
        options.push({ id: node.deptId, label: node.deptName, depth })
        if (node.children?.length) walk(node.children, depth + 1)
      }
    }
  }
  walk(tree.value, 0)
  return options
})

function toggleExpand(deptId: string) {
  const s = new Set(expanded.value)
  if (s.has(deptId)) s.delete(deptId)
  else s.add(deptId)
  expanded.value = s
}

function openConfirm(message: string, action: () => Promise<void>) {
  confirmMessage.value = message
  confirmAction.value = action
  confirmOpen.value = true
}

async function executeConfirm() {
  if (confirmAction.value) await confirmAction.value()
  confirmOpen.value = false
}

async function loadTree() {
  tree.value = await fetchDeptTree()
}

function openCreateRoot() {
  editingDept.value = null
  parentDeptId.value = null
  parentDeptName.value = ''
  formData.value = defaultForm()
  formErrors.value = {}
  dialogOpen.value = true
}

function openCreateChild(parent: DeptDTO) {
  editingDept.value = null
  parentDeptId.value = parent.deptId
  parentDeptName.value = parent.deptName
  formData.value = defaultForm()
  formErrors.value = {}
  dialogOpen.value = true
}

function openEdit(dept: DeptDTO) {
  editingDept.value = dept
  parentDeptId.value = dept.parentId
  parentDeptName.value = ''
  formData.value = {
    deptName: dept.deptName,
    deptCode: dept.deptCode,
    sortOrder: dept.sortOrder,
    remark: dept.remark || '',
  }
  formErrors.value = {}
  dialogOpen.value = true
}

function validateForm(): boolean {
  const errors: Record<string, string> = {}
  if (!formData.value.deptName.trim()) errors.deptName = '部门名称不能为空'
  if (!formData.value.deptCode.trim()) errors.deptCode = '部门编码不能为空'
  formErrors.value = errors
  return Object.keys(errors).length === 0
}

async function handleSave() {
  if (!validateForm()) return
  const payload = {
    deptName: formData.value.deptName,
    deptCode: formData.value.deptCode,
    sortOrder: formData.value.sortOrder,
    remark: formData.value.remark,
    parentId: parentDeptId.value,
  }
  if (editingDept.value) {
    await updateDept(editingDept.value.deptId, payload)
  } else {
    await createDept(payload)
  }
  dialogOpen.value = false
  await loadTree()
}

function handleDelete(deptId: string) {
  openConfirm('确定要删除该部门吗？删除后其子部门也将被移除。', async () => {
    await deleteDept(deptId)
    await loadTree()
  })
}

async function handleToggleStatus(dept: DeptDTO, enabled: boolean) {
  await updateDept(dept.deptId, { ...dept, status: enabled ? 1 : 0 })
  await loadTree()
}

onMounted(loadTree)
</script>

<template>
  <div class="space-y-4">
    <div class="flex items-center gap-2">
      <Button size="sm" @click="openCreateRoot">
        <Plus class="size-4" />新增根部门
      </Button>
    </div>

    <Card>
      <CardContent class="p-0">
        <Table class="table-fixed">
          <TableHeader>
            <TableRow>
              <TableHead>部门名称</TableHead>
              <TableHead>部门编码</TableHead>
              <TableHead>排序</TableHead>
              <TableHead>状态</TableHead>
              <TableHead class="w-72 text-center">操作</TableHead>
            </TableRow>
          </TableHeader>
          <TableBody>
            <TableRow v-for="node in flatNodes" :key="node.dept.deptId">
              <TableCell>
                <div class="flex items-center" :style="{ paddingLeft: `${node.depth * 20}px` }">
                  <button
                    class="flex size-5 shrink-0 items-center justify-center rounded"
                    :class="node.hasChildren ? 'hover:bg-accent cursor-pointer' : 'invisible'"
                    :aria-label="expanded.has(node.dept.deptId) ? '收起' : '展开'"
                    @click="toggleExpand(node.dept.deptId)"
                  >
                    <ChevronDown v-if="expanded.has(node.dept.deptId)" class="size-4" />
                    <ChevronRight v-else class="size-4" />
                  </button>
                  <span class="truncate font-medium">{{ node.dept.deptName }}</span>
                </div>
              </TableCell>
              <TableCell class="truncate">{{ node.dept.deptCode }}</TableCell>
              <TableCell class="tabular-nums">{{ node.dept.sortOrder }}</TableCell>
              <TableCell>
                <Switch
                  size="sm"
                  :model-value="node.dept.status === 1"
                  :disabled="node.dept.builtIn === 1"
                  @update:model-value="(val: boolean) => handleToggleStatus(node.dept, val)"
                />
              </TableCell>
              <TableCell class="text-center">
                <div class="flex justify-center gap-1">
                  <Button variant="ghost" size="sm" class="text-primary" :disabled="node.dept.builtIn === 1" @click="openCreateChild(node.dept)">
                    <Plus class="size-4" />子部门
                  </Button>
                  <Button variant="ghost" size="sm" class="text-primary" :disabled="node.dept.builtIn === 1" @click="openEdit(node.dept)">
                    <Pencil class="size-4" />编辑
                  </Button>
                  <Button variant="ghost" size="sm" class="text-destructive" :disabled="node.dept.builtIn === 1" @click="handleDelete(node.dept.deptId)">
                    <Trash2 class="size-4" />删除
                  </Button>
                </div>
              </TableCell>
            </TableRow>
            <TableEmpty v-if="flatNodes.length === 0" :colspan="5">暂无部门数据</TableEmpty>
          </TableBody>
        </Table>
      </CardContent>
    </Card>

    <Dialog v-model:open="dialogOpen">
      <DialogContent class="sm:max-w-md">
        <DialogHeader>
          <DialogTitle>{{ editingDept ? '编辑部门' : '新增部门' }}</DialogTitle>
          <DialogDescription>{{ editingDept ? '修改部门信息' : '创建一个新的部门' }}</DialogDescription>
        </DialogHeader>
        <div class="grid gap-4 py-4">
          <div class="grid grid-cols-4 items-center gap-4">
            <Label class="justify-end">上级部门</Label>
            <Select :model-value="parentDeptId || '__root__'" @update:model-value="(v: any) => { parentDeptId = v === '__root__' ? null : String(v) }">
              <SelectTrigger class="col-span-3 w-full">
                <SelectValue placeholder="无（根部门）" />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="__root__">无（根部门）</SelectItem>
                <SelectItem v-for="opt in parentOptions" :key="opt.id" :value="opt.id">
                  {{ '　'.repeat(opt.depth) }}{{ opt.label }}
                </SelectItem>
              </SelectContent>
            </Select>
          </div>
          <div class="grid grid-cols-4 items-center gap-x-4 gap-y-1">
            <Label class="justify-end">部门名称</Label>
            <Input v-model="formData.deptName" name="deptName" autocomplete="off" placeholder="请输入部门名称…" class="col-span-3" :aria-invalid="!!formErrors.deptName" />
            <template v-if="formErrors.deptName">
              <span />
              <p class="text-destructive col-span-3 text-xs">{{ formErrors.deptName }}</p>
            </template>
          </div>
          <div class="grid grid-cols-4 items-center gap-x-4 gap-y-1">
            <Label class="justify-end">部门编码</Label>
            <Input
              v-model="formData.deptCode"
              name="deptCode"
              autocomplete="off"
              placeholder="请输入部门编码…"
              class="col-span-3"
              :disabled="!!editingDept"
              :aria-invalid="!!formErrors.deptCode"
            />
            <template v-if="formErrors.deptCode">
              <span />
              <p class="text-destructive col-span-3 text-xs">{{ formErrors.deptCode }}</p>
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
