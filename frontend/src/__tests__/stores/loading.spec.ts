import { beforeEach, describe, expect, it } from 'vitest'
import { createPinia, setActivePinia } from 'pinia'
import { useLoadingStore } from '@/stores/loading'

describe('useLoadingStore', () => {
  beforeEach(() => setActivePinia(createPinia()))

  it('初始状态 isLoading 为 false', () => {
    const store = useLoadingStore()
    expect(store.isLoading).toBe(false)
  })

  it('start 后 isLoading 为 true', () => {
    const store = useLoadingStore()
    store.start()
    expect(store.isLoading).toBe(true)
  })

  it('start + end 后 isLoading 为 false', () => {
    const store = useLoadingStore()
    store.start()
    store.end()
    expect(store.isLoading).toBe(false)
  })

  it('并发：3 次 start + 2 次 end 仍为 loading', () => {
    const store = useLoadingStore()
    store.start()
    store.start()
    store.start()
    store.end()
    store.end()
    expect(store.isLoading).toBe(true)
  })

  it('end 不低于零', () => {
    const store = useLoadingStore()
    store.end()
    store.end()
    expect(store.count).toBe(0)
  })

  it('reset 强制归零', () => {
    const store = useLoadingStore()
    store.start()
    store.start()
    store.reset()
    expect(store.isLoading).toBe(false)
  })
})
