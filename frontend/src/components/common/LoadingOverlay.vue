<script setup lang="ts">
import { storeToRefs } from 'pinia'
import { useLoadingStore } from '@/stores/loading'

const { isLoading } = storeToRefs(useLoadingStore())
</script>

<template>
  <div
    v-if="isLoading"
    class="fixed inset-0 z-[9999] flex items-center justify-center bg-background/60"
  >
    <svg
      class="size-8 animate-spin text-primary"
      xmlns="http://www.w3.org/2000/svg"
      fill="none"
      viewBox="0 0 24 24"
    >
      <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4" />
      <path
        class="opacity-75"
        fill="currentColor"
        d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4z"
      />
    </svg>
  </div>
</template>
