<script setup lang="ts">
defineOptions({ name: 'AccessLogIndex' })

import { ref, computed, onMounted } from 'vue'
import {
  Table,
  TableBody,
  TableCell,
  TableEmpty,
  TableHead,
  TableHeader,
  TableRow,
} from '@/components/ui/table'
import { Card, CardContent } from '@/components/ui/card'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogHeader,
  DialogTitle,
} from '@/components/ui/dialog'
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select'
import {
  Pagination,
  PaginationContent,
  PaginationEllipsis,
  PaginationItem,
  PaginationNext,
  PaginationPrevious,
} from '@/components/ui/pagination'

import { Popover, PopoverContent, PopoverTrigger } from '@/components/ui/popover'
import { Calendar } from '@/components/ui/calendar'
import { parseCalendarDate, formatCalendarDate } from '@/utils/date'
import { Search, RotateCcw, Copy, CopyCheck, CalendarIcon } from '@lucide/vue'
import type { AccessLogDTO, LogPageParams } from '@/api/log'
import { fetchAccessLogs, fetchAccessLogDetail } from '@/api/log'
import { dict as dictMap } from '@/dict'

const logs = ref<AccessLogDTO[]>([])
const total = ref(0)
const page = ref(1)
const size = ref(10)
const searchUrl = ref('')
const searchUsername = ref('')
const searchStatus = ref('')
const searchMethod = ref('')
const searchStartTime = ref('')
const searchEndTime = ref('')
const startCalendarOpen = ref(false)
const endCalendarOpen = ref(false)
const startCalendarValue = ref<import('@internationalized/date').CalendarDate | undefined>()
const endCalendarValue = ref<import('@internationalized/date').CalendarDate | undefined>()
const dialogOpen = ref(false)
const detailLog = ref<AccessLogDTO | null>(null)
const detailLoading = ref(false)
const jumpPage = ref<number | undefined>()
const totalPages = computed(() => Math.ceil(total.value / size.value) || 1)

const formatHeaders = computed(() => {
  const h = detailLog.value?.requestHeaders
  if (!h) return ''
  try {
    return JSON.stringify(JSON.parse(h), null, 2)
  } catch {
    return h
  }
})

const methodColors = {
  GET: 'bg-[var(--color-method-get)]',
  POST: 'bg-[var(--color-method-post)]',
  PUT: 'bg-[var(--color-method-put)]',
  DELETE: 'bg-[var(--color-method-delete)]',
}

function methodClass(method: string) {
  return methodColors[method?.toUpperCase() as keyof typeof methodColors] || 'bg-muted text-muted-foreground'
}

function statusClass(status: number | null) {
  if (!status) return 'bg-muted text-muted-foreground'
  if (status >= 500) return 'bg-[var(--color-status-5xx)]'
  if (status >= 400) return 'bg-[var(--color-status-4xx)]'
  if (status >= 300) return 'bg-[var(--color-status-3xx)]'
  return 'bg-[var(--color-status-2xx)]'
}

async function loadLogs() {
  const params: LogPageParams = { page: page.value, size: size.value }
  if (searchUrl.value) params.requestUrl = searchUrl.value
  if (searchMethod.value) params.requestMethod = searchMethod.value
  if (searchUsername.value) params.username = searchUsername.value
  if (searchStatus.value) params.responseStatus = Number(searchStatus.value)
  if (searchStartTime.value) params.startTime = searchStartTime.value
  if (searchEndTime.value) params.endTime = searchEndTime.value
  const res: PageResponse<AccessLogDTO> = await fetchAccessLogs(params)
  logs.value = res.records
  total.value = res.total
}

const copiedId = ref<string | null>(null)

async function copyToClipboard(text: string, id: string) {
  await navigator.clipboard.writeText(text)
  copiedId.value = id
  setTimeout(() => { copiedId.value = null }, 2000)
}

function handleSearch() {
  page.value = 1
  loadLogs()
}

function resetSearch() {
  searchUrl.value = ''
  searchUsername.value = ''
  searchStatus.value = ''
  searchMethod.value = ''
  searchStartTime.value = ''
  searchEndTime.value = ''
  startCalendarValue.value = undefined
  endCalendarValue.value = undefined
  handleSearch()
}

function handleSizeChange(val: unknown) {
  size.value = Number(val)
  page.value = 1
  loadLogs()
}

function handleJump() {
  if (!jumpPage.value) return
  const p = Math.max(1, Math.min(jumpPage.value, totalPages.value))
  page.value = p
  jumpPage.value = undefined
  loadLogs()
}

async function openDetail(log: AccessLogDTO) {
  detailLog.value = null
  detailLoading.value = true
  dialogOpen.value = true
  try {
    detailLog.value = await fetchAccessLogDetail(log.logId)
  } finally {
    detailLoading.value = false
  }
}

