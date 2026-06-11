# 字典

## DictionaryElement 接口

```java
/**
 * 字典元素接口。
 *
 * @author zhangth
 */
public interface DictionaryElement<T> {
    String getCode();      // 返回 name()
    String getMessage();   // 描述
    T getValue();          // 字典值
}
```

## @Dictionary 注解

```java
/**
 * 字典注解，声明字典元信息。
 *
 * @author zhangth
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Dictionary {
    String name();   // 字典中文名称，如 "状态"
    String code();   // 字典编码，如 "status"
}
```

## 内置枚举

推荐格式：`@formatter:off/on` 包裹，`;` 结尾便于复制。

```java
/**
 * 系统状态。
 *
 * @author zhangth
 */
@Dictionary(name = "状态", code = "status")
public enum Status implements DictionaryElement<Integer> {
    //@formatter:off
    ENABLED(1, "启用"),
    DISABLED(0, "禁用"),
    //@formatter:on
    ;
}

@Dictionary(name = "是否内置", code = "built_in")
public enum Builtin implements DictionaryElement<Integer> {
    //@formatter:off
    YES(1, "内置"),
    NO(0, "非内置"),
    //@formatter:on
    ;
}

@Dictionary(name = "是否删除", code = "deleted")
public enum Deleted implements DictionaryElement<Integer> {
    //@formatter:off
    NO(0, "未删除"),
    YES(1, "已删除"),
    //@formatter:on
    ;
}

@Dictionary(name = "数据类型", code = "data_type")
public enum DataType implements DictionaryElement<String> {
    //@formatter:off
    STRING("STRING", "字符串"),
    NUMBER("NUMBER", "数字"),
    BOOLEAN("BOOLEAN", "布尔"),
    OBJECT("OBJECT", "对象"),
    ARRAY("ARRAY", "数组"),
    //@formatter:on
    ;
}
```

## 字典表

```sql
CREATE TABLE sys_dict (
    dict_id VARCHAR(32) PRIMARY KEY,
    dict_type VARCHAR(50) NOT NULL,
    dict_key VARCHAR(50) NOT NULL,
    dict_value VARCHAR(100) NOT NULL,
    sort INT DEFAULT 0,
    create_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    create_by VARCHAR(32),
    update_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_by VARCHAR(32),
    status INT DEFAULT 1,
    built_in INT DEFAULT 0,
    deleted INT DEFAULT 0,
    remark VARCHAR(500)
);
COMMENT ON TABLE sys_dict IS '字典表';
COMMENT ON COLUMN sys_dict.dict_id IS '字典ID';
COMMENT ON COLUMN sys_dict.dict_type IS '字典类型';
COMMENT ON COLUMN sys_dict.dict_key IS '字典键';
COMMENT ON COLUMN sys_dict.dict_value IS '字典值';
COMMENT ON COLUMN sys_dict.sort IS '排序';
```

## 检查清单

- [ ] 枚举实现 DictionaryElement\<T\>
- [ ] 枚举类加 @Dictionary(name, code) 注解
- [ ] 使用 @formatter:off/on + 末尾 `;`
- [ ] BizCode 也实现 DictionaryElement
- [ ] 字典表包含审计字段 + COMMENT ON
- [ ] 能使用枚举/字典的地方不使用魔法值
