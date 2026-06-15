package com.dfec.soft.secret.system.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.dfec.soft.secret.common.dto.common.R;
import com.dfec.soft.secret.system.dto.common.MenuDTO;
import com.dfec.soft.secret.system.mapper.MenuMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

/**
 * 菜单管理接口测试。
 *
 * @author zhangth
 * @since 1.0.0
 */
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class MenuControllerTest {

    private final MockMvc mockMvc;
    private final MenuMapper menuMapper;
    private final ObjectMapper objectMapper;

    MenuControllerTest(MockMvc mockMvc, MenuMapper menuMapper, ObjectMapper objectMapper) {
        this.mockMvc = mockMvc;
        this.menuMapper = menuMapper;
        this.objectMapper = objectMapper;
    }

    @BeforeEach
    void setUp() {
        menuMapper.delete(new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<>());
    }

    @Test
    void shouldCreateDirectory() throws Exception {
        MenuDTO menu = new MenuDTO();
        menu.setMenuName("系统管理");
        menu.setMenuType("D");
        menu.setIcon("Settings");
        menu.setSortOrder(1);

        mockMvc.perform(post("/menus")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(menu)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.data.menuId").exists())
            .andExpect(jsonPath("$.data.menuName").value("系统管理"))
            .andExpect(jsonPath("$.data.menuType").value("D"));
    }

    @Test
    void shouldCreateMenu() throws Exception {
        MenuDTO parent = new MenuDTO();
        parent.setMenuName("系统管理");
        parent.setMenuType("D");
        parent.setSortOrder(1);

        String parentJson = mockMvc.perform(post("/menus")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(parent)))
            .andReturn().getResponse().getContentAsString();

        String parentId = extractMenuId(parentJson);

        MenuDTO menu = new MenuDTO();
        menu.setMenuName("用户管理");
        menu.setMenuType("M");
        menu.setParentId(parentId);
        menu.setPath("/system/users");
        menu.setComponent("system/UserView");
        menu.setIcon("Users");
        menu.setSortOrder(1);

        mockMvc.perform(post("/menus")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(menu)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.data.menuName").value("用户管理"))
            .andExpect(jsonPath("$.data.menuType").value("M"))
            .andExpect(jsonPath("$.data.parentId").value(parentId));
    }

    @Test
    void shouldCreateButton() throws Exception {
        MenuDTO parent = new MenuDTO();
        parent.setMenuName("用户管理");
        parent.setMenuType("M");
        parent.setSortOrder(1);

        String parentJson = mockMvc.perform(post("/menus")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(parent)))
            .andReturn().getResponse().getContentAsString();

        String parentId = extractMenuId(parentJson);

        MenuDTO button = new MenuDTO();
        button.setMenuName("新增用户");
        button.setMenuType("B");
        button.setParentId(parentId);
        button.setPermission("system:user:create");
        button.setSortOrder(1);

        mockMvc.perform(post("/menus")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(button)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.data.menuType").value("B"))
            .andExpect(jsonPath("$.data.permission").value("system:user:create"));
    }

    @Test
    void shouldQueryTree() throws Exception {
        MenuDTO dir = new MenuDTO();
        dir.setMenuName("系统管理");
        dir.setMenuType("D");
        dir.setSortOrder(1);

        String dirJson = mockMvc.perform(post("/menus")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dir)))
            .andReturn().getResponse().getContentAsString();

        String dirId = extractMenuId(dirJson);

        MenuDTO child = new MenuDTO();
        child.setMenuName("用户管理");
        child.setMenuType("M");
        child.setParentId(dirId);
        child.setPath("/system/users");
        child.setSortOrder(1);

        mockMvc.perform(post("/menus")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(child)))
            .andExpect(status().isOk());

        mockMvc.perform(get("/menus/tree"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.data.length()").value(1))
            .andExpect(jsonPath("$.data[0].menuName").value("系统管理"))
            .andExpect(jsonPath("$.data[0].children.length()").value(1))
            .andExpect(jsonPath("$.data[0].children[0].menuName").value("用户管理"));
    }

    @Test
    void shouldUpdateMenu() throws Exception {
        MenuDTO menu = new MenuDTO();
        menu.setMenuName("旧名称");
        menu.setMenuType("M");
        menu.setPath("/old");
        menu.setSortOrder(1);

        String responseJson = mockMvc.perform(post("/menus")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(menu)))
            .andReturn().getResponse().getContentAsString();

        String menuId = extractMenuId(responseJson);

        MenuDTO updateRequest = new MenuDTO();
        updateRequest.setMenuName("新名称");
        updateRequest.setPath("/new");

        mockMvc.perform(put("/menus/" + menuId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.data.menuName").value("新名称"))
            .andExpect(jsonPath("$.data.path").value("/new"));
    }

    @Test
    void shouldCascadeDelete() throws Exception {
        MenuDTO dir = new MenuDTO();
        dir.setMenuName("系统管理");
        dir.setMenuType("D");
        dir.setSortOrder(1);

        String dirJson = mockMvc.perform(post("/menus")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dir)))
            .andReturn().getResponse().getContentAsString();

        String dirId = extractMenuId(dirJson);

        MenuDTO child = new MenuDTO();
        child.setMenuName("用户管理");
        child.setMenuType("M");
        child.setParentId(dirId);
        child.setPath("/system/users");
        child.setSortOrder(1);

        mockMvc.perform(post("/menus")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(child)))
            .andExpect(status().isOk());

        mockMvc.perform(delete("/menus/" + dirId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.data").value(dirId));

        mockMvc.perform(get("/menus"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.length()").value(0));
    }

    /**
     * 验证更新菜单时 parentId 不能等于自身。
     */
    @Test
    void shouldRejectParentIdEqualsSelf() throws Exception {
        MenuDTO menu = new MenuDTO();
        menu.setMenuName("系统管理");
        menu.setMenuType("D");
        menu.setSortOrder(1);

        String responseJson = mockMvc.perform(post("/menus")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(menu)))
            .andReturn().getResponse().getContentAsString();

        String menuId = extractMenuId(responseJson);

        MenuDTO updateRequest = new MenuDTO();
        updateRequest.setParentId(menuId);

        mockMvc.perform(put("/menus/" + menuId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(400));
    }

    /**
     * 验证无菜单时树接口返回空数组。
     */
    @Test
    void shouldReturnEmptyTreeWhenNoMenus() throws Exception {
        mockMvc.perform(get("/menus/tree"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.data").isArray())
            .andExpect(jsonPath("$.data.length()").value(0));
    }

    @SuppressWarnings("unchecked")
    private String extractMenuId(String responseJson) throws Exception {
        R<?> response = objectMapper.readValue(responseJson, R.class);
        return ((Map<String, Object>) objectMapper.convertValue(response.getData(), Map.class))
                .get("menuId").toString();
    }
}
