## ADDED Requirements

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
- **THEN** 返回 `List<DictItemDTO>`，按 sort_order 升序排列

#### Scenario: 字典无字典项
- **WHEN** dictId 有效但无关联字典项
- **THEN** 返回空列表

### Requirement: 字典项新增
DictItemController SHALL 提供 `POST /dicts/{dictId}/items` 接口，新增字典项。

#### Scenario: 新增字典项
- **WHEN** 请求体包含 item_key="ENABLED", item_value="1", sort_order=0
- **THEN** 返回创建后的 `DictItemDTO`

### Requirement: 字典项修改
DictItemController SHALL 提供 `PUT /dicts/{dictId}/items/{itemId}` 接口，修改字典项。

#### Scenario: 修改字典项值和排序
- **WHEN** itemId 存在，请求体修改 item_value 和 sort_order
- **THEN** 返回更新后的 `DictItemDTO`

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
