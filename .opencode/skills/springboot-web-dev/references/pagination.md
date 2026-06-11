# 分页排序

## QueryRequest

所有分页查询请求继承此类：

```java
/**
 * 分页查询基类。
 *
 * @author zhangth
 */
public class QueryRequest {
    /** 当前页码 */
    private Long page = 1L;

    /** 每页条数 */
    private Long size = 10L;

    /** 排序字段 */
    private String sortField;

    /** 排序方向 asc/desc */
    private String sortOrder;
}
```

使用：

```java
/**
 * 用户分页查询请求。
 *
 * @author zhangth
 */
public class UserPageRequest extends QueryRequest {
    /** 用户名 */
    private String username;
    /** 邮箱 */
    private String email;
    /** 状态 */
    private Integer status;
}
```

## PageResponse\<T\>

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

## 检查清单

- [ ] QueryRequest 作为基类，继承使用
- [ ] 字段含 Javadoc
- [ ] 分页返回 PageResponse，不直接返回 IPage
