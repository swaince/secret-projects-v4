## Why

系统需要将菜单权限授予用户/部门/岗位/角色，同时管理用户与部门/岗位/角色的多对多关系。为权限控制打基础。

## What Changes

**后端：授权管理 + 用户关系表**
- From: 无授权功能
- To: sys_authorization 菜单授权表 + sys_user_dept/post/role 关联表 + 对应 CRUD 接口
- Reason: 建立多维度的权限绑定体系
- Impact: non-breaking，纯新增

**前端：四种独立授权页面**
- From: 无授权入口
- To: 用户授权(4个Tab)/部门授权/岗位授权/角色授权
- Reason: 各自独立的交互，支持批量授权
- Impact: non-breaking，新增页面和菜单项

## Capabilities

### New Capabilities

- `auth-management`: 授权管理 — 菜单授权表 + 用户关系表 + 四种独立授权页面（支持批量）+ 用户 Tab 管理

### Modified Capabilities

无

## Impact

- **后端新增**：sys_authorization、sys_user_dept、sys_user_post、sys_user_role 四张表 + SubjectType 枚举 + Entity/DTO/Service/Controller
- **前端新增**：`src/api/auth.ts` + 4 个 View 页面
- **菜单**：授权管理(D) → 用户授权/部门授权/岗位授权/角色授权
- **测试**：后端集成测试
