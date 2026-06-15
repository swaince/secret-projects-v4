import { computed, ref } from 'vue'
import { defineStore } from 'pinia'

export const useLoadingStore = defineStore('loading', () => {
  const count = ref(0)
  const isLoading = computed(() => count.value > 0)

  function start() {
    count.value++
  }

  function end() {
    count.value = Math.max(0, count.value - 1)
  }

  function reset() {
    count.value = 0
  }

  return { count, isLoading, start, end, reset }
})
