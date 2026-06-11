# 字典管理 Implemention Plan

> **For agentic workers:** Use superpowers:subagent-driven-development to implement. Steps use checkbox (`- [ ]`) syntax.

**Goal:** 实现字典和字典项完整 CRUD（后端 common 模块 + 前端系统管理页面）

**Architecture:** Backend 双表 sys_dict + sys_dict_item（dict_id 外键），遵循 springboot-web-dev 全部规范（R\<T\>、构造器注入、MapStruct、TDD）。前端分页列表 + 右侧 Drawer 抽屉展示字典项。

**Tech Stack:** Spring Boot 3.5 · MyBatis-Plus 3.5 · Java 17 · Flyway · H2 · Vue 3.5 · shadcn-vue · pnpm

---

### Task 1: Flyway 数据库迁移

**Files:**
- Create: `backend/src/main/resources/db/migration/V1.0.0.0__init_dict_table.sql`

- [ ] **Step 1: 创建迁移脚本**

```sql
-- V1.0.0.0__init_dict_table.sql
CREATE TABLE sys_dict (
    dict_id VARCHAR(32) PRIMARY KEY,
    dict_name VARCHAR(50) NOT NULL,
    dict_code VARCHAR(50) NOT NULL,
    data_value_type VARCHAR(20) NOT NULL DEFAULT 'STRING',
    remark VARCHAR(500),
    create_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    create_by VARCHAR(32),
    update_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_by VARCHAR(32),
    status INT DEFAULT 1,
    built_in INT DEFAULT 0,
    deleted INT DEFAULT 0
);

COMMENT ON TABLE sys_dict IS '字典表';
COMMENT ON COLUMN sys_dict.dict_id IS '字典ID';
COMMENT ON COLUMN sys_dict.dict_name IS '字典名称';
COMMENT ON COLUMN sys_dict.dict_code IS '字典编码';
COMMENT ON COLUMN sys_dict.data_value_type IS '数据类型';
COMMENT ON COLUMN sys_dict.remark IS '备注';
COMMENT ON COLUMN sys_dict.create_at IS '创建时间';
COMMENT ON COLUMN sys_dict.create_by IS '创建人';
COMMENT ON COLUMN sys_dict.update_at IS '更新时间';
COMMENT ON COLUMN sys_dict.update_by IS '更新人';
COMMENT ON COLUMN sys_dict.status IS '状态 1-启用 0-禁用';
COMMENT ON COLUMN sys_dict.built_in IS '是否内置 1-是 0-否';
COMMENT ON COLUMN sys_dict.deleted IS '是否删除 1-是 0-否';

CREATE TABLE sys_dict_item (
    dict_item_id VARCHAR(32) PRIMARY KEY,
    dict_id VARCHAR(32) NOT NULL,
    item_key VARCHAR(50) NOT NULL,
    item_value VARCHAR(200) NOT NULL,
    sort_order INT DEFAULT 0,
    remark VARCHAR(500),
    create_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    create_by VARCHAR(32),
    update_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_by VARCHAR(32),
    status INT DEFAULT 1,
    built_in INT DEFAULT 0,
    deleted INT DEFAULT 0
);

COMMENT ON TABLE sys_dict_item IS '字典项表';
COMMENT ON COLUMN sys_dict_item.dict_item_id IS '字典项ID';
COMMENT ON COLUMN sys_dict_item.dict_id IS '字典ID';
COMMENT ON COLUMN sys_dict_item.item_key IS '字典项键';
COMMENT ON COLUMN sys_dict_item.item_value IS '字典项值';
COMMENT ON COLUMN sys_dict_item.sort_order IS '排序';
COMMENT ON COLUMN sys_dict_item.remark IS '备注';
COMMENT ON COLUMN sys_dict_item.create_at IS '创建时间';
COMMENT ON COLUMN sys_dict_item.create_by IS '创建人';
COMMENT ON COLUMN sys_dict_item.update_at IS '更新时间';
COMMENT ON COLUMN sys_dict_item.update_by IS '更新人';
COMMENT ON COLUMN sys_dict_item.status IS '状态 1-启用 0-禁用';
COMMENT ON COLUMN sys_dict_item.built_in IS '是否内置 1-是 0-否';
COMMENT ON COLUMN sys_dict_item.deleted IS '是否删除 1-是 0-否';
```

