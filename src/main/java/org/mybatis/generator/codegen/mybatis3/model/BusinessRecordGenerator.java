package org.mybatis.generator.codegen.mybatis3.model;

import org.apache.commons.lang3.StringUtils;
import org.mybatis.generator.api.CommentGenerator;
import org.mybatis.generator.api.FullyQualifiedTable;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.Plugin;
import org.mybatis.generator.api.dom.java.CompilationUnit;
import org.mybatis.generator.api.dom.java.Field;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.JavaVisibility;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.java.Parameter;
import org.mybatis.generator.api.dom.java.TopLevelClass;
import org.mybatis.generator.codegen.AbstractJavaGenerator;
import org.mybatis.generator.codegen.RootClassInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.mybatis.generator.internal.util.messages.Messages.getString;

/**
 * @author Sky Yeung
 */
public class BusinessRecordGenerator extends AbstractJavaGenerator {

    private static final String TAB = "    ";

    @Override
    public List<CompilationUnit> getCompilationUnits() {
        if (context.getBusinessModelGeneratorConfiguration() == null) {
            return new ArrayList<>();
        }
        FullyQualifiedTable table = introspectedTable.getFullyQualifiedTable();
        progressCallback.startTask(getString("Progress.8", table.toString()));
        Plugin plugins = context.getPlugins();
        CommentGenerator commentGenerator = context.getCommentGenerator();

        FullyQualifiedJavaType type = new FullyQualifiedJavaType(introspectedTable.getBusinessRecordType());
        TopLevelClass topLevelClass = new TopLevelClass(type);
        topLevelClass.setVisibility(JavaVisibility.PUBLIC);
        commentGenerator.addClassComment(topLevelClass, introspectedTable);
        commentGenerator.addClassAnnotation(topLevelClass);

        FullyQualifiedJavaType superClass = getSuperClass();
        if (superClass != null) {
            topLevelClass.setSuperClass(superClass);
            topLevelClass.addImportedType(superClass);
        }

        // 实现序列化接口
        FullyQualifiedJavaType serializableType = new FullyQualifiedJavaType("java.io.Serializable");
        topLevelClass.addSuperInterface(serializableType);
        topLevelClass.addImportedType(serializableType);

        // 引入PO
        topLevelClass.addImportedType(this.getPOFullyQualifiedType());

        // 引入lombok
        topLevelClass.addImportedType("lombok.AllArgsConstructor");
        topLevelClass.addImportedType("lombok.Builder");
        topLevelClass.addImportedType("lombok.Getter");
        topLevelClass.addImportedType("lombok.NoArgsConstructor");
        topLevelClass.addImportedType("lombok.Setter");
        topLevelClass.addImportedType("lombok.ToString");

        // 添加serialVersionUID
        Field serialVersionUID = new Field();
        serialVersionUID.setName("serialVersionUID");
        serialVersionUID.setFinal(true);
        serialVersionUID.setStatic(true);
        serialVersionUID.setVisibility(JavaVisibility.PRIVATE);
        serialVersionUID.setType(new FullyQualifiedJavaType("long"));
        // 生成serialVersionUID的值
        String randomNum = StringUtils.leftPad(String.valueOf(new Random().nextInt(100000000)), 9, "0");
        String serialVersionUidValue = String.valueOf(serializableType.hashCode()) + randomNum + "L";
        serialVersionUID.setInitializationString(serialVersionUidValue);
        topLevelClass.addField(serialVersionUID);

        List<IntrospectedColumn> introspectedColumns = getColumnsInThisClass();

        // 生成属性
        String rootClass = getRootClass();
        List<String> ignoreColumns = introspectedTable.getContext().getBusinessModelGeneratorConfiguration().getIgnoreColumns();
        for (IntrospectedColumn introspectedColumn : introspectedColumns) {
            if (RootClassInfo.getInstance(rootClass, warnings).containsProperty(introspectedColumn)) {
                continue;
            }
            if (ignoreColumns.contains(introspectedColumn.getActualColumnName())) {
                continue;
            }

            Field field = getJavaBeansField(introspectedColumn);
            if (plugins.modelFieldGenerated(field, topLevelClass, introspectedColumn, introspectedTable, Plugin.ModelClassType.BUSINESS_RECORD)) {
                topLevelClass.addField(field);
                topLevelClass.addImportedType(field.getType());
            }
        }

        // 生成方法
        Method buildPoMethod = this.getBuildPoMethod(introspectedColumns, ignoreColumns);
        Method buildByPoMethod = this.getBuildByPoMethod(introspectedColumns, ignoreColumns);
        topLevelClass.addMethod(buildPoMethod);
        topLevelClass.addMethod(buildByPoMethod);

        List<CompilationUnit> answer = new ArrayList<>();
        if (context.getPlugins().modelBaseRecordClassGenerated(topLevelClass, introspectedTable)) {
            answer.add(topLevelClass);
        }
        return answer;
    }

    /**
     * 获取对应po的全限定名
     *
     * @return po的全限定名
     */
    private FullyQualifiedJavaType getPOFullyQualifiedType() {
        String poTargetPackage = context.getJavaModelGeneratorConfiguration().getTargetPackage();
        String poFullyQualified = poTargetPackage + "." + getPOClassName();
        return new FullyQualifiedJavaType(poFullyQualified);
    }

