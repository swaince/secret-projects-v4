## Context

项目已有完整的字典管理功能：

- **后端**：`sys_dict` + `sys_dict_item` 两张表，提供 CRUD REST 端点（`/dicts`、`/dicts/{dictId}/items`）
- **前端**：管理页面 `DictView.vue` + API 层 `api/dict.ts`，支持字典和字典项的增删改查

当前缺陷：

1. 后端没有按 `dictCode` 查询字典项的接口（仅支持按 `dictId`）
2. 前端没有可复用的字典消费层（无 store/composable），业务页面无法便捷使用字典数据
3. 无缓存机制，每次使用都需网络请求
4. `itemValue` 始终为 string，缺少根据 `dataValueType` 的自动类型转换

约束：

- 后端：Spring Boot 3.5 + MyBatis-Plus + MapStruct + Flyway，测试用 H2
- 前端：Vue 3.5 + TypeScript 6 + Pinia (setup store) + axios (`@/utils/request.ts`)
- 前端 HTTP 层已做 `R<T>` 自动解包（拦截器返回 `data` 字段）

## Goals / Non-Goals

**Goals:**

- 后端新增按 dictCode 查询字典项的 GET 端点，返回带 `dataValueType` 的结构
- 前端实现 Pinia store 缓存层（内存 + localStorage 双层）
- 前端实现 `createDictAccessor` 工厂，支持注册表模式 + 对象属性访问
- 泛型参数化类型系统，`itemValue` 根据 `valueType` 声明获得正确的 TypeScript 类型
- 运行时根据 `dataValueType` 自动将 string 转换为目标类型

**Non-Goals:**

- 不做批量加载（一次请求多个 dictCode）
- 不改动现有字典管理页面的 CRUD 功能
- 不做字典项的全文搜索或模糊匹配
- 不做 SSR/服务端渲染场景的适配

## Decisions

### D1：后端接口路径与返回结构

- **选择**：`GET /dicts/items/by-code?dictCode=xxx`，返回 `R<DictWithItemsDTO>`（完整字典信息 + 字典项列表）
- **理由**：一次请求拿到字典元信息（dictName 可做标题展示、dataValueType 做类型转换）和全部字典项，无需额外请求
- **返回结构**：`{ dictId, dictName, dictCode, dataValueType, items: DictItemDTO[] }`
- **已考虑 alternative**：
  - `GET /dicts/code/{dictCode}/items` — RESTful 风格更纯，但 dictCode 可能含特殊字符，path param 不如 query param 安全 → 拒绝
  - 只返回 items + dataValueType（无字典基本信息）— 前端可能需要 dictName 做展示，且不符合统一 DTO 规范 → 拒绝
  - 前端组合调用（先查字典列表获取 dictId，再查 items）— 两次请求，增加复杂度 → 拒绝

### D2：前端分层架构

- **选择**：Store（缓存层）+ Accessor 工厂（类型安全的对象访问）分离
- **理由**：Store 保持纯粹的状态管理和持久化职责；Accessor 通过 Proxy 实现懒加载和属性访问，对组件透明
- **已考虑 alternative**：
  - Store 做全部逻辑 + 薄 composable — Store 过胖，混合网络/缓存/业务 → 拒绝
  - 纯 composable（无 Pinia）— DevTools 不可见，与项目 Pinia 惯例不一致 → 拒绝

### D3：缓存策略

- **选择**：内存 → localStorage → 网络三级读取，TTL 30 分钟
- **理由**：双层缓存覆盖页面内（内存）和跨刷新（localStorage）两种场景；30 分钟 TTL 平衡数据新鲜度与请求频率
- **已考虑 alternative**：
  - 仅内存缓存 — 刷新后丢失，频繁首屏请求 → 拒绝
  - sessionStorage — 关闭标签页丢失，不如 localStorage 持久 → 拒绝

### D4：API 使用风格

- **选择**：注册表模式 + 对象属性访问（`import { dict } from '@/dict'; dict.gender.items`）
- **理由**：dictCode 只在注册表定义一次，组件中零字符串硬编码；code 变更只改一处，维护成本最低
- **已考虑 alternative**：
  - `useDict('gender')` 字符串传参 — code 散落各组件，变更需全局搜索替换 → 拒绝
  - 枚举常量 + useDict(DICT_CODE.GENDER) — 仍需传参，类型推导不够直接 → 拒绝

### D5：类型系统

- **选择**：`DictItem<K extends string, V = string>` 泛型 + 注册表声明 `valueType` 映射
- **理由**：K 约束 itemKey 字面量实现选项枚举安全，V 根据 valueType 映射实现 itemValue 类型正确
- **运行时转换规则**：STRING→string, NUMBER→Number(), BOOLEAN→'true'/'1'判断, OBJECT/ARRAY→JSON.parse()

### D6：字典不存在时的行为

- **选择**：返回 null/空结构，不报错
- **理由**：前端组件渲染不应因字典缺失而中断；空 items 使 v-for 自然为空，无需额外错误处理

## Risks / Trade-offs

[Risk] localStorage 存储容量有限（约 5MB）→ Mitigation: 字典数据量通常很小（几十条 × 几十字节），远低于限制；store 提供 `invalidateAll()` 方法可手动清理

[Risk] Proxy 对象在 Vue 响应式系统中可能有边界情况 → Mitigation: Accessor 内部使用 `computed` + `ref` 构建响应式值，Proxy 仅做属性路由，不包裹响应式对象本身

[Trade-off] 注册表需手动维护（新增字典需在 registry 添加条目）→ 接受：一处维护远优于多处字符串散落，且 TypeScript 编译器会在注册表缺失时报错

[Trade-off] 双层缓存增加了数据一致性复杂度（字典修改后缓存可能陈旧）→ 接受：通过 invalidate 机制 + TTL 过期兜底；字典管理页操作后主动 invalidate

## Migration Plan

1. **后端先行**：新增 `GET /dicts/items/by-code` 端点 + 单元测试，验证接口可用
2. **前端基础层**：创建 `src/dict/types.ts` → `src/stores/dict.ts` → `src/dict/accessor.ts` → `src/dict/index.ts`
3. **前端 API 层**：在 `src/api/dict.ts` 新增 `fetchDictItemsByCode` 函数
4. **集成验证**：在一个现有页面试用 `dict.xxx` 验证完整链路
5. **字典管理页联动**：`DictView.vue` 保存/删除后调用 `invalidate`

回滚策略：各层独立，后端接口不影响现有端点；前端新增文件不影响现有功能，删除即回滚。

## Open Questions

- TTL 时长（30 分钟）是否需要做成可配置项，还是硬编码即可？
- 是否需要在 `DictItemDTO` 前端类型中补充 `itemLabel` 字段（后端 V1.0.0.2 已添加，前端未同步）？
