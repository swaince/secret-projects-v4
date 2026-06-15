# Retrospective: audit-logging

> Written: 2026-06-15 (after verify passed)
> Commit range: 未提交
> Worktree: E:\CodeRepo\AiCoder\secret-projects-v4

---

## 0. Evidence

- **Commit range**: 无（尚未提交）
- **Diff size**: +约 3500 行，跨 30+ 文件
- **Tasks done**: 20/20
- **Active hours**: ~2h
- **Subagent dispatches**: 4（后端 + 前端 + 请求方式过滤 + 详情优化）
- **New external dependencies**: none
- **Bugs encountered post-merge**: none
- **OpenSpec validate state at archive**: not-run
- **Test coverage signal**: 4 tests (AuditLogTest)

---

## 1. Wins

- [evidence: 1 个 Filter + 1 个 Interceptor 覆盖全部日志需求] 分层采集访问日志和操作日志，requestId 关联
- [evidence: @Async + 专用线程池] 日志写入零业务性能影响
- [evidence: verify 12/12 PASS] 首次验证即全部通过

## 2. Misses

- 🟡 [painful | evidence: Tailwind v4 无标准色板] 方法/状态码颜色经历 3 次修改才生效（Badge 不合并 class → 标准色不存在 → @theme inline 需定义变量）
- 🟡 [painful | evidence: createAt→createdAt 全栈重命名] 实体命名与项目惯例不一致，需修改 6 个文件
- 📌 [nit | evidence: 详情布局 5 次调整] 用户对布局细节反复调整（时间独占一行、处理器位置、用户名与时间同行）

## 3. Plan deviations

| Plan task | What changed | Why |
|-----------|--------------|-----|
| 无计划 | RequestMethod 枚举 + 字典 | 用户追加需求 |
| 无计划 | 方法/状态码颜色区分 | 用户追加需求 |
| 无计划 | 详情弹窗多次重构 | 用户持续优化布局 |
| 无计划 | multipart 请求跳过 body | 用户反馈 |

## 4. Skill / workflow compliance

同前，小变更压缩流程。

## 5. Surprises

- Tailwind CSS v4 的 `@theme inline` 只覆盖 shadcn 色板，标准色（green/blue/orange/red）不存在，需在 `design-system.css` 自定义变量
- Badge 组件不透明合并 `:class` prop，需改为 `<span>` 直接上 Tailwind
- Flyway 版本号需注意与其他变更的碰撞

## 6. Promote candidates → long-term learning

- [x] 🟡 **Tailwind v4 自定义颜色必须在 @theme inline 中定义** → **Promote to memory**
  > **Why**: 经历 3 次修改才知道 bg-green-100 在 Tailwind v4 中不存在
  > **How to apply**: 使用非 shadcn 颜色时，先在 design-system.css 的 @theme inline 定义变量
