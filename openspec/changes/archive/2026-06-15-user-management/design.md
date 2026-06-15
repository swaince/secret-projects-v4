## Context

系统管理下已有字典、岗位、角色、部门模块，均遵循统一的 CRUD 架构模式。用户管理是最后一个核心基础模块，需保持架构一致性。

约束：
- 后端：Spring Boot 3.5 + MyBatis-Plus + MapStruct + Flyway + H2 测试
- 前端：Vue 3.5 + Pinia + shadcn-vue + Tailwind CSS v4 + axios HttpClient
- 密码必须加密存储（BCrypt）
- 性别字段走字典系统（dict.gender）

## Goals / Non-Goals

**Goals:**
- 后端：SysUser 实体 + UserDTO + UserMapper + UserService + UserController + Flyway 建表
- 前端：API 层 + UserView.vue 管理页面 + 菜单注册
- 密码 BCrypt 加密，查询不返回密码
- 性别列走字典翻译，状态列 Switch 切换

**Non-Goals:**
- 不做用户-角色绑定（后续单独变更）
- 不做用户-部门/岗位关联
- 不做登录/认证功能
- 不做头像上传
- 不做密码策略校验（如复杂度要求）

## Decisions

### D1：表结构

- **选择**：`sys_user` 表，`user_id` VARCHAR(32) 主键（与其他模块一致）
- **理由**：统一 ID 生成策略

### D2：密码处理

- **选择**：Spring Security 的 BCryptPasswordEncoder 加密
- **理由**：项目已有 spring-boot-starter-security 依赖（或后续会引入），BCrypt 是行业标准
- **已考虑 alternative**：SHA256 + salt — 不如 BCrypt 抗暴力破解 → 拒绝

### D3：DTO 密码字段

- **选择**：UserDTO 中密码字段仅在创建/修改时接收，查询响应时排除（MapStruct 映射时忽略）
- **理由**：防止密码泄露

### D4：前端页面模式

- **选择**：参照 PostView.vue 的表格 + 对话框模式
- **理由**：与现有模块保持一致

### D5：性别字典

- **选择**：复用字典系统 `dict.gender`（需在注册表新增 gender 条目）
- **理由**：已有字典基础设施，无需硬编码

## Risks / Trade-offs

[Risk] spring-boot-starter-security 可能未引入 → Mitigation: 仅用 BCryptPasswordEncoder 类，不启用 Security 过滤链；若未引入则单独添加 bcrypt 依赖

[Trade-off] 密码修改时为空则不更新，前端需明确提示 → 接受：与常见管理后台交互一致

## Migration Plan

1. Flyway 创建 `sys_user` 表
2. 后端 Entity → DTO → Mapper → MapStruct → Service → Controller
3. 后端集成测试
4. 前端 API 层 → UserView.vue → 菜单注册
5. 字典注册表新增 gender

## Open Questions

无
