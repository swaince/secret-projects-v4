import http from '@/utils/request'

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

export interface DictPageParams {
  page: number
  size: number
  dictName?: string
  dataValueType?: string
}

export function fetchDicts(params: DictPageParams): Promise<PageResponse<DictDTO>> {
  return http.get<PageResponse<DictDTO>>('/dicts', params)
}

export function createDict(data: Partial<DictDTO>): Promise<DictDTO> {
  return http.post<DictDTO>('/dicts', data)
}

export function updateDict(dictId: string, data: Partial<DictDTO>): Promise<DictDTO> {
  return http.put<DictDTO>(`/dicts/${dictId}`, data)
}

export function deleteDict(dictId: string): Promise<string> {
  return http.delete<string>(`/dicts/${dictId}`)
}

export function deleteDicts(ids: string[]): Promise<string[]> {
  return http.delete<string[]>('/dicts', ids)
}

export function fetchDictItems(dictId: string): Promise<DictItemDTO[]> {
  return http.get<DictItemDTO[]>(`/dicts/${dictId}/items`)
}

export function createDictItem(dictId: string, data: Partial<DictItemDTO>): Promise<DictItemDTO> {
  return http.post<DictItemDTO>(`/dicts/${dictId}/items`, data)
}

export function updateDictItem(
  dictId: string,
  itemId: string,
  data: Partial<DictItemDTO>,
): Promise<DictItemDTO> {
  return http.put<DictItemDTO>(`/dicts/${dictId}/items/${itemId}`, data)
}

export function deleteDictItem(dictId: string, itemId: string): Promise<string> {
  return http.delete<string>(`/dicts/${dictId}/items/${itemId}`)
}

export function deleteDictItems(dictId: string, ids: string[]): Promise<string[]> {
  return http.delete<string[]>(`/dicts/${dictId}/items`, ids)
}
