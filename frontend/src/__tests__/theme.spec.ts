import { describe, it, expect, beforeEach } from 'vitest'
import { setActivePinia, createPinia } from 'pinia'
import { useThemeStore } from '@/stores/theme'

describe('useThemeStore', () => {
  beforeEach(() => {
    localStorage.clear()
    document.documentElement.classList.remove('dark')
  })

  function createStore() {
    setActivePinia(createPinia())
    return useThemeStore()
  }

  it('defaults to light when localStorage is empty', () => {
    expect(createStore().theme).toBe('light')
  })

  it('reads theme from localStorage on init', () => {
    localStorage.setItem('app-theme', 'dark')
    expect(createStore().theme).toBe('dark')
  })

  it('toggle switches light to dark', () => {
    const s = createStore()
    s.toggle()
    expect(s.theme).toBe('dark')
    expect(localStorage.getItem('app-theme')).toBe('dark')
  })

  it('toggle switches dark to light', () => {
    localStorage.setItem('app-theme', 'dark')
    const s = createStore()
    s.toggle()
    expect(s.theme).toBe('light')
    expect(localStorage.getItem('app-theme')).toBe('light')
  })

  it('falls back to light for invalid value', () => {
    localStorage.setItem('app-theme', 'invalid')
    expect(createStore().theme).toBe('light')
  })
})
