# 岗位管理 (Post Management) — Brainstorm

## 背景

系统管理模块下「组织架构」子模块需要岗位管理功能。菜单入口已存在（`/system/posts`），后端和前端均无现有代码。项目已有字典管理（Dict）作为完整参考模式。

## 决议链

### Q1: 岗位需要哪些业务字段？

**决议**：基础字段 + 岗位级别

- post_name — 岗位名称（必填）
- post_code — 岗位编码（必填，唯一）
- post_level — 岗位级别（关联字典）
- sort_order — 排序号
- remark — 备注

加上标准系统字段：status, built_in, deleted, create_at, create_by, update_at, update_by

### Q2: 岗位级别如何实现？

**决议**：INTEGER 类型，内置值：
- 1 = 操作员
- 2 = 审核员

用户可扩展更多级别。简单直接，无需关联字典。

### Q3: 功能范围？

**决议**：与字典管理完全一致

- CRUD（创建、读取、更新、删除）
- 分页查询 + 搜索过滤（按名称、级别）
- 状态切换（启用/禁用）
- 批量删除
- 内置岗位保护（禁止编辑/删除）

不包含：导入导出、岗位人员查看、部门关联。

## 设计取舍

| 决策点 | 选择 | 理由 |
|--------|------|------|
| 是否关联部门 | 不关联 | 部门管理尚未实现，YAGNI 原则 |
| post_level 实现 | INTEGER | 简单直接，内置操作员(1)/审核员(2) |
| 前端页面模式 | 单页面表格 + Dialog | 与字典管理一致，用户认知一致 |
| 编码唯一性 | 数据库 UNIQUE 约束 | 最可靠的唯一性保障 |
| 排序字段 | sort_order INT | 简单整数排序，满足需求 |

## 数据模型

```sql
CREATE TABLE sys_post (
    post_id       VARCHAR(32) PRIMARY KEY,
    post_name     VARCHAR(50)  NOT NULL,
    post_code     VARCHAR(50)  NOT NULL,
    post_level    INT          DEFAULT 1,
    sort_order    INT          DEFAULT 0,
    remark        VARCHAR(500),
    status        INT          DEFAULT 1,
    built_in      INT          DEFAULT 0,
    deleted       INT          DEFAULT 0,
    create_at     TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    create_by     VARCHAR(32),
    update_at     TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    update_by     VARCHAR(32),
    CONSTRAINT uk_post_code UNIQUE (post_code)
);
```

## 技术方案

完全复用字典管理的技术架构：

**后端**：Controller → Service → Mapper → Entity，MapStruct 转换，Flyway 迁移
**前端**：API 层 → PostView.vue（搜索卡片 + 表格卡片 + 分页 + Dialog）

## 前置依赖

- Flyway 迁移中预置内置岗位数据：操作员(level=1)、审核员(level=2)
