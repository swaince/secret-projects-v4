## Retrospective: auth-management

### 成果

- 后端新增 `sys_user_relation` / `sys_authorization` 两张表，支持用户与角色/岗位/部门的关系绑定及菜单授权
- 前端 5 个授权页面（用户授权 / 角色授权 / 岗位授权 / 部门授权 / 权限总览）
- 菜单授权支持父子级联勾选和按钮级权限分配
- 用户关系支持批量保存（userIds × targetIds）
- 权限总览 Tab 展示用户所有权限聚合（直接授权 + 角色/岗位/部门间接授权）
- 所有页面统一交互模式：👁 查看 + 行选中 + 保存后清空

### 技术要点

- 授权类型和关系类型使用大写字符串规范化（Service 层 `toUpperCase()`）
- 6 个管理页 Status toggle 修复（MapStruct `updateEntity` 去掉 `ignore = true`）
- MenuView 侧边栏消失已修复（`reset()` 后紧跟 `loadMenus()`）

### 遗留

- `SubjectType`/`RelationType` 未实现为 `@Dictionary` 枚举，沿用字符串管理
- 搜索表单 CardContent 上下边距从 `py-4` 收紧为 `py-2`