- [ ] **Step 2: 运行迁移确认成功**

Run: `./mvnw flyway:migrate -pl backend`
Expected: "Successfully applied 1 migration"

- [ ] **Step 3: Commit**

```bash
git add backend/src/main/resources/db/migration/V1.0.0.1__create_dict_table.sql
git commit -m "feat: add sys_dict and sys_dict_item tables via Flyway"
```

---

### Task 2: Entity + DTO

**Files:**
- Create: `backend/src/main/java/com/dfec/soft/secret/common/entity/SysDict.java`
- Create: `backend/src/main/java/com/dfec/soft/secret/common/entity/SysDictItem.java`
- Create: `backend/src/main/java/com/dfec/soft/secret/common/dto/common/DictDTO.java`
- Create: `backend/src/main/java/com/dfec/soft/secret/common/dto/common/DictItemDTO.java`
- Create: `backend/src/main/java/com/dfec/soft/secret/common/dto/request/DictCreateRequest.java`
- Create: `backend/src/main/java/com/dfec/soft/secret/common/dto/request/DictUpdateRequest.java`
- Create: `backend/src/main/java/com/dfec/soft/secret/common/dto/request/DictItemCreateRequest.java`
- Create: `backend/src/main/java/com/dfec/soft/secret/common/dto/request/DictItemUpdateRequest.java`
- Create: `backend/src/main/java/com/dfec/soft/secret/common/dto/request/DictPageRequest.java`
- Test: None (entities are POJOs, tested via Service)

- [ ] **Step 1: 创建目录结构**

```powershell
New-Item -ItemType Directory -Path "backend\src\main\java\com\dfec\soft\secret\common\entity" -Force
New-Item -ItemType Directory -Path "backend\src\main\java\com\dfec\soft\secret\common\dto\common" -Force
New-Item -ItemType Directory -Path "backend\src\main\java\com\dfec\soft\secret\common\dto\request" -Force
New-Item -ItemType Directory -Path "backend\src\main\java\com\dfec\soft\secret\common\mapper" -Force
New-Item -ItemType Directory -Path "backend\src\main\java\com\dfec\soft\secret\common\service" -Force
New-Item -ItemType Directory -Path "backend\src\main\java\com\dfec\soft\secret\common\controller" -Force
New-Item -ItemType Directory -Path "backend\src\main\java\com\dfec\soft\secret\common\mapstruct" -Force
New-Item -ItemType Directory -Path "backend\src\test\java\com\dfec\soft\secret\common\service" -Force
New-Item -ItemType Directory -Path "backend\src\test\java\com\dfec\soft\secret\common\controller" -Force
```

- [ ] **Step 2: 创建 SysDict.java**

```java
package com.dfec.soft.secret.common.entity;

import com.baomidou.mybatisplus.annotation.*;
import java.time.LocalDateTime;

/**
 * 字典实体。
 *
 * @author zhangth
 * @since 1.0.0
 */
@TableName("sys_dict")
public class SysDict {
    /** 字典ID */
    @TableId(type = IdType.INPUT)
    private String dictId;

    /** 字典名称 */
    @TableField("dict_name")
    private String dictName;

    /** 字典编码 */
    @TableField("dict_code")
    private String dictCode;

    /** 数据类型 */
    @TableField("data_value_type")
    private String dataValueType;

    /** 备注 */
    @TableField("remark")
    private String remark;

    /** 创建时间 */
    @TableField(value = "create_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    /** 创建人 */
    @TableField(value = "create_by", fill = FieldFill.INSERT)
    private String createdBy;

    /** 更新时间 */
    @TableField(value = "update_at", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    /** 更新人 */
    @TableField(value = "update_by", fill = FieldFill.INSERT_UPDATE)
    private String updatedBy;

    /** 状态 1-启用 0-禁用 */
    @TableField("status")
    private Integer status;

    /** 是否内置 1-是 0-否 */
    @TableField("built_in")
    private Integer builtIn;

    /** 是否删除 1-是 0-否 */
    @TableField("deleted")
    private Integer deleted;

    // getter / setter ...
}
```

