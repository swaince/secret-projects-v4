# 字典自动发现及自动注册 Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 应用启动时自动扫描 `@Dictionary` 枚举，全量同步内置字典到数据库。

**Architecture:** 新建 `DictSyncRunner implements ApplicationRunner`，通过 `ClassPathScanningCandidateComponentProvider` 扫描 `@Dictionary` 枚举，在事务中先删除所有 `built_in=1` 记录再全量插入。Flyway 迁移为 `sys_dict_item` 新增 `item_label` 列。

**Tech Stack:** Spring Boot 3.5.14, Java 17, MyBatis-Plus 3.5.15, Flyway, H2 (test), PostgreSQL (prod)

---

## File Structure

| 操作 | 文件路径 | 职责 |
|------|---------|------|
| Create | `backend/src/main/resources/db/migration/V1.0.0.2__add_dict_item_label.sql` | Flyway 迁移：新增 item_label 列 |
| Modify | `backend/src/main/java/com/dfec/soft/secret/system/entity/SysDictItem.java` | 新增 itemLabel 字段 |
| Modify | `backend/src/main/java/com/dfec/soft/secret/system/dto/common/DictItemDTO.java` | 新增 itemLabel 字段 |
| Create | `backend/src/main/java/com/dfec/soft/secret/common/config/DictSyncRunner.java` | 启动时扫描+同步 |
| Create | `backend/src/test/java/com/dfec/soft/secret/common/config/DictSyncRunnerTest.java` | DictSyncRunner 测试 |
| Modify | `backend/src/test/java/com/dfec/soft/secret/system/service/DictItemServiceTest.java` | 适配 itemLabel |
| Modify | `backend/src/test/java/com/dfec/soft/secret/system/controller/DictItemControllerTest.java` | 适配 itemLabel |

---

## Task 1: Flyway 迁移脚本

**Files:**
- Create: `backend/src/main/resources/db/migration/V1.0.0.2__add_dict_item_label.sql`

- [ ] **Step 1: 创建迁移脚本**

```sql
ALTER TABLE sys_dict_item
    ADD COLUMN item_label VARCHAR(100) DEFAULT '' NOT NULL;

COMMENT ON COLUMN sys_dict_item.item_label IS '字典项标签';
```

- [ ] **Step 2: 验证 Flyway 迁移可执行**

Run: `./mvnw test -pl backend -Dtest=HealthControllerTest`
Expected: PASS（Flyway 在测试启动时自动执行迁移，H2 兼容 ALTER TABLE ADD COLUMN）

- [ ] **Step 3: Commit**

```bash
git add backend/src/main/resources/db/migration/V1.0.0.2__add_dict_item_label.sql
git commit -m "feat(dict): add item_label column via Flyway migration"
```

---

## Task 2: SysDictItem 实体新增 itemLabel

**Files:**
- Modify: `backend/src/main/java/com/dfec/soft/secret/system/entity/SysDictItem.java`

- [ ] **Step 1: 在 itemKey 和 itemValue 之间新增 itemLabel 字段**

在 `itemKey` 字段后、`itemValue` 字段前新增：

```java
/**
 * 字典项标签。
 */
@TableField("item_label")
private String itemLabel;
```

- [ ] **Step 2: 新增 getter/setter**

在 `getItemKey()`/`setItemKey()` 之后新增：

```java
public String getItemLabel() { return itemLabel; }
public void setItemLabel(String itemLabel) { this.itemLabel = itemLabel; }
```

- [ ] **Step 3: 验证编译通过**

Run: `./mvnw compile -pl backend`
Expected: BUILD SUCCESS（含 PMD 检查通过）

---

## Task 3: DictItemDTO 新增 itemLabel

**Files:**
- Modify: `backend/src/main/java/com/dfec/soft/secret/system/dto/common/DictItemDTO.java`

- [ ] **Step 1: 新增 itemLabel 字段**

在 `itemKey` 相关字段后新增：

```java
private String itemLabel;
```

- [ ] **Step 2: 新增 getter/setter（如果 DTO 使用手动 getter/setter 风格）**

