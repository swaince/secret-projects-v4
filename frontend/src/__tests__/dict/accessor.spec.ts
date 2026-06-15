import { beforeEach, describe, expect, it, vi } from 'vitest'
import { createPinia, setActivePinia } from 'pinia'
import { nextTick } from 'vue'
import { createDictAccessor } from '@/dict/accessor'

vi.mock('@/api/dict', () => ({
  fetchDictByCode: vi.fn<(code: string) => Promise<null>>(),
}))

import { fetchDictByCode } from '@/api/dict'

const mockFetch = vi.mocked(fetchDictByCode)

describe('createDictAccessor', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
    localStorage.clear()
    vi.clearAllMocks()
  })

  it('属性访问返回 accessor 对象', () => {
    mockFetch.mockResolvedValue(null)
    const dict = createDictAccessor({ gender: { code: 'sys_gender', valueType: 'STRING' } })
    const accessor = dict.gender

    expect(accessor).toHaveProperty('items')
    expect(accessor).toHaveProperty('loading')
    expect(accessor).toHaveProperty('getLabel')
    expect(accessor).toHaveProperty('refresh')
  })

  it('getLabel 返回对应 itemLabel', async () => {
    mockFetch.mockResolvedValue({
      dictId: '1',
      dictName: '性别',
      dictCode: 'sys_gender',
      dataValueType: 'STRING',
      items: [
        {
          dictItemId: 'i1',
          dictId: '1',
          itemKey: 'M',
          itemLabel: '男',
          itemValue: 'M',
          sortOrder: 0,
          remark: '',
          status: 1,
          builtIn: 0,
          createdAt: '',
        },
        {
          dictItemId: 'i2',
          dictId: '1',
          itemKey: 'F',
          itemLabel: '女',
          itemValue: 'F',
          sortOrder: 1,
          remark: '',
          status: 1,
          builtIn: 0,
          createdAt: '',
        },
      ],
    })

    const dict = createDictAccessor({ gender: { code: 'sys_gender', valueType: 'STRING' } })
    void dict.gender.loading
    await nextTick()
    await new Promise((r) => setTimeout(r, 50))

    expect(dict.gender.getLabel('M')).toBe('男')
    expect(dict.gender.getLabel('F')).toBe('女')
    expect(dict.gender.getLabel('X')).toBe('')
  })

  it('未注册的属性返回 undefined', () => {
    mockFetch.mockResolvedValue(null)
    const dict = createDictAccessor({ gender: { code: 'sys_gender', valueType: 'STRING' } })
    expect((dict as Record<string, unknown>).unknown).toBeUndefined()
  })
})