- [ ] **Step 3: 创建 SysDictItem.java**

```java
package com.dfec.soft.secret.common.entity;

import com.baomidou.mybatisplus.annotation.*;
import java.time.LocalDateTime;

@TableName("sys_dict_item")
public class SysDictItem {
    @TableId(type = IdType.INPUT)
    private String dictItemId;

    @TableField("dict_id")
    private String dictId;

    @TableField("item_key")
    private String itemKey;

    @TableField("item_value")
    private String itemValue;

    @TableField("sort_order")
    private Integer sortOrder;

    @TableField("remark")
    private String remark;

    @TableField(value = "create_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    // ... additional audit fields same pattern as SysDict
    // getter / setter ...
}
```

- [ ] **Step 4: 创建 DTO 和 Request 类**

DictDTO.java:
```java
package com.dfec.soft.secret.common.dto.common;

public class DictDTO {
    private String dictId;
    private String dictName;
    private String dictCode;
    private String dataValueType;
    private String remark;
    private LocalDateTime createdAt;
    // getter / setter ...
}
```

DictCreateRequest.java:
```java
package com.dfec.soft.secret.common.dto.request;

import jakarta.validation.constraints.NotBlank;

public class DictCreateRequest {
    @NotBlank(message = "字典名称不能为空")
    private String dictName;

    @NotBlank(message = "字典编码不能为空")
    private String dictCode;

    private String dataValueType;
    private String remark;
    // getter / setter ...
}
```

DictPageRequest.java:
```java
package com.dfec.soft.secret.common.dto.request;

public class DictPageRequest extends QueryRequest {
    private String dictName;
    // getter / setter ...
}
```

(Repeat similar patterns for DictItemDTO, DictItemCreateRequest, etc.)

- [ ] **Step 5: Commit**

```bash
git add backend/src/main/java/com/dfec/soft/secret/common/
git commit -m "feat: add dict entity, DTO, and request classes"
```

---

### Task 3: Mapper + MapStruct

**Files:**
- Create: `backend/src/main/java/com/dfec/soft/secret/common/mapper/DictMapper.java`
- Create: `backend/src/main/java/com/dfec/soft/secret/common/mapper/DictItemMapper.java`
- Create: `backend/src/main/java/com/dfec/soft/secret/common/mapstruct/DictStructMapper.java`
- Create: `backend/src/main/java/com/dfec/soft/secret/common/mapstruct/DictItemStructMapper.java`

- [ ] **Step 1: 创建 Mapper 接口**

DictMapper.java:
```java
@Mapper
public interface DictMapper extends BaseMapper<SysDict> {
}
```

DictItemMapper.java:
```java
@Mapper
public interface DictItemMapper extends BaseMapper<SysDictItem> {
}
```

- [ ] **Step 2: 创建 MapStruct**

DictStructMapper.java:
```java
@Mapper(componentModel = "spring")
public interface DictStructMapper {
    DictDTO entityToDTO(SysDict entity);
    List<DictDTO> entityToDTO(List<SysDict> list);
    IPage<DictDTO> entityToDTO(IPage<SysDict> page);
    SysDict requestToEntity(DictCreateRequest request);
    void updateEntity(@MappingTarget SysDict entity, DictUpdateRequest request);
}
```

DictItemStructMapper.java: (same pattern for SysDictItem)

- [ ] **Step 3: 编译确认 MapStruct 生成**

Run: `./mvnw compile -pl backend`
Expected: BUILD SUCCESS

- [ ] **Step 4: Commit**

```bash
git add backend/src/main/java/com/dfec/soft/secret/common/mapper/ backend/src/main/java/com/dfec/soft/secret/common/mapstruct/
git commit -m "feat: add dict mapper and mapstruct interfaces"
```

