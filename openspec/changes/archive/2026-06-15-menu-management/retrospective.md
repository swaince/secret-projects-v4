# Retrospective: menu-management

> Written: 2026-06-15 (after verify passed)
> Commit range: 未提交
> Worktree: E:\CodeRepo\AiCoder\secret-projects-v4

---

## 0. Evidence

- **Commit range**: 无（尚未提交）
- **Diff size**: +约 3000 行，跨 30+ 文件（含布局改造）
- **Tasks done**: 18/18
- **Active hours**: ~1.5h
- **Subagent dispatches**: 3（后端 + 前端 + 修复）
- **New external dependencies**: none
- **Bugs encountered post-merge**: none
- **OpenSpec validate state at archive**: not-run
- **Test coverage signal**: 后端 8 tests (MenuControllerTest)

---

## 1. Wins

- [evidence: 前端动态菜单一次实现成功] 替换 menu.ts + 动态路由 + 布局改造一次完成，type-check + lint 通过
- [evidence: 8/8 tests pass] 后端树查询/级联删除/parentId 校验/空树全部覆盖
- [evidence: V1.0.1.1 按钮数据] 6 个页面的按钮权限标识一次性初始化完毕

## 2. Misses

- 🟡 [painful | evidence: V1.0.0.9 checksum 冲突] 修改已应用的 Flyway 迁移导致 duplicate key 错误，需创建新迁移修复
- 🟡 [painful | evidence: V1.0.0.9 component=NULL] 菜单配置的 component 字段遗漏，导致路由缺组件警告
- 📌 [nit | evidence: toMenuItems] 侧边栏未过滤按钮类型菜单，需追加 `menuType !== 'B'` 过滤

## 3. Plan deviations

| Plan task | What changed | Why |
|-----------|--------------|-----|
| Task 5 数据初始化 | 菜单配置 component 为 NULL | 子代理遗漏，后续 V1.0.1.0 修复 |
| 无计划 | 新增 V1.0.1.1 按钮权限数据 | 用户追加需求 |
| 无计划 | 级联删除优化为单次查询 | verify WARNING 修复 |
| 无计划 | 侧边栏过滤按钮 | 用户反馈 |

## 4. Skill / workflow compliance

同前两次 retrospective，小变更压缩流程。

## 5. Surprises

- Flyway 已执行的迁移绝不能修改内容（checksum 校验），只能新增迁移修复
- 动态菜单的 `import.meta.glob` 路径必须精确匹配（含 `/src/views/` 前缀）
- 按钮类型菜单需要在多个地方过滤：路由生成 + 侧边栏渲染

## 6. Promote candidates → long-term learning

- [x] 🟡 **Flyway 迁移已应用后禁止修改** → **Promoted to memory** (已在 dict-adapter retro 提出，本次再次触发)
  > **Why**: 两次犯同一错误（修改已执行的迁移 → checksum 冲突 / duplicate key）
  > **How to apply**: 任何数据修正必须创建新版本号迁移，永远不改已有 .sql 文件

- [ ] 🟡 **数据迁移脚本必须为所有 menuType=M 的菜单填充 component 字段** → **Promote to memory**
  > **Why**: NULL component 导致前端路由无组件，出现 Vue Router 警告
  > **How to apply**: 编写菜单数据迁移时，逐条检查 M 类型是否有对应 component 值
