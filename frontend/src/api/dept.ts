import type { R } from '@/api/dict'

export interface DeptDTO {
  deptId: string
  deptName: string
  deptCode: string
  parentId: string | null
  sortOrder: number
  remark: string
  status: number
  builtIn: number
  createdAt: string
  children: DeptDTO[]
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

export function fetchDeptTree(): Promise<DeptDTO[]> {
  return request<DeptDTO[]>('/depts')
}

export function createDept(data: Partial<DeptDTO>): Promise<DeptDTO> {
  return request<DeptDTO>('/depts', { method: 'POST', body: JSON.stringify(data) })
}

export function updateDept(deptId: string, data: Partial<DeptDTO>): Promise<DeptDTO> {
  return request<DeptDTO>(`/depts/${deptId}`, { method: 'PUT', body: JSON.stringify(data) })
}

export function deleteDept(deptId: string): Promise<string> {
  return request<string>(`/depts/${deptId}`, { method: 'DELETE' })
}

export function deleteDepts(ids: string[]): Promise<string[]> {
  return request<string[]>('/depts', { method: 'DELETE', body: JSON.stringify(ids) })
}
