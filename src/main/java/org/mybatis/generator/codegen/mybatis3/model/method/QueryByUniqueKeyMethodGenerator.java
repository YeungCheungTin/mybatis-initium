package org.mybatis.generator.codegen.mybatis3.model.method;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.Interface;
import org.mybatis.generator.api.dom.java.JavaVisibility;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.java.Parameter;
import org.mybatis.generator.codegen.mybatis3.javamapper.elements.AbstractJavaMapperMethodGenerator;
import org.mybatis.generator.config.CandidateKey;
import org.mybatis.generator.config.UpdateConfiguration;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * @author YangXiangTian
 */
public class QueryByUniqueKeyMethodGenerator extends AbstractJavaMapperMethodGenerator {


    @Override
    public void addInterfaceElements(Interface interfaze) {
        UpdateConfiguration updateConfiguration = introspectedTable.getTableConfiguration().getUpdateConfiguration();
        if (updateConfiguration == null) {
            return;
        }
        List<CandidateKey> candidateKeys = updateConfiguration.getCandidateKeys();
        if (candidateKeys == null || candidateKeys.size() == 0) {
            return;
        }
        Set<FullyQualifiedJavaType> importedTypes = new TreeSet<>();
        FullyQualifiedJavaType boType = this.getBOFullyQualifiedType();
        importedTypes.add(boType);
        for (CandidateKey candidateKey : candidateKeys) {
            List<IntrospectedColumn> primaryKeyColumns = introspectedTable.getPrimaryKeyColumns(candidateKey);
            // 生成方法
            Method method = new Method();
            // 设置返回类型
            method.setReturnType(boType);
            // 设置为public
            method.setVisibility(JavaVisibility.PUBLIC);
            // 设置方法名
            method.setName("get" + this.getBOName());
            for (IntrospectedColumn primaryKeyColumn : primaryKeyColumns) {
                // 设置参数
                FullyQualifiedJavaType paramType = primaryKeyColumn.getFullyQualifiedJavaType();
                String paramName = primaryKeyColumn.getJavaProperty();
                method.addParameter(new Parameter(paramType, paramName));
            }
            context.getCommentGenerator().addDataServiceQueryMethodComment(method, primaryKeyColumns, introspectedTable);
            interfaze.addMethod(method);
        }
        interfaze.addImportedTypes(importedTypes);
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
     * 获取BO类名
     *
     * @return BO类名
     */
    private String getBOClassName() {
        return introspectedTable.getFullyQualifiedTable().getDomainObjectName().substring(1) + "BO";
    }

    /**
     * 获取BO名称(方法后缀)
     */
    private String getBOName() {
        return introspectedTable.getFullyQualifiedTable().getDomainObjectName().substring(1);
    }

}