```java
public String getItemLabel() { return itemLabel; }
public void setItemLabel(String itemLabel) { this.itemLabel = itemLabel; }
```

- [ ] **Step 3: 验证编译通过**

Run: `./mvnw compile -pl backend`
Expected: BUILD SUCCESS

- [ ] **Step 4: Commit**

```bash
git add backend/src/main/java/com/dfec/soft/secret/system/entity/SysDictItem.java
git add backend/src/main/java/com/dfec/soft/secret/system/dto/common/DictItemDTO.java
git commit -m "feat(dict): add itemLabel field to SysDictItem and DictItemDTO"
```

---

## Task 4: 适配现有测试

**Files:**
- Modify: `backend/src/test/java/com/dfec/soft/secret/system/service/DictItemServiceTest.java`
- Modify: `backend/src/test/java/com/dfec/soft/secret/system/controller/DictItemControllerTest.java`

- [ ] **Step 1: 检查现有测试中构造 SysDictItem / DictItemDTO 的位置，补充 itemLabel 赋值**

查找测试中所有 `setItemKey` 或 `setItemValue` 调用，在其后补充 `setItemLabel`。例如：

```java
item.setItemLabel("启用");
```

- [ ] **Step 2: 运行现有字典项测试**

Run: `./mvnw test -pl backend -Dtest=DictItemServiceTest,DictItemControllerTest`
Expected: ALL PASS

- [ ] **Step 3: Commit**

```bash
git add backend/src/test/java/com/dfec/soft/secret/system/service/DictItemServiceTest.java
git add backend/src/test/java/com/dfec/soft/secret/system/controller/DictItemControllerTest.java
git commit -m "test(dict): adapt existing dict item tests for itemLabel field"
```

---

## Task 5: DictSyncRunner 测试（TDD）

**Files:**
- Create: `backend/src/test/java/com/dfec/soft/secret/common/config/DictSyncRunnerTest.java`

- [ ] **Step 1: 创建测试类骨架**

```java
package com.dfec.soft.secret.common.config;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.dfec.soft.secret.common.constants.Builtin;
import com.dfec.soft.secret.common.constants.Status;
import com.dfec.soft.secret.system.entity.SysDict;
import com.dfec.soft.secret.system.entity.SysDictItem;
import com.dfec.soft.secret.system.mapper.DictItemMapper;
import com.dfec.soft.secret.system.mapper.DictMapper;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class DictSyncRunnerTest {

    private final DictMapper dictMapper;
    private final DictItemMapper dictItemMapper;

    DictSyncRunnerTest(DictMapper dictMapper, DictItemMapper dictItemMapper) {
        this.dictMapper = dictMapper;
        this.dictItemMapper = dictItemMapper;
    }
}
```

- [ ] **Step 2: 测试——启动后内置字典已同步到 DB**

```java
@Test
void shouldSyncBuiltinDictsOnStartup() {
    LambdaQueryWrapper<SysDict> wrapper = new LambdaQueryWrapper<SysDict>()
            .eq(SysDict::getBuiltIn, Builtin.YES.getValue());
    Long count = dictMapper.selectCount(wrapper);
    assertThat(count).isGreaterThanOrEqualTo(5L);
}
```

- [ ] **Step 3: 测试——内置字典项 item_key/item_label/item_value 映射正确**

```java
@Test
void shouldMapDictItemFieldsCorrectly() {
    LambdaQueryWrapper<SysDict> dictWrapper = new LambdaQueryWrapper<SysDict>()
            .eq(SysDict::getDictCode, "status");
    SysDict statusDict = dictMapper.selectOne(dictWrapper);
    assertThat(statusDict).isNotNull();
    assertThat(statusDict.getDictName()).isEqualTo("状态");

    LambdaQueryWrapper<SysDictItem> itemWrapper = new LambdaQueryWrapper<SysDictItem>()
            .eq(SysDictItem::getDictId, statusDict.getDictId())
            .eq(SysDictItem::getItemKey, "ENABLED");
    SysDictItem enabledItem = dictItemMapper.selectOne(itemWrapper);
    assertThat(enabledItem).isNotNull();
    assertThat(enabledItem.getItemLabel()).isEqualTo("启用");
    assertThat(enabledItem.getItemValue()).isEqualTo("1");
    assertThat(enabledItem.getSortOrder()).isEqualTo(0);
}
```

