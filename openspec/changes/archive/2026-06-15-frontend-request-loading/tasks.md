## 1. Loading Store

- [x] 1.1 创建 `src/stores/loading.ts` Pinia setup store（count, isLoading, start, end, reset）

## 2. Axios 拦截器扩展

- [x] 2.1 扩展 `HttpClient` 方法签名，get/post/put/delete 支持可选的 `options?: { silent?: boolean }` 参数
- [x] 2.2 请求拦截器：非 silent 时调用 `useLoadingStore().start()`
- [x] 2.3 响应拦截器：非 silent 时调用 `useLoadingStore().end()`
- [x] 2.4 错误拦截器：非 silent 时调用 `useLoadingStore().end()`

## 3. LoadingOverlay 组件

- [x] 3.1 创建 `src/components/common/LoadingOverlay.vue`（fixed 全屏遮罩 + spinner）
- [x] 3.2 App.vue 挂载 `<LoadingOverlay />`

## 4. 字典请求 silent

- [x] 4.1 `fetchDictByCode` 调用传入 `{ silent: true }`

## 5. 测试与验证

- [x] 5.1 编写 `useLoadingStore` 单元测试（start/end/并发/不低于零）
- [x] 5.2 运行 `pnpm type-check` 和 `pnpm lint` 确认无错误
