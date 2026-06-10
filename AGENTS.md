# secret-projects-v4

Maven 多模块项目，含 `backend`（Spring Boot）和 `frontend`（Vue 3）。

## 构建与运行

```bash
# 全量构建
./mvnw clean package

# 前端开发用 pnpm，Maven 包装仅用于生产构建
# 日常前端开发直接在 frontend/ 下操作
```

## OpenSpec 工作流

默认 schema: `superpowers-bridge`（`openspec/config.yaml`）。
用 `openspec new` 创建变更，按 artifact 流程推进。

## 开发原则

**强制 TDD（测试驱动开发）**：先写测试，再写实现代码。没有测试，不写代码。

**文档语言**：所有生成的文档必须使用简体中文。

## 代码索引

CodeGraph MCP 已启用（`opencode.jsonc`），可用 `codegraph_explore` 查询符号引用。