    /**
     * 获取对应BO的全限定名
     *
     * @return PO的全限定名
     */
    private FullyQualifiedJavaType getBOFullyQualifiedType() {
        String boTargetPackage = context.getBusinessModelGeneratorConfiguration().getTargetPackage();
        String boFullyQualified = boTargetPackage + "." + getBOClassName();
        return new FullyQualifiedJavaType(boFullyQualified);
    }

    /**
     * 获取PO类名
     *
     * @return PO类名
     */
    private String getPOClassName() {
        return introspectedTable.getFullyQualifiedTable().getDomainObjectName().substring(1) + "PO";
    }

    /**
     * 获取BO类名
     *
     * @return BO类名
     */
    private String getBOClassName() {
        return introspectedTable.getFullyQualifiedTable().getDomainObjectName().substring(1) + "BO";
    }

    /**
     * 获取getter、setter后缀
     *
     * @param propertyName 属性名
     */
    private String getPropertyMethodSuffix(String propertyName) {
        return String.valueOf(Character.toUpperCase(propertyName.charAt(0))) + propertyName.substring(1);
    }

    /**
     * 生成buildByPO方法
     *
     * @param introspectedColumns 表字段队列
     * @param ignoreColumns       忽略字段队列
     * @return buildByPO方法
     */
    private Method getBuildByPoMethod(List<IntrospectedColumn> introspectedColumns, List<String> ignoreColumns) {
        FullyQualifiedJavaType poFullyQualifiedType = this.getPOFullyQualifiedType();
        FullyQualifiedJavaType boFullyQualifiedType = this.getBOFullyQualifiedType();

        Method method = new Method();
        method.setVisibility(JavaVisibility.PUBLIC);
        method.setReturnType(boFullyQualifiedType);
        method.setName("buildByPO");
        method.addParameter(new Parameter(poFullyQualifiedType, "po"));

        StringBuilder sb = new StringBuilder();
        sb.append("if (po == null) {\n").append(TAB).append(TAB).append(TAB)
                .append("return null;\n").append(TAB).append(TAB).append("}\n");
        for (IntrospectedColumn introspectedColumn : introspectedColumns) {
            if (ignoreColumns.contains(introspectedColumn.getActualColumnName())) {
                continue;
            }
            String javaProperty = introspectedColumn.getJavaProperty();
            String javaMethodSuffix = this.getPropertyMethodSuffix(javaProperty);
            sb.append(TAB).append(TAB).append("this.").append(javaProperty).append(" = ")
                    .append("po.get").append(javaMethodSuffix).append("();\n");
        }
        sb.append(TAB).append(TAB).append("return this;");
        method.addBodyLine(sb.toString());
        return method;
    }

    /**
     * 生成buildPO方法
     *
     * @param introspectedColumns 表字段队列
     * @param ignoreColumns       忽略字段队列
     * @return buildPO方法
     */
    private Method getBuildPoMethod(List<IntrospectedColumn> introspectedColumns, List<String> ignoreColumns) {
        FullyQualifiedJavaType poFullyQualifiedType = this.getPOFullyQualifiedType();

        Method method = new Method();
        method.setVisibility(JavaVisibility.PUBLIC);
        method.setReturnType(poFullyQualifiedType);
        method.setName("buildPO");

        StringBuilder sb = new StringBuilder();
        sb.append("return ").append(this.getPOClassName()).append(".builder()\n");
        for (IntrospectedColumn introspectedColumn : introspectedColumns) {
            if (ignoreColumns.contains(introspectedColumn.getActualColumnName())) {
                continue;
            }
            String javaProperty = introspectedColumn.getJavaProperty();
            sb.append(TAB).append(TAB).append(TAB).append(TAB).append(".").append(javaProperty).append("(this.")
                    .append(javaProperty).append(")\n");
        }
        sb.append(TAB).append(TAB).append(TAB).append(TAB).append(".build();");
        method.addBodyLine(sb.toString());
        return method;
    }


    private FullyQualifiedJavaType getSuperClass() {
        FullyQualifiedJavaType superClass;
        if (introspectedTable.getRules().generatePrimaryKeyClass()) {
            superClass = new FullyQualifiedJavaType(introspectedTable.getPrimaryKeyType());
        } else {
            String rootClass = getRootClass();
            if (rootClass != null) {
                superClass = new FullyQualifiedJavaType(rootClass);
            } else {
                superClass = null;
            }
        }

        return superClass;
    }

    private boolean includePrimaryKeyColumns() {
        return !introspectedTable.getRules().generatePrimaryKeyClass() && introspectedTable.hasPrimaryKeyColumns();
    }

    private boolean includeBLOBColumns() {
        return !introspectedTable.getRules().generateRecordWithBLOBsClass() && introspectedTable.hasBLOBColumns();
    }

    private List<IntrospectedColumn> getColumnsInThisClass() {
        List<IntrospectedColumn> introspectedColumns;
        if (includePrimaryKeyColumns()) {
            if (includeBLOBColumns()) {
                introspectedColumns = introspectedTable.getAllColumns();
            } else {
                introspectedColumns = introspectedTable.getNonBLOBColumns();
            }
        } else {
            if (includeBLOBColumns()) {
                introspectedColumns = introspectedTable.getNonPrimaryKeyColumns();
            } else {
                introspectedColumns = introspectedTable.getBaseColumns();
            }
        }

        return introspectedColumns;
    }

}
