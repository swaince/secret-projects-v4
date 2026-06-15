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
