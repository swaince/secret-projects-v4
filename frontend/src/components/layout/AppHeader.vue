<script setup lang="ts">
import { Bell, Sun, Moon } from '@lucide/vue'
import { useThemeStore } from '@/stores/theme'
import { Button } from '@/components/ui/button'
import { Avatar, AvatarFallback } from '@/components/ui/avatar'
import { Separator } from '@/components/ui/separator'
import {
  DropdownMenu, DropdownMenuContent, DropdownMenuItem, DropdownMenuTrigger,
} from '@/components/ui/dropdown-menu'
import type { MenuItem } from '@/stores/menu'

defineProps<{ menuItems: MenuItem[]; activePrimaryId: string }>()
const emit = defineEmits<{ (e: 'navigate', item: MenuItem): void }>()
const theme = useThemeStore()
</script>

<template>
  <header class="flex h-14 shrink-0 items-center border-b bg-background select-none">
    <button
      v-if="menuItems.length"
      class="flex w-[16rem] shrink-0 items-center gap-2.5 px-3 py-1 transition-colors hover:bg-accent rounded-none"
      @click="emit('navigate', menuItems[0]!)"
    >
      <span class="flex h-8 w-8 items-center justify-center rounded-md bg-primary text-primary-foreground text-sm font-bold tracking-tight">S</span>
      <span class="font-semibold text-sm tracking-tight">Secret Projects</span>
    </button>

    <Separator orientation="vertical" class="mr-4 -ml-px" />

    <nav class="flex items-center gap-0.5">
      <Button
        v-for="item in menuItems" :key="item.id"
        variant="ghost" size="sm"
        class="relative text-sm font-medium transition-all duration-200"
        :class="item.id === activePrimaryId
          ? 'text-foreground after:absolute after:bottom-0 after:left-1/2 after:h-0.5 after:w-3/4 after:-translate-x-1/2 after:rounded-full after:bg-primary'
          : 'text-muted-foreground hover:text-foreground'"
        @click="emit('navigate', item)"
      >{{ item.title }}</Button>
    </nav>

    <div class="ml-auto flex items-center gap-1 pr-2">
      <DropdownMenu>
        <DropdownMenuTrigger as-child>
          <Button variant="ghost" size="icon" class="h-9 w-9 rounded-lg transition-colors hover:bg-accent" data-test="notification" aria-label="消息通知">
            <Bell class="h-[18px] w-[18px]" />
            <span v-if="false" class="absolute right-1.5 top-1.5 flex h-4 min-w-4 items-center justify-center rounded-full bg-destructive px-1 text-[10px] font-medium text-destructive-foreground leading-none">3</span>
          </Button>
        </DropdownMenuTrigger>
        <DropdownMenuContent align="end" class="w-72">
          <div class="px-3 py-2 text-xs font-medium text-muted-foreground">通知</div>
          <DropdownMenuItem disabled class="py-6 justify-center text-muted-foreground">暂无新通知</DropdownMenuItem>
        </DropdownMenuContent>
      </DropdownMenu>

      <Button variant="ghost" size="icon" class="h-9 w-9 rounded-lg transition-colors hover:bg-accent" data-test="theme-toggle" aria-label="切换主题" @click="theme.toggle()">
        <Sun v-if="theme.theme === 'dark'" class="h-[18px] w-[18px] transition-transform duration-500" />
        <Moon v-else class="h-[18px] w-[18px] transition-transform duration-500" />
      </Button>

      <DropdownMenu>
        <DropdownMenuTrigger as-child>
          <Button variant="ghost" size="icon" class="h-9 w-9 rounded-lg" data-test="user-menu" aria-label="用户菜单">
            <Avatar class="h-7 w-7 transition-transform hover:scale-105">
              <AvatarFallback class="text-xs">U</AvatarFallback>
            </Avatar>
          </Button>
        </DropdownMenuTrigger>
        <DropdownMenuContent align="end" class="w-48">
          <div class="px-3 py-2">
            <p class="text-sm font-medium">管理员</p>
            <p class="text-xs text-muted-foreground">admin@secret.dev</p>
          </div>
          <DropdownMenuItem>个人中心</DropdownMenuItem>
          <DropdownMenuItem>退出登录</DropdownMenuItem>
        </DropdownMenuContent>
      </DropdownMenu>
    </div>
  </header>
</template>
