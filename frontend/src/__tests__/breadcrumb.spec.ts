import { describe, it, expect } from 'vitest'
import type { MenuItem } from '@/stores/menu'

interface BreadcrumbItem { title: string; path?: string }

function getBreadcrumbs(routePath: string, cfg: MenuItem[]): BreadcrumbItem[] {
  function getAncestors(path: string): MenuItem[] {
    function search(items: MenuItem[], ancestors: MenuItem[]): MenuItem[] | null {
      for (const item of items) {
        const cur = [...ancestors, item]
        if (item.path === path) return cur
        if (item.children) {
          const r = search(item.children, cur)
          if (r) return r
        }
      }
      return null
    }
    return search(cfg, []) || []
  }
  const ancestors = getAncestors(routePath)
  if (!ancestors.length) return []
  return ancestors.map((item, i) => ({ title: item.title, path: i < ancestors.length - 1 ? item.path : undefined }))
}

const cfg: MenuItem[] = [
  { id: 'dash', title: '仪表盘', path: '/dash' },
  { id: 'sys', title: '系统管理', children: [
    { id: 'user', title: '用户管理', children: [{ id: 'list', title: '用户列表', path: '/sys/users' }] },
    { id: 'dept', title: '部门管理', path: '/sys/depts' },
  ]},
]

describe('useBreadcrumb helpers', () => {
  it('single item for root path', () => {
    const c = getBreadcrumbs('/dash', cfg)
    expect(c).toHaveLength(1)
    expect(c[0]!.title).toBe('仪表盘')
    expect(c[0]!.path).toBeUndefined()
  })
  it('full chain for 3-level path', () => {
    const c = getBreadcrumbs('/sys/users', cfg)
    expect(c).toHaveLength(3)
    expect(c[0]!.title).toBe('系统管理')
    expect(c[1]!.title).toBe('用户管理')
    expect(c[2]!.title).toBe('用户列表')
  })
  it('chain for 2-level path', () => {
    const c = getBreadcrumbs('/sys/depts', cfg)
    expect(c).toHaveLength(2)
    expect(c[0]!.title).toBe('系统管理')
    expect(c[1]!.title).toBe('部门管理')
  })
  it('empty for unmatched', () => { expect(getBreadcrumbs('/x', cfg)).toEqual([]) })
  it('last item has no path', () => {
    expect(getBreadcrumbs('/sys/depts', cfg).pop()!.path).toBeUndefined()
  })
})
