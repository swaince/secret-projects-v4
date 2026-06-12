import http from '@/utils/request'

export interface RoleDTO {
  roleId: string
  roleName: string
  roleCode: string
  sortOrder: number
  remark: string
  status: number
  builtIn: number
  createdAt: string
}

export interface RolePageParams {
  page: number
  size: number
  roleName?: string
  roleCode?: string
}

export function fetchRoles(params: RolePageParams): Promise<PageResponse<RoleDTO>> {
  return http.get<PageResponse<RoleDTO>>('/roles', params)
}

export function createRole(data: Partial<RoleDTO>): Promise<RoleDTO> {
  return http.post<RoleDTO>('/roles', data)
}

export function updateRole(roleId: string, data: Partial<RoleDTO>): Promise<RoleDTO> {
  return http.put<RoleDTO>(`/roles/${roleId}`, data)
}

export function deleteRole(roleId: string): Promise<string> {
  return http.delete<string>(`/roles/${roleId}`)
}

export function deleteRoles(ids: string[]): Promise<string[]> {
  return http.delete<string[]>('/roles', ids)
}
