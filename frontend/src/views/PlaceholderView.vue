<script setup lang="ts">
import { useRoute } from 'vue-router'

const route = useRoute()
</script>

<template>
  <div class="space-y-4">
    <h1 class="text-2xl font-bold tracking-tight">{{ (route.meta.title as string) || route.path }}</h1>
    <p class="text-muted-foreground">页面开发中…</p>
  </div>
</template>
