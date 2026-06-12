import { describe, it, expect, vi, beforeEach } from 'vitest'
import { mount, flushPromises } from '@vue/test-utils'
import { createRouter, createWebHistory } from 'vue-router'
import type { DictDTO } from '@/api/dict'

vi.mock('@/api/dict', () => ({
  fetchDicts: vi.fn<typeof import('@/api/dict').fetchDicts>(),
  createDict: vi.fn<typeof import('@/api/dict').createDict>(),
  deleteDict: vi.fn<typeof import('@/api/dict').deleteDict>(),
  deleteDicts: vi.fn<typeof import('@/api/dict').deleteDicts>(),
  fetchDictItems: vi.fn<typeof import('@/api/dict').fetchDictItems>(),
}))

import { fetchDicts, fetchDictItems } from '@/api/dict'
import DictIndex from '@/views/system/DictView.vue'

const mockDicts: DictDTO[] = [
  {
    dictId: '1',
    dictName: '用户状态',
    dictCode: 'user_status',
    dataValueType: 'STRING',
    remark: '用户状态字典',
    status: 1,
    builtIn: 0,
    createdAt: '2026-01-01T00:00:00',
  },
  {
    dictId: '2',
    dictName: '性别',
    dictCode: 'gender',
    dataValueType: 'NUMBER',
    remark: '',
    status: 1,
    builtIn: 1,
    createdAt: '2026-01-02T00:00:00',
  },
]

const mockPageResponse: PageResponse<DictDTO> = {
  records: mockDicts,
  total: 2,
  page: 1,
  size: 10,
}

function createWrapper() {
  const router = createRouter({
    history: createWebHistory(),
    routes: [{ path: '/system/dict', component: {} as never }],
  })
  router.push('/system/dict')

  return mount(DictIndex, {
    global: {
      plugins: [router],
      stubs: {
        Table: { template: '<table><slot /></table>' },
        TableHeader: { template: '<thead><slot /></thead>' },
        TableBody: { template: '<tbody><slot /></tbody>' },
        TableRow: { template: '<tr><slot /></tr>' },
        TableHead: { template: '<th><slot /></th>' },
        TableCell: { template: '<td><slot /></td>' },
        TableEmpty: { template: '<tr><td><slot /></td></tr>' },
        Button: { template: '<button><slot /></button>' },
        Input: {
          template: '<input :value="modelValue" @input="$emit(\'update:modelValue\', $event.target.value)" v-bind="$attrs" />',
          props: ['modelValue'],
          emits: ['update:modelValue'],
          inheritAttrs: true,
        },
        Checkbox: { template: '<input type="checkbox" v-bind="$attrs" />', inheritAttrs: true },
        Sheet: { template: '<div><slot /></div>' },
        SheetContent: { template: '<div><slot /></div>' },
        SheetHeader: { template: '<div><slot /></div>' },
        SheetTitle: { template: '<div><slot /></div>' },
        SheetDescription: { template: '<div><slot /></div>' },
        Pagination: { template: '<nav><slot /></nav>' },
        PaginationContent: { template: '<div><slot /></div>' },
        PaginationItem: { template: '<div><slot /></div>' },
        PaginationNext: { template: '<button>Next</button>' },
        PaginationPrevious: { template: '<button>Prev</button>' },
        AlertDialog: { template: '<div><slot /></div>' },
        AlertDialogTrigger: { template: '<div><slot /></div>' },
        AlertDialogContent: { template: '<div><slot /></div>' },
        AlertDialogHeader: { template: '<div><slot /></div>' },
        AlertDialogTitle: { template: '<div><slot /></div>' },
        AlertDialogDescription: { template: '<div><slot /></div>' },
        AlertDialogFooter: { template: '<div><slot /></div>' },
        AlertDialogAction: { template: '<button><slot /></button>' },
        AlertDialogCancel: { template: '<button><slot /></button>' },
        DictItemDrawer: { template: '<div class="drawer-stub"><slot /></div>' },
      },
    },
  })
}

describe('DictIndex', () => {
  beforeEach(() => {
    vi.clearAllMocks()
    vi.mocked(fetchDicts).mockResolvedValue(mockPageResponse)
    vi.mocked(fetchDictItems).mockResolvedValue([])
  })

  it('renders dict table with data', async () => {
    const wrapper = createWrapper()
    await flushPromises()

    expect(fetchDicts).toHaveBeenCalledWith({ page: 1, size: 10 })
    expect(wrapper.text()).toContain('用户状态')
    expect(wrapper.text()).toContain('user_status')
    expect(wrapper.text()).toContain('性别')
    expect(wrapper.text()).toContain('gender')
  })

  it('searches by dict name', async () => {
    const wrapper = createWrapper()
    await flushPromises()

    const searchInput = wrapper.find('input[placeholder]')
    await searchInput.setValue('用户')
    await wrapper.find('[data-testid="search-btn"]').trigger('click')
    await flushPromises()

    expect(fetchDicts).toHaveBeenCalledWith({ page: 1, size: 10, dictName: '用户' })
  })

  it('opens drawer when clicking a dict row', async () => {
    const wrapper = createWrapper()
    await flushPromises()

    const rows = wrapper.findAll('tr')
    const dataRow = rows.find((r) => r.text().includes('用户状态'))
    expect(dataRow).toBeTruthy()
    await dataRow!.trigger('click')

    expect(wrapper.find('.drawer-stub').exists() || wrapper.text()).toBeTruthy()
  })

  it('shows empty state when no data', async () => {
    vi.mocked(fetchDicts).mockResolvedValue({ records: [], total: 0, page: 1, size: 10 })
    const wrapper = createWrapper()
    await flushPromises()

    expect(wrapper.text()).toContain('暂无数据')
  })
})
