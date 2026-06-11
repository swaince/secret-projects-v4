# Flyway

## 命名规范

`V{major}.{minor}.{patch}.{seq}__{description}.sql`

- 示例：`V1.0.0.0__init_data.sql`、`V1.0.0.1__add_email_column.sql`
- 第四段为版本内序号，从 0 开始递增
- 版本升级时序号从 0 重新开始
- **已执行的脚本不可修改**

## SQL 规范

```sql
-- V1.0.0.0__init_data.sql
CREATE TABLE sys_user (
    user_id VARCHAR(32) PRIMARY KEY,
    username VARCHAR(50) NOT NULL,
    email VARCHAR(100),
    create_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    create_by VARCHAR(32),
    update_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_by VARCHAR(32),
    status INT DEFAULT 1,
    built_in INT DEFAULT 0,
    deleted INT DEFAULT 0,
    remark VARCHAR(500)
);

COMMENT ON TABLE sys_user IS '用户表';
COMMENT ON COLUMN sys_user.user_id IS '用户ID';
COMMENT ON COLUMN sys_user.username IS '用户名';
```

- **SQL 必须使用标准语法**，禁止数据库个性化语法（`::`、反引号）
- **禁止使用数据库关键字/保留字作为表名/字段名**（`order`、`group`、`key`）
- **主键使用带表前缀命名**：正例 `user_id`，反例 `id`
- **必须包含字段说明**：`COMMENT ON TABLE` 和 `COMMENT ON COLUMN`
- 禁止 boolean/BOOLEAN，统一 INT
- 所有表包含审计字段：create_at/create_by/update_at/update_by/status/built_in/deleted/remark
- 测试环境禁用 Flyway

## 检查清单

- [ ] 命名格式 V{major}.{minor}.{patch}.{seq}__{description}.sql
- [ ] 标准 SQL，无数据库特异性语法
- [ ] 无数据库关键字作标识符
- [ ] 主键带表前缀（user_id 非 id）
- [ ] COMMENT ON TABLE + COMMENT ON COLUMN 完整
- [ ] 审计字段齐全（8个）
- [ ] 无 boolean 类型
- [ ] 已执行脚本不修改
- [ ] 测试环境 flyway.enabled: false
