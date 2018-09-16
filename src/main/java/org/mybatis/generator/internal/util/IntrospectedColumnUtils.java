package org.mybatis.generator.internal.util;

import org.mybatis.generator.api.IntrospectedColumn;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Sky Yeung
 */
public class IntrospectedColumnUtils {

    public static List<String> getActualColumnNames(List<IntrospectedColumn> columns) {
        List<String> columnNames = new ArrayList<>();
        if (columns != null && !columns.isEmpty()) {
            for (IntrospectedColumn column : columns) {
                columnNames.add(column.getActualColumnName());
            }
        }
        return columnNames;
    }

    public static Map<String, String> getJavaPropertyNames(List<IntrospectedColumn> columns) {
        Map<String, String> columnNames = new HashMap<>();
        if (columns != null && !columns.isEmpty()) {
            for (IntrospectedColumn column : columns) {
                columnNames.put(column.getActualColumnName(), column.getJavaProperty());
            }
        }
        return columnNames;
    }
}
