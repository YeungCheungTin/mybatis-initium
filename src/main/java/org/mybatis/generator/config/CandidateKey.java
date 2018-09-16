package org.mybatis.generator.config;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.internal.util.StringUtility;

import java.util.List;
import java.util.Map;

/**
 * 候选码配置
 *
 * @author YangXiangTian
 */
public class CandidateKey extends PropertyHolder {

    private String name;

    private List<String> columnNames;

    private List<String> ignoreColumns;

    public String getName() {
        return StringUtility.upperFirstLetter(name);
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getColumnNames() {
        return columnNames;
    }

    public void setColumnNames(List<String> columnNames) {
        this.columnNames = columnNames;
    }

    public boolean isSetName() {
        return this.name != null && !"".equals(this.name.trim());
    }

    public String getMethodSuffix(List<String> actualColumnNames, Map<String, String> javaPropertyNames) {
        StringBuilder sb = new StringBuilder();
        for (String columnName : getColumnNames()) {
            if (actualColumnNames.contains(columnName)) {
                sb.append(StringUtility.upperFirstLetter(javaPropertyNames.get(columnName)));
            } else {
                System.err.println("[配置错误]: 字段" + columnName + "不存在，由候选码" + this + "组成的update方法未被生成，请检查配置");
                return null;
            }
        }
        String candidateName;
        if (this.isSetName()) {
            candidateName = this.getName();
        } else {
            candidateName = sb.toString();
        }
        return candidateName;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("(");
        for (String columnName : getColumnNames()) {
            stringBuilder.append(columnName).append(",");
        }
        stringBuilder.deleteCharAt(stringBuilder.lastIndexOf(","));
        stringBuilder.append(")");
        return stringBuilder.toString();
    }

    public List<String> getIgnoreColumns() {
        return ignoreColumns;
    }

    public void setIgnoreColumns(List<String> ignoreColumns) {
        this.ignoreColumns = ignoreColumns;
    }

    public boolean isIgnoreColumn(IntrospectedColumn introspectedColumn) {
        return ignoreColumns.contains(introspectedColumn.getActualColumnName());
    }
}
