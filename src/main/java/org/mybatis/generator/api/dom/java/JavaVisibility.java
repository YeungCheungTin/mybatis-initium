package org.mybatis.generator.api.dom.java;

/**
 * Typesafe enum of possible Java visibility settings
 * 
 * @author Jeff Butler
 */
public enum JavaVisibility {
    
    PUBLIC("public "),
    PRIVATE("private "),
    PROTECTED("protected "),
    DEFAULT("");

    private String value;

    JavaVisibility(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
