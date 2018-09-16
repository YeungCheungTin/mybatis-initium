package org.mybatis.generator.config;

import static org.mybatis.generator.internal.util.messages.Messages.getString;

/**
 * Typesafe enum of different model types
 *
 * @author Jeff Butler
 */
public enum ModelType {
    HIERARCHICAL("hierarchical"),
    FLAT("flat"),
    CONDITIONAL("conditional");

    private final String modelType;

    ModelType(String modelType) {
        this.modelType = modelType;
    }

    public String getModelType() {
        return modelType;
    }

    public static ModelType getModelType(String type) {
        if (HIERARCHICAL.getModelType().equalsIgnoreCase(type)) {
            return HIERARCHICAL;
        } else if (FLAT.getModelType().equalsIgnoreCase(type)) {
            return FLAT;
        } else if (CONDITIONAL.getModelType().equalsIgnoreCase(type)) {
            return CONDITIONAL;
        } else {
            throw new RuntimeException(getString("RuntimeError.13", type));
        }
    }
}
