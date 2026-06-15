# request-loading

前端全局请求 loading 遮罩机制。

## ADDED Requirements

### Requirement: Loading Store 计数器
`src/stores/loading.ts` SHALL 导出 Pinia setup store `useLoadingStore`，通过计数器管理全局 loading 状态。

#### Scenario: 单个请求触发 loading
- **WHEN** 一个非 silent 请求发出
- **THEN** `isLoading` SHALL 为 `true`

#### Scenario: 请求完成后关闭 loading
- **WHEN** 唯一活跃的请求完成（成功或失败）
- **THEN** `isLoading` SHALL 为 `false`

#### Scenario: 并发请求合并
- **WHEN** 3 个非 silent 请求同时进行，其中 2 个已完成
- **THEN** `isLoading` SHALL 仍为 `true`（直到第 3 个完成）

#### Scenario: count 不低于零
- **WHEN** `end()` 被调用但 count 已为 0
- **THEN** count SHALL 保持 0，不变为负数

### Requirement: Axios 拦截器自动触发
`src/utils/request.ts` 的 axios 实例 SHALL 通过请求/响应拦截器自动调用 loading store。

#### Scenario: 普通请求触发 start
- **WHEN** 发起 `http.get('/dicts')` 无 silent 配置
- **THEN** 请求拦截器 SHALL 调用 `store.start()`

#### Scenario: 响应成功触发 end
- **WHEN** 请求成功返回
- **THEN** 响应拦截器 SHALL 调用 `store.end()`

#### Scenario: 响应错误触发 end
- **WHEN** 请求返回错误（网络错误或业务错误）
- **THEN** 错误拦截器 SHALL 调用 `store.end()`

### Requirement: Silent 排除机制
HttpClient SHALL 支持 `silent` 选项，标记为 silent 的请求不触发 loading。

#### Scenario: silent 请求不触发 loading
- **WHEN** 发起 `http.get('/dicts/items/by-code', params, { silent: true })`
- **THEN** 请求拦截器 SHALL 不调用 `store.start()`

#### Scenario: silent 请求响应不触发 end
- **WHEN** silent 请求完成
- **THEN** 响应拦截器 SHALL 不调用 `store.end()`

### Requirement: LoadingOverlay 全局遮罩组件
`src/components/common/LoadingOverlay.vue` SHALL 在 `isLoading` 为 true 时显示全屏遮罩。

#### Scenario: loading 时显示遮罩
- **WHEN** `isLoading` 为 `true`
- **THEN** SHALL 显示全屏半透明遮罩层 + 居中 spinner

#### Scenario: 非 loading 时隐藏
- **WHEN** `isLoading` 为 `false`
- **THEN** SHALL 不渲染遮罩

#### Scenario: 遮罩阻止交互
- **WHEN** 遮罩显示时
- **THEN** 遮罩 SHALL 覆盖整个视口且 z-index 足够高，阻止底层元素交互

### Requirement: App.vue 挂载
`src/App.vue` SHALL 挂载 `<LoadingOverlay />` 组件。

#### Scenario: 组件存在于 DOM
- **WHEN** 应用启动
- **THEN** `LoadingOverlay` SHALL 存在于 App.vue 的模板中

### Requirement: 字典请求默认 silent
`src/api/dict.ts` 中的 `fetchDictByCode` SHALL 传入 `{ silent: true }`。

#### Scenario: 字典查询不触发 loading
- **WHEN** 调用 `fetchDictByCode('data_type')`
- **THEN** SHALL 不触发全局 loading 遮罩
