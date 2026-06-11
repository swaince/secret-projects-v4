## Context

secret-projects-v4 前端已有 admin-layout 布局组件，后端 `springboot-web-dev` 技能已定义字典模块完整规范（DictionaryElement 接口、@Dictionary 注解、内置枚举），但实际字典 CRUD 代码尚未实现。字典管理是后台系统基础设施——所有枚举、下拉选项、状态值均依赖字典，必须在业务模块开发前落地。

约束：
- 后端必须遵循 `springboot-web-dev` 技能全部规范（禁止 Lombok、构造器注入、R\<T\> 响应、MapStruct、TDD 等）
- 前端必须使用 shadcn-vue 组件，`design-system.css` 最终裁定样式，五个前端技能协同
- 两端均需强制 TDD

## Goals / Non-Goals

**Goals:**
- 实现字典管理完整 CRUD（sys_dict 新增/修改/删除/批量删除/分页列表）
- 实现字典项管理完整 CRUD（新增/修改/删除/批量删除/按字典ID批量删除/按字典编码批量删除/查询字典项列表）
- 前端字典管理页面（分页列表 + 右侧抽屉展示字典项）
- Flyway 迁移脚本（sys_dict + sys_dict_item DDL）
- 字典创建时指定 data_value_type（DataType 枚举），约束该字典下所有字典项值类型

**Non-Goals:**
- 不实现字典数据的导入/导出
- 不实现字典项拖拽排序（仅数值编辑 sort_order）
- 不实现字典值类型运行时校验（前端创建字典项时校验 value 格式）
- 不实现字典缓存（Redis）

## Decisions

### D1：数据模型 — 双表 sys_dict + sys_dict_item

- **选择**：`sys_dict`（字典元数据）和 `sys_dict_item`（字典项），通过 `dict_id` 外键关联
- **理由**：独立字典表可存储字典级配置（如 data_value_type），便于区分"字典"和"字典项"两个管理层次；前端天然映射为两级页面（列表 + 抽屉）
- **已考虑 alternative**：单表方案（维持现有 `sys_dict` 的 `dict_type` 分组）——字典元数据无法独立存储，data_value_type 等字段无处存放

### D2：排序字段 — sort_order

- **选择**：字典项排序使用 `sort_order`（Integer），前端数值编辑
- **理由**：`sort` 是 SQL 关键字/保留字，避免潜在兼容问题；数值编辑足够满足后台管理需求
- **已考虑 alternative**：拖拽排序（前端复杂度高，移动端不友好，需求未要求）

### D3：前端交互 — 分页列表 + 右侧抽屉

- **选择**：主区域分页展示字典列表，点击字典行弹出 shadcn-vue Drawer 展示字典项（不分页，支持批量删除）
- **理由**：字典项通常数量不多（<100 条），无需分页；抽屉避免页面跳转，操作流畅
- **已考虑 alternative**：两级菜单（字典列表页 → 点击进入字典项页）——页面跳转割裂体验，不符合后台管理习惯

### D4：后端模块归属 — common 包

- **选择**：字典模块放在 `common/` 下（controller/service/mapper/entity/dto），作为跨模块共享基础设施
- **理由**：字典被所有业务模块引用（用户状态、部门类型等），放在 common 包符合 `springboot-web-dev` 项目结构规范
- **已考虑 alternative**：放在 `system/` 下——字典作为基础设施不应归属特定模块

### D5：data_value_type 字段

- **选择**：字典表含 `data_value_type` 字段，值取自 `DataType` 枚举（STRING/NUMBER/BOOLEAN/OBJECT/ARRAY），默认 STRING
- **理由**：后端可根据类型校验字典项 value 格式，前端可根据类型切换输入组件（如 NUMBER 显示数字输入框）
- **已考虑 alternative**：不限类型（纯字符串存储）——失去类型约束，调用方需自行解析

## Risks / Trade-offs

- [Risk] 字典项通过 `dict_id` 关联字典，删除字典时可能遗留孤儿子典项 → Mitigation：删除字典时级联删除或先校验无子项
- [Risk] 按字典编码批量删除（deleteByDictCode）依赖 dict_code 唯一性 → Mitigation：dict_code 加唯一约束
- [Trade-off] 字典项不分页列表可能数据量过大 → 接受理由：字典项通常 < 100 条，若未来数据增长可加虚拟滚动

## Migration Plan

1. 创建 Flyway 迁移脚本 `V1.0.0.0__init_data.sql`（sys_dict + sys_dict_item DDL）
2. 部署后端代码（common 包，新增接口）
3. 部署前端代码（字典管理页面）
4. 回滚：删除新增的 Flyway 脚本 + 后端代码，前端回退

## Open Questions

- 无。所有关键技术决策已在 brainstorm 阶段达成共识。
