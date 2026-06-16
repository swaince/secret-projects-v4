<script setup lang="ts">
defineOptions({ name: 'DeptTreeItem' })

import { ref, computed } from 'vue'
import { Checkbox } from '@/components/ui/checkbox'
import { ChevronRight, ChevronDown } from '@lucide/vue'
import type { DeptDTO } from '@/api/dept'

const props = defineProps<{
  dept: DeptDTO
  depth: number
  selected: string[]
}>()

const emit = defineEmits<{
  toggle: [id: string]
}>()

const checked = computed(() => props.selected.includes(props.dept.deptId))
const expanded = ref(true)

function toggleExpand() {
  expanded.value = !expanded.value
}
</script>

<template>
  <div>
    <div
      class="flex items-center text-sm transition-colors hover:bg-accent/50 cursor-pointer border-b"
      @click="emit('toggle', dept.deptId)"
    >
      <div class="flex w-10 shrink-0 justify-center py-1">
        <Checkbox
          :model-value="checked"
          :aria-label="dept.deptName"
          class="shrink-0"
        />
      </div>
      <div class="flex flex-1 items-center truncate py-1 pr-2" :style="{ paddingLeft: `${depth * 20}px` }">
        <button
          v-if="(dept.children && dept.children.length > 0)"
          class="flex size-5 shrink-0 items-center justify-center rounded hover:bg-muted"
          @click.stop="toggleExpand"
        >
          <ChevronDown v-if="expanded" class="size-4" />
          <ChevronRight v-else class="size-4" />
        </button>
        <span v-else class="w-5 shrink-0" />
        <span class="truncate">{{ dept.deptName }}</span>
      </div>
    </div>
    <DeptTreeItem
      v-if="expanded"
      v-for="child in dept.children"
      :key="child.deptId"
      :dept="child"
      :depth="depth + 1"
      :selected="selected"
      @toggle="emit('toggle', $event)"
    />
  </div>
</template>
