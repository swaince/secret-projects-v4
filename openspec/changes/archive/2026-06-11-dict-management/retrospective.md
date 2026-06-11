# Retrospective: dict-management

> Written: 2026-06-11 (after verify passed)
> Commit range: uncommitted (all changes in worktree on main)
> Worktree: `/Users/relax/CodeRepo/AiCoding/secret-projects-v4` (main branch, no worktree)

---

## 0. Evidence

- **Commit range**: uncommitted — 所有变更在 worktree 中待提交
- **Diff size**: +235 / -58 lines across 18 files（本次会话增量；后端主体代码在先前会话中已实现）
- **Tasks done**: 41/41 (`tasks.md` 全部 `[x]`)
- **Active hours**: ~3h（单次会话）
- **Subagent dispatches**: 5（探索前端结构、验证 spec 覆盖等）
- **New external dependencies**: shadcn-vue 组件（table, dialog, alert-dialog, pagination, checkbox, select, label, card, switch）— 均为 shadcn-vue CLI 安装的本地组件，无新 npm 包
- **Bugs encountered post-merge**: none（尚未 merge）
- **OpenSpec validate state at archive**: pass (2/2 items valid)
- **Test coverage signal**: Backend 29 tests (JUnit 5 + H2), Frontend 38 tests (Vitest + jsdom)

Commit chain: 尚未提交，变更集中在单次会话。

---

## 1. Wins

- [evidence: `DictServiceTest`, `DictItemServiceTest`, `DictControllerTest`, `DictItemControllerTest` — 29 tests] 后端 TDD 完整覆盖，Service + Controller 层均有测试，验证阶段发现的 3 个缺失端点和 1 个唯一性校验全部通过 TDD 补齐
- [evidence: `DictIndex.spec.ts` — 4 tests] 前端也遵循 TDD，先写测试确认 RED，再实现确认 GREEN
- [evidence: reka-ui Checkbox 文档查阅] 查阅 reka-ui 官方文档纠正了 Checkbox API 用法（`modelValue` 而非 `checked`），避免了运行时 bug
- [evidence: `shadcn-vue/dist/tailwind.css`] 通过阅读 shadcn-vue 的自定义变体源码，找到半选样式不生效的根因（缺少 `data-indeterminate` 变体），用 `data-[state=indeterminate]:` 任意变体解决
- [evidence: `.opencode/rules/frontend.md`] 将页面开发中积累的 13 个模块规则沉淀为前端规则文件，后续页面可复用
- [evidence: `frontend/AGENTS.md` 新增规则] 确立「禁止修改 `src/components/ui/` 组件库源码」规则，通过 class/slot 定制

## 2. Misses

- 🟡 [painful | evidence: verify 第一轮发现 3 CRITICAL] 首轮实现遗漏了 2 个 spec 端点（`DELETE /items/all`、`DELETE /items/by-code`）和 dictCode 唯一性校验。原因：实现时未逐条对照 spec 场景，依赖 tasks.md 粒度不够细
- 🟡 [painful | evidence: Checkbox `checked` → `modelValue` 修复] 初始实现使用了错误的 Checkbox API（`:checked` + `@update:checked`），因为未先查阅 reka-ui 文档。直到用户反馈「checkbox 用法不对」才发现
- 📌 [nit | evidence: `selectByIdNotDeleted` NPE → 404] 后端 `selectByIdNotDeleted()` 返回 null 时未抛异常，导致调用方 NPE → 500。应在编写时就考虑防御性编程
- 📌 [nit | evidence: design.md D4] design.md 写 common 包，实际实现在 system 包，直到 verify 才发现偏离

## 3. Plan deviations

| Plan task | What changed | Why |
|-----------|--------------|-----|
| 2.5-2.6 | 计划创建独立 Request 类，实际使用 DictDTO + 校验分组 | 后端先前会话已采用此模式，保持一致性 |
| D4 包位置 | 计划 common 包，实际 system 包 | 字典 CRUD 属于系统管理业务代码，common 仅放通用基础设施 |
| 8.5 路由 | 计划直接添加路由，实际扩展了 MenuItem 接口支持 component 字段 | 需要让特定路由使用真实组件而非 PlaceholderView |
| Jackson 日期格式 | 计划外新增 | 用户反馈日期格式不正确，`date-format` 仅对 `java.util.Date` 生效，需 `Jackson2ObjectMapperBuilderCustomizer` |

## 4. Skill / workflow compliance

| Skill | Used |
|---|---|
| superpowers:brainstorming | ✓ (prior session) |
| superpowers:writing-plans | ✓ (prior session) |
| superpowers:using-git-worktrees | ✗ |
| superpowers:subagent-driven-development | ✗ |
| (transitive) superpowers:test-driven-development | ✓ (partial) |
| (transitive) superpowers:requesting-code-review | ✗ |
| superpowers:finishing-a-development-branch | — (pending) |

