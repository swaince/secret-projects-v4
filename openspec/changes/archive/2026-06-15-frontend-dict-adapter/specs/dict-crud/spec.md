# dict-crud

## ADDED Requirements

### Requirement: 按字典编码查询字典及字典项
DictItemController SHALL 提供 `GET /dicts/items/by-code?dictCode=xxx` 接口，按字典编码查询字典基本信息及其全部启用字典项。

#### Scenario: 按编码查询存在的字典
- **WHEN** 请求 `GET /dicts/items/by-code?dictCode=sys_gender`，且该字典存在、状态启用
- **THEN** 返回 `R<DictWithItemsDTO>`，包含 dictId、dictName、dictCode、dataValueType 及 items 列表（仅 status=1 且 deleted=0 的项，按 sortOrder 升序）

#### Scenario: 字典编码不存在
- **WHEN** 请求 `GET /dicts/items/by-code?dictCode=not_exist`
- **THEN** 返回 `R<null>`，code=200，data 为 null，不抛出异常

#### Scenario: 字典存在但无启用的字典项
- **WHEN** 请求的 dictCode 对应字典存在，但所有字典项均为 status=0 或 deleted=1
- **THEN** 返回 `DictWithItemsDTO`，items 为空列表

#### Scenario: 缺少 dictCode 参数
- **WHEN** 请求 `GET /dicts/items/by-code`（无 dictCode 参数）
- **THEN** 返回 code=400，提示参数缺失

### Requirement: DictWithItemsDTO 结构
DictWithItemsDTO SHALL 包含字典基本信息和字典项列表。

#### Scenario: DTO 字段完整性
- **WHEN** 查询成功返回 DictWithItemsDTO
- **THEN** SHALL 包含以下字段：dictId(String)、dictName(String)、dictCode(String)、dataValueType(String)、items(List&lt;DictItemDTO&gt;)

#### Scenario: items 中的 DictItemDTO 包含 itemLabel
- **WHEN** 查询返回的字典项
- **THEN** 每个 DictItemDTO SHALL 包含 itemLabel 字段
