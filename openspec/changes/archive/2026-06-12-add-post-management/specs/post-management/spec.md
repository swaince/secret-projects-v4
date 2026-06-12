## ADDED Requirements

### Requirement: 岗位分页查询

系统 SHALL 提供岗位分页查询接口 `GET /posts`，支持按岗位名称和岗位级别过滤，返回分页结果。

#### Scenario: 无条件分页查询
- **WHEN** 用户请求 `GET /posts?page=1&size=10` 且无过滤条件
- **THEN** 返回第一页最多 10 条未删除的岗位记录，包含 total、page、size 字段

#### Scenario: 按名称模糊搜索
- **WHEN** 用户请求 `GET /posts?page=1&size=10&postName=操作`
- **THEN** 仅返回 post_name 包含「操作」的岗位记录

#### Scenario: 按编码模糊搜索
- **WHEN** 用户请求 `GET /posts?page=1&size=10&postCode=oper`
- **THEN** 仅返回 post_code 包含「oper」的岗位记录

#### Scenario: 按级别过滤
- **WHEN** 用户请求 `GET /posts?page=1&size=10&postLevel=1`
- **THEN** 仅返回 post_level 等于 1 的岗位记录

---

### Requirement: 岗位详情查询

系统 SHALL 提供岗位详情接口 `GET /posts/{postId}`，返回单个岗位的完整信息。

#### Scenario: 查询存在的岗位
- **WHEN** 用户请求 `GET /posts/{postId}` 且该 ID 对应一条未删除记录
- **THEN** 返回该岗位的完整 DTO（含所有字段）

#### Scenario: 查询不存在的岗位
- **WHEN** 用户请求 `GET /posts/{postId}` 且该 ID 不存在或已删除
- **THEN** 返回 404 错误码

---

### Requirement: 创建岗位

系统 SHALL 提供创建岗位接口 `POST /posts`，岗位编码 MUST 唯一（在未删除记录中）。

#### Scenario: 成功创建岗位
- **WHEN** 用户提交有效的岗位数据（postName 非空、postCode 非空且唯一）
- **THEN** 创建岗位记录，生成 UUID v7 作为 ID，设置 createdAt/createdBy，返回完整 DTO

#### Scenario: 编码重复
- **WHEN** 用户提交的 postCode 已存在于未删除记录中
- **THEN** 返回 409 冲突错误码

#### Scenario: 必填字段缺失
- **WHEN** 用户提交数据缺少 postName 或 postCode
- **THEN** 返回 400 参数校验错误

---

### Requirement: 更新岗位

系统 SHALL 提供更新岗位接口 `PUT /posts/{postId}`，内置岗位 MUST 不可更新。

#### Scenario: 成功更新普通岗位
- **WHEN** 用户提交更新数据且目标岗位 builtIn=0
- **THEN** 更新岗位字段，设置 updatedAt/updatedBy，返回更新后的 DTO

#### Scenario: 更新内置岗位被拒绝
- **WHEN** 用户尝试更新 builtIn=1 的岗位
- **THEN** 返回 403 禁止操作错误码

#### Scenario: 更新时编码冲突
- **WHEN** 用户更新 postCode 为另一条未删除记录已有的编码
- **THEN** 返回 409 冲突错误码

---

### Requirement: 删除岗位

系统 SHALL 提供删除岗位接口 `DELETE /posts/{postId}`，使用软删除，内置岗位 MUST 不可删除。

#### Scenario: 成功删除普通岗位
- **WHEN** 用户删除 builtIn=0 的岗位
- **THEN** 将 deleted 字段设为 1，设置 updatedAt/updatedBy，返回被删除的 ID

#### Scenario: 删除内置岗位被拒绝
- **WHEN** 用户尝试删除 builtIn=1 的岗位
- **THEN** 返回 403 禁止操作错误码

---

### Requirement: 批量删除岗位

系统 SHALL 提供批量删除接口 `DELETE /posts`，接收 ID 列表，跳过内置岗位。

#### Scenario: 批量删除普通岗位
- **WHEN** 用户提交包含多个 ID 的列表，所有目标岗位 builtIn=0
- **THEN** 全部软删除，返回成功删除的 ID 列表

#### Scenario: 批量删除包含内置岗位
- **WHEN** 用户提交的 ID 列表中包含 builtIn=1 的岗位
- **THEN** 跳过内置岗位，仅删除非内置岗位，返回实际删除的 ID 列表

---

### Requirement: 岗位状态切换

系统 SHALL 支持通过更新接口切换岗位的 status 字段（1=启用, 0=禁用），内置岗位 MUST 不可切换状态。

#### Scenario: 启用/禁用普通岗位
- **WHEN** 用户更新 builtIn=0 的岗位 status 字段
- **THEN** status 字段被更新，返回更新后的 DTO

#### Scenario: 切换内置岗位状态被拒绝
- **WHEN** 用户尝试更新 builtIn=1 的岗位 status
- **THEN** 返回 403 禁止操作错误码

---

### Requirement: 岗位级别枚举定义

系统 SHALL 通过 Java 枚举 `PostLevel` 定义岗位级别常量，并使用 `@Dictionary` 注解注册为字典。

#### Scenario: 枚举包含内置级别
- **WHEN** 系统启动并执行字典同步
- **THEN** PostLevel 枚举包含 OPERATOR(1, "操作员") 和 AUDITOR(2, "审核员")，自动注册为 post_level 字典

---

### Requirement: 前端岗位管理页面

前端 SHALL 提供岗位管理页面 `PostView.vue`，挂载到 `/system/posts` 路由。

#### Scenario: 页面加载展示岗位列表
- **WHEN** 用户导航到 `/system/posts`
- **THEN** 展示搜索卡片（名称输入框 + 编码输入框 + 级别选择 + 搜索/重置按钮）和表格（名称、编码、级别、排序、状态、内置、创建时间、备注、操作列）

#### Scenario: 按岗位编码搜索
- **WHEN** 用户在编码搜索框输入关键词并点击搜索
- **THEN** 仅返回 post_code 包含该关键词的岗位记录

#### Scenario: 搜索下拉框支持清除选择
- **WHEN** 用户在级别下拉框中选择「全部」
- **THEN** 清除级别过滤条件，显示所有岗位

#### Scenario: 创建岗位默认排序号
- **WHEN** 用户打开新增岗位表单
- **THEN** 排序号字段自动填入当前岗位总数 + 1

#### Scenario: 内置岗位禁用操作按钮
- **WHEN** 岗位 builtIn=1
- **THEN** 编辑按钮和删除按钮 disabled，复选框 disabled，状态开关 disabled
