<script setup lang="ts">
defineOptions({ name: 'MenuIndex' })

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
import type { MenuDTO } from '@/api/menu'
import { fetchMenuList, createMenu, updateMenu, deleteMenu } from '@/api/menu'
import { dict } from '@/dict'
import { useMenuStore } from '@/stores/menu'

interface FlatNode {
  menu: MenuDTO
  depth: number
  hasChildren: boolean
}

const menuStore = useMenuStore()
const tree = ref<MenuDTO[]>([])
const expanded = ref<Set<string>>(new Set())
const dialogOpen = ref(false)
const editingMenu = ref<MenuDTO | null>(null)
const parentMenuId = ref<string | null>(null)

const defaultForm = () => ({
  menuName: '',
  menuType: 'M' as string,
  path: '',
  component: '',
  icon: '',
  permission: '',
  sortOrder: 0,
  redirect: '',
  visible: 1,
  status: 1,
  requireAuth: 1,
})
const formData = ref(defaultForm())
const formErrors = ref<Record<string, string>>({})
const confirmOpen = ref(false)
const confirmMessage = ref('')
const confirmAction = ref<(() => Promise<void>) | null>(null)

function flattenTree(nodes: MenuDTO[], depth: number): FlatNode[] {
  const result: FlatNode[] = []
  for (const node of nodes) {
    const hasChildren = !!(node.children && node.children.length > 0)
    result.push({ menu: node, depth, hasChildren })
    if (hasChildren && expanded.value.has(node.menuId)) {
      result.push(...flattenTree(node.children!, depth + 1))
    }
  }
  return result
}

const flatNodes = computed(() => flattenTree(tree.value, 0))

function collectDescendantIds(nodes: MenuDTO[], result: Set<string>) {
  for (const node of nodes) {
    result.add(node.menuId)
    if (node.children?.length) collectDescendantIds(node.children, result)
  }
}

function findNode(nodes: MenuDTO[], id: string): MenuDTO | null {
  for (const node of nodes) {
    if (node.menuId === id) return node
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
  if (editingMenu.value) {
    excludeIds.add(editingMenu.value.menuId)
    const node = findNode(tree.value, editingMenu.value.menuId)
    if (node?.children?.length) collectDescendantIds(node.children, excludeIds)
  }
  const options: ParentOption[] = []
  function walk(nodes: MenuDTO[], depth: number) {
    for (const node of nodes) {
      if (!excludeIds.has(node.menuId) && node.menuType !== 'B') {
        options.push({ id: node.menuId, label: node.menuName, depth })
        if (node.children?.length) walk(node.children, depth + 1)
      }
    }
  }
  walk(tree.value, 0)
  return options
})

