import http from '@/utils/request'

export interface MenuDTO {
  menuId: string
  parentId: string | null
  menuName: string
  menuType: string
  path: string | null
  component: string | null
  icon: string | null
  sortOrder: number
  status: number
  permission: string | null
  visible: number
  redirect: string | null
  requireAuth: number
  createdAt: string
  children?: MenuDTO[]
}

export function fetchMenuTree(): Promise<MenuDTO[]> {
  return http.get<MenuDTO[]>('/menus/tree', undefined, { silent: true })
}

export function fetchMenuList(): Promise<MenuDTO[]> {
  return http.get<MenuDTO[]>('/menus')
}

export function createMenu(data: Partial<MenuDTO>): Promise<MenuDTO> {
  return http.post<MenuDTO>('/menus', data)
}

export function updateMenu(menuId: string, data: Partial<MenuDTO>): Promise<MenuDTO> {
  return http.put<MenuDTO>(`/menus/${menuId}`, data)
}

export function deleteMenu(menuId: string): Promise<string> {
  return http.delete<string>(`/menus/${menuId}`)
}
