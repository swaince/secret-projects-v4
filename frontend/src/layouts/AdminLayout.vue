<script setup lang="ts">
import { useMenuNavigation } from '@/composables/useMenuNavigation'
import AppHeader from '@/components/layout/AppHeader.vue'
import AppSidebar from '@/components/layout/AppSidebar.vue'
import AppBreadcrumb from '@/components/layout/AppBreadcrumb.vue'
import { SidebarProvider, SidebarTrigger } from '@/components/ui/sidebar'

const { activePrimaryId, sidebarMenu, menuConfig, navigate } = useMenuNavigation()
</script>

<template>
  <div class="flex h-dvh flex-col">
    <AppHeader :menu-items="menuConfig" :active-primary-id="activePrimaryId" @navigate="navigate" />
    <div class="flex min-h-0 flex-1">
      <SidebarProvider :default-open="true" class="min-h-0">
        <AppSidebar v-if="sidebarMenu.length" :menu-items="sidebarMenu" />
        <main class="min-w-0 flex-1 overflow-auto bg-muted/30 dark:bg-background">
          <div class="flex items-center gap-2 px-6 pt-4 pb-2">
            <SidebarTrigger />
            <AppBreadcrumb class="!px-0 !pt-0 !pb-0" />
          </div>
          <div class="p-6"><RouterView /></div>
        </main>
      </SidebarProvider>
    </div>
  </div>
</template>
