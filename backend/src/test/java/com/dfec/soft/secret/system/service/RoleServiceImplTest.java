package com.dfec.soft.secret.system.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.dfec.soft.secret.common.dto.common.PageResponse;
import com.dfec.soft.secret.common.exception.OuterException;
import com.dfec.soft.secret.system.dto.common.RoleDTO;
import com.dfec.soft.secret.system.dto.request.RolePageRequest;
import com.dfec.soft.secret.system.entity.SysRole;
import com.dfec.soft.secret.system.mapper.RoleMapper;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * 角色服务测试。
 *
 * @author zhangth
 * @since 1.0.0
 */
@SpringBootTest
@AutoConfigureTestDatabase(replace = Replace.ANY)
class RoleServiceImplTest {

    private final RoleService roleService;
    private final RoleMapper roleMapper;

    RoleServiceImplTest(RoleService roleService, RoleMapper roleMapper) {
        this.roleService = roleService;
        this.roleMapper = roleMapper;
    }

    @BeforeEach
    void setUp() {
        roleMapper.delete(new LambdaQueryWrapper<>());
    }

    @Test
    void shouldPageRoles() {
        createRole("角色A", "code_a");
        createRole("角色B", "code_b");

        RolePageRequest request = new RolePageRequest();
        request.setPage(1L);
        request.setSize(10L);

        PageResponse<RoleDTO> response = roleService.page(request);

        assertThat(response.getRecords()).hasSize(2);
        assertThat(response.getTotal()).isEqualTo(2L);
        assertThat(response.getPage()).isEqualTo(1L);
        assertThat(response.getSize()).isEqualTo(10L);
    }

    @Test
    void shouldCreateRole() {
        RoleDTO request = new RoleDTO();
        request.setRoleName("管理员");
        request.setRoleCode("admin");
        request.setSortOrder(3);
        request.setRemark("管理员角色");

        RoleDTO result = roleService.create(request, "test-user");

        assertThat(result.getRoleId()).isNotNull();
        assertThat(result.getRoleName()).isEqualTo("管理员");
        assertThat(result.getRoleCode()).isEqualTo("admin");
        assertThat(roleMapper.selectById(result.getRoleId())).isNotNull();
    }

    @Test
    void shouldRejectDuplicateRoleCode() {
        createRole("角色A", "dup_code");

        RoleDTO request = new RoleDTO();
        request.setRoleName("角色B");
        request.setRoleCode("dup_code");

        assertThatThrownBy(() -> roleService.create(request, "test-user"))
            .isInstanceOf(OuterException.class)
            .hasMessageContaining("角色编码已存在");
    }

    @Test
    void shouldUpdateRole() {
        String roleId = createRole("原名称", "update_code");

        RoleDTO request = new RoleDTO();
        request.setRoleName("新名称");
        request.setRoleCode("update_code");

        RoleDTO result = roleService.update(roleId, request, "test-user");

        assertThat(result.getRoleName()).isEqualTo("新名称");
    }

    @Test
    void shouldRejectUpdateBuiltinRole() {
        String roleId = createBuiltinRole("内置角色", "builtin_code");

        RoleDTO request = new RoleDTO();
        request.setRoleName("修改名称");
        request.setRoleCode("builtin_code");

        assertThatThrownBy(() -> roleService.update(roleId, request, "test-user"))
            .isInstanceOf(OuterException.class)
            .hasMessageContaining("内置角色不可修改");
    }

    @Test
    void shouldDeleteRole() {
        String roleId = createRole("待删除", "delete_code");

        String deletedId = roleService.deleteById(roleId, "test-user");

        assertThat(deletedId).isEqualTo(roleId);
        assertThat(roleMapper.selectById(roleId)).isNull();
    }

    @Test
    void shouldRejectDeleteBuiltinRole() {
        String roleId = createBuiltinRole("内置角色", "builtin_del");

        assertThatThrownBy(() -> roleService.deleteById(roleId, "test-user"))
            .isInstanceOf(OuterException.class)
            .hasMessageContaining("内置角色不可删除");
    }

    @Test
    void shouldBatchDeleteSkipBuiltin() {
        String normalId = createRole("普通角色", "normal_code");
        String builtinId = createBuiltinRole("内置角色", "builtin_batch");
        List<String> ids = Arrays.asList(normalId, builtinId);

        List<String> result = roleService.delete(ids, "test-user");

        assertThat(result).containsExactly(normalId);
        assertThat(roleMapper.selectById(normalId)).isNull();
        assertThat(roleMapper.selectById(builtinId)).isNotNull();
    }

    private String createRole(String roleName, String roleCode) {
        RoleDTO request = new RoleDTO();
        request.setRoleName(roleName);
        request.setRoleCode(roleCode);
        return roleService.create(request, "test-user").getRoleId();
    }

    private String createBuiltinRole(String roleName, String roleCode) {
        RoleDTO request = new RoleDTO();
        request.setRoleName(roleName);
        request.setRoleCode(roleCode);
        String roleId = roleService.create(request, "test-user").getRoleId();
        SysRole entity = roleMapper.selectById(roleId);
        entity.setBuiltIn(1);
        roleMapper.updateById(entity);
        return roleId;
    }
}
