package io.postgers.cdc.enums;

/**
 * @version 1.0
 * @Author xjh
 * @email gavinxiao@gmail.com
 * @github https://github.com/11000100111010101100111
 * @Date 2025/3/21 23:25
 * @description
 */
public enum CDCType {
    INSERT("c"),
    UPDATE("u"),
    DELETE("d"),
    REPLACE("r"),
    ALTER_TABLE(""),
    UN_KNOW("-"),
    ;
    String code;
    CDCType(String code) {
        this.code = code;
    }

    public static CDCType of(String code) {
        if (null == code) {
            return UN_KNOW;
        }
        code = code.trim().toLowerCase();
        for (CDCType value : values()) {
            if (value.code.equals(code)) {
                return value;
            }
        }
        return UN_KNOW;
    }
}
