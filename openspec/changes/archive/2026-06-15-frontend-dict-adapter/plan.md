# 前端字典适配 Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 建立字典消费的标准路径，前后端协作实现按 dictCode 查询字典 + 前端类型安全的缓存式字典访问。

**Architecture:** 后端新增 `GET /dicts/items/by-code` 端点返回 `DictWithItemsDTO`；前端 Pinia store 做内存+localStorage 双层缓存，`createDictAccessor` 工厂通过 Proxy 提供注册表式属性访问，泛型类型系统保证 `itemValue` 的编译期类型推导和运行时转换。

**Tech Stack:** Spring Boot 3.5 / MyBatis-Plus / MapStruct / H2 测试 / Vue 3.5 / TypeScript 6 / Pinia / Vitest

---

## Task 1: 后端 DictWithItemsDTO

**Files:**
- Create: `backend/src/main/java/com/dfec/soft/secret/system/dto/common/DictWithItemsDTO.java`

- [ ] **Step 1: 创建 DTO 类**

```java
package com.dfec.soft.secret.system.dto.common;

import java.util.List;

public class DictWithItemsDTO {
    private String dictId;
    private String dictName;
    private String dictCode;
    private String dataValueType;
    private List<DictItemDTO> items;

    // getters and setters
}
```

- [ ] **Step 2: 验证编译通过**

Run: `./mvnw compile -pl backend -q`
Expected: BUILD SUCCESS

---

## Task 2: 后端 Service 方法

**Files:**
- Modify: `backend/src/main/java/com/dfec/soft/secret/system/service/DictItemService.java`
- Modify: `backend/src/main/java/com/dfec/soft/secret/system/service/impl/DictItemServiceImpl.java`

- [ ] **Step 1: 接口新增方法声明**

在 `DictItemService` 接口新增：

```java
DictWithItemsDTO getWithItemsByCode(String dictCode);
```

- [ ] **Step 2: 实现方法**

在 `DictItemServiceImpl` 实现：

```java
@Override
public DictWithItemsDTO getWithItemsByCode(String dictCode) {
    SysDict dict = dictMapper.selectOne(
        new LambdaQueryWrapper<SysDict>()
            .eq(SysDict::getDictCode, dictCode)
            .eq(SysDict::getDeleted, 0)
            .eq(SysDict::getStatus, 1)
    );
    if (dict == null) {
        return null;
    }
    List<DictItemDTO> items = listByDictId(dict.getDictId());
    DictWithItemsDTO result = new DictWithItemsDTO();
    result.setDictId(dict.getDictId());
    result.setDictName(dict.getDictName());
    result.setDictCode(dict.getDictCode());
    result.setDataValueType(dict.getDataValueType());
    result.setItems(items);
    return result;
}
```

注意：需注入 `DictMapper`（若未注入）。

- [ ] **Step 3: 验证编译通过**

Run: `./mvnw compile -pl backend -q`
Expected: BUILD SUCCESS

---

## Task 3: 后端 Controller 端点

**Files:**
- Modify: `backend/src/main/java/com/dfec/soft/secret/system/controller/DictItemController.java`

- [ ] **Step 1: 新增端点方法**

```java
@GetMapping("/dicts/items/by-code")
public R<DictWithItemsDTO> getByCode(@RequestParam @NotBlank String dictCode) {
    return R.ok(dictItemService.getWithItemsByCode(dictCode));
}
```

注意：该端点路径独立于 `/dicts/{dictId}/items` 的嵌套路径，需确认 controller 的 `@RequestMapping` 基路径兼容，或将此方法放在 `DictController` 上。检查现有 `DictController` 已有 `DELETE /dicts/items/by-code` 端点，同路径不同方法（GET vs DELETE），放在同一 controller 更合理。

- [ ] **Step 2: 验证编译通过**

Run: `./mvnw compile -pl backend -q`
Expected: BUILD SUCCESS

---

## Task 4: 后端集成测试

**Files:**
- Create: `backend/src/test/java/com/dfec/soft/secret/system/controller/DictItemByCodeTest.java`

- [ ] **Step 1: 编写测试类（H2 + SpringBootTest）**

```java
package com.dfec.soft.secret.system.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class DictItemByCodeTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldReturnDictWithItemsByCode() throws Exception {
        // 依赖 DictSyncRunner 启动时同步的内置字典
        mockMvc.perform(get("/api/dicts/items/by-code").param("dictCode", "status"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.data.dictCode").value("status"))
            .andExpect(jsonPath("$.data.dataValueType").exists())
            .andExpect(jsonPath("$.data.items").isArray());
    }

    @Test
    void shouldReturnNullWhenDictCodeNotExist() throws Exception {
        mockMvc.perform(get("/api/dicts/items/by-code").param("dictCode", "not_exist_xxx"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    void shouldReturn400WhenDictCodeMissing() throws Exception {
        mockMvc.perform(get("/api/dicts/items/by-code"))
            .andExpect(status().isBadRequest());
    }
}
```

