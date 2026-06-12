<script setup lang="ts">
import 'vue-sonner/style.css'
import { Toaster } from '@/components/ui/sonner'
import { useThemeStore } from '@/stores/theme'

const themeStore = useThemeStore()
</script>

<template>
  <RouterView />
  <Toaster position="top-center" rich-colors :theme="themeStore.theme" />
</template>
