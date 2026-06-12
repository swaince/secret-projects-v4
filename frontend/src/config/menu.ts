import type { RouteRecordRaw, RouteComponent } from 'vue-router'
import PlaceholderView from '@/views/PlaceholderView.vue'

export interface MenuItem {
  id: string
  title: string
  icon?: string
  path?: string
  component?: RouteComponent | (() => Promise<RouteComponent | { default: RouteComponent }>)
  children?: MenuItem[]
  meta?: {
    breadcrumb?: string
    keepAlive?: boolean
  }
}

export const menuConfig: MenuItem[] = [
  { id: 'dashboard', title: '仪表盘', icon: 'LayoutDashboard', path: '/dashboard' },

  {
    id: 'content', title: '内容管理', icon: 'FileText',
    children: [
      { id: 'article-list', title: '文章列表', icon: 'FileText', path: '/content/articles' },
      { id: 'category', title: '分类管理', icon: 'FolderTree', path: '/content/categories' },
      { id: 'comment', title: '评论审核', icon: 'MessageSquareText', path: '/content/comments' },
    ],
  },

  {
    id: 'system', title: '系统管理', icon: 'Settings',
    children: [
      {
        id: 'user-mgmt', title: '用户管理', icon: 'Users',
        children: [
          { id: 'user-list', title: '用户列表', icon: 'UserRound', path: '/system/users' },
          { id: 'role-list', title: '角色管理', icon: 'ShieldCheck', path: '/system/roles' },
          { id: 'perm-list', title: '权限配置', icon: 'LockKeyhole', path: '/system/permissions' },
        ],
      },
      {
        id: 'org-mgmt', title: '组织架构', icon: 'Building2',
        children: [
          { id: 'dept-list', title: '部门管理', icon: 'Building', path: '/system/depts' },
        ],
      },
      {
        id: 'post-list',
        title: '岗位管理',
        icon: 'BriefcaseBusiness',
        path: '/system/posts',
        component: () => import('@/views/system/PostView.vue'),
      },
      { id: 'menu-config', title: '菜单配置', icon: 'Menu', path: '/system/menus' },
      {
        id: 'dict-list',
        title: '字典管理',
        icon: 'BookType',
        path: '/system/dict',
        component: () => import('@/views/system/DictView.vue'),
      },
      { id: 'param-config', title: '参数设置', icon: 'SlidersHorizontal', path: '/system/params' },
    ],
  },

  {
    id: 'monitor', title: '运维监控', icon: 'Activity',
    children: [
      { id: 'online-user', title: '在线用户', icon: 'UserCheck', path: '/monitor/online' },
      { id: 'server-info', title: '服务器信息', icon: 'Server', path: '/monitor/server' },
      { id: 'cache-monitor', title: '缓存监控', icon: 'Database', path: '/monitor/cache' },
    ],
  },

  {
    id: 'audit', title: '日志审计', icon: 'ScrollText',
    children: [
      { id: 'login-log', title: '登录日志', icon: 'LogIn', path: '/audit/login' },
      { id: 'oper-log', title: '操作日志', icon: 'ClipboardList', path: '/audit/operation' },
    ],
  },
]

export function createRoutesFromMenu(items: MenuItem[]): RouteRecordRaw[] {
  const routes: RouteRecordRaw[] = []
  for (const item of items) {
    if (item.path) {
      routes.push({
        path: item.path,
        name: item.id,
        component: item.component ?? PlaceholderView,
        meta: { title: item.title, ...item.meta },
      })
    }
    if (item.children) routes.push(...createRoutesFromMenu(item.children))
  }
  return routes
}
