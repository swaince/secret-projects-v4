package com.dfec.soft.secret.system.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.dfec.soft.secret.common.exception.OuterException;
import com.dfec.soft.secret.system.dto.common.DeptDTO;
import com.dfec.soft.secret.system.entity.SysDept;
import com.dfec.soft.secret.system.mapper.DeptMapper;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * 部门服务测试。
 *
 * @author zhangth
 * @since 1.0.0
 */
@SpringBootTest
@AutoConfigureTestDatabase(replace = Replace.ANY)
class DeptServiceImplTest {

    private final DeptService deptService;
    private final DeptMapper deptMapper;

    DeptServiceImplTest(DeptService deptService, DeptMapper deptMapper) {
        this.deptService = deptService;
        this.deptMapper = deptMapper;
    }

    @BeforeEach
    void setUp() {
        deptMapper.delete(new LambdaQueryWrapper<>());
    }

    @Test
    void shouldReturnTreeHierarchy() {
        String rootId = createDept("总公司", "root", null);
        String childId = createDept("技术部", "tech", rootId);
        createDept("前端组", "frontend", childId);

        List<DeptDTO> tree = deptService.tree();

        assertThat(tree).hasSize(1);
        assertThat(tree.get(0).getDeptName()).isEqualTo("总公司");
        assertThat(tree.get(0).getChildren()).hasSize(1);
        assertThat(tree.get(0).getChildren().get(0).getDeptName()).isEqualTo("技术部");
        assertThat(tree.get(0).getChildren().get(0).getChildren()).hasSize(1);
        assertThat(tree.get(0).getChildren().get(0).getChildren().get(0).getDeptName()).isEqualTo("前端组");
    }

    @Test
    void shouldCreateRootDept() {
        DeptDTO request = new DeptDTO();
        request.setDeptName("总公司");
        request.setDeptCode("root");

        DeptDTO result = deptService.create(request, "test-user");

        assertThat(result.getDeptId()).isNotNull();
        assertThat(result.getDeptName()).isEqualTo("总公司");
        assertThat(result.getParentId()).isNull();
        assertThat(deptMapper.selectById(result.getDeptId())).isNotNull();
    }

    @Test
    void shouldCreateChildDept() {
        String parentId = createDept("总公司", "root", null);

        DeptDTO request = new DeptDTO();
        request.setDeptName("技术部");
        request.setDeptCode("tech");
        request.setParentId(parentId);

        DeptDTO result = deptService.create(request, "test-user");

        assertThat(result.getDeptId()).isNotNull();
        assertThat(result.getParentId()).isEqualTo(parentId);
    }

    @Test
    void shouldRejectDuplicateDeptCode() {
        createDept("部门A", "dup_code", null);

        DeptDTO request = new DeptDTO();
        request.setDeptName("部门B");
        request.setDeptCode("dup_code");

        assertThatThrownBy(() -> deptService.create(request, "test-user"))
            .isInstanceOf(OuterException.class)
            .hasMessageContaining("部门编码已存在");
    }

    @Test
    void shouldUpdateDept() {
        String deptId = createDept("原名称", "update_code", null);

        DeptDTO request = new DeptDTO();
        request.setDeptName("新名称");
        request.setDeptCode("update_code");
        request.setRemark("备注");

        DeptDTO result = deptService.update(deptId, request, "test-user");

        assertThat(result.getDeptName()).isEqualTo("新名称");
        assertThat(result.getRemark()).isEqualTo("备注");
    }

    @Test
    void shouldRejectUpdateBuiltinDept() {
        String deptId = createBuiltinDept("内置部门", "builtin_code", null);

        DeptDTO request = new DeptDTO();
        request.setDeptName("修改名称");
        request.setDeptCode("builtin_code");

        assertThatThrownBy(() -> deptService.update(deptId, request, "test-user"))
            .isInstanceOf(OuterException.class)
            .hasMessageContaining("内置部门不可修改");
    }

    @Test
    void shouldDeleteLeafDept() {
        String deptId = createDept("待删除", "delete_code", null);

        String deletedId = deptService.deleteById(deptId, "test-user");

        assertThat(deletedId).isEqualTo(deptId);
        assertThat(deptMapper.selectById(deptId)).isNull();
    }

    @Test
    void shouldRejectDeleteDeptWithChildren() {
        String parentId = createDept("父部门", "parent_code", null);
        createDept("子部门", "child_code", parentId);

        assertThatThrownBy(() -> deptService.deleteById(parentId, "test-user"))
            .isInstanceOf(OuterException.class)
            .hasMessageContaining("存在子部门，无法删除");
    }

    @Test
    void shouldRejectDeleteBuiltinDept() {
        String deptId = createBuiltinDept("内置部门", "builtin_del", null);

        assertThatThrownBy(() -> deptService.deleteById(deptId, "test-user"))
            .isInstanceOf(OuterException.class)
            .hasMessageContaining("内置部门不可删除");
    }

    @Test
    void shouldBatchDeleteSkipBuiltinAndWithChildren() {
        String leafId = createDept("叶子部门", "leaf_code", null);
        String builtinId = createBuiltinDept("内置部门", "builtin_batch", null);
        String parentId = createDept("父部门", "parent_batch", null);
        createDept("子部门", "child_batch", parentId);

        List<String> ids = Arrays.asList(leafId, builtinId, parentId);

        List<String> result = deptService.delete(ids, "test-user");

        assertThat(result).containsExactly(leafId);
        assertThat(deptMapper.selectById(leafId)).isNull();
        assertThat(deptMapper.selectById(builtinId)).isNotNull();
        assertThat(deptMapper.selectById(parentId)).isNotNull();
    }

    private String createDept(String deptName, String deptCode, String parentId) {
        DeptDTO request = new DeptDTO();
        request.setDeptName(deptName);
        request.setDeptCode(deptCode);
        request.setParentId(parentId);
        return deptService.create(request, "test-user").getDeptId();
    }

    private String createBuiltinDept(String deptName, String deptCode, String parentId) {
        String deptId = createDept(deptName, deptCode, parentId);
        SysDept entity = deptMapper.selectById(deptId);
        entity.setBuiltIn(1);
        deptMapper.updateById(entity);
        return deptId;
    }
}
