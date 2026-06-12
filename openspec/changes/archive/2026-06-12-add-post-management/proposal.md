## Why

系统管理模块「组织架构」下需要岗位管理能力，用于定义和管理组织内的工作岗位。当前菜单入口已配置但指向占位页面，后端无任何岗位数据结构和接口。岗位管理是组织架构的基础模块，为后续用户-岗位关联、权限分配提供数据支撑。

## What Changes

**新增后端岗位管理模块**
- 新增 `sys_post` 数据表（Flyway 迁移）
- 新增 Entity / DTO / Mapper / MapStruct / Service / Controller 全套代码
- REST API: `GET /posts`（分页）、`GET /posts/{id}`、`POST /posts`、`PUT /posts/{id}`、`DELETE /posts/{id}`、`DELETE /posts`（批量）
- 预置内置岗位数据：操作员(level=1)、审核员(level=2)

**新增前端岗位管理页面**
- 新增 `api/post.ts` 接口层
- 新增 `views/system/PostView.vue` 页面
- menu.ts 中已有路由入口 `/system/posts`，添加 component 引用

## Capabilities

### New Capabilities

- `post-management`: 岗位的完整生命周期管理，包括 CRUD、分页查询、搜索过滤、状态切换、批量删除、内置保护

### Modified Capabilities

（无现有 spec 需要修改）

## Impact

- **数据库**：新增 `sys_post` 表 + partial unique index
- **后端**：`system` 包下新增 controller/service/mapper/entity/dto/mapstruct 各一组文件
- **前端**：新增 `api/post.ts` + `views/system/PostView.vue`，修改 `config/menu.ts`（添加 component 引用）
- **依赖**：无新增外部依赖，完全使用已有技术栈
- **破坏性**：无。纯新增功能，不影响现有模块
