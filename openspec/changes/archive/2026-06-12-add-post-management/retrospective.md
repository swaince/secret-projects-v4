# Retrospective: add-post-management

## 概要

为系统管理模块添加岗位管理功能（完整 CRUD + 前后端），遵循字典管理模块的已有模式。

## 完成情况

- **后端**：10 个文件（Migration + Entity + Mapper + DTO + MapStruct + Service + Controller + Tests）
- **前端**：3 个文件（API + PostView + menu config 更新）
- **测试**：48 tests 全部通过
- **耗时**：单会话内完成设计 + 实现

## 需求变更记录

实施过程中用户调整了以下需求：

| 原始设计 | 最终实现 | 原因 |
|----------|----------|------|
| post_level 关联字典 | INTEGER + PostLevel 枚举 | 用户明确要求简化 |
| 预置内置岗位数据 | 无内置数据，仅枚举定义 | 用户不需要内置岗位行 |
| 三级菜单（组织架构下） | 二级菜单（系统管理直属） | 用户要求菜单层级调整 |
| 无编码搜索 | 新增 postCode 搜索 | 用户追加需求 |
| Select 不可清除 | Select 支持「全部」选项 | 用户追加需求 |
| 排序号手动填写 | 默认值 = 总条数 + 1 | 用户追加需求 |

## 发现的 Bug

### MapStruct updateEntity 覆盖主键

- **现象**：编辑保存后字段不更新
- **根因**：MapStruct `updateEntity(@MappingTarget)` 将 DTO 中 null 的 postId 覆盖了实体主键，导致 `updateById` 的 WHERE 条件匹配 0 行
- **修复**：添加 `@Mapping(target = "postId", ignore = true)` 及其他不可变字段
- **影响范围**：PostStructMapper + DictStructMapper（同一模式，一并修复）

## 经验教训

1. **MapStruct @MappingTarget 必须显式 ignore 主键和不可变字段** — 这是容易遗漏的陷阱，后续新模块应在模板中默认包含
2. **spec 应在需求变更时同步更新** — 本次在 verify 阶段才发现偏差，增加了补充工作
3. **字典管理模式可高度复用** — 岗位管理几乎完全复制 Dict 模式，未来类似模块可进一步模板化

## 改进建议

- 考虑为 MapStruct 创建项目级别的配置（`@MapperConfig`），统一忽略 id/status/builtIn/deleted/createdAt/createdBy 字段
- 搜索 Select 的「全部」清除模式可以抽取为通用封装
