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

import type { PageResponse, R } from '@/api/dict'

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

export function fetchRoles(params: RolePageParams): Promise<PageResponse<RoleDTO>> {
  const query = new URLSearchParams({
    page: String(params.page),
    size: String(params.size),
    ...(params.roleName ? { roleName: params.roleName } : {}),
    ...(params.roleCode ? { roleCode: params.roleCode } : {}),
  })
  return request<PageResponse<RoleDTO>>(`/roles?${query}`)
}

export function createRole(data: Partial<RoleDTO>): Promise<RoleDTO> {
  return request<RoleDTO>('/roles', { method: 'POST', body: JSON.stringify(data) })
}

export function updateRole(roleId: string, data: Partial<RoleDTO>): Promise<RoleDTO> {
  return request<RoleDTO>(`/roles/${roleId}`, { method: 'PUT', body: JSON.stringify(data) })
}

export function deleteRole(roleId: string): Promise<string> {
  return request<string>(`/roles/${roleId}`, { method: 'DELETE' })
}

export function deleteRoles(ids: string[]): Promise<string[]> {
  return request<string[]>('/roles', { method: 'DELETE', body: JSON.stringify(ids) })
}
