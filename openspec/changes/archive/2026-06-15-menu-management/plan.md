# 菜单管理 Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 实现数据库驱动的菜单管理（无限层级树 + 按钮权限 + 前端动态路由），替换硬编码 menu.ts。

**Architecture:** 后端 sys_menu 表（parentId 自引用树），Service 层组装树结构返回。前端 Pinia store 加载菜单树 → 动态注册路由 → 布局组件渲染导航。管理页面用树形表格 CRUD。

**Tech Stack:** Spring Boot 3.5 / MyBatis-Plus / MapStruct / Flyway / Vue 3.5 / vue-router 4 / Pinia / shadcn-vue

---

## Task 1: Flyway 建表 + MenuType 枚举

**Files:**
- Create: `backend/src/main/resources/db/migration/V1.0.0.8__create_menu_table.sql`
- Create: `backend/src/main/java/com/dfec/soft/secret/common/constants/MenuType.java`

- [ ] **Step 1: 建表脚本**

```sql
CREATE TABLE sys_menu (
    menu_id       VARCHAR(32)   PRIMARY KEY,
    parent_id     VARCHAR(32),
    menu_name     VARCHAR(50)   NOT NULL,
    menu_type     VARCHAR(1)    NOT NULL,
    path          VARCHAR(200),
    component     VARCHAR(200),
    icon          VARCHAR(50),
    sort_order    INT           DEFAULT 0,
    status        INT           DEFAULT 1,
    permission    VARCHAR(100),
    visible       INT           DEFAULT 1,
    redirect      VARCHAR(200),
    require_auth  INT           DEFAULT 1,
    create_at     TIMESTAMP     DEFAULT CURRENT_TIMESTAMP,
    create_by     VARCHAR(32),
    update_at     TIMESTAMP     DEFAULT CURRENT_TIMESTAMP,
    update_by     VARCHAR(32),
    deleted       INT           DEFAULT 0
);

COMMENT ON TABLE sys_menu IS '系统菜单表';
```

- [ ] **Step 2: MenuType 枚举**

```java
@Dictionary(name = "菜单类型", code = "menu_type")
public enum MenuType implements DictionaryElement<String> {
    DIRECTORY("D", "目录"),
    MENU("M", "菜单"),
    BUTTON("B", "按钮");
    // ...
}
```

---

## Task 2: 后端 Entity + DTO + Mapper + MapStruct

**Files:**
- Create: SysMenu.java, MenuDTO.java, MenuMapper.java, MenuStructMapper.java

- [ ] **Step 1: SysMenu 实体** — 所有字段，@TableName("sys_menu")
- [ ] **Step 2: MenuDTO** — 含 `List<MenuDTO> children` 字段（用于树响应），children 不持久化
- [ ] **Step 3: MenuMapper** — extends BaseMapper<SysMenu>
- [ ] **Step 4: MenuStructMapper** — entityToDTO (单 + 列表), requestToEntity, updateEntity

---

## Task 3: 后端 Service

**Files:**
- Create: MenuService.java, MenuServiceImpl.java

- [ ] **Step 1: 接口方法** — tree(), list(), create(MenuDTO, userId), update(menuId, MenuDTO, userId), delete(menuId, userId)
- [ ] **Step 2: 树组装逻辑** — 查全部 deleted=0 + status=1 → 按 parentId 分组 → 递归构建树
- [ ] **Step 3: 级联删除** — 删除时递归查找所有子孙 menuId，批量逻辑删除
- [ ] **Step 4: parentId 校验** — 更新时不允许 parentId = menuId

---

## Task 4: 后端 Controller

**Files:**
- Create: MenuController.java

- [ ] **Step 1: 端点定义**
  - `GET /menus/tree` — 返回菜单树
  - `GET /menus` — 返回平铺列表
  - `POST /menus` — 创建
  - `PUT /menus/{menuId}` — 修改
  - `DELETE /menus/{menuId}` — 删除

---

## Task 5: 数据初始化脚本

**Files:**
- Create: `V1.0.0.9__init_menu_data.sql`

- [ ] **Step 1: 读取现有 menu.ts，转换为 INSERT SQL**
  - 包含系统管理、运维监控、日志审计等已有菜单
  - parentId 关联正确
  - component 字段存储相对路径（如 `system/UserView`）

---

## Task 6: 后端测试

**Files:**
- Create: MenuControllerTest.java

- [ ] **Step 1: 测试用例** — 创建目录/菜单/按钮、树查询、修改、级联删除
- [ ] **Step 2: 运行测试**

---

## Task 7: 前端 API 层

**Files:**
- Create: `frontend/src/api/menu.ts`

- [ ] **Step 1: 接口定义**

```typescript
export interface MenuDTO {
  menuId: string
  parentId: string | null
  menuName: string
  menuType: string
  path: string | null
  component: string | null
  icon: string | null
  sortOrder: number
  status: number
  permission: string | null
  visible: number
  redirect: string | null
  requireAuth: number
  children?: MenuDTO[]
}
```

---

## Task 8: 前端动态菜单 Store

**Files:**
- Create: `frontend/src/stores/menu.ts`
- Modify: `frontend/src/router/index.ts`

- [ ] **Step 1: menuStore** — `loadMenus()` 请求树接口，存储菜单数据
- [ ] **Step 2: generateRoutes()** — 遍历菜单树，menuType=M 生成 RouteRecordRaw（component 用动态 import）
- [ ] **Step 3: router 改造** — 保留基础路由（登录/404），动态路由通过 `router.addRoute()` 注册

---

## Task 9: 前端菜单管理页面

**Files:**
- Create: `frontend/src/views/system/MenuView.vue`
- Modify: `frontend/src/dict/index.ts`

- [ ] **Step 1: MenuView.vue** — 树形表格（参照 DeptView 的 flatNodes 模式），对话框表单根据 menuType 动态显示字段
- [ ] **Step 2: dict 注册** — `menuType: { code: 'menu_type', valueType: 'STRING' }`

---

## Task 10: 替换静态菜单

**Files:**
- Modify: 布局组件（AppSidebar 等）
- Delete: `frontend/src/config/menu.ts`

- [ ] **Step 1: 布局组件改造** — 从 menuStore 读取菜单树，替代 `import { menuConfig } from '@/config/menu'`
- [ ] **Step 2: 删除 menu.ts**
- [ ] **Step 3: 清理 menu.ts 相关的 composables 引用**

---

## Task 11: 验证

- [ ] **Step 1:** `./mvnw test -pl backend`
- [ ] **Step 2:** `pnpm type-check`
- [ ] **Step 3:** `pnpm lint:oxlint`