- [ ] **Step 2: 运行测试**

Run: `./mvnw test -pl backend -Dtest=DictItemByCodeTest`
Expected: 3 tests PASS

- [ ] **Step 3: Commit**

```bash
git add backend/src/main/java/com/dfec/soft/secret/system/dto/common/DictWithItemsDTO.java
git add backend/src/main/java/com/dfec/soft/secret/system/service/DictItemService.java
git add backend/src/main/java/com/dfec/soft/secret/system/service/impl/DictItemServiceImpl.java
git add backend/src/main/java/com/dfec/soft/secret/system/controller/
git add backend/src/test/
git commit -m "feat(dict): add GET /dicts/items/by-code endpoint"
```

---

## Task 5: 前端 API 层与类型修订

**Files:**
- Modify: `frontend/src/api/dict.ts`

- [ ] **Step 1: DictItemDTO 补充 itemLabel 字段**

在 `DictItemDTO` 接口中添加：

```typescript
itemLabel: string
```

- [ ] **Step 2: 新增 DictWithItemsDTO 接口**

```typescript
export interface DictWithItemsDTO {
  dictId: string
  dictName: string
  dictCode: string
  dataValueType: string
  items: DictItemDTO[]
}
```

- [ ] **Step 3: 新增 fetchDictByCode 函数**

```typescript
export function fetchDictByCode(dictCode: string): Promise<DictWithItemsDTO | null> {
  return http.get<DictWithItemsDTO | null>('/dicts/items/by-code', { dictCode })
}
```

- [ ] **Step 4: 验证类型**

Run: `pnpm type-check` (in `frontend/`)
Expected: 无错误

---

## Task 6: 前端字典类型系统

**Files:**
- Create: `frontend/src/dict/types.ts`

- [ ] **Step 1: 创建类型文件**

```typescript
export type DataValueType = 'STRING' | 'NUMBER' | 'BOOLEAN' | 'OBJECT' | 'ARRAY'

export type ValueTypeMap = {
  STRING: string
  NUMBER: number
  BOOLEAN: boolean
  OBJECT: Record<string, unknown>
  ARRAY: unknown[]
}

export interface DictItem<K extends string = string, V = string> {
  dictItemId: string
  itemKey: K
  itemLabel: string
  itemValue: V
  sortOrder: number
}

export interface DictRegistryEntry<V extends DataValueType = 'STRING'> {
  code: string
  valueType?: V
}

export interface DictAccessor<K extends string = string, V = string> {
  items: import('vue').ComputedRef<DictItem<K, V>[]>
  loading: import('vue').Ref<boolean>
  getLabel: (key: K) => string
  refresh: () => Promise<void>
}
```

- [ ] **Step 2: 验证类型**

Run: `pnpm type-check` (in `frontend/`)
Expected: 无错误

---

## Task 7: 前端 Store 缓存层

**Files:**
- Create: `frontend/src/stores/dict.ts`

- [ ] **Step 1: 编写 convertValue 工具函数**

```typescript
import type { DataValueType } from '@/dict/types'

export function convertValue(raw: string, type: DataValueType): unknown {
  switch (type) {
    case 'NUMBER':
      return Number(raw)
    case 'BOOLEAN':
      return raw === 'true' || raw === '1'
    case 'OBJECT':
    case 'ARRAY':
      try {
        return JSON.parse(raw)
      } catch {
        return raw
      }
    default:
      return raw
  }
}
```

- [ ] **Step 2: 实现 useDictStore**

