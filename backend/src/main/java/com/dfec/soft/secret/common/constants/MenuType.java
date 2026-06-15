package com.dfec.soft.secret.common.constants;

/**
 * 菜单类型。
 *
 * @author zhangth
 * @since 1.0.0
 */
@Dictionary(name = "菜单类型", code = "menu_type")
public enum MenuType implements DictionaryElement<String> {
    //@formatter:off
    DIRECTORY("D", "目录"),
    MENU("M", "菜单"),
    BUTTON("B", "按钮"),
    //@formatter:on
    ;

    private final String value;
    private final String message;

    MenuType(String value, String message) { this.value = value; this.message = message; }

    @Override public String getCode() { return name(); }
    @Override public String getMessage() { return message; }
    @Override public String getValue() { return value; }
}
