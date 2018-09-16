package org.mybatis.generator.api.dom.xml;

/**
 * @author Jeff Butler
 */
public class Attribute {

    private String name;

    private String value;

    public String getFormattedContent() {

        return name + "=\"" + value + '\"';
    }

    public Attribute(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }
}
