# Retrospective: add-dept-management

## 概要

部门管理（树形结构），与岗位/角色的扁平 CRUD 不同，引入了 parent_id 自关联和内存树构建。

## 完成情况

- 后端 10 文件 + 前端 3 文件
- 78 tests 全部通过（16 new）
- 树查询 + 子部门删除保护 + 递归前端组件

## 技术亮点

- 后端 tree() 一次查全量，内存中按 parentId 分组递归构建，同级按 sortOrder 排序
- 前端递归组件渲染树形，展开/折叠状态管理
- 删除保护：有子部门时返回 409

## 经验

树形结构虽然比扁平 CRUD 复杂，但核心模式（Entity/Service/Controller/MapStruct）仍然复用。关键差异在 Service 层的 tree 构建逻辑和 Controller 返回类型（List 而非 PageResponse）。