---

### Task 4: DictService (TDD)

**Files:**
- Create: `backend/src/test/java/com/dfec/soft/secret/common/service/DictServiceTest.java`
- Create: `backend/src/main/java/com/dfec/soft/secret/common/service/DictService.java`
- Create: `backend/src/main/java/com/dfec/soft/secret/common/service/DictServiceImpl.java`

- [ ] **Step 1: 编写测试用例**

```java
package com.dfec.soft.secret.common.service;

import static org.assertj.core.api.Assertions.*;
import com.dfec.soft.secret.common.entity.SysDict;
import com.dfec.soft.secret.common.mapper.DictMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class DictServiceTest {

    private final DictService dictService;
    private final DictMapper dictMapper;

    public DictServiceTest(DictService dictService, DictMapper dictMapper) {
        this.dictService = dictService;
        this.dictMapper = dictMapper;
    }

    @BeforeEach
    void setUp() {
        dictMapper.delete(new LambdaQueryWrapper<>());
    }

    @Test
    void shouldPageDictionaries() {
        SysDict dict = new SysDict();
        dict.setDictName("测试字典");
        dict.setDictCode("test_dict");
        dictMapper.insert(dict);

        DictPageRequest query = new DictPageRequest();
        query.setPage(1L);
        query.setSize(10L);
        PageResponse<DictDTO> result = dictService.page(query);

        assertThat(result.getTotal()).isEqualTo(1L);
        assertThat(result.getRecords().get(0).getDictName()).isEqualTo("测试字典");
    }

    @Test
    void shouldCreateDictionary() {
        DictCreateRequest request = new DictCreateRequest();
        request.setDictName("状态字典");
        request.setDictCode("status_dict");
        request.setDataValueType("STRING");

        DictDTO result = dictService.create(request);

        assertThat(result.getDictName()).isEqualTo("状态字典");
        assertThat(result.getDictCode()).isEqualTo("status_dict");
    }

    @Test
    void shouldDeleteDictionaryAndItems() {
        // Insert dict + items
        SysDict dict = new SysDict();
        dict.setDictId("dict1");
        dictMapper.insert(dict);
        // Insert dict items...

        String deletedId = dictService.deleteById("dict1");

        assertThat(deletedId).isEqualTo("dict1");
        assertThat(dictMapper.selectById("dict1")).isNull();
    }

    @Test
    void shouldBatchDelete() {
        SysDict d1 = new SysDict(); d1.setDictId("d1"); dictMapper.insert(d1);
        SysDict d2 = new SysDict(); d2.setDictId("d2"); dictMapper.insert(d2);

        List<String> deleted = dictService.delete(List.of("d1", "d2"));

        assertThat(deleted).containsExactly("d1", "d2");
    }
}
```

- [ ] **Step 2: 运行测试确认失败**

Run: `./mvnw test -pl backend -Dtest=DictServiceTest`
Expected: FAIL — DictService not found

- [ ] **Step 3: 创建 DictService 接口**

```java
package com.dfec.soft.secret.common.service;

import com.dfec.soft.secret.common.dto.common.DictDTO;
import com.dfec.soft.secret.common.dto.request.*;
import jakarta.validation.Valid;
import java.util.List;

/**
 * 字典服务接口。
 *
 * @author zhangth
 * @since 1.0.0
 */
public interface DictService {

    PageResponse<DictDTO> page(DictPageRequest query);

    DictDTO getById(String dictId);

    DictDTO create(@Valid DictCreateRequest request);

    DictDTO update(String dictId, @Valid DictUpdateRequest request);

    List<String> delete(List<String> ids);

    String deleteById(String dictId);
}
```

- [ ] **Step 4: 创建 DictServiceImpl**

