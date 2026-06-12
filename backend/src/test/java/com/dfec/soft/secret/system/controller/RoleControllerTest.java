package com.dfec.soft.secret.system.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.dfec.soft.secret.common.dto.common.R;
import com.dfec.soft.secret.system.dto.common.RoleDTO;
import com.dfec.soft.secret.system.entity.SysRole;
import com.dfec.soft.secret.system.mapper.RoleMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Arrays;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

/**
 * 角色控制器测试。
 *
 * @author zhangth
 * @since 1.0.0
 */
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class RoleControllerTest {

    private final MockMvc mockMvc;
    private final RoleMapper roleMapper;
    private final ObjectMapper objectMapper;

    RoleControllerTest(MockMvc mockMvc, RoleMapper roleMapper, ObjectMapper objectMapper) {
        this.mockMvc = mockMvc;
        this.roleMapper = roleMapper;
        this.objectMapper = objectMapper;
    }

    @BeforeEach
    void setUp() {
        roleMapper.delete(new LambdaQueryWrapper<>());
    }

    @Test
    void shouldCreateAndGetRole() throws Exception {
        RoleDTO request = new RoleDTO();
        request.setRoleName("测试角色");
        request.setRoleCode("test_role");

        String responseJson = mockMvc.perform(post("/roles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.data.roleName").value("测试角色"))
            .andReturn().getResponse().getContentAsString();

        R<?> response = objectMapper.readValue(responseJson, R.class);
        String roleId = objectMapper.convertValue(response.getData(),
            java.util.Map.class).get("roleId").toString();

        mockMvc.perform(get("/roles/" + roleId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.data.roleName").value("测试角色"));
    }

    @Test
    void shouldReturnEmptyPage() throws Exception {
        mockMvc.perform(get("/roles")
                .param("page", "1")
                .param("size", "10"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.data.total").value(0));
    }

    @Test
    void shouldDeleteRole() throws Exception {
        RoleDTO request = new RoleDTO();
        request.setRoleName("待删除");
        request.setRoleCode("to_delete");

        String responseJson = mockMvc.perform(post("/roles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andReturn().getResponse().getContentAsString();

        R<?> response = objectMapper.readValue(responseJson, R.class);
        String roleId = objectMapper.convertValue(response.getData(),
            java.util.Map.class).get("roleId").toString();

        mockMvc.perform(delete("/roles/" + roleId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.data").value(roleId));
    }

    @Test
    void shouldRejectDuplicateRoleCode() throws Exception {
        RoleDTO first = new RoleDTO();
        first.setRoleName("角色A");
        first.setRoleCode("dup_code");
        mockMvc.perform(post("/roles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(first)))
            .andExpect(status().isOk());

        RoleDTO second = new RoleDTO();
        second.setRoleName("角色B");
        second.setRoleCode("dup_code");
        mockMvc.perform(post("/roles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(second)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(409));
    }

    @Test
    void shouldBatchDeleteSkipBuiltin() throws Exception {
        RoleDTO normal = new RoleDTO();
        normal.setRoleName("普通角色");
        normal.setRoleCode("normal_batch");
        String normalJson = mockMvc.perform(post("/roles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(normal)))
            .andReturn().getResponse().getContentAsString();
        R<?> normalResp = objectMapper.readValue(normalJson, R.class);
        String normalId = objectMapper.convertValue(normalResp.getData(),
            java.util.Map.class).get("roleId").toString();

        RoleDTO builtin = new RoleDTO();
        builtin.setRoleName("内置角色");
        builtin.setRoleCode("builtin_batch");
        String builtinJson = mockMvc.perform(post("/roles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(builtin)))
            .andReturn().getResponse().getContentAsString();
        R<?> builtinResp = objectMapper.readValue(builtinJson, R.class);
        String builtinId = objectMapper.convertValue(builtinResp.getData(),
            java.util.Map.class).get("roleId").toString();

        SysRole entity = roleMapper.selectById(builtinId);
        entity.setBuiltIn(1);
        roleMapper.updateById(entity);

        mockMvc.perform(delete("/roles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Arrays.asList(normalId, builtinId))))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.data.length()").value(1))
            .andExpect(jsonPath("$.data[0]").value(normalId));
    }

    @Test
    void shouldUpdateRole() throws Exception {
        RoleDTO create = new RoleDTO();
        create.setRoleName("原名称");
        create.setRoleCode("upd_code");
        String createJson = mockMvc.perform(post("/roles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(create)))
            .andReturn().getResponse().getContentAsString();
        R<?> createResp = objectMapper.readValue(createJson, R.class);
        String roleId = objectMapper.convertValue(createResp.getData(),
            java.util.Map.class).get("roleId").toString();

        RoleDTO update = new RoleDTO();
        update.setRoleName("新名称");
        update.setRoleCode("upd_code");

        mockMvc.perform(put("/roles/" + roleId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(update)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.data.roleName").value("新名称"));
    }
}
