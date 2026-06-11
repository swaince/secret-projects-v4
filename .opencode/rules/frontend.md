# 前端页面开发规则

基于字典管理页面实践总结的通用 CRUD 页面布局规则与注意事项。

## 页面布局

三段式 Card 布局，垂直间距 `space-y-4`：

```
┌─────────────────────────────┐
│  搜索表单 (Card)             │
└─────────────────────────────┘
  操作栏 (无 Card)
┌─────────────────────────────┐
│  数据表格 (Card, p-0)        │
│  ─────────────────────────  │
│  分页器 (border-t)           │
└─────────────────────────────┘
```

- 搜索表单放在 `<Card><CardContent class="py-4">` 内
- 操作栏（新增、批量删除等按钮）不用 Card 包裹，直接 `<div class="flex items-center gap-2">`
- 数据表格放在 `<Card><CardContent class="p-0">` 内，表格撑满无内边距
- 分页器在表格下方，`border-t` 分隔

## 搜索表单

- Label 和输入控件**水平排列**（左右），不采用上下布局
- 所有输入控件**固定等宽**（如 `w-48`），不用 grid 自适应
- 搜索/重置按钮跟在表单项后面，与表单项底部对齐 `items-end`
- 示例结构：`Label + Input | Label + Select | 搜索按钮 + 重置按钮`

## 操作栏

- 新增按钮常驻显示
- 批量删除按钮仅在选中项 > 0 时显示（`v-if="selected.length > 0"`）

## 数据表格

- 表格添加 `table-fixed` 确保列宽可控
- 勾选列：`w-10`，表头放全选 Checkbox
- 操作列：固定宽度（如 `w-72`），表头 `text-center`，单元格 `text-center` + `flex justify-center`
- 其他列：不设宽度，自动等分剩余空间
- 文本列添加 `truncate` 防止长文本溢出
- 数字/时间列添加 `tabular-nums` 保持等宽数字对齐
- 时间列表头和数据居中 `text-center`
- 空状态使用 `<TableEmpty>` 显示「暂无数据」
- 行点击打开详情时添加 `class="cursor-pointer"`，操作列和勾选列使用 `@click.stop` 阻止冒泡

## 分页器

从左到右排列：分页器 → 每页条数 → 跳转到 → 总条数。

- 整体 `flex items-center justify-end gap-4 border-t px-4 py-3`
- Pagination 组件覆盖内置居中样式：`class="mx-0 w-auto justify-end"`
- 每页条数：`<Select>` 下拉，选项 10/20/50/100
- 跳转页码：`<Input type="number">` 带 `aria-label="跳转页码"`，回车触发跳转
- 总条数：`<span class="text-muted-foreground text-sm tabular-nums">`

## 弹框表单（Dialog）

- 使用 `grid grid-cols-4 items-center gap-x-4 gap-y-1` 布局
- Label 占 1 列，`class="justify-end"` 右对齐（Label 组件是 flex 容器，`text-right` 无效）
- 输入控件占 3 列 `col-span-3`
- Select 的 SelectTrigger 添加 `class="col-span-3 w-full"` 确保全宽
- 校验错误显示在输入控件下方：`<span />` 占位 + `<p class="col-span-3 text-destructive text-xs">`

## 按钮规范

- 所有按钮统一 `size="sm"`
- 所有按钮必须配图标（`<Icon class="size-4" />`），图标在文字前
- 按钮颜色约定：
  - 编辑：`variant="ghost" class="text-primary"`，图标 `Pencil`
  - 详情：`variant="ghost" class="text-amber-500"`，图标 `Eye`
  - 删除：`variant="ghost" class="text-destructive"`，图标 `Trash2`
  - 新增：默认 variant，图标 `Plus`
  - 搜索：默认 variant，图标 `Search`
  - 重置：`variant="outline"`，图标 `RotateCcw`
  - 批量删除：`variant="destructive"`，图标 `Trash2`
  - 保存：默认 variant，图标 `Check`
  - 取消：`variant="outline"`，图标 `X`

## Checkbox 用法

reka-ui Checkbox API **没有** `checked` / `update:checked`，正确用法：

```vue
<Checkbox
  :model-value="selected.includes(id)"
  @update:model-value="toggleSelect(id, !!$event)"
/>
```

### 全选/半选

表头 Checkbox 支持三态（全选/半选/未选）：

```vue
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
```

注意：
- shadcn-vue 自定义变体 `data-checked:` 匹配 `[data-state="checked"]`，但**没有** `data-indeterminate:` 变体
- 半选样式必须使用 Tailwind 任意变体 `data-[state=indeterminate]:`
- 半选图标通过 `#default` slot 根据 `state` 条件渲染

## 删除确认

所有破坏性操作必须弹出 AlertDialog 确认，禁止直接执行。

使用共享确认弹窗模式，避免每个删除按钮都包裹 AlertDialog：

```typescript
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
```

## 表单校验

- 使用 `formErrors: Record<string, string>` 存储错误
- 打开弹窗时清空错误 `formErrors.value = {}`
- 提交时调用 `validate()` 返回 boolean，失败则 return
- 错误输入添加 `:aria-invalid="!!formErrors.fieldName"` 显示红色边框
- 错误文本紧跟输入控件下方，`text-destructive text-xs`

## Input 属性

- 必须添加 `name` 属性（对应字段名）
- 非登录表单添加 `autocomplete="off"` 避免密码管理器触发
- placeholder 以 `…` 结尾（如「请输入字典名称…」）

## Sheet / Drawer

- 覆盖内置宽度：`class="data-[side=right]:w-[40%] min-w-[400px] data-[side=right]:sm:max-w-none"`
  - 必须使用 `data-[side=right]:` 前缀覆盖组件内置样式
- 添加 `overscroll-contain` 防止滚动穿透

## Pagination 组件

覆盖内置居中样式需要传 class：`class="mx-0 w-auto justify-end"`

内置样式 `mx-auto flex w-full justify-center` 通过 tailwind-merge 被覆盖。

## 组件库源码

`src/components/ui/` 下的文件**禁止修改**。通过以下方式定制：
- `class` prop 覆盖/追加样式
- `#default` slot 自定义渲染内容
- 业务层封装 wrapper 组件