onMounted(loadLogs)
</script>

<template>
  <div class="space-y-4">
    <Card>
      <CardContent class="py-4">
        <div class="flex flex-wrap items-end gap-4">
          <div class="flex items-center gap-2">
            <Label class="shrink-0">请求URL</Label>
            <Input
              v-model="searchUrl"
              name="requestUrl"
              autocomplete="off"
              placeholder="请输入URL…"
              class="w-40"
            />
          </div>
          <div class="flex items-center gap-2">
            <Label class="shrink-0">用户名</Label>
            <Input
              v-model="searchUsername"
              name="username"
              autocomplete="off"
              placeholder="请输入用户名…"
              class="w-40"
            />
          </div>
          <div class="flex items-center gap-2">
            <Label class="shrink-0">响应码</Label>
            <Select
              :model-value="searchStatus || '__all__'"
              @update:model-value="(v: any) => { searchStatus = v === '__all__' ? '' : String(v) }"
            >
              <SelectTrigger class="w-40">
                <SelectValue placeholder="全部" />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="__all__">全部</SelectItem>
                <SelectItem value="200">200</SelectItem>
                <SelectItem value="400">400</SelectItem>
                <SelectItem value="403">403</SelectItem>
                <SelectItem value="404">404</SelectItem>
                <SelectItem value="500">500</SelectItem>
              </SelectContent>
            </Select>
          </div>
          <div class="flex items-center gap-2">
            <Label class="shrink-0">开始时间</Label>
            <Popover v-model:open="startCalendarOpen">
              <PopoverTrigger as-child>
                <Button variant="outline" class="w-48 justify-start text-left font-normal" :class="{ 'text-muted-foreground': !searchStartTime }">
                  <CalendarIcon class="mr-2 size-4" />
                  {{ searchStartTime || '不限' }}
                </Button>
              </PopoverTrigger>
              <PopoverContent class="w-auto p-0" align="start">
                <Calendar v-model="startCalendarValue" locale="zh-CN" />
                <div class="flex justify-end gap-2 border-t p-2">
                  <Button size="sm" variant="ghost" @click="startCalendarValue = undefined; searchStartTime = ''; startCalendarOpen = false">清除</Button>
                  <Button size="sm" @click="searchStartTime = formatCalendarDate(startCalendarValue); startCalendarOpen = false">确认</Button>
                </div>
              </PopoverContent>
            </Popover>
          </div>
          <div class="flex items-center gap-2">
            <Label class="shrink-0">结束时间</Label>
            <Popover v-model:open="endCalendarOpen">
              <PopoverTrigger as-child>
                <Button variant="outline" class="w-48 justify-start text-left font-normal" :class="{ 'text-muted-foreground': !searchEndTime }">
                  <CalendarIcon class="mr-2 size-4" />
                  {{ searchEndTime || '不限' }}
                </Button>
              </PopoverTrigger>
              <PopoverContent class="w-auto p-0" align="start">
                <Calendar v-model="endCalendarValue" locale="zh-CN" />
                <div class="flex justify-end gap-2 border-t p-2">
                  <Button size="sm" variant="ghost" @click="endCalendarValue = undefined; searchEndTime = ''; endCalendarOpen = false">清除</Button>
                  <Button size="sm" @click="searchEndTime = formatCalendarDate(endCalendarValue); endCalendarOpen = false">确认</Button>
                </div>
              </PopoverContent>
            </Popover>
          </div>
          <div class="flex items-center gap-2">
            <Label class="shrink-0">请求方式</Label>
            <Select :model-value="searchMethod || '__all__'" @update:model-value="(v: any) => { searchMethod = v === '__all__' ? '' : String(v) }">
              <SelectTrigger class="w-40">
                <SelectValue placeholder="全部" />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="__all__">全部</SelectItem>
                <SelectItem v-for="item in dictMap.requestMethod.items.value" :key="item.itemKey" :value="item.itemKey">
                  {{ item.itemLabel }}
                </SelectItem>
              </SelectContent>
            </Select>
          </div>
          <div class="flex gap-2">
            <Button size="sm" data-testid="search-btn" @click="handleSearch">
              <Search class="size-4" />搜索
            </Button>
            <Button size="sm" variant="outline" @click="resetSearch()">
              <RotateCcw class="size-4" />重置
            </Button>
          </div>
        </div>
      </CardContent>
    </Card>

    <Card>
      <CardContent class="p-0">
        <Table class="table-fixed">
          <TableHeader>
            <TableRow>
              <TableHead>请求URL</TableHead>
              <TableHead class="w-20 text-center">方法</TableHead>
              <TableHead class="w-20 text-center">响应码</TableHead>
              <TableHead class="w-20 text-center">耗时</TableHead>
              <TableHead class="w-36">IP</TableHead>
              <TableHead class="w-28">用户名</TableHead>
              <TableHead class="w-44 text-center">时间</TableHead>
            </TableRow>
          </TableHeader>
          <TableBody>
            <TableRow
              v-for="log in logs"
              :key="log.logId"
              tabindex="0"
              role="button"
              class="cursor-pointer hover:bg-muted/50 focus-visible:ring-2 focus-visible:ring-ring focus-visible:outline-none"
              @click="openDetail(log)"
              @keydown.prevent="(e: KeyboardEvent) => { if (e.key === 'Enter' || e.key === ' ') openDetail(log) }"
            >
              <TableCell class="truncate">{{ log.requestUrl }}</TableCell>
              <TableCell class="text-center">
                <span :class="['inline-flex items-center rounded-md border px-2 py-0.5 text-xs font-semibold', methodClass(log.requestMethod)]">
                  {{ log.requestMethod }}
                </span>
              </TableCell>
              <TableCell class="text-center">
                <span :class="['inline-flex items-center rounded-md border px-2 py-0.5 text-xs font-semibold', statusClass(log.responseStatus)]">
                  {{ log.responseStatus ?? '-' }}
                </span>
              </TableCell>
              <TableCell class="tabular-nums text-center">{{ log.durationMs ? log.durationMs + ' ms' : '-' }}</TableCell>
              <TableCell class="truncate tabular-nums">{{ log.clientIp ?? '-' }}</TableCell>
              <TableCell class="truncate">{{ log.username ?? '-' }}</TableCell>
              <TableCell class="tabular-nums text-center">{{ log.createdAt }}</TableCell>
            </TableRow>
            <TableEmpty v-if="logs.length === 0" :colspan="7">暂无数据</TableEmpty>
          </TableBody>
        </Table>

        <div class="flex items-center justify-end gap-4 border-t px-4 py-3">
          <Pagination
            v-model:page="page"
            :total="total"
            :items-per-page="size"
            class="mx-0 w-auto justify-end"
            @update:page="loadLogs"
          >
            <PaginationContent v-slot="{ items }">
              <PaginationPrevious />
              <template v-for="(item, index) in items" :key="index">
                <PaginationItem
                  v-if="item.type === 'page'"
                  :value="item.value"
                  :is-active="item.value === page"
                />
                <PaginationEllipsis v-else :index="index" />
              </template>
              <PaginationNext />
            </PaginationContent>
          </Pagination>
          <div class="flex items-center gap-1.5">
            <span class="shrink-0 text-sm">每页</span>
            <Select :model-value="String(size)" @update:model-value="handleSizeChange">
              <SelectTrigger class="h-8 w-[70px]">
                <SelectValue />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="10">10</SelectItem>
                <SelectItem value="20">20</SelectItem>
                <SelectItem value="50">50</SelectItem>
                <SelectItem value="100">100</SelectItem>
              </SelectContent>
            </Select>
            <span class="shrink-0 text-sm">条</span>
          </div>
          <div class="flex items-center gap-1.5">
            <span class="shrink-0 text-sm">跳至</span>
            <Input
              v-model.number="jumpPage"
              type="number"
              name="jumpPage"
              autocomplete="off"
              aria-label="跳转页码"
              class="h-8 w-16 text-center tabular-nums"
              :min="1"
              :max="totalPages"
              @keydown.enter="handleJump"
            />
            <span class="shrink-0 text-sm">页</span>
          </div>
          <span class="text-muted-foreground shrink-0 text-sm tabular-nums">共 {{ total }} 条</span>
        </div>
      </CardContent>
    </Card>

    <Dialog v-model:open="dialogOpen">
      <DialogContent class="sm:max-w-2xl">
        <DialogHeader>
          <DialogTitle>访问日志详情</DialogTitle>
          <DialogDescription>查看完整的请求与响应信息</DialogDescription>
        </DialogHeader>
        <div v-if="detailLoading" class="py-8 text-center text-muted-foreground text-sm">加载中...</div>
        <div v-else-if="detailLog" class="max-h-[70vh] overflow-y-auto space-y-5 py-2">
          <div class="flex items-center gap-2">
            <span class="font-mono text-sm break-all">{{ detailLog.requestUrl }}</span>
              <Button variant="ghost" size="sm" class="size-7 shrink-0" aria-label="复制URL" @click="copyToClipboard(detailLog.requestUrl, 'url')">
              <CopyCheck v-if="copiedId === 'url'" class="size-3.5 text-green-600" />
              <Copy v-else class="size-3.5" />
            </Button>
          </div>
          <div class="grid grid-cols-3 gap-3 text-sm">
            <div>
              <span class="text-muted-foreground text-xs">方法</span>
              <p><span :class="['inline-flex items-center rounded-md border px-2 py-0.5 text-xs font-semibold', methodClass(detailLog.requestMethod)]">{{ detailLog.requestMethod }}</span></p>
            </div>
            <div>
              <span class="text-muted-foreground text-xs">状态码</span>
              <p><span :class="['inline-flex items-center rounded-md border px-2 py-0.5 text-xs font-semibold', statusClass(detailLog.responseStatus)]">{{ detailLog.responseStatus ?? '-' }}</span></p>
            </div>
            <div>
              <span class="text-muted-foreground text-xs">耗时</span>
              <p class="font-mono">{{ detailLog.durationMs ? detailLog.durationMs + ' ms' : '-' }}</p>
            </div>
          </div>
          <div class="grid grid-cols-2 gap-3 text-sm">
            <div>
              <span class="text-muted-foreground text-xs">请求ID</span>
              <p class="font-mono break-all">{{ detailLog.requestId }}</p>
            </div>
            <div>
              <span class="text-muted-foreground text-xs">客户端IP</span>
              <p>{{ detailLog.clientIp ?? '-' }}</p>
            </div>
          </div>
          <div class="grid grid-cols-2 gap-3 text-sm">
            <div>
              <span class="text-muted-foreground text-xs">用户名</span>
              <p>{{ detailLog.username ?? '-' }}</p>
            </div>
            <div>
              <span class="text-muted-foreground text-xs">请求时间</span>
              <p class="font-mono">{{ detailLog.createdAt }}</p>
            </div>
          </div>
          <div class="text-sm">
            <span class="text-muted-foreground text-xs">User-Agent</span>
            <p class="break-all">{{ detailLog.userAgent ?? '-' }}</p>
          </div>
          <div v-if="detailLog.requestParams">
            <div class="flex items-center gap-2">
              <span class="text-muted-foreground text-xs">请求参数</span>
              <Button variant="ghost" size="sm" class="size-6 shrink-0" aria-label="复制请求参数" @click="copyToClipboard(detailLog.requestParams!, 'params')">
                <CopyCheck v-if="copiedId === 'params'" class="size-3 text-green-600" />
                <Copy v-else class="size-3" />
              </Button>
            </div>
            <pre class="mt-1 rounded-md bg-muted p-3 font-mono text-xs whitespace-pre-wrap break-all">{{ detailLog.requestParams }}</pre>
          </div>
          <div v-if="formatHeaders">
            <div class="flex items-center gap-2">
              <span class="text-muted-foreground text-xs">请求头</span>
              <Button variant="ghost" size="sm" class="size-6 shrink-0" aria-label="复制请求头" @click="copyToClipboard(formatHeaders, 'headers')">
                <CopyCheck v-if="copiedId === 'headers'" class="size-3 text-green-600" />
                <Copy v-else class="size-3" />
              </Button>
            </div>
            <pre class="mt-1 rounded-md bg-muted p-3 font-mono text-xs whitespace-pre-wrap break-all">{{ formatHeaders }}</pre>
          </div>
          <div v-if="detailLog.requestBody">
            <div class="flex items-center gap-2">
              <span class="text-muted-foreground text-xs">请求体</span>
              <Button variant="ghost" size="sm" class="size-6 shrink-0" aria-label="复制请求体" @click="copyToClipboard(detailLog.requestBody!, 'body')">
                <CopyCheck v-if="copiedId === 'body'" class="size-3 text-green-600" />
                <Copy v-else class="size-3" />
              </Button>
            </div>
            <pre class="mt-1 rounded-md bg-muted p-3 font-mono text-xs whitespace-pre-wrap break-all">{{ detailLog.requestBody }}</pre>
          </div>
          <div v-if="detailLog.exceptionStack">
            <div class="flex items-center gap-2">
              <span class="text-muted-foreground text-xs">异常堆栈</span>
              <Button variant="ghost" size="sm" class="size-6 shrink-0" aria-label="复制异常堆栈" @click="copyToClipboard(detailLog.exceptionStack!, 'exception')">
                <CopyCheck v-if="copiedId === 'exception'" class="size-3 text-green-600" />
                <Copy v-else class="size-3" />
              </Button>
            </div>
            <pre class="mt-1 rounded-md bg-destructive/10 p-3 font-mono text-xs whitespace-pre-wrap break-all text-destructive">{{ detailLog.exceptionStack }}</pre>
          </div>
        </div>
      </DialogContent>
    </Dialog>
  </div>
</template>
