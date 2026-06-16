import http from '@/utils/request'

export function fetchAuthMenuIds(subjectType: string, subjectId: string): Promise<string[]> {
  return http.get<string[]>(`/authorizations/${subjectType}/${subjectId}`)
}

export function saveAuthMenu(subjectType: string, subjectIds: string[], menuIds: string[]): Promise<number> {
  return http.post<number>(`/authorizations/${subjectType}`, { subjectIds, menuIds })
}

export function fetchUserRelations(relationType: string, userId: string): Promise<string[]> {
  return http.get<string[]>(`/relations/${relationType}/${userId}`)
}

export function saveUserRelations(relationType: string, userIds: string[], targetIds: string[]): Promise<number> {
  return http.post<number>(`/relations/${relationType}`, { userIds, targetIds })
}

export function fetchAllUserMenuIds(userId: string): Promise<string[]> {
  return http.get<string[]>(`/authorizations/user/${userId}/all`)
}
