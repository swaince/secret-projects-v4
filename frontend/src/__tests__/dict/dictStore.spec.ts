import { beforeEach, describe, expect, it, vi } from 'vitest'
import { createPinia, setActivePinia } from 'pinia'
import { useDictStore } from '@/stores/dict'

vi.mock('@/api/dict', () => ({
  fetchDictByCode: vi.fn<(code: string) => Promise<null>>(),
}))

import { fetchDictByCode } from '@/api/dict'

const mockFetch = vi.mocked(fetchDictByCode)

describe('useDictStore', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
    localStorage.clear()
    vi.clearAllMocks()
  })

  it('首次加载发起网络请求并缓存', async () => {
    mockFetch.mockResolvedValue({
      dictId: '1',
      dictName: '性别',
      dictCode: 'gender',
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
      ],
    })

    const store = useDictStore()
    await store.load('gender')

    expect(mockFetch).toHaveBeenCalledWith('gender')
    const items = store.getItems('gender')
    expect(items).not.toBeUndefined()
    expect(items![0].itemKey).toBe('M')
  })

  it('内存缓存命中不请求网络', async () => {
    mockFetch.mockResolvedValue({
      dictId: '1',
      dictName: '性别',
      dictCode: 'gender',
      dataValueType: 'STRING',
      items: [],
    })

    const store = useDictStore()
    await store.load('gender')
    mockFetch.mockClear()

    await store.load('gender')
    expect(mockFetch).not.toHaveBeenCalled()
  })

  it('invalidate 清除缓存后重新请求', async () => {
    mockFetch.mockResolvedValue({
      dictId: '1',
      dictName: '性别',
      dictCode: 'gender',
      dataValueType: 'STRING',
      items: [],
    })

    const store = useDictStore()
    await store.load('gender')
    store.invalidate('gender')
    mockFetch.mockClear()

    await store.load('gender')
    expect(mockFetch).toHaveBeenCalledWith('gender')
  })

  it('dictCode 不存在返回 void 且无缓存', async () => {
    mockFetch.mockResolvedValue(null)

    const store = useDictStore()
    await store.load('not_exist')
    expect(store.getItems('not_exist')).toBeUndefined()
  })
})
