## 1. 后端：数据库与枚举

- [x] 1.1 创建 Flyway 迁移脚本建 `sys_menu` 表
- [x] 1.2 创建 `MenuType` 枚举（@Dictionary: D=目录, M=菜单, B=按钮）

## 2. 后端：实体与 DTO

- [x] 2.1 创建 `SysMenu` 实体类
- [x] 2.2 创建 `MenuDTO`（含 children 树字段用于树查询响应）

## 3. 后端：Mapper 与 MapStruct

- [x] 3.1 创建 `MenuMapper` 接口
- [x] 3.2 创建 `MenuStructMapper`

## 4. 后端：Service

- [x] 4.1 创建 `MenuService` 接口（tree, list, create, update, delete）
- [x] 4.2 创建 `MenuServiceImpl`（树组装逻辑、级联删除、parentId 自引用校验）

## 5. 后端：Controller

- [x] 5.1 创建 `MenuController`（GET /menus/tree, GET /menus, POST, PUT, DELETE）

## 6. 后端：数据初始化

- [x] 6.1 创建 Flyway 数据迁移脚本，将现有 menu.ts 菜单数据插入 sys_menu 表

## 7. 后端：测试

- [x] 7.1 编写 `MenuControllerTest` 集成测试（树查询、创建各类型、修改、删除级联）

## 8. 前端：API 层

- [x] 8.1 创建 `src/api/menu.ts`（MenuDTO, fetchMenuTree, fetchMenuList, createMenu, updateMenu, deleteMenu）

## 9. 前端：动态菜单 Store

- [x] 9.1 创建 `src/stores/menu.ts`（加载菜单树、生成路由、注册动态路由）
- [x] 9.2 修改 `src/router/index.ts`（支持动态路由注册，保留基础路由）

## 10. 前端：菜单管理页面

- [x] 10.1 创建 `src/views/system/MenuView.vue`（树形表格 + 对话框 CRUD）
- [x] 10.2 字典注册表新增 `menuType: { code: 'menu_type', valueType: 'STRING' }`

## 11. 前端：替换静态菜单

- [x] 11.1 修改布局组件从 menuStore 读取菜单数据（替代 menu.ts 导入）
- [x] 11.2 删除 `src/config/menu.ts`

## 12. 验证

- [x] 12.1 运行 `./mvnw test -pl backend` 确认后端测试通过
- [x] 12.2 运行 `pnpm type-check` 和 `pnpm lint` 确认前端无错误
