## Why

当前字典系统只有管理侧（CRUD），缺少消费侧的基础设施。业务页面需要使用字典数据（如性别下拉、状态标签）时，没有标准方式获取字典项：后端缺按编码查询接口，前端无缓存层和类型安全的访问机制。每个页面都需自行拼装请求、手动缓存、硬编码 dictCode 字符串，导致重复代码和维护负担。本变更建立字典消费的标准路径，使业务页面能通过类型安全的对象属性访问方式一行代码获取字典数据。

## What Changes

**后端：新增按编码查询接口**
- From: 仅支持 `GET /dicts/{dictId}/items`（按 ID 查询）
- To: 新增 `GET /dicts/items/by-code?dictCode=xxx`，返回带 `dataValueType` 的字典项列表
- Reason: 前端消费场景只知道 dictCode，不知道 dictId
- Impact: non-breaking，新增端点不影响现有接口

**前端：新增字典消费层**
- From: 无字典消费基础设施，各页面自行处理
- To: 提供 Pinia store（双层缓存）+ `createDictAccessor` 工厂（注册表模式、Proxy 属性访问、泛型类型推导、itemValue 自动类型转换）
- Reason: 统一字典使用方式，消除重复代码，提供类型安全
- Impact: non-breaking，纯新增文件

## Capabilities

### New Capabilities

- `dict-consumer`: 前端字典消费层 — 包含 Pinia 缓存 store、createDictAccessor 工厂、注册表定义、DictItem 泛型类型系统、itemValue 运行时类型转换

### Modified Capabilities

- `dict-crud`: 新增 `GET /dicts/items/by-code?dictCode=xxx` 查询端点，返回 `DictWithItemsDTO`（完整字典信息 + items 列表）

## Impact

- **后端 API**：新增 1 个 GET 端点，需新建 DTO 类 `DictWithItemsDTO`（字典信息 + items 列表），Service 层新增 `getWithItemsByCode` 方法
- **前端新增文件**：`src/dict/types.ts`、`src/dict/accessor.ts`、`src/dict/index.ts`、`src/stores/dict.ts`、`src/api/dict.ts`（追加函数）
- **前端类型修订**：`DictItemDTO` 需补充 `itemLabel` 字段（后端已有，前端类型未同步）
- **依赖**：无新外部依赖，全部基于现有 Vue 3 + Pinia + TypeScript + axios
- **测试**：后端需 H2 集成测试验证新端点；前端需 Vitest 单元测试覆盖 store 缓存逻辑和 accessor 类型转换
