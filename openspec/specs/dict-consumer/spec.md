# dict-consumer Specification

## Purpose
TBD - created by archiving change frontend-dict-adapter. Update Purpose after archive.
## Requirements
### Requirement: 字典 API 函数
`src/api/dict.ts` SHALL 新增 `fetchDictByCode(dictCode: string)` 函数，调用 `GET /dicts/items/by-code?dictCode=xxx`。

#### Scenario: 请求成功
- **WHEN** 调用 `fetchDictByCode('sys_gender')`
- **THEN** 返回 `Promise<DictWithItemsDTO | null>`，包含字典信息和字典项列表

#### Scenario: 字典不存在
- **WHEN** 调用 `fetchDictByCode('not_exist')` 且后端返回 data 为 null
- **THEN** 返回 `null`

### Requirement: 字典 Store 缓存层
`src/stores/dict.ts` SHALL 导出 Pinia setup store `useDictStore`，提供内存 + localStorage 双层缓存。

#### Scenario: 首次加载（缓存未命中）
- **WHEN** 调用 `store.load('sys_gender')` 且内存和 localStorage 均无该 dictCode 缓存
- **THEN** SHALL 发起网络请求获取数据，写入内存缓存和 localStorage，返回字典数据

#### Scenario: 内存缓存命中
- **WHEN** 调用 `store.load('sys_gender')` 且内存缓存存在且未过期
- **THEN** SHALL 直接返回内存缓存数据，不发起网络请求

#### Scenario: localStorage 缓存命中
- **WHEN** 调用 `store.load('sys_gender')` 且内存缓存为空，但 localStorage 存在且未过期
- **THEN** SHALL 从 localStorage 读取并恢复到内存缓存，不发起网络请求

#### Scenario: 缓存过期
- **WHEN** 调用 `store.load('sys_gender')` 且缓存存在但 timestamp 超过 TTL（默认 30 分钟）
- **THEN** SHALL 发起网络请求刷新数据，更新内存和 localStorage

#### Scenario: 手动失效单个字典
- **WHEN** 调用 `store.invalidate('sys_gender')`
- **THEN** SHALL 清除该 dictCode 的内存缓存和 localStorage 条目

#### Scenario: 手动失效全部缓存
- **WHEN** 调用 `store.invalidateAll()`
- **THEN** SHALL 清除所有字典的内存缓存和 localStorage 条目

### Requirement: 字典注册表
`src/dict/index.ts` SHALL 导出 `dict` 对象作为全局字典注册表，通过 `createDictAccessor` 工厂创建。

#### Scenario: 注册表定义
- **WHEN** 定义 `dict = createDictAccessor({ gender: { code: 'sys_gender', valueType: 'STRING' } })`
- **THEN** `dict.gender` SHALL 可访问，返回字典访问器对象

#### Scenario: 新增字典注册
- **WHEN** 需要在页面使用新字典
- **THEN** 只需在 `src/dict/index.ts` 注册表中添加一行定义，组件中即可通过 `dict.xxx` 访问

### Requirement: 字典访问器属性访问
`createDictAccessor` SHALL 返回 Proxy 对象，通过属性访问触发对应 dictCode 的数据加载。

#### Scenario: 访问 items
- **WHEN** 在组件中访问 `dict.gender.items`
- **THEN** SHALL 返回 `ComputedRef<DictItem<K, V>[]>`，包含该字典的全部启用项

#### Scenario: 访问 getLabel
- **WHEN** 调用 `dict.gender.getLabel('M')`
- **THEN** SHALL 返回对应 itemKey='M' 的 itemLabel 值

#### Scenario: 访问 loading
- **WHEN** 在组件中访问 `dict.gender.loading`
- **THEN** SHALL 返回 `Ref<boolean>`，表示该字典是否正在加载中

#### Scenario: 访问 refresh
- **WHEN** 调用 `dict.gender.refresh()`
- **THEN** SHALL 强制失效缓存并重新请求该字典数据

#### Scenario: 懒加载
- **WHEN** 首次访问 `dict.gender.items`
- **THEN** SHALL 触发数据加载（若缓存未命中）；在数据到达前 items 为空数组

### Requirement: DictItem 泛型类型系统
`src/dict/types.ts` SHALL 导出 `DictItem<K, V>` 泛型接口。

#### Scenario: K 约束 itemKey 字面量
- **WHEN** 注册表声明 `gender: { code: 'sys_gender', valueType: 'STRING' }` 且组件泛型声明 `K = 'M' | 'F'`
- **THEN** `dict.gender.items` 的每项 `itemKey` 类型 SHALL 为 `K`

#### Scenario: V 根据 valueType 推导
- **WHEN** 注册表声明 `valueType: 'NUMBER'`
- **THEN** 对应 `DictItem` 的 `itemValue` 类型 SHALL 为 `number`

#### Scenario: 默认 valueType
- **WHEN** 注册表未声明 `valueType` 或声明为 `'STRING'`
- **THEN** `itemValue` 类型 SHALL 为 `string`

### Requirement: itemValue 运行时类型转换
字典 store 在缓存数据前 SHALL 根据 `dataValueType` 将 `itemValue` 从 string 转换为目标类型。

#### Scenario: STRING 类型不转换
- **WHEN** `dataValueType` 为 `'STRING'`
- **THEN** `itemValue` SHALL 保持原始 string 值

#### Scenario: NUMBER 类型转换
- **WHEN** `dataValueType` 为 `'NUMBER'` 且原始值为 `"42"`
- **THEN** `itemValue` SHALL 转换为数字 `42`

#### Scenario: BOOLEAN 类型转换
- **WHEN** `dataValueType` 为 `'BOOLEAN'` 且原始值为 `"true"` 或 `"1"`
- **THEN** `itemValue` SHALL 转换为 `true`

#### Scenario: BOOLEAN 假值转换
- **WHEN** `dataValueType` 为 `'BOOLEAN'` 且原始值为 `"false"` 或 `"0"`
- **THEN** `itemValue` SHALL 转换为 `false`

#### Scenario: OBJECT 类型转换
- **WHEN** `dataValueType` 为 `'OBJECT'` 且原始值为合法 JSON 对象字符串
- **THEN** `itemValue` SHALL 转换为解析后的对象

#### Scenario: ARRAY 类型转换
- **WHEN** `dataValueType` 为 `'ARRAY'` 且原始值为合法 JSON 数组字符串
- **THEN** `itemValue` SHALL 转换为解析后的数组

#### Scenario: JSON 解析失败降级
- **WHEN** `dataValueType` 为 `'OBJECT'` 或 `'ARRAY'` 但原始值不是合法 JSON
- **THEN** SHALL 保持原始 string 值，不抛出异常

### Requirement: 前端 DictItemDTO 补充 itemLabel
`src/api/dict.ts` 中的 `DictItemDTO` 接口 SHALL 包含 `itemLabel` 字段。

#### Scenario: 类型定义包含 itemLabel
- **WHEN** 查看 `DictItemDTO` 类型定义
- **THEN** SHALL 包含 `itemLabel: string` 字段

