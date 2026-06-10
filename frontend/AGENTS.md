# Frontend

Vue 3.5 · Vite 8 · TypeScript 6 · Tailwind CSS v4 · shadcn-vue · pnpm

## 开发命令

```bash
# 安装依赖（首次）
pnpm install

# 启动开发服务器
pnpm dev

# 类型检查
pnpm type-check

# Lint + 自动修复
pnpm lint

# 格式化
pnpm format

# 构建（先 type-check，再 vite build）
pnpm build

# 单元测试（Vitest）
pnpm test:unit

# 运行单个测试
pnpm test:unit -- path/to/test.spec.ts

# E2E 测试（Playwright）— 首次需安装浏览器
npx playwright install
pnpm test:e2e
```

## 组件库规范

**必须使用 shadcn-vue**，或基于 UI 组件库二次封装，禁止裸写 UI 组件。
shadcn-vue 底层依赖 reka-ui 基元组件，**任何组件使用前必须先查阅对应的 reka-ui 组件文档**，避免编写错误代码。

**`src/styles/design-system.css` 作为设计系统，具有最终决定权**。所有颜色、字体、圆角、阴影、间距等样式变量以此文件为准，禁止在组件中硬编码样式值。

### 使用流程

1. **搜索组件**：使用 `shadcnVue_search_items_in_registries` 查询可用组件
2. **安装组件**：使用 `shadcnVue_get_add_command_for_items` 获取安装命令并执行
3. **查看源码**：使用 `shadcnVue_view_items_in_registries` 查看组件详细信息和文件内容
4. **查看示例**：使用 `shadcnVue_get_item_examples_from_registries` 获取完整示例代码
5. **查 reka-ui 文档**：前往 [reka-ui 文档](https://reka-ui.com/llms.txt) 查看底层基元组件 API

### 写代码前强制输出

使用新组件、封装新组件、或用不熟悉的 API 时，**先输出以下三行再写代码**，否则停：

1. **查了哪个文档？**（shadcn-vue MCP / Reka UI llms.txt / 哪个页面）
2. **API 是什么？**（props / slots / events / v-model 绑定方式）
3. **边界在哪里？**（展示组件只管渲染 emit 原始值；容器组件管理状态和转换）

封装组件前，额外问自己：**这个组件最小可能的 API 面是什么？** 能用一个 `v-model` 传对象就不拆成五个 prop；能从容器传文本就不让组件自己判断含义。

**禁止根据经验猜测，所有使用方法必须有文档支撑。**

## 技术要点

- **包管理器**: pnpm（不是 npm），锁文件 `pnpm-lock.yaml`。
- **Tailwind CSS v4**: CSS-first 配置（`src/styles/tailwind.css`），无 `tailwind.config.js`。通过 `@tailwindcss/vite` 插件集成。
- **路径别名**: `@` → `./src`（vite.config.ts 和 tsconfig.json 均已配置）。
- **类型检查**: 使用 `vue-tsc`（不是 `tsc`），因为需要处理 `.vue` 文件类型。
- **Prettier**: 无分号，单引号，printWidth 100。集成 `prettier-plugin-tailwindcss`。
- **ESLint + Oxlint**: 双重检查，`pnpm lint` 顺序执行 oxlint 和 eslint。
- **路由**: `vue-router` 已安装但 `routes` 数组为空。`App.vue` 中无 `<RouterView />`，待添加。
- **Pinia**: Setup Store 语法（Composition API 风格）。
- **目前无 `components/`、`views/`、`pages/` 目录**，项目处于脚手架初始状态。
