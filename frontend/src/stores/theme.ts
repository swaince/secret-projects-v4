import { ref, watch } from 'vue'
import { defineStore } from 'pinia'

export type ThemeMode = 'light' | 'dark'

function isValidTheme(v: string | null): v is ThemeMode {
  return v === 'light' || v === 'dark'
}

export const useThemeStore = defineStore('theme', () => {
  const stored = localStorage.getItem('app-theme')
  const theme = ref<ThemeMode>(isValidTheme(stored) ? stored : 'light')

  function toggle() {
    theme.value = theme.value === 'light' ? 'dark' : 'light'
  }

  watch(theme, (v) => {
    localStorage.setItem('app-theme', v)
    document.documentElement.classList.toggle('dark', v === 'dark')
  }, { immediate: true, flush: 'sync' })

  return { theme, toggle }
})
