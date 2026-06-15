## Why

前端所有页面的接口请求缺少统一的 loading 反馈机制。用户触发操作后无视觉指示，可能重复点击导致重复请求。需要一个全局自动的 loading 遮罩层，请求进行中阻止交互，请求完成后自动消失。

## What Changes

**前端：新增全局请求 loading 遮罩**
- From: 无全局 loading 机制，各页面自行管理
- To: 请求发出即显示全屏遮罩，所有请求完成后隐藏；支持 `silent` 排除
- Reason: 统一用户体验，防止重复操作
- Impact: non-breaking，现有业务代码零改动

## Capabilities

### New Capabilities

- `request-loading`: 全局请求 loading 机制 — 包含 Pinia store（计数器）、axios 拦截器扩展（silent 排除）、LoadingOverlay 全屏遮罩组件、App.vue 挂载

### Modified Capabilities

无

## Impact

- **前端新增文件**：`src/stores/loading.ts`、`src/components/common/LoadingOverlay.vue`
- **前端修改文件**：`src/utils/request.ts`（拦截器 + 方法签名扩展）、`src/App.vue`（挂载组件）、`src/api/dict.ts`（字典请求加 silent）
- **依赖**：无新外部依赖
- **测试**：Vitest 单元测试覆盖 store 计数逻辑
