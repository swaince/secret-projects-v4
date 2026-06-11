# brainstorm: dict-management

## 背景

secret-projects-v4 前端已有 admin-layout 布局组件，后端 `springboot-web-dev` 技能已定义字典模块完整规范（DictionaryElement、@Dictionary、内置枚举、sys_dict 表 DDL），但实际代码尚未实现。字典管理是后台系统的基础设施，所有枚举/下拉选项依赖字典。

## 决策链

### Q1: 字典管理菜单位置

- **选 A**：系统管理 > 字典管理。与用户管理、角色管理同级一级菜单。

### Q2: 数据模型

| 方案 | 描述 |
|------|------|
| A: 双表 | sys_dict（字典元数据）+ sys_dict_item（字典项） |
| B: 单表 | 仅 sys_dict，dict_type 字符串分组 |
| C: 混合 | 单表存储，Service 层封装两层 |

- **选 A**：`sys_dict` 和 `sys_dict_item` 双表，通过 `dict_id` 关联。

### Q3: 字典字段

**sys_dict**: dict_id (PK), dict_name, dict_code, data_value_type, remark + 8 个审计字段（create_at, create_by, update_at, update_by, status, built_in, deleted, remark）

**sys_dict_item**: dict_item_id (PK), dict_id (FK), item_key, item_value, sort_order, status, built_in, deleted, remark + 审计字段

### Q4: 前端页面布局

- 主区域：字典分页列表（page）
- 点击字典详情 → 右侧弹出抽屉（Drawer）→ 字典项列表（不分页，批量删除）

### Q5: 字典项排序

- 使用 `sort_order` 字段（避免 SQL 关键字 `sort`）
- 数值编辑排序，不支持手动拖拽

### Q6: 字典项状态

- 支持 status（启用/禁用）+ built_in（是否内置）+ deleted（软删除）

### Q7: 数据类型限定

- 字典类型创建时选择 `data_value_type`，引用 `DataType` 枚举（STRING/NUMBER/BOOLEAN/OBJECT/ARRAY）

### Q8: CRUD 接口

**字典 (sys_dict)**:
- 新增: create
- 删除: deleteById
- 修改: update
- 批量删除: delete(List<String> ids)
- 分页列表: page(DictPageRequest)

**字典项 (sys_dict_item)**:
- 新增: create
- 修改: update
- 删除: deleteById
- 批量删除: delete(List<String> ids)
- 根据字典ID批量删除: deleteByDictId(String dictId)
- 根据字典编码批量删除: deleteByDictCode(String dictCode)
- 查询字典项(不分页): listByDictId(String dictId)

## 设计总览

### 表结构

```sql
CREATE TABLE sys_dict (
    dict_id VARCHAR(32) PRIMARY KEY,
    dict_name VARCHAR(50) NOT NULL,
    dict_code VARCHAR(50) NOT NULL,
    data_value_type VARCHAR(20) NOT NULL DEFAULT 'STRING',
    remark VARCHAR(500),
    -- audit fields
    create_at, create_by, update_at, update_by,
    status INT DEFAULT 1, built_in INT DEFAULT 0, deleted INT DEFAULT 0
);

CREATE TABLE sys_dict_item (
    dict_item_id VARCHAR(32) PRIMARY KEY,
    dict_id VARCHAR(32) NOT NULL,
    item_key VARCHAR(50) NOT NULL,
    item_value VARCHAR(200) NOT NULL,
    sort_order INT DEFAULT 0,
    remark VARCHAR(500),
    -- audit fields
    create_at, create_by, update_at, update_by,
    status INT DEFAULT 1, built_in INT DEFAULT 0, deleted INT DEFAULT 0
);
```

### 后端模块

```
common/
├── controller/
│   ├── DictController.java       # /dicts
│   └── DictItemController.java   # /dicts/{dictId}/items
├── service/
│   ├── DictService.java / DictServiceImpl.java
│   └── DictItemService.java / DictItemServiceImpl.java
├── mapper/
│   ├── DictMapper.java (extends BaseMapper<SysDict>)
│   └── DictItemMapper.java (extends BaseMapper<SysDictItem>)
├── entity/
│   ├── SysDict.java
│   └── SysDictItem.java
├── dto/
│   ├── common/ DictDTO.java, DictItemDTO.java
│   ├── request/ DictCreateRequest.java, DictItemCreateRequest.java
│   └── response/
└── mapstruct/
    ├── DictStructMapper.java
    └── DictItemStructMapper.java
```

### 前端

```
views/system/dict/
├── Index.vue              # 字典分页列表
└── components/
    └── DictItemDrawer.vue # 右侧抽屉，字典项列表
```

### 遵循规范

- **后端**: `springboot-web-dev` 技能全部规范（R<T>、构造器注入、MapStruct、Flyway、TDD、Javadoc 等）
- **前端**: shadcn-vue + design-system.css 最终裁定 + 五个前端技能协同

## 测试策略（强制 TDD）

| 测试目标 | 文件 | 测试内容 |
|---------|------|---------|
| DictService | DictServiceTest.java | 分页查询、创建、更新、删除、批量删除 |
| DictItemService | DictItemServiceTest.java | 新增/修改/删除/批量删除/按dictId删/按dictCode删/查询 |
| DictStructMapper | 集成测试（编译时） | Entity ↔ DTO 转换正确性 |
| dict/index.vue | DictPage.spec.ts | 列表渲染、搜索、抽屉弹出 |
| dict/DictItemDrawer | DictItemDrawer.spec.ts | 字典项渲染、新增、删除、批量删除 |

## 实现依赖

- **Flyway 迁移**: V1.0.0.0__init_data.sql（sys_dict + sys_dict_item DDL）
- **shadcn-vue 组件**: Drawer（抽屉）、Table（表格）、Button、Input、Dialog（确认删除）、Select（data_type 下拉）
