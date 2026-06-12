## Context

系统管理模块下「组织架构」需要岗位管理功能。当前状态：

- 菜单入口已配置（`/system/posts`），使用 PlaceholderView
- 后端无任何岗位相关代码（无 Entity/Service/Controller/Migration）
- 前端无 API 层和页面组件
- 字典管理（Dict）模块已完整实现，作为参考模式

技术栈约束：后端 Spring Boot 3.5 + MyBatis-Plus + MapStruct + Flyway + PostgreSQL；前端 Vue 3.5 + shadcn-vue + Tailwind v4。

## Goals / Non-Goals

**Goals:**
- 实现岗位的完整 CRUD（创建、查询、更新、删除）
- 分页查询 + 按名称/级别搜索过滤
- 状态切换（启用/禁用）
- 批量删除
- 内置岗位保护（禁止编辑/删除）
- 预置内置岗位数据：操作员(level=1)、审核员(level=2)

**Non-Goals:**
- 岗位与部门关联（部门管理尚未实现）
- 岗位人员查看/分配
- 导入导出功能
- 岗位层级树结构

## Decisions

### D1：数据模型设计

- **选择**：`sys_post` 表，`post_level` 使用 INT 类型
- **理由**：岗位级别为固定少量值（操作员=1, 审核员=2），INT 足够表达且查询高效
- **已考虑 alternative**：
  - 关联字典：引入不必要的复杂度，级别值不频繁变动
  - VARCHAR 枚举字符串：类型不安全，占用空间大

### D2：编码唯一性保障

- **选择**：数据库 UNIQUE 约束 (`uk_post_code`)
- **理由**：最可靠的唯一性保证，并发场景下不会重复
- **已考虑 alternative**：
  - 应用层查重：并发下可能穿透
  - Redis 分布式锁：过度设计

### D3：前端页面结构

- **选择**：单页面 `PostView.vue`（搜索卡片 + 表格 + 分页 + Dialog 表单）
- **理由**：与字典管理一致，用户认知统一，开发效率高
- **已考虑 alternative**：
  - 独立路由 create/edit 页面：对简单 CRUD 过重

### D4：ID 生成策略

- **选择**：`UidService.next()` 生成 UUID v7，VARCHAR(32) 存储
- **理由**：与项目现有模式一致（Dict 模块同样使用）

### D5：软删除策略

- **选择**：`deleted` 字段标记删除，查询时过滤 `deleted=0`
- **理由**：项目统一模式，保留数据可追溯性

## Risks / Trade-offs

- [Risk] `post_level` INT 值无语义说明 → Mitigation: 前端维护 level 到显示名称的映射常量，后端注释说明
- [Trade-off] 不关联部门导致岗位无组织归属 → 接受：部门模块未实现，后续可通过中间表扩展
- [Risk] `post_code` UNIQUE 约束在软删除场景下可能冲突（删除后同编码无法重建）→ Mitigation: UNIQUE 约束加 `WHERE deleted = 0` 条件（PostgreSQL partial unique index）

## Migration Plan

1. **数据库迁移**：`V1.0.0.3__create_post_table.sql`
   - 创建 `sys_post` 表
   - 插入内置岗位数据（操作员、审核员）
   - 创建 partial unique index: `CREATE UNIQUE INDEX uk_post_code ON sys_post(post_code) WHERE deleted = 0`

2. **后端部署**：新增 Controller/Service/Mapper/Entity，无破坏性变更

3. **前端部署**：新增 API 层 + 页面组件，menu.ts 添加 component 引用

4. **回滚策略**：`V1.0.0.3` 迁移可通过 Flyway undo 回滚（删表即可），后端/前端代码删除无副作用

## Open Questions

无。设计简单明确，完全复用已验证的字典管理模式。
