# Retrospective: admin-layout-component

> Written: 2026-06-11 (after verify passed with warnings)
> Commit range: implemented in prior sessions, verify/retro added this session
> Worktree: main branch

---

## 0. Evidence

- **Commit range**: 先前会话实现，本次会话仅补充 verify + retrospective artifacts
- **Diff size**: 实现代码在先前会话中完成
- **Tasks done**: 33/33
- **Active hours**: ~1h（本次会话 verify + retro 部分）
- **Subagent dispatches**: 1（spec 验证）
- **New external dependencies**: shadcn-vue 组件（sidebar, breadcrumb, dropdown-menu, avatar, badge, tooltip, collapsible, separator, skeleton, sheet）
- **Bugs encountered post-merge**: none
- **OpenSpec validate state at archive**: pass (2/2)
- **Test coverage signal**: 38 Vitest tests (含 AdminLayout, AppSidebar, AppHeader, AppBreadcrumb, menu, breadcrumb, theme, menuNavigation)

---

## 1. Wins

- [evidence: 9 test files, 38 tests] 前端组件测试覆盖全面，Layout/Sidebar/Header/Breadcrumb/Theme 均有测试
- [evidence: `useMenuNavigation.ts`, `useBreadcrumb.ts`] 业务逻辑抽取为 composable，组件保持轻量
- [evidence: `design-system.css`] 建立了设计系统 CSS 变量，为后续页面奠定基础
- [evidence: `menu.ts:createRoutesFromMenu`] 菜单配置驱动路由生成，新增页面只需改配置

## 2. Misses

- 🟡 [painful | evidence: `AppHeader.vue:31-39`] Header 一级菜单未渲染图标，与 spec 不一致
- 🟡 [painful | evidence: `AppHeader.vue:22-26`] Logo/Title 硬编码，未通过 props 传入
- 📌 [nit | evidence: `AppHeader.vue:47`] 消息通知 badge `v-if="false"` 占位，功能未实现
- 📌 [nit | evidence: `AdminLayout.vue:15`] 侧边栏折叠状态未持久化到 localStorage
- 📌 [nit | evidence: `AppSidebar.vue:23`] `toggleSidebar` 导入未使用，lint 报错

## 3. Plan deviations

| Plan task | What changed | Why |
|-----------|--------------|-----|
| 消息通知 | badge 用 v-if="false" 占位 | 后端通知接口尚未实现，前端预留位置 |
| 路由懒加载 | 大部分路由使用同步 PlaceholderView | 真实页面组件尚未开发，PlaceholderView 作为临时 stub |

## 4. Skill / workflow compliance

| Skill | Used |
|---|---|
| superpowers:brainstorming | ✓ |
| superpowers:writing-plans | ✓ |
| superpowers:using-git-worktrees | ✗ |
| superpowers:subagent-driven-development | ✗ |
| (transitive) superpowers:test-driven-development | ✓ |
| (transitive) superpowers:requesting-code-review | ✗ |
| superpowers:finishing-a-development-branch | — (pending) |

### Deliberately Skipped Skills

- **`superpowers:using-git-worktrees`**
  - **What was skipped**: 整个 skill
  - **Why this cycle**: 先前会话实现时用户未要求 worktree 隔离，直接在 main 分支开发
  - **How to prevent recurrence**: `one-off — schema boundary case` — 项目初期脚手架阶段，main 分支即开发分支

- **`superpowers:subagent-driven-development`**
  - **What was skipped**: 整个 skill
  - **Why this cycle**: 先前会话以交互式对话实现，前端 UI 组件需要即时视觉反馈
  - **How to prevent recurrence**: `scope-judgment rule` — 前端 UI 组件开发常需交互式调整，subagent 批量模式不适用

- **`superpowers:requesting-code-review`**
  - **What was skipped**: 整个 skill
  - **Why this cycle**: 使用 openspec-verify-change 替代，覆盖了 spec 合规检查
  - **How to prevent recurrence**: `skill description tightening` — verify 检查 spec 合规，code-review 检查代码质量，两者互补

## 5. Surprises

- shadcn-vue Sidebar 组件的 `collapsible="icon"` 模式自动处理折叠态 tooltip 和图标显示，无需手动实现
- `useSidebar` composable 需要 mock 才能在测试中使用，直接导入会因缺少 SidebarProvider 报错

## 6. Promote candidates → long-term learning

- [ ] 📌 **UI 占位功能用 prop 控制而非 v-if="false"** → **Promote to** `.opencode/rules/frontend.md`
  > **Why**: `v-if="false"` 是死代码，应使用 prop（如 `showNotification`）控制功能开关
  > **How to apply**: 所有预留功能位使用 prop 默认值控制，而非硬编码条件
