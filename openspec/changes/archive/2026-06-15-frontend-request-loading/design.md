## Context

项目前端使用 axios HttpClient（`src/utils/request.ts`）发起所有请求。当前无全局 loading 机制，用户操作后无视觉反馈，无法阻止重复操作。现有页面各自管理局部 loading 状态，不统一。

约束：
- 前端：Vue 3.5 + Pinia (setup store) + Tailwind CSS v4 + shadcn-vue
- HTTP 层已有响应拦截器（解包 `R<T>`、错误 toast）
- 字典请求（`fetchDictByCode`）等不应触发 loading

## Goals / Non-Goals

**Goals:**
- 全局遮罩层：请求进行中显示半透明蒙层 + spinner，阻止用户交互
- 自动触发：所有请求默认触发，无需业务代码改动
- 可排除：通过 `{ silent: true }` 选项跳过特定请求
- 并发合并：多个并发请求只显示一个遮罩，全部完成后隐藏

**Non-Goals:**
- 不做顶部进度条（NProgress）
- 不做按钮级 loading（各页面可自行实现，不在本变更范围）
- 不做延迟显示（无防闪烁逻辑）
- 不做请求超时自动隐藏

## Decisions

### D1：状态管理方式

- **选择**：Pinia setup store（`useLoadingStore`）
- **理由**：与项目现有 Pinia 惯例一致，DevTools 可观察
- **已考虑 alternative**：模块级 ref — DevTools 不可见 → 拒绝

### D2：计数器机制

- **选择**：`count` 整数计数器，`isLoading = count > 0`
- **理由**：天然支持并发请求合并，无需额外去重逻辑
- **已考虑 alternative**：Set<requestId> — 过度设计 → 拒绝

### D3：排除机制

- **选择**：axios config 扩展 `silent: boolean` 字段
- **理由**：零侵入，调用时按需传入即可
- **已考虑 alternative**：URL 白名单 — 维护成本高，新接口容易遗漏 → 拒绝

### D4：遮罩组件实现

- **选择**：Tailwind 工具类实现，固定定位全屏遮罩 + SVG spinner
- **理由**：无需引入第三方 loading 库，保持依赖精简
- **已考虑 alternative**：shadcn-vue 无内置 loading 组件；引入 vue-loading-overlay 等第三方库 — 过重 → 拒绝

### D5：挂载位置

- **选择**：App.vue 根级别
- **理由**：全局覆盖，不受路由切换影响

## Risks / Trade-offs

[Risk] 某些长时间请求（如文件上传）会持续显示遮罩 → Mitigation: 这类请求应传 `silent: true` 并自行管理进度 UI

[Trade-off] 立即显示无延迟，快速请求可能感知到闪烁 → 接受：用户明确要求无延迟，且全局遮罩的视觉权重足够，短暂显示不算干扰

[Trade-off] 所有请求默认触发，可能在某些场景过度阻塞 → 接受：通过 silent 排除机制解决

## Migration Plan

1. 创建 `stores/loading.ts`
2. 扩展 `utils/request.ts` 拦截器 + HttpClient 方法签名
3. 创建 `components/common/LoadingOverlay.vue`
4. App.vue 挂载组件
5. 字典相关请求加 `silent: true`

回滚：删除 store + 组件 + 拦截器改动即可，不影响其他功能。

## Open Questions

无
