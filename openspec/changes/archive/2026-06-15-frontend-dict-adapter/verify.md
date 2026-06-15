## Verification Report: frontend-dict-adapter

### Summary

| 维度 | 状态 |
|------|------|
| Completeness | 25/25 tasks complete, 9 requirements covered |
| Correctness | 35 PASS / 2 FAIL / 1 WARNING |
| Coherence | 6/6 design decisions followed |

---

### CRITICAL (必须修复)

1. **字典项查询未过滤 status=1**
   - `backend/src/main/java/com/dfec/soft/secret/system/service/impl/DictItemServiceImpl.java:60-67`
   - `listByDictId` 方法只过滤了 `deleted=0`，未过滤 `status=1`
   - Spec 要求：「仅返回 status=1 且 deleted=0 的启用项」
   - **修复**：在 `listByDictId` 的 LambdaQueryWrapper 中添加 `.eq(SysDictItem::getStatus, 1)`

2. **缺少测试场景：字典存在但无启用字典项**
   - `backend/src/test/java/com/dfec/soft/secret/system/controller/DictItemByCodeTest.java`
   - Spec 要求场景「字典存在但所有字典项均为 status=0 或 deleted=1 → items 为空列表」未覆盖
   - **修复**：新增测试用例，插入一条 status=0 的字典项，验证返回 items 为空

---

### WARNING (建议修复)

1. **缺少 dictCode 参数时响应码不一致**
   - `backend/src/test/java/.../DictItemByCodeTest.java:98-99`
   - Spec 要求返回 code=400，实际可能返回 code=500（全局异常处理器将 `@NotBlank` 校验异常映射为 500）
   - **修复**：检查 `GlobalExceptionHandler` 是否正确处理 `HandlerMethodValidationException` → 400，或更新 spec 匹配实际行为

---

### SUGGESTION (可选改进)

无

---

### Final Assessment

**2 个 CRITICAL 问题需修复后才能归档。**

主要风险：`listByDictId` 返回禁用状态的字典项，业务页面下拉框会显示不应出现的选项。