- [ ] **Step 4: 测试——业务字典不受影响**

```java
@Test
void shouldNotAffectBusinessDicts() {
    SysDict bizDict = new SysDict();
    bizDict.setDictId("biz-test-001");
    bizDict.setDictName("业务字典");
    bizDict.setDictCode("biz_test");
    bizDict.setBuiltIn(Builtin.NO.getValue());
    bizDict.setStatus(Status.ENABLED.getValue());
    bizDict.setDeleted(0);
    dictMapper.insert(bizDict);

    LambdaQueryWrapper<SysDict> wrapper = new LambdaQueryWrapper<SysDict>()
            .eq(SysDict::getDictCode, "biz_test");
    SysDict found = dictMapper.selectOne(wrapper);
    assertThat(found).isNotNull();
    assertThat(found.getDictName()).isEqualTo("业务字典");
}
```

- [ ] **Step 5: 运行测试，确认首次失败（DictSyncRunner 尚未实现时部分测试可能因无数据而失败）**

Run: `./mvnw test -pl backend -Dtest=DictSyncRunnerTest`
Expected: 部分测试 FAIL（`shouldSyncBuiltinDictsOnStartup` 和 `shouldMapDictItemFieldsCorrectly`）— 这正是 TDD 的红灯阶段

注意：如果 `DictSyncRunner` 类尚不存在，Spring Context 无法启动。可先创建空壳类（见 Task 6 Step 1），再回来运行此步骤验证红灯。

---

## Task 6: DictSyncRunner 实现

**Files:**
- Create: `backend/src/main/java/com/dfec/soft/secret/common/config/DictSyncRunner.java`

- [ ] **Step 1: 创建类骨架**

```java
package com.dfec.soft.secret.common.config;

import com.dfec.soft.secret.common.constants.Dictionary;
import com.dfec.soft.secret.common.constants.DictionaryElement;
import com.dfec.soft.secret.common.service.UidService;
import com.dfec.soft.secret.system.entity.SysDict;
import com.dfec.soft.secret.system.entity.SysDictItem;
import com.dfec.soft.secret.system.mapper.DictItemMapper;
import com.dfec.soft.secret.system.mapper.DictMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Component
public class DictSyncRunner implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(DictSyncRunner.class);
    private static final String BASE_PACKAGE = "com.dfec.soft.secret";

    private final DictMapper dictMapper;
    private final DictItemMapper dictItemMapper;
    private final UidService uidService;

    public DictSyncRunner(DictMapper dictMapper, DictItemMapper dictItemMapper, UidService uidService) {
        this.dictMapper = dictMapper;
        this.dictItemMapper = dictItemMapper;
        this.uidService = uidService;
    }

    @Override
    public void run(ApplicationArguments args) {
        syncBuiltinDicts();
    }
}
```

- [ ] **Step 2: 实现 scanDictionaryEnums()**

```java
@SuppressWarnings("unchecked")
private List<Class<? extends DictionaryElement<?>>> scanDictionaryEnums() {
    ClassPathScanningCandidateComponentProvider scanner =
            new ClassPathScanningCandidateComponentProvider(false);
    scanner.addIncludeFilter(new AnnotationTypeFilter(Dictionary.class));

    List<Class<? extends DictionaryElement<?>>> result = new ArrayList<>();
    Set<org.springframework.beans.factory.config.BeanDefinition> candidates =
            scanner.findCandidateComponents(BASE_PACKAGE);

    for (org.springframework.beans.factory.config.BeanDefinition bd : candidates) {
        try {
            Class<?> clazz = Class.forName(bd.getBeanClassName());
            if (clazz.isEnum() && DictionaryElement.class.isAssignableFrom(clazz)) {
                result.add((Class<? extends DictionaryElement<?>>) clazz);
            }
        } catch (ClassNotFoundException e) {
            log.warn("无法加载类: {}", bd.getBeanClassName(), e);
        }
    }
    return result;
}
```