### Deliberately Skipped Skills

- **`superpowers:using-git-worktrees`**
  - **What was skipped**: 整个 skill — 未创建隔离 worktree
  - **Why this cycle**: 用户在 apply 阶段明确指示「不使用git worktree」（直接回复文字），属于用户显式覆盖
  - **How to prevent recurrence**: `one-off — schema boundary case` — 用户指令优先级高于 schema 默认流程，此场景无需预防。若后续 cycle 用户未明确拒绝，则默认执行 worktree 创建

- **`superpowers:subagent-driven-development`**
  - **What was skipped**: 整个 skill — 未使用 subagent 执行 plan.md 微任务
  - **Why this cycle**: 后端代码在进入本次会话时已全部实现（41 tasks 中 4 个已标记完成，实际文件全部存在），剩余工作仅为前端实现 + 验证修复。用户以交互式对话方式逐步指导 UI 调整（布局、按钮、图标、颜色等），不适合 subagent 批量执行
  - **How to prevent recurrence**: `scope-judgment rule` — 当 apply 阶段进入时，若后端已完成且前端需要交互式 UI 调整，subagent-driven-development 的批量执行模式不适用。可在 schema instruction 中增加判断：「若剩余任务主要为 UI 调整类且用户以交互模式工作，可降级为直接执行」

- **`superpowers:requesting-code-review`**
  - **What was skipped**: 整个 skill — 未在任务完成后分派 code-reviewer subagent
  - **Why this cycle**: 使用了 `openspec-verify-change` skill 进行了两轮完整验证（覆盖 completeness / correctness / coherence 三个维度），功能上替代了 code review 的 spec 合规检查。但缺少代码质量层面的 review（如命名、模式一致性）
  - **How to prevent recurrence**: `skill description tightening` — verify-change 检查 spec 合规，requesting-code-review 检查代码质量，两者互补而非替代。应在 schema instruction 中明确：verify 通过后仍需 code-review

## 5. Surprises

- reka-ui Checkbox 的 prop 名是 `modelValue` 而非 `checked`，且事件是 `update:modelValue` 而非 `update:checked`。shadcn-vue 的 Checkbox 组件包装层没有重命名这些 prop
- shadcn-vue 的 `tailwind.css` 定义了 `data-checked` 自定义变体（匹配 `[data-state="checked"]`），但没有定义 `data-indeterminate` 变体，需要用 Tailwind v4 任意变体 `data-[state=indeterminate]:` 手动匹配
- Spring Boot 的 `spring.jackson.date-format` 配置仅对 `java.util.Date` 生效，对 `LocalDateTime` 无效，需要通过 `Jackson2ObjectMapperBuilderCustomizer` 注册 `LocalDateTimeSerializer`
- SheetContent 组件内置 `data-[side=right]:sm:max-w-sm` 限制最大宽度，自定义宽度需用相同 `data-[side=right]:` 前缀覆盖

## 6. Promote candidates → long-term learning

- [ ] 🔴 **使用 shadcn-vue / reka-ui 组件前必须查文档，禁止凭经验猜测 API** → **Promote to** `frontend/AGENTS.md`
  > **Why**: Checkbox 使用 `checked` 而非 `modelValue` 导致功能不生效，浪费了多轮调试。reka-ui 的 API 与常见 UI 库（如 Element Plus）差异大
  > **How to apply**: 每次使用新的 shadcn-vue 组件时，先输出三行检查（查了哪个文档、API 是什么、边界在哪里），已写入 `frontend/AGENTS.md`

- [ ] 🟡 **实现完成后必须逐条对照 spec 场景验证覆盖** → **Promote to** `.opencode/rules/WORKFLOW.md`
  > **Why**: 首轮 verify 发现 3 个 CRITICAL 遗漏（2 个端点 + 1 个校验），均因未逐条对照 spec 而遗漏
  > **How to apply**: apply 阶段每完成一个模块（如 Controller），立即对照 spec 中对应 Requirement 的所有 Scenario 逐条打勾

- [ ] 🟡 **后端 selectById 类方法必须处理 null → 抛 NOT_FOUND** → **Promote to** springboot-web-dev skill (`references/service.md`)
  > **Why**: `selectByIdNotDeleted` 返回 null 导致下游 NPE → 500，spec 要求 404
  > **How to apply**: Service 层所有 selectById / selectOne 方法，null 时抛 `OuterException(BizCode.NOT_FOUND)`

- [ ] 📌 **覆盖 shadcn-vue 组件内置样式需使用相同 data-attribute 修饰符前缀** → **Promote to** `.opencode/rules/frontend.md`
  > **Why**: SheetContent 和 Pagination 组件的内置样式使用 `data-[side=right]:` 等修饰符，普通 class 无法覆盖
  > **How to apply**: 已写入 `.opencode/rules/frontend.md` Sheet/Drawer 和 Pagination 章节
