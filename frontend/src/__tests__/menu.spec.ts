import { describe, it, expect } from 'vitest'
import type { MenuItem } from '@/config/menu'

function createRoutesFromMenu(items: MenuItem[]) {
  const routes: { path: string; name: string; meta: Record<string, unknown> }[] = []
  for (const item of items) {
    if (item.path) {
      routes.push({ path: item.path, name: item.id, meta: { title: item.title, ...item.meta } })
    }
    if (item.children) {
      routes.push(...createRoutesFromMenu(item.children))
    }
  }
  return routes
}

describe('createRoutesFromMenu', () => {
  it('returns empty array for empty input', () => {
    expect(createRoutesFromMenu([])).toEqual([])
  })

  it('generates route for single leaf item', () => {
    const items: MenuItem[] = [{ id: 'home', title: '首页', path: '/home' }]
    const routes = createRoutesFromMenu(items)
    expect(routes).toHaveLength(1)
    expect(routes[0]!.path).toBe('/home')
    expect(routes[0]!.name).toBe('home')
    expect(routes[0]!.meta.title).toBe('首页')
  })

  it('recursively generates routes for nested items', () => {
    const items: MenuItem[] = [{ id: 'sys', title: '系统', children: [{ id: 'u', title: '用户', path: '/users' }] }]
    const routes = createRoutesFromMenu(items)
    expect(routes).toHaveLength(1)
    expect(routes[0]!.path).toBe('/users')
  })

  it('skips container nodes without path', () => {
    const items: MenuItem[] = [{ id: 'sys', title: '系统', children: [{ id: 'u', title: '用户', path: '/users' }] }]
    const routes = createRoutesFromMenu(items)
    expect(routes).toHaveLength(1)
  })

  it('handles mixed 2 and 3-level hierarchy', () => {
    const items: MenuItem[] = [
      { id: 'dash', title: '仪表盘', path: '/dash' },
      { id: 'sys', title: '系统', children: [
        { id: 'dept', title: '部门', path: '/sys/depts' },
        { id: 'user', title: '用户', children: [{ id: 'list', title: '列表', path: '/sys/users' }] },
      ]},
    ]
    const routes = createRoutesFromMenu(items)
    expect(routes.map(r => r.path)).toEqual(['/dash', '/sys/depts', '/sys/users'])
  })

  it('inherits meta properties', () => {
    const items: MenuItem[] = [{ id: 'pg', title: '页', path: '/pg', meta: { keepAlive: true, breadcrumb: '自定义' } }]
    const routes = createRoutesFromMenu(items)
    expect(routes[0]!.meta.keepAlive).toBe(true)
    expect(routes[0]!.meta.breadcrumb).toBe('自定义')
  })
})
