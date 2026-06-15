import http from '@/utils/request'

export interface UserDTO {
  userId: string
  username: string
  displayName: string | null
  gender: string
  status: number
  accountExpireTime: string | null
  passwordExpireTime: string | null
  lastLoginTime: string | null
  createdAt: string
}

export interface UserPageParams {
  page: number
  size: number
  username?: string
  displayName?: string
  gender?: string
  status?: number
}

export function fetchUsers(params: UserPageParams): Promise<PageResponse<UserDTO>> {
  return http.get<PageResponse<UserDTO>>('/users', params)
}

export function createUser(data: Partial<UserDTO> & { password: string }): Promise<UserDTO> {
  return http.post<UserDTO>('/users', data)
}

export function updateUser(userId: string, data: Partial<UserDTO> & { password?: string }): Promise<UserDTO> {
  return http.put<UserDTO>(`/users/${userId}`, data)
}

export function deleteUser(userId: string): Promise<string> {
  return http.delete<string>(`/users/${userId}`)
}

export function deleteUsers(ids: string[]): Promise<string[]> {
  return http.delete<string[]>('/users', ids)
}
