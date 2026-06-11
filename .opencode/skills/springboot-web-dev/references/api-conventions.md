# 接口规范

## R\<T\> 统一响应

所有接口与全局异常处理器必须返回 `R<T>`：

```java
/**
 * 统一响应封装。
 *
 * @author zhangth
 */
public class R<T> {
    private int code;
    private String message;
    private T data;

    public static <T> R<T> ok(T data) { R<T> r = new R<>(); r.code = 200; r.message = "success"; r.data = data; return r; }
    public static <T> R<T> ok() { return ok(null); }
    public static <T> R<T> err(int code, String message) { R<T> r = new R<>(); r.code = code; r.message = message; return r; }
    public static <T> R<T> err(String message) { return err(500, message); }
}
```

## PageResponse\<T\>

分页必须用 `PageResponse<T>` 包装：

```java
/**
 * 分页响应封装。
 *
 * @author zhangth
 */
public class PageResponse<T> {
    private List<T> records;
    private Long total;
    private Long page;
    private Long size;

    public static <T> PageResponse<T> of(IPage<T> page) {
        PageResponse<T> r = new PageResponse<>();
        r.records = page.getRecords();
        r.total = page.getTotal();
        r.page = page.getCurrent();
        r.size = page.getSize();
        return r;
    }
}
```

## URL 约定

- 前缀由部署层处理（gateway/nginx），代码中不硬编码 `/api`
- 路径参数使用明确命名：`{userId}` 而非 `{id}`

| 操作 | 方法 | URL | 返回 |
|------|------|-----|------|
| 分页列表 | GET | /users | `PageResponse<UserDTO>` |
| 详情 | GET | /users/{userId} | `UserDTO` |
| 新增 | POST | /users | `UserDTO`（创建后） |
| 修改 | PUT | /users/{userId} | `UserDTO`（更新后） |
| 批量删除 | DELETE | /users（Body: ids） | `List<String>`（被删 id） |
| 单条删除 | DELETE | /users/{userId} | `String`（被删 id） |

## 方法命名规范

Controller 与 Service 方法统一命名：

| 方法 | 用途 |
|------|------|
| `list` | 全量查询，过滤排序 |
| `page` | 分页查询，过滤排序 |
| `getById` | 单条详情 |
| `create` | 新增 |
| `update` | 更新 |
| `delete(ids)` | 批量删除 |
| `deleteById` | 单条删除 |
| `upload` | 导入 |
| `export` | 导出 |
| `download` | 下载 |

## 检查清单

- [ ] 所有接口返回 `R<T>`，无裸实体/字符串
- [ ] 分页返回 `PageResponse<T>`，不直接返回 `IPage`
- [ ] 路径参数显式命名（`{userId}` 非 `{id}`）
- [ ] 路径前缀不在代码中硬编码
- [ ] 方法命名符合规范表
- [ ] 删除接口返回被删 id（批量返回 `List<String>`，单条返回 `String`）
- [ ] create/update 返回实体 DTO
