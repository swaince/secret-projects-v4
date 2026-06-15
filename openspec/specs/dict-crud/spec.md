# dict-crud

## Purpose

字典与字典项后端 CRUD 接口规格。
## Requirements
### Requirement: 字典分页查询
DictController SHALL 提供 `GET /dicts` 接口，支持分页、按字典名称模糊搜索、排序。

#### Scenario: 分页查询默认列表
- **WHEN** 请求 `GET /dicts?page=1&size=10`
- **THEN** 返回 `PageResponse<DictDTO>`，包含 records/total/page/size

#### Scenario: 按名称搜索
- **WHEN** 请求 `GET /dicts?dictName=状态&page=1&size=10`
- **THEN** 返回 dict_name 包含"状态"的字典列表

### Requirement: 字典新增
DictController SHALL 提供 `POST /dicts` 接口，创建新字典。

#### Scenario: 创建字典
- **WHEN** 请求体包含 dict_name="用户状态", dict_code="user_status", data_value_type="STRING"
- **THEN** 返回创建后的 `DictDTO`，code=200

#### Scenario: 字典编码重复
- **WHEN** 请求 dict_code 已存在
- **THEN** 返回 code=409，提示编码重复

### Requirement: 字典修改
DictController SHALL 提供 `PUT /dicts/{dictId}` 接口，修改字典信息。

#### Scenario: 修改字典名称
- **WHEN** 请求体包含 dict_name="用户状态V2"，dict_id 存在
- **THEN** 返回更新后的 `DictDTO`，dict_name 已变更

#### Scenario: 修改不存在的字典
- **WHEN** dictId 不存在
- **THEN** 返回 code=404

### Requirement: 字典删除
DictController SHALL 提供 `DELETE /dicts/{dictId}` 接口，删除字典。删除时 SHALL 级联删除关联的字典项。

#### Scenario: 删除字典及关联项
- **WHEN** dictId 存在且有 3 条关联字典项
- **THEN** 字典和 3 条字典项均被删除，返回被删 dictId

### Requirement: 字典批量删除
DictController SHALL 提供 `DELETE /dicts` 接口，批量删除字典及关联项。

#### Scenario: 批量删除
- **WHEN** 请求体包含 ids=["id1","id2"]
- **THEN** 返回被删除的 dictId 列表

### Requirement: 字典项查询
DictItemController SHALL 提供 `GET /dicts/{dictId}/items` 接口，查询指定字典下的所有字典项（不分页）。

#### Scenario: 查询字典项列表
- **WHEN** dictId 有效
- **THEN** 返回 `List<DictItemDTO>`，按 sort_order 升序排列，每个 DictItemDTO SHALL 包含 `itemLabel` 字段

#### Scenario: 字典无字典项
- **WHEN** dictId 有效但无关联字典项
- **THEN** 返回空列表

### Requirement: 字典项新增
DictItemController SHALL 提供 `POST /dicts/{dictId}/items` 接口，新增字典项。

#### Scenario: 新增字典项
- **WHEN** 请求体包含 item_key="ENABLED", item_label="启用", item_value="1", sort_order=0
- **THEN** 返回创建后的 `DictItemDTO`，SHALL 包含 `itemLabel` 字段

### Requirement: 字典项修改
DictItemController SHALL 提供 `PUT /dicts/{dictId}/items/{itemId}` 接口，修改字典项。

#### Scenario: 修改字典项值和排序
- **WHEN** itemId 存在，请求体修改 item_value、item_label 和 sort_order
- **THEN** 返回更新后的 `DictItemDTO`，SHALL 包含更新后的 `itemLabel` 字段

### Requirement: 字典项删除
DictItemController SHALL 提供 `DELETE /dicts/{dictId}/items/{itemId}` 接口，删除单个字典项。

#### Scenario: 删除字典项
- **WHEN** itemId 存在
- **THEN** 返回被删 itemId

### Requirement: 字典项批量删除
DictItemController SHALL 提供 `DELETE /dicts/{dictId}/items` 接口，批量删除字典项。

#### Scenario: 批量删除
- **WHEN** 请求体包含 ids=["id1","id2"]
- **THEN** 返回被删除的 itemId 列表

### Requirement: 按字典ID批量删除字典项
DictItemController SHALL 提供 `DELETE /dicts/{dictId}/items/all` 接口，删除指定字典下的所有字典项。

#### Scenario: 删除字典下所有项
- **WHEN** dictId 有效
- **THEN** 该字典下的所有字典项被删除，返回被删 itemId 列表

### Requirement: 按字典编码批量删除字典项
DictItemController SHALL 提供 `DELETE /dicts/items/by-code` 接口，按字典编码删除字典项。

#### Scenario: 按编码删除
- **WHEN** 请求体包含 dictCode="user_status"
- **THEN** 该编码对应字典下的所有字典项被删除

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

