# theme-persistence

## Requirements

### Requirement: 主题状态管理
系统 SHALL 使用 Pinia Setup Store（`useThemeStore`）管理主题状态，支持 `'light'` 和 `'dark'` 两种模式。

#### Scenario: 默认主题
- **WHEN** 用户首次访问应用（localStorage 中无 `app-theme` 记录）
- **THEN** 主题 SHALL 初始化为 `'light'`

#### Scenario: 从 localStorage 恢复
- **WHEN** localStorage 中存在有效主题值 `'dark'`
- **THEN** store 初始化时 SHALL 读取并应用该值作为当前主题

### Requirement: 主题切换
`useThemeStore` SHALL 提供 `toggle()` 方法，在 `'light'` 和 `'dark'` 之间切换当前主题。

#### Scenario: 亮色切换到暗色
- **WHEN** 当前主题为 `'light'` 且调用 `toggle()`
- **THEN** 主题 SHALL 变为 `'dark'`，localStorage 中 `app-theme` SHALL 更新为 `'dark'`

#### Scenario: 暗色切换到亮色
- **WHEN** 当前主题为 `'dark'` 且调用 `toggle()`
- **THEN** 主题 SHALL 变为 `'light'`，localStorage 中 `app-theme` SHALL 更新为 `'light'`

### Requirement: DOM 样式同步
系统 SHALL 在主题变更时同步操作 `document.documentElement.classList`，确保 Tailwind v4 暗色模式正确生效。

#### Scenario: 添加暗色 class
- **WHEN** 当前主题变更为 `'dark'`
- **THEN** `document.documentElement.classList` SHALL 包含 `'dark'`

#### Scenario: 移除暗色 class
- **WHEN** 当前主题变更为 `'light'`
- **THEN** `document.documentElement.classList` SHALL 不包含 `'dark'`

### Requirement: 主题持久化
主题状态 SHALL 通过 `localStorage` 以 key `app-theme` 持久化，跨页面刷新和会话保持。

#### Scenario: 刷新后保持
- **WHEN** 用户切换主题为 `'dark'` 后刷新页面
- **THEN** 页面重新加载后主题 SHALL 保持为 `'dark'`

#### Scenario: 无效值处理
- **WHEN** localStorage 中 `app-theme` 的值不是 `'light'` 或 `'dark'`（如 `null`、`undefined`、其他字符串）
- **THEN** 系统 SHALL 回退为默认值 `'light'`
