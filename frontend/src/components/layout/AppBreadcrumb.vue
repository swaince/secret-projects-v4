<script setup lang="ts">
import { useBreadcrumb } from '@/composables/useBreadcrumb'
import {
  Breadcrumb, BreadcrumbList, BreadcrumbItem, BreadcrumbLink, BreadcrumbPage, BreadcrumbSeparator,
} from '@/components/ui/breadcrumb'

const { breadcrumbs } = useBreadcrumb()
</script>

<template>
  <Breadcrumb v-if="breadcrumbs.length" class="px-6 pt-4 pb-2">
    <BreadcrumbList>
      <template v-for="(crumb, i) in breadcrumbs" :key="i">
        <BreadcrumbItem>
          <BreadcrumbLink
            v-if="crumb.path"
            :href="crumb.path"
            class="text-sm font-medium transition-colors hover:text-foreground"
          >{{ crumb.title }}</BreadcrumbLink>
          <BreadcrumbPage v-else class="text-sm font-medium">{{ crumb.title }}</BreadcrumbPage>
        </BreadcrumbItem>
        <BreadcrumbSeparator v-if="i < breadcrumbs.length - 1" />
      </template>
    </BreadcrumbList>
  </Breadcrumb>
</template>
