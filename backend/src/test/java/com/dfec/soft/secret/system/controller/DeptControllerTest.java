package com.dfec.soft.secret.system.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.dfec.soft.secret.common.dto.common.R;
import com.dfec.soft.secret.system.dto.common.DeptDTO;
import com.dfec.soft.secret.system.entity.SysDept;
import com.dfec.soft.secret.system.mapper.DeptMapper;
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
 * 部门控制器测试。
 *
 * @author zhangth
 * @since 1.0.0
 */
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class DeptControllerTest {

    private final MockMvc mockMvc;
    private final DeptMapper deptMapper;
    private final ObjectMapper objectMapper;

    DeptControllerTest(MockMvc mockMvc, DeptMapper deptMapper, ObjectMapper objectMapper) {
        this.mockMvc = mockMvc;
        this.deptMapper = deptMapper;
        this.objectMapper = objectMapper;
    }

    @BeforeEach
    void setUp() {
        deptMapper.delete(new LambdaQueryWrapper<>());
    }

    @Test
    void shouldCreateAndGetDept() throws Exception {
        DeptDTO request = new DeptDTO();
        request.setDeptName("测试部门");
        request.setDeptCode("test_dept");

        String responseJson = mockMvc.perform(post("/depts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.data.deptName").value("测试部门"))
            .andReturn().getResponse().getContentAsString();

        R<?> response = objectMapper.readValue(responseJson, R.class);
        String deptId = objectMapper.convertValue(response.getData(),
            java.util.Map.class).get("deptId").toString();

        mockMvc.perform(get("/depts/" + deptId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.data.deptName").value("测试部门"));
    }

    @Test
    void shouldReturnTree() throws Exception {
        DeptDTO root = new DeptDTO();
        root.setDeptName("总公司");
        root.setDeptCode("root");

        String rootJson = mockMvc.perform(post("/depts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(root)))
            .andReturn().getResponse().getContentAsString();
        R<?> rootResp = objectMapper.readValue(rootJson, R.class);
        String rootId = objectMapper.convertValue(rootResp.getData(),
            java.util.Map.class).get("deptId").toString();

        DeptDTO child = new DeptDTO();
        child.setDeptName("技术部");
        child.setDeptCode("tech");
        child.setParentId(rootId);
        mockMvc.perform(post("/depts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(child)))
            .andExpect(status().isOk());

        mockMvc.perform(get("/depts"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.data.length()").value(1))
            .andExpect(jsonPath("$.data[0].deptName").value("总公司"))
            .andExpect(jsonPath("$.data[0].children.length()").value(1))
            .andExpect(jsonPath("$.data[0].children[0].deptName").value("技术部"));
    }

    @Test
    void shouldDeleteDept() throws Exception {
        DeptDTO request = new DeptDTO();
        request.setDeptName("待删除");
        request.setDeptCode("to_delete");

        String responseJson = mockMvc.perform(post("/depts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andReturn().getResponse().getContentAsString();

        R<?> response = objectMapper.readValue(responseJson, R.class);
        String deptId = objectMapper.convertValue(response.getData(),
            java.util.Map.class).get("deptId").toString();

        mockMvc.perform(delete("/depts/" + deptId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.data").value(deptId));
    }

    @Test
    void shouldRejectDuplicateDeptCode() throws Exception {
        DeptDTO first = new DeptDTO();
        first.setDeptName("部门A");
        first.setDeptCode("dup_code");
        mockMvc.perform(post("/depts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(first)))
            .andExpect(status().isOk());

        DeptDTO second = new DeptDTO();
        second.setDeptName("部门B");
        second.setDeptCode("dup_code");
        mockMvc.perform(post("/depts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(second)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(409));
    }

    @Test
    void shouldBatchDeleteSkipBuiltinAndWithChildren() throws Exception {
        DeptDTO normal = new DeptDTO();
        normal.setDeptName("普通部门");
        normal.setDeptCode("normal_batch");
        String normalJson = mockMvc.perform(post("/depts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(normal)))
            .andReturn().getResponse().getContentAsString();
        R<?> normalResp = objectMapper.readValue(normalJson, R.class);
        String normalId = objectMapper.convertValue(normalResp.getData(),
            java.util.Map.class).get("deptId").toString();

        DeptDTO builtin = new DeptDTO();
        builtin.setDeptName("内置部门");
        builtin.setDeptCode("builtin_batch");
        String builtinJson = mockMvc.perform(post("/depts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(builtin)))
            .andReturn().getResponse().getContentAsString();
        R<?> builtinResp = objectMapper.readValue(builtinJson, R.class);
        String builtinId = objectMapper.convertValue(builtinResp.getData(),
            java.util.Map.class).get("deptId").toString();

        SysDept entity = deptMapper.selectById(builtinId);
        entity.setBuiltIn(1);
        deptMapper.updateById(entity);

        mockMvc.perform(delete("/depts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Arrays.asList(normalId, builtinId))))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.data.length()").value(1))
            .andExpect(jsonPath("$.data[0]").value(normalId));
    }

    @Test
    void shouldUpdateDept() throws Exception {
        DeptDTO create = new DeptDTO();
        create.setDeptName("原名称");
        create.setDeptCode("upd_code");
        String createJson = mockMvc.perform(post("/depts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(create)))
            .andReturn().getResponse().getContentAsString();
        R<?> createResp = objectMapper.readValue(createJson, R.class);
        String deptId = objectMapper.convertValue(createResp.getData(),
            java.util.Map.class).get("deptId").toString();

        DeptDTO update = new DeptDTO();
        update.setDeptName("新名称");
        update.setDeptCode("upd_code");
        update.setRemark("新备注");

        mockMvc.perform(put("/depts/" + deptId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(update)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.data.deptName").value("新名称"))
            .andExpect(jsonPath("$.data.remark").value("新备注"));
    }
}
