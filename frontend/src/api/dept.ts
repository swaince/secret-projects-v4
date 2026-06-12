import http from '@/utils/request'

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

export function fetchDeptTree(): Promise<DeptDTO[]> {
  return http.get<DeptDTO[]>('/depts')
}

export function createDept(data: Partial<DeptDTO>): Promise<DeptDTO> {
  return http.post<DeptDTO>('/depts', data)
}

export function updateDept(deptId: string, data: Partial<DeptDTO>): Promise<DeptDTO> {
  return http.put<DeptDTO>(`/depts/${deptId}`, data)
}

export function deleteDept(deptId: string): Promise<string> {
  return http.delete<string>(`/depts/${deptId}`)
}

export function deleteDepts(ids: string[]): Promise<string[]> {
  return http.delete<string[]>('/depts', ids)
}