```java
package com.dfec.soft.secret.common.service;

import com.dfec.soft.secret.common.entity.SysDict;
import com.dfec.soft.secret.common.mapper.DictMapper;
import com.dfec.soft.secret.common.mapper.DictItemMapper;
import com.dfec.soft.secret.common.mapstruct.DictStructMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Validated
public class DictServiceImpl implements DictService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DictServiceImpl.class);
    private final DictMapper dictMapper;
    private final DictItemMapper dictItemMapper;
    private final DictStructMapper mapper;

    public DictServiceImpl(DictMapper dictMapper, DictItemMapper dictItemMapper, DictStructMapper mapper) {
        this.dictMapper = dictMapper;
        this.dictItemMapper = dictItemMapper;
        this.mapper = mapper;
    }

    @Override
    public PageResponse<DictDTO> page(DictPageRequest query) {
        LambdaQueryWrapper<SysDict> queryWrapper = new LambdaQueryWrapper<SysDict>()
            .like(StringUtils.isNotBlank(query.getDictName()), SysDict::getDictName, query.getDictName())
            .orderByAsc(SysDict::getDictCode);
        Page<SysDict> page = new Page<>(query.getPage(), query.getSize());
        IPage<SysDict> result = dictMapper.selectPage(page, queryWrapper);
        return PageResponse.of(mapper.entityToDTO(result));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public DictDTO create(@Valid DictCreateRequest request) {
        SysDict entity = mapper.requestToEntity(request);
        dictMapper.insert(entity);
        return mapper.entityToDTO(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String deleteById(String dictId) {
        dictMapper.deleteById(dictId);
        LambdaQueryWrapper<SysDictItem> itemQueryWrapper = new LambdaQueryWrapper<SysDictItem>()
            .eq(SysDictItem::getDictId, dictId);
        dictItemMapper.delete(itemQueryWrapper);
        return dictId;
    }
    // ... update, delete, getById follow same pattern
}
```

- [ ] **Step 5: 运行测试确认通过**

Run: `./mvnw test -pl backend -Dtest=DictServiceTest`
Expected: 4 tests PASS

- [ ] **Step 6: Commit**

```bash
git add backend/src/main/java/com/dfec/soft/secret/common/service/ backend/src/test/java/com/dfec/soft/secret/common/service/
git commit -m "feat: add DictService with TDD (page/create/delete/batch delete)"
```

---

### Task 5: DictItemService (TDD)

**Files:** Create DictItemServiceTest → DictItemService → DictItemServiceImpl

Same TDD pattern as Task 4 with test cases for: create, update, delete, batch delete, deleteByDictId, deleteByDictCode, listByDictId.

- [ ] **Step 1-6**: Follow same RED-GREEN-REFACTOR cycle. Commit after GREEN.

---

### Task 6-7: DictController + DictItemController (TDD)

Same TDD cycle — write controller integration tests first, then implement controllers with `@RestController`, constructor injection, return `R<T>`.

---

### Task 8: 前端字典管理页面 (TDD)

**Files:**
- Create: `frontend/src/views/system/dict/Index.vue`
- Create: `frontend/src/views/system/dict/components/DictItemDrawer.vue`
- Create: `frontend/src/__tests__/DictIndex.spec.ts`
- Modify: `frontend/src/config/menu.ts` (add 字典管理 route)
- Modify: `frontend/src/router/index.ts` (auto-generate)

- [ ] **Step 1: 添加菜单配置**

Add to `frontend/src/config/menu.ts` under system children:
```typescript
{ id: 'dict-list', title: '字典管理', icon: 'BookType', path: '/system/dict' },
```

- [ ] **Step 2: 编写页面测试**

DictIndex.spec.ts mock API calls, test table rendering and drawer open/close.

- [ ] **Step 3-4**: Implement Index.vue + DictItemDrawer.vue using shadcn-vue Table/Drawer/Button. Run tests → GREEN → Commit.

---

### Task 9: 全面验证

- [ ] **Step 1**: `./mvnw test -pl backend` → all backend tests pass
- [ ] **Step 2**: `./mvnw compile -pl backend` → MapStruct + PMD clean
- [ ] **Step 3**: `pnpm test:unit` → all frontend tests pass
- [ ] **Step 4**: `pnpm type-check && pnpm lint && pnpm build` → clean
- [ ] **Step 5**: Commit
