## 1. 后端：DictWithItemsDTO 与查询接口

- [x] 1.1 创建 `DictWithItemsDTO` 类（dictId, dictName, dictCode, dataValueType, items: List<DictItemDTO>）
- [x] 1.2 `DictItemService` 新增 `getWithItemsByCode(String dictCode)` 方法（查字典 → 查启用项 → 组装 DTO）
- [x] 1.3 `DictItemController` 新增 `GET /dicts/items/by-code?dictCode=xxx` 端点（@RequestParam @NotBlank）
- [x] 1.4 编写 H2 集成测试：覆盖正常查询、字典不存在返回 null、无启用项返回空 items、缺少参数返回 400

## 2. 前端：API 层与类型修订

- [x] 2.1 `src/api/dict.ts` 中 `DictItemDTO` 接口补充 `itemLabel: string` 字段
- [x] 2.2 新增 `DictWithItemsDTO` 接口定义（dictId, dictName, dictCode, dataValueType, items）
- [x] 2.3 新增 `fetchDictByCode(dictCode: string): Promise<DictWithItemsDTO | null>` 函数

## 3. 前端：字典类型系统

- [x] 3.1 创建 `src/dict/types.ts`，定义 `DataValueType`、`ValueTypeMap`、`DictItem<K, V>` 泛型接口
- [x] 3.2 定义 `DictRegistryEntry` 类型（code: string, valueType?: DataValueType）
- [x] 3.3 定义 `DictAccessor<K, V>` 类型（items, loading, getLabel, refresh）

## 4. 前端：字典 Store 缓存层

- [x] 4.1 创建 `src/stores/dict.ts`，定义 Pinia setup store `useDictStore`
- [x] 4.2 实现内存缓存结构 `Map<dictCode, { data, timestamp }>`
- [x] 4.3 实现 localStorage 读写（key: `app_dict_cache`，JSON 序列化）
- [x] 4.4 实现 `load(dictCode)` 方法：内存 → localStorage → 网络三级读取 + TTL 判断
- [x] 4.5 实现 `invalidate(dictCode)` 和 `invalidateAll()` 方法
- [x] 4.6 实现 `itemValue` 运行时类型转换（convertValue 函数，覆盖 STRING/NUMBER/BOOLEAN/OBJECT/ARRAY）

## 5. 前端：Accessor 工厂与注册表

- [x] 5.1 创建 `src/dict/accessor.ts`，实现 `createDictAccessor` 工厂函数（Proxy + 懒加载）
- [x] 5.2 每个属性访问返回 `{ items: ComputedRef, loading: Ref, getLabel(key), refresh() }`
- [x] 5.3 创建 `src/dict/index.ts`，定义初始注册表并导出 `dict` 对象

## 6. 前端：单元测试

- [x] 6.1 编写 `useDictStore` 测试：缓存命中、过期刷新、invalidate、localStorage 恢复
- [x] 6.2 编写 `createDictAccessor` 测试：属性访问、getLabel、懒加载、refresh
- [x] 6.3 编写 `convertValue` 测试：各 dataValueType 转换 + JSON 解析失败降级

## 7. 集成联动

- [x] 7.1 `DictView.vue` 字典保存/删除操作后调用 `useDictStore().invalidate(dictCode)`
- [x] 7.2 运行 `pnpm type-check` 和 `pnpm lint` 确认无类型错误和 lint 问题
- [x] 7.3 运行 `./mvnw test -pl backend` 确认后端测试通过
