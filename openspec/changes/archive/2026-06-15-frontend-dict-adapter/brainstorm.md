# 前端字典适配 — 头脑风暴记录

## 背景

项目已有完整的字典管理后端（`sys_dict` + `sys_dict_item` 表，CRUD 端点齐全）和前端管理页面（`DictView.vue`）。但缺少：

1. **按 dictCode 查询字典项的后端接口** — 现有接口仅支持按 dictId 查询
2. **前端可复用的字典消费层** — 没有 store/composable，业务页面无法便捷获取字典数据
3. **字典缓存机制** — 每次使用都需网络请求
4. **类型安全** — 字典项的 itemValue 始终为 string，缺少根据 dataValueType 的自动转换和类型推导

## 决议链

### Q1：变更范围？

**决定**：前后端都做。

- 后端：新增按 dictCode 查询字典项的 GET 接口
- 前端：Store（缓存层）+ Accessor（注册表工厂）+ 类型系统

### Q2：类型推导等级？

**决定**：泛型参数化。

- `DictItem<K, V>` — K 约束 itemKey 字面量，V 约束 itemValue 的运行时类型
- 注册表中声明 valueType 实现编译期推导
- 后端返回 dataValueType 实现运行时转换

### Q3：缓存策略？

**决定**：内存 + localStorage 双层。

- 读取顺序：内存 → localStorage → 网络请求
- 写入：网络返回后同时写内存和 localStorage
- TTL：默认 30 分钟，过期后下次访问触发刷新
- 失效：字典管理页保存/删除后调用 invalidate(code)

### Q4：API 使用风格？

**决定**：对象属性访问模式（注册表 + Proxy）。

**否决方案**：
- `useDict<T>('gender')` — 组件中仍需传字符串，dictCode 变更需全局搜索替换
- `useDicts('gender', 'orderStatus')` — 同上，仍是硬编码字符串

**采纳方案**：
```typescript
// 注册表定义（唯一维护点）
export const dict = createDictAccessor({
  gender: { code: 'sys_gender', valueType: 'STRING' },
  orderStatus: { code: 'order_status', valueType: 'NUMBER' },
})

// 组件中直接导入属性访问，零字符串
import { dict } from '@/dict'
dict.gender.items       // Ref<DictItem<'M'|'F', string>[]>
dict.gender.getLabel('M')  // '男'
```

优势：dictCode 变更只改注册表一处，组件代码零感知。

### Q5：批量加载？

**决定**：不需要。每次只查单个 dictCode，通过缓存减少重复请求。

### Q6：itemValue 类型转换？

**决定**：双重保障。

- **编译期**：注册表声明 `valueType: 'NUMBER'`，TypeScript 推导 `itemValue: number`
- **运行时**：后端接口返回 `dataValueType`，前端据此做 `string → 目标类型` 转换

转换规则：
| dataValueType | 转换逻辑 | TypeScript 类型 |
|---|---|---|
| STRING | 原样返回 | `string` |
| NUMBER | `Number(raw)` | `number` |
| BOOLEAN | `raw === 'true' \|\| raw === '1'` | `boolean` |
| OBJECT | `JSON.parse(raw)` | `Record<string, unknown>` |
| ARRAY | `JSON.parse(raw)` | `unknown[]` |

## 设计取舍

### 架构方案对比

| 方案 | 描述 | 优点 | 缺点 | 结论 |
|------|------|------|------|------|
| A: Store 缓存层 + Accessor 主 API | Store 管缓存/持久化，Accessor 工厂做类型安全的对象访问 | 职责分离清晰，符合项目惯例 | 文件稍多 | ✅ 采纳 |
| B: Store 做主逻辑 + 薄 Composable | 全部逻辑在 Store | 集中调试 | Store 过胖，混合请求/缓存/业务 | ❌ |
| C: 纯 Composable | 模块级 Map 缓存 | 最简单 | 不走 Pinia，DevTools 不可见 | ❌ |

### 后端接口设计

新增端点：`GET /dicts/items/by-code?dictCode=xxx`

返回结构：
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "dictId": "xxx",
    "dictName": "性别",
    "dictCode": "sys_gender",
    "dataValueType": "STRING",
    "items": [
      { "dictItemId": "...", "itemKey": "M", "itemLabel": "男", "itemValue": "M", "sortOrder": 0 }
    ]
  }
}
```

- 字典不存在 → 返回 null（不报错）
- 只返回 status=1 且 deleted=0 的启用项
- 按 sortOrder 排序

### 前端文件结构

```
src/
├── dict/
│   ├── index.ts          ← 注册表（唯一维护点）
│   ├── accessor.ts       ← createDictAccessor 工厂（Proxy 实现）
│   └── types.ts          ← DictItem<K,V>、DataValueType、ValueTypeMap
├── stores/
│   └── dict.ts           ← useDictStore（内存+localStorage 缓存层）
├── api/
│   └── dict.ts           ← 新增 fetchDictItemsByCode(code) 函数
```
