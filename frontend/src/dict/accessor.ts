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
