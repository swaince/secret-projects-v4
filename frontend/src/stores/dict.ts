import { reactive } from 'vue'
import { defineStore } from 'pinia'
import { fetchDictByCode } from '@/api/dict'
import type { DataValueType, DictItem } from '@/dict/types'

interface CacheEntry {
  dictId: string
  dictName: string
  dictCode: string
  dataValueType: DataValueType
  items: DictItem<string, unknown>[]
  timestamp: number
}

const STORAGE_KEY = 'app_dict_cache'
const TTL = 30 * 60 * 1000

export function convertValue(raw: string, type: DataValueType): unknown {
  switch (type) {
    case 'STRING':
      return raw
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
  }
}

function readStorage(): Record<string, CacheEntry> {
  try {
    const raw = localStorage.getItem(STORAGE_KEY)
    if (!raw) return {}
    return JSON.parse(raw)
  } catch {
    return {}
  }
}

function writeStorage(cache: Map<string, CacheEntry>) {
  const obj: Record<string, CacheEntry> = {}
  cache.forEach((v, k) => {
    obj[k] = v
  })
  localStorage.setItem(STORAGE_KEY, JSON.stringify(obj))
}

export const useDictStore = defineStore('dict', () => {
  const cache: Map<string, CacheEntry> = reactive(new Map())

  function isExpired(entry: CacheEntry): boolean {
    return Date.now() - entry.timestamp > TTL
  }

  async function load(dictCode: string): Promise<void> {
    const memEntry = cache.get(dictCode)
    if (memEntry && !isExpired(memEntry)) return

    const stored = readStorage()
    const storageEntry = stored[dictCode]
    if (storageEntry && !isExpired(storageEntry)) {
      cache.set(dictCode, storageEntry)
      return
    }

    const dto = await fetchDictByCode(dictCode)
    if (!dto) return

    const type = dto.dataValueType as DataValueType
    const items: DictItem<string, unknown>[] = dto.items.map((item) => ({
      dictItemId: item.dictItemId,
      itemKey: item.itemKey,
      itemLabel: item.itemLabel,
      itemValue: convertValue(item.itemValue, type),
      sortOrder: item.sortOrder,
    }))

    const entry: CacheEntry = {
      dictId: dto.dictId,
      dictName: dto.dictName,
      dictCode: dto.dictCode,
      dataValueType: type,
      items,
      timestamp: Date.now(),
    }

    cache.set(dictCode, entry)
    writeStorage(cache)
  }

  function invalidate(dictCode: string) {
    cache.delete(dictCode)
    const stored = readStorage()
    delete stored[dictCode]
    localStorage.setItem(STORAGE_KEY, JSON.stringify(stored))
  }

  function invalidateAll() {
    cache.clear()
    localStorage.removeItem(STORAGE_KEY)
  }

  function getItems(dictCode: string): DictItem<string, unknown>[] | undefined {
    return cache.get(dictCode)?.items
  }

  return { load, invalidate, invalidateAll, getItems }
})