- [ ] **Step 3: 实现 deleteBuiltinDicts()**

```java
private void deleteBuiltinDicts() {
    dictItemMapper.delete(new LambdaQueryWrapper<SysDictItem>()
            .eq(SysDictItem::getBuiltIn, 1));
    dictMapper.delete(new LambdaQueryWrapper<SysDict>()
            .eq(SysDict::getBuiltIn, 1));
}
```

- [ ] **Step 4: 实现 insertBuiltinDicts()**

```java
private void insertBuiltinDicts(List<Class<? extends DictionaryElement<?>>> enumClasses) {
    for (Class<? extends DictionaryElement<?>> enumClass : enumClasses) {
        Dictionary annotation = enumClass.getAnnotation(Dictionary.class);
        DictionaryElement<?>[] constants = enumClass.getEnumConstants();
        if (constants == null || constants.length == 0) {
            continue;
        }

        String dictId = uidService.next();
        String dataValueType = inferDataValueType(constants[0].getValue());

        SysDict dict = new SysDict();
        dict.setDictId(dictId);
        dict.setDictName(annotation.name());
        dict.setDictCode(annotation.code());
        dict.setDataValueType(dataValueType);
        dict.setBuiltIn(1);
        dict.setStatus(1);
        dict.setDeleted(0);
        dictMapper.insert(dict);

        for (int i = 0; i < constants.length; i++) {
            DictionaryElement<?> element = constants[i];
            SysDictItem item = new SysDictItem();
            item.setDictItemId(uidService.next());
            item.setDictId(dictId);
            item.setItemKey(element.getCode());
            item.setItemLabel(element.getMessage());
            item.setItemValue(String.valueOf(element.getValue()));
            item.setSortOrder(i);
            item.setBuiltIn(1);
            item.setStatus(1);
            item.setDeleted(0);
            dictItemMapper.insert(item);
        }
    }
}

private String inferDataValueType(Object value) {
    if (value instanceof Integer || value instanceof Long) {
        return "NUMBER";
    } else if (value instanceof Boolean) {
        return "BOOLEAN";
    }
    return "STRING";
}
```

- [ ] **Step 5: 实现 syncBuiltinDicts()**

```java
@Transactional
public void syncBuiltinDicts() {
    log.info("开始同步内置字典...");
    List<Class<? extends DictionaryElement<?>>> enumClasses = scanDictionaryEnums();
    log.info("扫描发现 {} 个 @Dictionary 枚举", enumClasses.size());

    deleteBuiltinDicts();
    insertBuiltinDicts(enumClasses);

    log.info("内置字典同步完成");
}
```

- [ ] **Step 6: 运行测试验证绿灯**

Run: `./mvnw test -pl backend -Dtest=DictSyncRunnerTest`
Expected: ALL PASS

- [ ] **Step 7: Commit**

```bash
git add backend/src/main/java/com/dfec/soft/secret/common/config/DictSyncRunner.java
git add backend/src/test/java/com/dfec/soft/secret/common/config/DictSyncRunnerTest.java
git commit -m "feat(dict): implement DictSyncRunner for auto-discovery and registration"
```

---

## Task 7: 全量验证

- [ ] **Step 1: 运行全部测试**

Run: `./mvnw test -pl backend`
Expected: ALL PASS

- [ ] **Step 2: 运行静态分析**

Run: `./mvnw compile -pl backend`
Expected: BUILD SUCCESS（PMD + P3C 通过）

- [ ] **Step 3: 本地启动验证（可选）**

Run: `./mvnw spring-boot:run -pl backend`
Expected: 日志输出"开始同步内置字典..."、"扫描发现 5 个 @Dictionary 枚举"、"内置字典同步完成"

- [ ] **Step 4: Final Commit（如有未提交改动）**

```bash
git status
git add -A
git commit -m "feat(dict): dict auto-discovery and registration complete"
```
