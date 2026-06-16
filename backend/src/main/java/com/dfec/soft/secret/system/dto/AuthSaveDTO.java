package com.dfec.soft.secret.system.dto;

import java.util.List;

/**
 * 菜单授权保存请求 DTO。
 *
 * @author zhangth
 * @since 1.0.0
 */
public class AuthSaveDTO {
    /**
     * 主体ID列表。
     */
    private List<String> subjectIds;

    /**
     * 菜单ID列表。
     */
    private List<String> menuIds;

    public List<String> getSubjectIds() { return subjectIds; }
    public void setSubjectIds(List<String> subjectIds) { this.subjectIds = subjectIds; }
    public List<String> getMenuIds() { return menuIds; }
    public void setMenuIds(List<String> menuIds) { this.menuIds = menuIds; }

    @Override
    public String toString() {
        return "AuthSaveDTO{subjectIds=" + subjectIds + ", menuIds=" + menuIds + "}";
    }
}