```typescript
import { defineStore } from 'pinia'
import { reactive } from 'vue'
import { fetchDictByCode } from '@/api/dict'
import type { DataValueType } from '@/dict/types'

interface CacheEntry {
  dictId: string
  dictName: string
  dictCode: string
  dataValueType: DataValueType
  items: Array<{ dictItemId: string; itemKey: string; itemLabel: string; itemValue: unknown; sortOrder: number }>
  timestamp: number
}

const STORAGE_KEY = 'app_dict_cache'
const DEFAULT_TTL = 30 * 60 * 1000 // 30 minutes

export const useDictStore = defineStore('dict', () => {
  const cache = reactive(new Map<string, CacheEntry>())

  function isExpired(entry: CacheEntry): boolean {
    return Date.now() - entry.timestamp > DEFAULT_TTL
  }

  function readLocalStorage(): Map<string, CacheEntry> {
    try {
      const raw = localStorage.getItem(STORAGE_KEY)
      if (!raw) return new Map()
      return new Map(Object.entries(JSON.parse(raw)))
    } catch {
      return new Map()
    }
  }

  function writeLocalStorage() {
    const obj = Object.fromEntries(cache.entries())
    localStorage.setItem(STORAGE_KEY, JSON.stringify(obj))
  }

  async function load(dictCode: string): Promise<CacheEntry | null> {
    // 1. memory
    const mem = cache.get(dictCode)
    if (mem && !isExpired(mem)) return mem

    // 2. localStorage
    const stored = readLocalStorage().get(dictCode)
    if (stored && !isExpired(stored)) {
      cache.set(dictCode, stored)
      return stored
    }

    // 3. network
    const data = await fetchDictByCode(dictCode)
    if (!data) return null

    const entry: CacheEntry = {
      dictId: data.dictId,
      dictName: data.dictName,
      dictCode: data.dictCode,
      dataValueType: data.dataValueType as DataValueType,
      items: data.items.map((item) => ({
        dictItemId: item.dictItemId,
        itemKey: item.itemKey,
        itemLabel: item.itemLabel,
        itemValue: convertValue(item.itemValue, data.dataValueType as DataValueType),
        sortOrder: item.sortOrder,
      })),
      timestamp: Date.now(),
    }
    cache.set(dictCode, entry)
    writeLocalStorage()
    return entry
  }

  function invalidate(dictCode: string) {
    cache.delete(dictCode)
    const stored = readLocalStorage()
    stored.delete(dictCode)
    localStorage.setItem(STORAGE_KEY, JSON.stringify(Object.fromEntries(stored.entries())))
  }

  function invalidateAll() {
    cache.clear()
    localStorage.removeItem(STORAGE_KEY)
  }

  function getItems(dictCode: string) {
    return cache.get(dictCode)?.items ?? []
  }

  return { cache, load, invalidate, invalidateAll, getItems }
})
```

- [ ] **Step 3: 验证类型**

Run: `pnpm type-check` (in `frontend/`)
Expected: 无错误

---

## Task 8: 前端 Accessor 工厂

**Files:**
- Create: `frontend/src/dict/accessor.ts`

- [ ] **Step 1: 实现 createDictAccessor**

```typescript
import { computed, ref, type ComputedRef, type Ref } from 'vue'
import { useDictStore } from '@/stores/dict'
import type { DataValueType, DictAccessor, DictItem, DictRegistryEntry, ValueTypeMap } from './types'

type InferValue<E> = E extends DictRegistryEntry<infer V>
  ? V extends keyof ValueTypeMap
    ? ValueTypeMap[V]
    : string
  : string

type DictAccessorMap<T extends Record<string, DictRegistryEntry<DataValueType>>> = {
  [K in keyof T]: DictAccessor<string, InferValue<T[K]>>
}

export function createDictAccessor<T extends Record<string, DictRegistryEntry<DataValueType>>>(
  registry: T,
): DictAccessorMap<T> {
  const accessors = new Map<string, DictAccessor<string, unknown>>()

  return new Proxy({} as DictAccessorMap<T>, {
    get(_target, prop: string) {
      if (accessors.has(prop)) return accessors.get(prop)

      const entry = registry[prop]
      if (!entry) return undefined

      const store = useDictStore()
      const loading = ref(false) as Ref<boolean>
      const items: ComputedRef<DictItem<string, unknown>[]> = computed(
        () => (store.getItems(entry.code) as DictItem<string, unknown>[]) ?? [],
      )

      // trigger lazy load
      loading.value = true
      store.load(entry.code).finally(() => {
        loading.value = false
      })

      const getLabel = (key: string): string => {
        const item = items.value.find((i) => i.itemKey === key)
        return item?.itemLabel ?? ''
      }

      const refresh = async () => {
        store.invalidate(entry.code)
        loading.value = true
        await store.load(entry.code)
        loading.value = false
      }

      const accessor: DictAccessor<string, unknown> = { items, loading, getLabel, refresh }
      accessors.set(prop, accessor)
      return accessor
    },
  })
}
```

- [ ] **Step 2: 验证类型**

Run: `pnpm type-check` (in `frontend/`)
Expected: 无错误

---

## Task 9: 前端注册表

**Files:**
- Create: `frontend/src/dict/index.ts`

- [ ] **Step 1: 创建注册表文件**

