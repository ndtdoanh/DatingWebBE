package org.kiennguyenfpt.datingapp.enums;

public enum ROLE {

    ADMIN("Admin", "Administration"),
    USER("User", "User");

    private static final ROLE[] VALUES;

    static {
        VALUES = values();
    }

    private final String value;
    private final String description;

    ROLE(String value, String description) {
        this.value = value;
        this.description = description;
    }

    public String value() {
        return this.value;
    }

    public String description() {
        return this.description;
    }

    public static ROLE valueOfProperty(String name) {
        ROLE property = resolveProperty(name);
        if (property == null) {
            throw new IllegalArgumentException("No matching constant for [" + name + "]");
        }
        return property;
    }

    public static ROLE resolveProperty(String name) {
        for (ROLE property : VALUES) {
            if (property.value.equals(name)) { // Sử dụng .equals() để so sánh chuỗi
                return property;
            }
        }
        return null;
    }

}
