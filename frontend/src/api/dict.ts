export interface DictDTO {
  dictId: string
  dictName: string
  dictCode: string
  dataValueType: string
  remark: string
  status: number
  builtIn: number
  createdAt: string
}

export interface DictItemDTO {
  dictItemId: string
  dictId: string
  itemKey: string
  itemValue: string
  sortOrder: number
  remark: string
  status: number
  builtIn: number
  createdAt: string
}

export interface PageResponse<T> {
  records: T[]
  total: number
  page: number
  size: number
}

export interface R<T> {
  code: number
  message: string
  data: T
}

export interface DictPageParams {
  page: number
  size: number
  dictName?: string
  dataValueType?: string
}

const BASE = '/api'

async function request<T>(url: string, options?: RequestInit): Promise<T> {
  const res = await fetch(`${BASE}${url}`, {
    headers: { 'Content-Type': 'application/json' },
    ...options,
  })
  const json: R<T> = await res.json()
  if (json.code !== 200) throw new Error(json.message)
  return json.data
}

export function fetchDicts(params: DictPageParams): Promise<PageResponse<DictDTO>> {
  const query = new URLSearchParams({
    page: String(params.page),
    size: String(params.size),
    ...(params.dictName ? { dictName: params.dictName } : {}),
    ...(params.dataValueType ? { dataValueType: params.dataValueType } : {}),
  })
  return request<PageResponse<DictDTO>>(`/dicts?${query}`)
}

export function createDict(data: Partial<DictDTO>): Promise<DictDTO> {
  return request<DictDTO>('/dicts', { method: 'POST', body: JSON.stringify(data) })
}

export function updateDict(dictId: string, data: Partial<DictDTO>): Promise<DictDTO> {
  return request<DictDTO>(`/dicts/${dictId}`, { method: 'PUT', body: JSON.stringify(data) })
}

export function deleteDict(dictId: string): Promise<string> {
  return request<string>(`/dicts/${dictId}`, { method: 'DELETE' })
}

export function deleteDicts(ids: string[]): Promise<string[]> {
  return request<string[]>('/dicts', { method: 'DELETE', body: JSON.stringify(ids) })
}

export function fetchDictItems(dictId: string): Promise<DictItemDTO[]> {
  return request<DictItemDTO[]>(`/dicts/${dictId}/items`)
}

export function createDictItem(dictId: string, data: Partial<DictItemDTO>): Promise<DictItemDTO> {
  return request<DictItemDTO>(`/dicts/${dictId}/items`, {
    method: 'POST',
    body: JSON.stringify(data),
  })
}

export function updateDictItem(
  dictId: string,
  itemId: string,
  data: Partial<DictItemDTO>,
): Promise<DictItemDTO> {
  return request<DictItemDTO>(`/dicts/${dictId}/items/${itemId}`, {
    method: 'PUT',
    body: JSON.stringify(data),
  })
}

export function deleteDictItem(dictId: string, itemId: string): Promise<string> {
  return request<string>(`/dicts/${dictId}/items/${itemId}`, { method: 'DELETE' })
}

export function deleteDictItems(dictId: string, ids: string[]): Promise<string[]> {
  return request<string[]>(`/dicts/${dictId}/items`, {
    method: 'DELETE',
    body: JSON.stringify(ids),
  })
}
