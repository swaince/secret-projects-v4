<script setup lang="ts">
import { ref } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ChevronDown } from '@lucide/vue'
import {
  LayoutDashboard, FileText, FolderTree, MessageSquareText, Settings,
  Users, UserRound, UserCog, Shield, ShieldCheck, LockKeyhole, Building2, Building,
  BriefcaseBusiness, UserPlus, Menu, SlidersHorizontal, Activity, UserCheck,
  Server, Database, ScrollText, LogIn, ClipboardList, BookType,
} from '@lucide/vue'
import {
  Sidebar, SidebarContent, SidebarGroup, SidebarGroupContent,
  SidebarMenu, SidebarMenuItem, SidebarMenuButton,
  SidebarMenuSub, SidebarMenuSubItem, SidebarMenuSubButton,
  useSidebar,
} from '@/components/ui/sidebar'
import { Tooltip, TooltipContent, TooltipTrigger } from '@/components/ui/tooltip'
import type { MenuItem } from '@/stores/menu'

defineProps<{ menuItems: MenuItem[] }>()
const router = useRouter()
const route = useRoute()
const { toggleSidebar, state } = useSidebar()
const expandedId = ref<string | null>(null)

function toggleExpand(id: string) { expandedId.value = expandedId.value === id ? null : id }
function isExpanded(id: string) { return expandedId.value === id }
function isActive(p: string) { return route.path === p }

const iconMap: Record<string, unknown> = {
  LayoutDashboard, FileText, FolderTree, MessageSquareText, Settings,
  Users, UserRound, UserCog, Shield, ShieldCheck, LockKeyhole, Building2, Building,
  BriefcaseBusiness, UserPlus, Menu, SlidersHorizontal, Activity, UserCheck,
  Server, Database, ScrollText, LogIn, ClipboardList, BookType,
}

function resolveIcon(name: string) {
  return iconMap[name]
}

function handleClick(item: MenuItem) {
  if (item.children?.length) toggleExpand(item.id)
  else if (item.path) router.push(item.path)
}
</script>

<template>
  <Sidebar collapsible="icon" class="top-14 h-auto border-r">
    <SidebarContent>
      <SidebarGroup>
        <SidebarGroupContent>
          <SidebarMenu>
            <template v-for="item in menuItems" :key="item.id">
              <SidebarMenuItem>
                <!-- Level 2: parent/leaf item -->
                <Tooltip v-if="(state as string) === 'collapsed'" :delay-duration="0">
                  <TooltipTrigger as-child>
                    <SidebarMenuButton :is-active="isActive(item.path || '')" @click="handleClick(item)">
                      <component v-if="item.icon" :is="resolveIcon(item.icon)" class="h-4 w-4 shrink-0" aria-hidden="true" />
                      <span v-else class="h-4 w-4 shrink-0" />
                      <span>{{ item.title }}</span>
                      <ChevronDown v-if="item.children?.length" class="ml-auto h-3.5 w-3.5 shrink-0 transition-transform duration-200" :class="{ 'rotate-180': isExpanded(item.id) }" aria-hidden="true" />
                      <span v-else class="h-3.5 w-3.5 shrink-0" />
                    </SidebarMenuButton>
                  </TooltipTrigger>
                  <TooltipContent side="right" :side-offset="8">{{ item.title }}</TooltipContent>
                </Tooltip>

                <SidebarMenuButton v-else :is-active="isActive(item.path || '')" @click="handleClick(item)">
                  <component v-if="item.icon" :is="resolveIcon(item.icon)" class="h-4 w-4 shrink-0" aria-hidden="true" />
                  <span v-else class="h-4 w-4 shrink-0" />
                  <span>{{ item.title }}</span>
                  <ChevronDown v-if="item.children?.length" class="ml-auto h-3.5 w-3.5 shrink-0 transition-transform duration-200" :class="{ 'rotate-180': isExpanded(item.id) }" aria-hidden="true" />
                  <span v-else class="h-3.5 w-3.5 shrink-0" />
                </SidebarMenuButton>

                <!-- Level 3: children, as SidebarMenuSub sibling -->
                <SidebarMenuSub v-if="item.children && isExpanded(item.id)">
                  <SidebarMenuSubItem v-for="child in item.children" :key="child.id">
                    <SidebarMenuSubButton class="cursor-pointer" :is-active="isActive(child.path || '')" @click="handleClick(child)">
                      <span>{{ child.title }}</span>
                    </SidebarMenuSubButton>
                  </SidebarMenuSubItem>
                </SidebarMenuSub>
              </SidebarMenuItem>
            </template>
          </SidebarMenu>
        </SidebarGroupContent>
      </SidebarGroup>
    </SidebarContent>
  </Sidebar>
</template>
