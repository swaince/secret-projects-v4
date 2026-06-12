# dict-crud (delta)

字典项新增 `item_label` 字段。

## MODIFIED Requirements

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