```typescript
import { createDictAccessor } from './accessor'

export const dict = createDictAccessor({
  status: { code: 'status', valueType: 'NUMBER' },
  dataValueType: { code: 'data_value_type', valueType: 'STRING' },
  builtIn: { code: 'built_in', valueType: 'NUMBER' },
  deleted: { code: 'deleted', valueType: 'NUMBER' },
})

export type { DictItem, DictAccessor, DataValueType } from './types'
```

- [ ] **Step 2: 验证类型**

Run: `pnpm type-check` (in `frontend/`)
Expected: 无错误

- [ ] **Step 3: Commit**

```bash
git add frontend/src/dict/ frontend/src/stores/dict.ts frontend/src/api/dict.ts
git commit -m "feat(dict): add frontend dict consumer layer with cache and type inference"
```

---

## Task 10: 前端单元测试 — convertValue

**Files:**
- Create: `frontend/src/__tests__/dict/convertValue.spec.ts`

- [ ] **Step 1: 编写 convertValue 测试**

```typescript
import { describe, expect, it } from 'vitest'
import { convertValue } from '@/stores/dict'

describe('convertValue', () => {
  it('STRING 保持原值', () => {
    expect(convertValue('hello', 'STRING')).toBe('hello')
  })

  it('NUMBER 转数字', () => {
    expect(convertValue('42', 'NUMBER')).toBe(42)
    expect(convertValue('3.14', 'NUMBER')).toBe(3.14)
  })

  it('BOOLEAN true', () => {
    expect(convertValue('true', 'BOOLEAN')).toBe(true)
    expect(convertValue('1', 'BOOLEAN')).toBe(true)
  })

  it('BOOLEAN false', () => {
    expect(convertValue('false', 'BOOLEAN')).toBe(false)
    expect(convertValue('0', 'BOOLEAN')).toBe(false)
  })

  it('OBJECT 解析 JSON', () => {
    expect(convertValue('{"a":1}', 'OBJECT')).toEqual({ a: 1 })
  })

  it('ARRAY 解析 JSON', () => {
    expect(convertValue('[1,2,3]', 'ARRAY')).toEqual([1, 2, 3])
  })

  it('OBJECT 解析失败降级为 string', () => {
    expect(convertValue('not json', 'OBJECT')).toBe('not json')
  })

  it('ARRAY 解析失败降级为 string', () => {
    expect(convertValue('not json', 'ARRAY')).toBe('not json')
  })
})
```

- [ ] **Step 2: 运行测试**

Run: `pnpm test:unit -- src/__tests__/dict/convertValue.spec.ts` (in `frontend/`)
Expected: 8 tests PASS

---

## Task 11: 前端单元测试 — useDictStore

**Files:**
- Create: `frontend/src/__tests__/dict/dictStore.spec.ts`

- [ ] **Step 1: 编写 store 测试**

```typescript
import { beforeEach, describe, expect, it, vi } from 'vitest'
import { createPinia, setActivePinia } from 'pinia'
import { useDictStore } from '@/stores/dict'

vi.mock('@/api/dict', () => ({
  fetchDictByCode: vi.fn(),
}))

import { fetchDictByCode } from '@/api/dict'

const mockFetch = vi.mocked(fetchDictByCode)

describe('useDictStore', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
    localStorage.clear()
    vi.clearAllMocks()
  })

  it('首次加载发起网络请求并缓存', async () => {
    mockFetch.mockResolvedValue({
      dictId: '1',
      dictName: '性别',
      dictCode: 'gender',
      dataValueType: 'STRING',
      items: [{ dictItemId: 'i1', dictId: '1', itemKey: 'M', itemLabel: '男', itemValue: 'M', sortOrder: 0, remark: '', status: 1, builtIn: 0, createdAt: '' }],
    })

    const store = useDictStore()
    const result = await store.load('gender')

    expect(mockFetch).toHaveBeenCalledWith('gender')
    expect(result).not.toBeNull()
    expect(result!.items[0].itemKey).toBe('M')
  })

  it('内存缓存命中不请求网络', async () => {
    mockFetch.mockResolvedValue({
      dictId: '1',
      dictName: '性别',
      dictCode: 'gender',
      dataValueType: 'STRING',
      items: [],
    })

    const store = useDictStore()
    await store.load('gender')
    mockFetch.mockClear()

    await store.load('gender')
    expect(mockFetch).not.toHaveBeenCalled()
  })

  it('invalidate 清除缓存后重新请求', async () => {
    mockFetch.mockResolvedValue({
      dictId: '1',
      dictName: '性别',
      dictCode: 'gender',
      dataValueType: 'STRING',
      items: [],
    })

    const store = useDictStore()
    await store.load('gender')
    store.invalidate('gender')
    mockFetch.mockClear()

    await store.load('gender')
    expect(mockFetch).toHaveBeenCalledWith('gender')
  })

  it('dictCode 不存在返回 null', async () => {
    mockFetch.mockResolvedValue(null)

    const store = useDictStore()
    const result = await store.load('not_exist')
    expect(result).toBeNull()
  })
})
```

