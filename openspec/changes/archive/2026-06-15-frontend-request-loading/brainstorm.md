# 前端接口请求 Loading — 头脑风暴记录

## 背景

项目前端通过 `src/utils/request.ts` 的 axios HttpClient 发起所有 HTTP 请求。当前没有全局 loading 机制，用户发起操作后无视觉反馈，也无法阻止重复操作。

## 决议链

### Q1：Loading 形式？

**决定**：全局遮罩层（全屏半透明蒙层 + 居中 spinner）。

否决方案：
- 顶部进度条（NProgress）— 视觉反馈弱，不阻止交互
- 按钮级 loading — 侵入性强，每个按钮都要改
- 组合方案 — YAGNI，先实现最核心的全局遮罩

### Q2：触发策略？

**决定**：默认所有请求触发，特定请求可通过 `{ silent: true }` 排除。

排除场景：字典缓存查询、轮询、后台静默刷新等不需要用户感知的请求。

### Q3：延迟策略？

**决定**：不延迟，请求发出立即显示遮罩。

理由：用户明确不需要防闪烁逻辑，保持实现简单。

### Q4：架构方案？

**决定**：Axios 拦截器 + Pinia store + 全局遮罩组件（方案 A）。

否决方案：
- 方案 B（模块级 ref）— DevTools 不可见，与项目 Pinia 惯例不一致
- 方案 C（请求级 composable）— 侵入现有代码，不满足「全局自动」需求

## 设计概要

### 文件结构

```
src/
├── stores/loading.ts         ← Pinia store（count + isLoading）
├── utils/request.ts          ← 扩展拦截器，支持 silent 配置
├── components/common/
│   └── LoadingOverlay.vue    ← 全局遮罩组件
└── App.vue                   ← 挂载 <LoadingOverlay />
```

### Loading Store

- `count: ref(0)` — 活跃请求计数器
- `isLoading: computed(() => count > 0)` — 是否显示遮罩
- `start()` — count++
- `end()` — count-- (最小为 0)
- `reset()` — 强制归零（异常恢复）

### 拦截器机制

- 请求拦截器：若 `config.silent !== true` → `store.start()`
- 响应拦截器：若非 silent → `store.end()`
- 错误拦截器：若非 silent → `store.end()`
- 并发请求通过计数器自动合并（多个请求同时进行，遮罩持续到最后一个完成）

### 排除机制

HttpClient 方法签名扩展，支持传入 `silent` 选项：
- `http.get(url, params, { silent: true })` — 不触发 loading
- `fetchDictByCode` 等缓存类请求默认传 silent

### LoadingOverlay 组件

- 全屏半透明遮罩（fixed, inset-0, z-[9999]）
- 居中 spinner（animate-spin SVG）
- `v-if="isLoading"` 控制显示/隐藏
- 使用 Tailwind 工具类实现，不引入第三方 loading 库

### App.vue 挂载

在 App.vue 根级别挂载 `<LoadingOverlay />`，位于 `<RouterView />` 之前/之后均可。
