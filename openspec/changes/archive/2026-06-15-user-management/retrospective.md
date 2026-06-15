# Retrospective: user-management

> Written: 2026-06-15 (after verify passed)
> Commit range: 未提交
> Worktree: E:\CodeRepo\AiCoder\secret-projects-v4

---

## 0. Evidence

- **Commit range**: 无（尚未提交）
- **Diff size**: +约 1500 行，跨 20+ 文件
- **Tasks done**: 14/14
- **Active hours**: ~1h
- **Subagent dispatches**: 2（后端 + 前端并行）
- **New external dependencies**: spring-security-crypto (BCrypt), @internationalized/date (Calendar)
- **Bugs encountered post-merge**: none
- **OpenSpec validate state at archive**: not-run
- **Test coverage signal**: 后端 5 tests (UserControllerTest), 前端无新增测试

---

## 1. Wins

- [evidence: 并行 subagent 一次完成] 后端 + 前端同时实现，零返工
- [evidence: verify 后修复 2 CRITICAL] 验证流程有效发现密码泄露和索引问题
- [evidence: Gender 枚举 + @Dictionary] 字典自动同步机制复用成功

## 2. Misses

- 🟡 [painful | evidence: verify CRITICAL #1] `@JsonProperty(WRITE_ONLY)` 导致测试序列化失败，改用 `@JsonInclude(NON_NULL)` 才兼容测试和响应排除
- 🟡 [painful | evidence: H2 不支持 WHERE 部分索引] Flyway 迁移使用 PostgreSQL 特有语法（WHERE 条件索引），H2 测试不兼容
- 📌 [nit | evidence: Flyway 版本号 V1.0.0.6] 子代理自行选择版本号，可能与实际数据库已跑过的版本冲突

## 3. Plan deviations

| Plan task | What changed | Why |
|-----------|--------------|-----|
| Task 2 DTO | 未创建独立 UserCreateDTO | 复用 UserDTO + 分组校验，与项目现有模式一致 |
| Task 8 页面 | 新增 displayName 字段 + Calendar 日期选择器 | 用户追加需求 |
| 无计划 | 添加 Gender 枚举 | 字典表中无 gender 数据，需枚举驱动自动同步 |
| 无计划 | 搜索表单重构为对象 + resetSearch 函数 | 代码质量改善 |
| 无计划 | a11y 修复（aria-label） | web-design-guidelines 审查发现 |

## 4. Skill / workflow compliance

| Skill | Used |
|-------|------|
| superpowers:brainstorming | ✓ (简化版) |
| superpowers:writing-plans | ✗ |
| superpowers:using-git-worktrees | ✗ |
| superpowers:subagent-driven-development | ✗ |
| (transitive) superpowers:test-driven-development | ✗ |
| (transitive) superpowers:requesting-code-review | ✗ |
| superpowers:finishing-a-development-branch | ✗ |

### Deliberately Skipped Skills

- 同 frontend-request-loading retrospective，理由一致：标准 CRUD 模块，模式成熟，单会话完成，verify + web-design-guidelines 审查替代了 code-review。

## 5. Surprises

- `@JsonProperty(access = WRITE_ONLY)` 会阻止测试中的 DTO 序列化——测试复用同一 DTO 做请求体时不兼容
- H2 不支持 PostgreSQL 的 `WHERE` 条件索引——需要所有迁移保持跨数据库兼容或用条件判断
- shadcn-vue Calendar 组件依赖 `@internationalized/date`，需手动安装（CLI 未自动添加）

## 6. Promote candidates → long-term learning

- [ ] 🟡 **DTO 密码字段用 `@JsonInclude(NON_NULL)` + MapStruct ignore，不用 WRITE_ONLY** → **Promote to memory** (type: feedback)
  > **Why**: WRITE_ONLY 会破坏测试中对同一 DTO 的序列化；NON_NULL 配合 MapStruct 设 null 更安全
  > **How to apply**: 凡是需要「请求接收但响应排除」的字段，用 NON_NULL + MapStruct ignore 模式

- [ ] 🟡 **Flyway 迁移必须兼容 H2（禁用 WHERE 条件索引等 PG 特有语法）** → **Promote to project AGENTS.md** (`backend/AGENTS.md` Flyway 段)
  > **Why**: 本次 WHERE 索引导致全部 5 测试 ERROR
  > **How to apply**: 编写 Flyway 迁移时，避免 PostgreSQL 特有语法（部分索引、FILTER、ON CONFLICT 等）

- [ ] 📌 **shadcn-vue CLI add 不自动安装所有 peer deps** → **One-off**
  > **Why**: Calendar 需要 @internationalized/date 但 CLI 未安装，需手动 pnpm add
