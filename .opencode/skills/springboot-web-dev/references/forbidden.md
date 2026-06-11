# 禁止事项

| 禁止 | 替代方案 |
|------|----------|
| Lombok（@Data, @Slf4j, @RequiredArgsConstructor） | 手写 getter/setter、Logger、构造器 |
| BeanUtils.copyProperties | 使用 MapStruct |
| @Autowired 字段注入 | 构造器注入 + private final |
| JPA 注解（@Entity, @Column, @OneToMany） | MyBatis-Plus @TableName |
| @TableLogic / @Version | 手动管理 |
| IService\<T\> / ServiceImpl\<M, T\> | 直接注入 Mapper 操作 |
| 返回裸实体或字符串 | 包装为 R\<T\> |
| 分页直接返回 IPage | PageResponse\<T\> 包装 |
| Controller 写业务逻辑 | 委托给 Service |
| 硬编码密码/密钥 | 环境变量或配置中心 |
| System.out / e.printStackTrace() | SLF4J LOGGER |
| 修改已执行的 Flyway 脚本 | 新增迁移脚本 |
| 测试用 PostgreSQL | H2 自动替换 |
| 硬编码魔法值（status = 1） | 枚举或字典 |
| 单行 Javadoc（/** xxx */） | 标准多行格式 |
| 缺少类/方法/字段 Javadoc | 全部必须包含 |
| 变量名缩写（qw, req, msg） | 使用完整单词 |
| boolean / BOOLEAN 类型 | 统一 Integer（0/1） |
| 数据库关键字作标识符 | 避免 order、group、key |
| SQL 内联 -- 注释 | 使用 COMMENT ON |
| 主键名 id | 带表前缀 user_id、dict_id |
| 数据库特异性语法（::、反引号） | 标准 SQL |
| 裸 @Transactional | @Transactional(rollbackFor = Exception.class) |
| 校验注解缺 message | 必须显式提供 |
| DTO 继承 Entity | 仅限 dto 包内继承 |
| @RequestMapping 硬编码 /api | 部署层处理 |
| 路径参数泛化 {id} | 显式命名 {userId} |
| 批量接口用 /batch | 共用集合 URL + Body 传参 |

**属性拷贝唯一方式：MapStruct。**

## 检查清单

- [ ] 无任何禁止项出现在代码中
- [ ] 逐一核对禁止项列表
- [ ] 属性拷贝仅使用 MapStruct
