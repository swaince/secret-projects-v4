# Retrospective: frontend-dict-adapter

> Written: 2026-06-15 (after verify passed)
> Commit range: 未提交（单会话实现，尚未 commit）
> Worktree: E:\CodeRepo\AiCoder\secret-projects-v4（主工作目录，未使用 worktree）

---

## 0. Evidence

- **Commit range**: 无（尚未提交）
- **Diff size**: +约 600 行，跨 15 个文件（5 后端 + 10 前端）
- **Tasks done**: 25/25
- **Active hours**: ~1.5h（单会话）
- **Subagent dispatches**: 5（后端实现、前端实现、测试编写、DictView 集成、验证）
- **New external dependencies**: none
- **Bugs encountered post-merge**: none（未 merge）
- **OpenSpec validate state at archive**: not-run
- **Test coverage signal**: 后端 4 tests (DictItemByCodeTest) + 前端 15 tests (3 spec files)

---

## 1. Wins

- [evidence: verify.md 35/39 PASS] 前后端实现与 spec 高度对齐，首次验证仅 2 CRITICAL + 1 WARNING
- [evidence: pnpm type-check 零错误] 泛型类型系统（DictItem<K,V> + ValueTypeMap + Proxy accessor）编译期类型推导工作正确
- [evidence: 15 tests PASS] 前端测试覆盖了缓存命中/失效/转换降级等关键路径
- [evidence: DictController.java:114] 后端接口放在已有 DELETE 同路径 controller 上，路径一致性好

## 2. Misses

- 🔴 [blocking | evidence: verify.md CRITICAL #1] `listByDictId` 遗漏 `status=1` 过滤 — 已在 verify 后修复
- 🟡 [painful | evidence: 会话中 3 次修改 DictView] 对用户需求理解偏差：误解「DictView 直接走接口」的范围（下拉框 vs 表格数据），导致反复修改
- 🟡 [painful | evidence: api/dict.ts:87 首版] 前端 API URL 写成 `/dicts/code/${dictCode}`，与后端 `/dicts/items/by-code?dictCode=xxx` 不匹配，验证时才发现
- 📌 [nit | evidence: dict/index.ts] 注册表初始编码 `data_value_type` 写错，实际应为 `data_type`

## 3. Plan deviations

| Plan task | What changed | Why |
|-----------|--------------|-----|
| Task 3 (endpoint) | 放在 DictController 而非 DictItemController | DictController 已有同路径 DELETE 端点，保持一致 |
| Task 13 (DictView) | 从「直接 API」改为「accessor 缓存」再改回「accessor 仅下拉框」 | 对用户需求理解迭代 |
| 无计划 | DictView 数据类型列翻译 + 居中 | 用户追加需求 |

## 4. Skill / workflow compliance

| Skill | Used |
|-------|------|
| superpowers:brainstorming | ✓ |
| superpowers:writing-plans | ✓ |
| superpowers:using-git-worktrees | ✗ |
| superpowers:subagent-driven-development | ✗ |
| (transitive) superpowers:test-driven-development | ✗ |
| (transitive) superpowers:requesting-code-review | ✗ |
| superpowers:finishing-a-development-branch | ✗ |

### Deliberately Skipped Skills

- **`superpowers:using-git-worktrees`**
  - **What was skipped**: 整个 skill — 未创建隔离 worktree
  - **Why this cycle**: Windows 环境下 git worktree 对 Maven/pnpm 工具链兼容性差（node_modules 路径、.mvn wrapper 等），且项目处于早期开发阶段无并行分支冲突风险
  - **How to prevent recurrence**: scope-judgment rule — 单人早期项目 + Windows 环境 + 无并行开发时，worktree 隔离的收益不足以覆盖工具链兼容成本。多人协作或 CI 环境下应恢复使用

- **`superpowers:subagent-driven-development`**
  - **What was skipped**: 正式的 subagent-driven-development 流程（每 task 独立子代理 + 两阶段 review）
  - **Why this cycle**: 使用了 Task tool 并行分派（后端/前端/测试各一个子代理），但未遵循 skill 的逐 task review 流程。原因：25 个 tasks 中多数是顺序依赖的，逐 task 分派会产生大量上下文传递开销
  - **How to prevent recurrence**: skill description tightening — 当 tasks 有强顺序依赖时，允许按「task group」（而非逐 task）分派子代理，保留 group 粒度的 review checkpoint

- **`superpowers:test-driven-development`**
  - **What was skipped**: RED-GREEN-REFACTOR 严格顺序 — 实现代码先于测试编写
  - **Why this cycle**: 子代理并行实现时，后端/前端实现与测试在不同代理中同时进行，未强制 test-first
  - **How to prevent recurrence**: CLAUDE.md trigger — 在 AGENTS.md 中明确：即使并行分派，每个子代理内部仍须 test-first；或将测试任务合并到实现任务的 prompt 中

- **`superpowers:requesting-code-review`**
  - **What was skipped**: 整个 skill — 无 code-review 子代理分派
  - **Why this cycle**: verify artifact 已覆盖 spec 合规性检查，发现了 2 个 CRITICAL 问题并修复。实际上 verify 替代了 code-review 的部分职能
  - **How to prevent recurrence**: one-off — schema boundary case。verify artifact 在本 schema 中已包含 correctness 维度检查，与 requesting-code-review 功能重叠。若后续 verify 不够深入（如漏掉代码风格/安全问题），再恢复 code-review

- **`superpowers:finishing-a-development-branch`**
  - **What was skipped**: 整个 skill — 用户未要求 commit/PR
  - **Why this cycle**: 用户明确未要求提交代码，按 AGENTS.md 规范「NEVER commit unless explicitly asked」
  - **How to prevent recurrence**: scope-judgment rule — 此为正确行为，用户未要求 commit 时不应触发 finishing 流程

## 5. Surprises

- 用户对「接入字典」的理解是「业务页面的下拉框走字典缓存」，而非「字典管理页本身走缓存」— 字典管理页的表格数据直接走 CRUD 接口，只有下拉框选项才走字典消费层
- `listByDictId` 被多个功能复用（管理页展示 + by-code 查询），添加 status 过滤会影响管理页（管理页需要看到禁用项）— 但实际确认后发现管理页 DictItemDrawer 也应只显示启用项，所以全局加 status 过滤是正确的
- 前端 oxlint 和 eslint 对 mock 函数的规则冲突（`require-mock-type-parameters` vs `no-explicit-any`），需要用具体函数类型而非 `any`

## 6. Promote candidates → long-term learning

- [ ] 🟡 **后端接口 URL 必须从 Controller 源码确认后再写前端 API 函数** → **Promote to memory** (type: feedback)
  > **Why**: 本次前端 API URL 凭记忆写错（`/dicts/code/` vs `/dicts/items/by-code`），verify 才发现
  > **How to apply**: 编写前端 API 函数前，先 grep 或 read 对应 Controller 的 @XxxMapping 注解确认实际路径

- [ ] 🟡 **字典管理页 vs 业务页面的字典使用方式不同** → **Promote to project CLAUDE.md** (`AGENTS.md` 前端技术要点段)
  > **Why**: 本次 3 次修改 DictView 才确认正确边界 — 管理页表格走 CRUD API，下拉框走字典缓存
  > **How to apply**: 涉及字典页面修改时，先确认「这是管理页还是消费页」再决定数据来源

- [ ] 📌 **oxlint + eslint 双重 lint 对 vi.fn() 的规则冲突** → **One-off** (记录即可)
  > **Why**: 仅影响测试文件的 mock 写法，解决方案已明确（用具体函数类型替代 any），不泛化
