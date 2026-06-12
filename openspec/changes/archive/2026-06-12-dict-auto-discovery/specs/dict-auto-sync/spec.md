# dict-auto-sync

应用启动时自动扫描 `@Dictionary` 枚举，全量同步内置字典到数据库。

## ADDED Requirements

### Requirement: 启动时扫描 @Dictionary 枚举
DictSyncRunner SHALL 在应用启动时扫描基础包 `com.dfec.soft.secret` 下所有标注了 `@Dictionary` 且实现了 `DictionaryElement` 接口的枚举类。

#### Scenario: 发现所有 @Dictionary 枚举
- **WHEN** 应用启动，基础包下存在 5 个 `@Dictionary` 枚举（BizCode, Status, DataValueType, Builtin, Deleted）
- **THEN** DictSyncRunner 扫描结果 SHALL 包含这 5 个枚举类

#### Scenario: 忽略非枚举类
- **WHEN** 某个类标注了 `@Dictionary` 但不是枚举类型或未实现 `DictionaryElement`
- **THEN** DictSyncRunner SHALL 跳过该类，不纳入同步范围

### Requirement: 全量同步内置字典
DictSyncRunner SHALL 在事务中执行全量同步：先删除所有 `built_in=1` 的字典及字典项，再从扫描到的枚举重新插入。

#### Scenario: 首次启动同步
- **WHEN** 应用首次启动，`sys_dict` 表中无内置字典
- **THEN** SHALL 插入所有 `@Dictionary` 枚举对应的 `sys_dict` 记录（`built_in=1`, `status=1`, `deleted=0`）及其枚举常量对应的 `sys_dict_item` 记录

#### Scenario: 重复启动幂等
- **WHEN** 应用非首次启动，`sys_dict` 表已有上次同步的内置字典
- **THEN** SHALL 先删除所有 `built_in=1` 的记录，再全量重新插入，最终数据与首次启动一致

#### Scenario: 业务字典不受影响
- **WHEN** `sys_dict` 表中存在 `built_in=0` 的业务字典
- **THEN** 同步过程 SHALL 不删除、不修改任何 `built_in=0` 的记录

### Requirement: 字典字段映射
DictSyncRunner SHALL 按以下规则将枚举数据映射到数据库字段。

#### Scenario: sys_dict 记录映射
- **WHEN** 枚举类标注 `@Dictionary(name="状态", code="status")`
- **THEN** 插入的 `sys_dict` 记录 SHALL 满足：`dict_name="状态"`, `dict_code="status"`, `data_value_type` 由第一个枚举元素的 `getValue()` 返回类型推断, `built_in=1`, `status=1`, `deleted=0`

#### Scenario: sys_dict_item 记录映射
- **WHEN** 枚举常量 `ENABLED` 实现 `getCode()="ENABLED"`, `getMessage()="启用"`, `getValue()=1`
- **THEN** 插入的 `sys_dict_item` 记录 SHALL 满足：`item_key="ENABLED"`, `item_label="启用"`, `item_value="1"`, `sort_order` 为枚举 ordinal, `built_in=1`, `status=1`, `deleted=0`

### Requirement: 同步失败拒绝启动
DictSyncRunner SHALL 在同步失败时抛出异常，阻止应用启动。

#### Scenario: 数据库异常导致同步失败
- **WHEN** 同步过程中发生数据库异常
- **THEN** 事务 SHALL 回滚，异常向上传播，Spring Boot SHALL 终止启动

### Requirement: 同步日志
DictSyncRunner SHALL 记录同步过程的关键日志。

#### Scenario: 正常同步日志
- **WHEN** 同步成功完成
- **THEN** SHALL 输出 INFO 级别日志，包含扫描发现的枚举数量和同步完成信息

#### Scenario: 同步失败日志
- **WHEN** 同步过程中发生异常
- **THEN** SHALL 输出 ERROR 级别日志，包含异常信息