function toggleExpand(menuId: string) {
  const s = new Set(expanded.value)
  if (s.has(menuId)) s.delete(menuId)
  else s.add(menuId)
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

function buildTree(flatList: MenuDTO[]): MenuDTO[] {
  const map = new Map<string, MenuDTO>()
  for (const item of flatList) map.set(item.menuId, { ...item, children: [] })
  const roots: MenuDTO[] = []
  for (const item of flatList) {
    const node = map.get(item.menuId)!
    if (item.parentId && map.has(item.parentId)) {
      map.get(item.parentId)!.children!.push(node)
    } else {
      roots.push(node)
    }
  }
  return roots
}

async function loadTree() {
  const list = await fetchMenuList()
  tree.value = buildTree(list)
}

function openCreateRoot() {
  editingMenu.value = null
  parentMenuId.value = null
  formData.value = defaultForm()
  formErrors.value = {}
  dialogOpen.value = true
}

function openCreateChild(parent: MenuDTO) {
  editingMenu.value = null
  parentMenuId.value = parent.menuId
  formData.value = defaultForm()
  formErrors.value = {}
  dialogOpen.value = true
}

function openEdit(menu: MenuDTO) {
  editingMenu.value = menu
  parentMenuId.value = menu.parentId
  formData.value = {
    menuName: menu.menuName,
    menuType: menu.menuType,
    path: menu.path || '',
    component: menu.component || '',
    icon: menu.icon || '',
    permission: menu.permission || '',
    sortOrder: menu.sortOrder,
    redirect: menu.redirect || '',
    visible: menu.visible,
    status: menu.status,
    requireAuth: menu.requireAuth,
  }
  formErrors.value = {}
  dialogOpen.value = true
}

function validateForm(): boolean {
  const errors: Record<string, string> = {}
  if (!formData.value.menuName.trim()) errors.menuName = '菜单名称不能为空'
  if (formData.value.menuType === 'M' && !formData.value.path.trim()) errors.path = '路由路径不能为空'
  formErrors.value = errors
  return Object.keys(errors).length === 0
}

async function handleSave() {
  if (!validateForm()) return
  const payload: Partial<MenuDTO> = {
    menuName: formData.value.menuName,
    menuType: formData.value.menuType,
    sortOrder: formData.value.sortOrder,
    status: formData.value.status,
    visible: formData.value.visible,
    parentId: parentMenuId.value,
  }
  if (formData.value.menuType !== 'B') {
    payload.icon = formData.value.icon || null
    payload.redirect = formData.value.redirect || null
  }
  if (formData.value.menuType === 'M') {
    payload.path = formData.value.path || null
    payload.component = formData.value.component || null
    payload.permission = formData.value.permission || null
    payload.requireAuth = formData.value.requireAuth
  }
  if (formData.value.menuType === 'B') {
    payload.permission = formData.value.permission || null
  }
  if (editingMenu.value) {
    await updateMenu(editingMenu.value.menuId, payload)
  } else {
    await createMenu(payload)
  }
  dialogOpen.value = false
  await loadTree()
  menuStore.reset()
  await menuStore.loadMenus()
}

function handleDelete(menuId: string) {
  openConfirm('确定要删除该菜单吗？删除后其子菜单也将被移除。', async () => {
    await deleteMenu(menuId)
    await loadTree()
    menuStore.reset()
    await menuStore.loadMenus()
  })
}

async function handleToggleStatus(menu: MenuDTO, enabled: boolean) {
  await updateMenu(menu.menuId, { status: enabled ? 1 : 0 })
  await loadTree()
  menuStore.reset()
  await menuStore.loadMenus()
}

onMounted(loadTree)
</script>

<template>
  <div class="space-y-4">
    <div class="flex items-center gap-2">
      <Button size="sm" @click="openCreateRoot">
        <Plus class="size-4" />新增菜单
      </Button>
    </div>

    <Card>
      <CardContent class="p-0">
        <Table class="table-fixed">
          <TableHeader>
            <TableRow>
              <TableHead class="w-56">菜单名称</TableHead>
              <TableHead class="w-20">图标</TableHead>
              <TableHead class="w-20">类型</TableHead>
              <TableHead>路径</TableHead>
              <TableHead>权限标识</TableHead>
              <TableHead class="w-16">排序</TableHead>
              <TableHead class="w-16">状态</TableHead>
              <TableHead class="w-72 text-center">操作</TableHead>
            </TableRow>
          </TableHeader>
          <TableBody>
            <TableRow v-for="node in flatNodes" :key="node.menu.menuId">
              <TableCell>
                <div class="flex items-center" :style="{ paddingLeft: `${node.depth * 20}px` }">
                  <button
                    class="flex size-5 shrink-0 items-center justify-center rounded"
                    :class="node.hasChildren ? 'hover:bg-accent cursor-pointer' : 'invisible'"
                    :aria-label="expanded.has(node.menu.menuId) ? '收起' : '展开'"
                    @click="toggleExpand(node.menu.menuId)"
                  >
                    <ChevronDown v-if="expanded.has(node.menu.menuId)" class="size-4" />
                    <ChevronRight v-else class="size-4" />
                  </button>
                  <span class="truncate font-medium">{{ node.menu.menuName }}</span>
                </div>
              </TableCell>
              <TableCell class="truncate text-muted-foreground">{{ node.menu.icon || '-' }}</TableCell>
              <TableCell>{{ dict.menuType.getLabel(node.menu.menuType) }}</TableCell>
              <TableCell class="truncate">{{ node.menu.path || '-' }}</TableCell>
              <TableCell class="truncate">{{ node.menu.permission || '-' }}</TableCell>
              <TableCell class="tabular-nums">{{ node.menu.sortOrder }}</TableCell>
              <TableCell>
                <Switch
                  size="sm"
                  :model-value="node.menu.status === 1"
                  @update:model-value="(val: boolean) => handleToggleStatus(node.menu, val)"
                />
              </TableCell>
              <TableCell class="text-center">
                <div class="flex justify-center gap-1">
                  <Button variant="ghost" size="sm" class="text-primary" @click="openCreateChild(node.menu)">
                    <Plus class="size-4" />子项
                  </Button>
                  <Button variant="ghost" size="sm" class="text-primary" @click="openEdit(node.menu)">
                    <Pencil class="size-4" />编辑
                  </Button>
                  <Button variant="ghost" size="sm" class="text-destructive" @click="handleDelete(node.menu.menuId)">
                    <Trash2 class="size-4" />删除
                  </Button>
                </div>
              </TableCell>
            </TableRow>
            <TableEmpty v-if="flatNodes.length === 0" :colspan="8">暂无菜单数据</TableEmpty>
          </TableBody>
        </Table>
      </CardContent>
    </Card>

    <Dialog v-model:open="dialogOpen">
      <DialogContent class="sm:max-w-lg">
        <DialogHeader>
          <DialogTitle>{{ editingMenu ? '编辑菜单' : '新增菜单' }}</DialogTitle>
          <DialogDescription>{{ editingMenu ? '修改菜单信息' : '创建一个新的菜单项' }}</DialogDescription>
        </DialogHeader>
        <div class="grid gap-4 py-4">
          <div class="grid grid-cols-4 items-center gap-4">
            <Label class="justify-end">菜单类型</Label>
            <Select v-model="formData.menuType" :disabled="!!editingMenu">
              <SelectTrigger class="col-span-3 w-full">
                <SelectValue />
              </SelectTrigger>
              <SelectContent>
                <SelectItem v-for="item in dict.menuType.items.value" :key="item.itemKey" :value="item.itemKey">
                  {{ item.itemLabel }}
                </SelectItem>
              </SelectContent>
            </Select>
          </div>
          <div class="grid grid-cols-4 items-center gap-4">
            <Label class="justify-end">上级菜单</Label>
            <Select :model-value="parentMenuId || '__root__'" @update:model-value="(v: any) => { parentMenuId = v === '__root__' ? null : String(v) }">
              <SelectTrigger class="col-span-3 w-full">
                <SelectValue placeholder="无（根菜单）" />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="__root__">无（根菜单）</SelectItem>
                <SelectItem v-for="opt in parentOptions" :key="opt.id" :value="opt.id">
                  {{ '　'.repeat(opt.depth) }}{{ opt.label }}
                </SelectItem>
              </SelectContent>
            </Select>
          </div>
          <div class="grid grid-cols-4 items-center gap-x-4 gap-y-1">
            <Label class="justify-end">菜单名称</Label>
            <Input v-model="formData.menuName" autocomplete="off" placeholder="请输入菜单名称…" class="col-span-3" :aria-invalid="!!formErrors.menuName" />
            <template v-if="formErrors.menuName">
              <span />
              <p class="text-destructive col-span-3 text-xs">{{ formErrors.menuName }}</p>
            </template>
          </div>
          <template v-if="formData.menuType !== 'B'">
            <div class="grid grid-cols-4 items-center gap-4">
              <Label class="justify-end">图标</Label>
              <Input v-model="formData.icon" autocomplete="off" placeholder="Lucide 图标名称" class="col-span-3" />
            </div>
          </template>
          <template v-if="formData.menuType === 'M'">
            <div class="grid grid-cols-4 items-center gap-x-4 gap-y-1">
              <Label class="justify-end">路由路径</Label>
              <Input v-model="formData.path" autocomplete="off" placeholder="/system/menus" class="col-span-3" :aria-invalid="!!formErrors.path" />
              <template v-if="formErrors.path">
                <span />
                <p class="text-destructive col-span-3 text-xs">{{ formErrors.path }}</p>
              </template>
            </div>
            <div class="grid grid-cols-4 items-center gap-4">
              <Label class="justify-end">组件路径</Label>
              <Input v-model="formData.component" autocomplete="off" placeholder="system/MenuView" class="col-span-3" />
            </div>
            <div class="grid grid-cols-4 items-center gap-4">
              <Label class="justify-end">权限标识</Label>
              <Input v-model="formData.permission" autocomplete="off" placeholder="system:menu:list" class="col-span-3" />
            </div>
            <div class="grid grid-cols-4 items-center gap-4">
              <Label class="justify-end">需要认证</Label>
              <Switch :model-value="formData.requireAuth === 1" @update:model-value="(v: boolean) => { formData.requireAuth = v ? 1 : 0 }" />
            </div>
          </template>
          <template v-if="formData.menuType === 'B'">
            <div class="grid grid-cols-4 items-center gap-4">
              <Label class="justify-end">权限标识</Label>
              <Input v-model="formData.permission" autocomplete="off" placeholder="system:menu:add" class="col-span-3" />
            </div>
          </template>
          <template v-if="formData.menuType !== 'B'">
            <div class="grid grid-cols-4 items-center gap-4">
              <Label class="justify-end">重定向</Label>
              <Input v-model="formData.redirect" autocomplete="off" placeholder="重定向路径" class="col-span-3" />
            </div>
            <div class="grid grid-cols-4 items-center gap-4">
              <Label class="justify-end">是否可见</Label>
              <Switch :model-value="formData.visible === 1" @update:model-value="(v: boolean) => { formData.visible = v ? 1 : 0 }" />
            </div>
          </template>
          <div class="grid grid-cols-4 items-center gap-4">
            <Label class="justify-end">排序</Label>
            <Input v-model.number="formData.sortOrder" type="number" autocomplete="off" placeholder="排序号" class="col-span-3" />
          </div>
          <div class="grid grid-cols-4 items-center gap-4">
            <Label class="justify-end">状态</Label>
            <Switch :model-value="formData.status === 1" @update:model-value="(v: boolean) => { formData.status = v ? 1 : 0 }" />
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
