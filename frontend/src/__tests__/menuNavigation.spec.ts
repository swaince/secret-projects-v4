import { describe, it, expect } from 'vitest'
import type { MenuItem } from '@/config/menu'

function findActivePrimaryId(path: string, menuConfig: MenuItem[]): string {
  function findDescendant(item: MenuItem, p: string): boolean {
    if (item.path === p) return true
    return item.children?.some(c => findDescendant(c, p)) ?? false
  }
  for (const item of menuConfig) {
    if (item.path === path || findDescendant(item, path)) return item.id
  }
  return ''
}

function getSidebarMenu(activeId: string, menuConfig: MenuItem[]): MenuItem[] {
  return menuConfig.find(i => i.id === activeId)?.children || []
}

const cfg: MenuItem[] = [
  { id: 'dash', title: '仪表盘', path: '/dash' },
  { id: 'sys', title: '系统', children: [
    { id: 'user', title: '用户', children: [{ id: 'list', title: '列表', path: '/sys/users' }] },
    { id: 'dept', title: '部门', path: '/sys/depts' },
  ]},
]

describe('useMenuNavigation helpers', () => {
  it('returns empty for unmatched path', () => { expect(findActivePrimaryId('/x', cfg)).toBe('') })
  it('finds primary for top-level', () => { expect(findActivePrimaryId('/dash', cfg)).toBe('dash') })
  it('finds primary for nested', () => {
    expect(findActivePrimaryId('/sys/users', cfg)).toBe('sys')
    expect(findActivePrimaryId('/sys/depts', cfg)).toBe('sys')
  })
  it('returns sidebar for active primary', () => {
    expect(getSidebarMenu('sys', cfg)).toHaveLength(2)
    expect(getSidebarMenu('sys', cfg)[0]!.id).toBe('user')
  })
  it('empty sidebar for leaf primary', () => { expect(getSidebarMenu('dash', cfg)).toEqual([]) })
})