- [ ] **Step 2: 运行测试**

Run: `pnpm test:unit -- src/__tests__/dict/dictStore.spec.ts` (in `frontend/`)
Expected: 4 tests PASS

---

## Task 12: 前端单元测试 — createDictAccessor

**Files:**
- Create: `frontend/src/__tests__/dict/accessor.spec.ts`

- [ ] **Step 1: 编写 accessor 测试**

```typescript
import { beforeEach, describe, expect, it, vi } from 'vitest'
import { createPinia, setActivePinia } from 'pinia'
import { nextTick } from 'vue'
import { createDictAccessor } from '@/dict/accessor'

vi.mock('@/api/dict', () => ({
  fetchDictByCode: vi.fn(),
}))

import { fetchDictByCode } from '@/api/dict'

const mockFetch = vi.mocked(fetchDictByCode)

describe('createDictAccessor', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
    localStorage.clear()
    vi.clearAllMocks()
  })

  it('属性访问返回 accessor 对象', () => {
    mockFetch.mockResolvedValue(null)
    const dict = createDictAccessor({ gender: { code: 'sys_gender', valueType: 'STRING' } })
    const accessor = dict.gender

    expect(accessor).toHaveProperty('items')
    expect(accessor).toHaveProperty('loading')
    expect(accessor).toHaveProperty('getLabel')
    expect(accessor).toHaveProperty('refresh')
  })

  it('getLabel 返回对应 itemLabel', async () => {
    mockFetch.mockResolvedValue({
      dictId: '1',
      dictName: '性别',
      dictCode: 'sys_gender',
      dataValueType: 'STRING',
      items: [
        { dictItemId: 'i1', dictId: '1', itemKey: 'M', itemLabel: '男', itemValue: 'M', sortOrder: 0, remark: '', status: 1, builtIn: 0, createdAt: '' },
        { dictItemId: 'i2', dictId: '1', itemKey: 'F', itemLabel: '女', itemValue: 'F', sortOrder: 1, remark: '', status: 1, builtIn: 0, createdAt: '' },
      ],
    })

    const dict = createDictAccessor({ gender: { code: 'sys_gender', valueType: 'STRING' } })
    dict.gender // trigger load
    await nextTick()
    await new Promise((r) => setTimeout(r, 10))

    expect(dict.gender.getLabel('M')).toBe('男')
    expect(dict.gender.getLabel('F')).toBe('女')
    expect(dict.gender.getLabel('X')).toBe('')
  })

  it('未注册的属性返回 undefined', () => {
    mockFetch.mockResolvedValue(null)
    const dict = createDictAccessor({ gender: { code: 'sys_gender', valueType: 'STRING' } })
    expect((dict as any).unknown).toBeUndefined()
  })
})
```

- [ ] **Step 2: 运行测试**

Run: `pnpm test:unit -- src/__tests__/dict/accessor.spec.ts` (in `frontend/`)
Expected: 3 tests PASS

- [ ] **Step 3: Commit**

```bash
git add frontend/src/__tests__/dict/
git commit -m "test(dict): add unit tests for convertValue, store, and accessor"
```

---

## Task 13: 集成联动 — DictView invalidate

**Files:**
- Modify: `frontend/src/views/system/DictView.vue`

- [ ] **Step 1: 在 DictView.vue 的保存/删除成功回调中调用 invalidate**

在字典保存成功和删除成功的逻辑之后，添加：

```typescript
import { useDictStore } from '@/stores/dict'

// 在 setup 中
const dictStore = useDictStore()

// 在保存成功后（已知 dictCode）
dictStore.invalidate(dictCode)

// 在删除成功后（已知 dictCode）
dictStore.invalidate(dictCode)
```

- [ ] **Step 2: 运行前端全量检查**

Run: `pnpm type-check` (in `frontend/`)
Run: `pnpm lint` (in `frontend/`)
Expected: 无错误

- [ ] **Step 3: 运行后端全量测试**

Run: `./mvnw test -pl backend`
Expected: BUILD SUCCESS

- [ ] **Step 4: Commit**

```bash
git add frontend/src/views/system/DictView.vue
git commit -m "feat(dict): invalidate dict cache on DictView save/delete"
```
