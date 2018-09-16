package org.mybatis.generator.codegen.mybatis3.model.method;

import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.Interface;
import org.mybatis.generator.api.dom.java.JavaVisibility;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.java.Parameter;
import org.mybatis.generator.codegen.mybatis3.javamapper.elements.AbstractJavaMapperMethodGenerator;

import java.util.Set;
import java.util.TreeSet;

/**
 * @author YangXiangTian
 */
public class SaveMethodGenerator extends AbstractJavaMapperMethodGenerator {

    @Override
    public void addInterfaceElements(Interface interfaze) {
        Set<FullyQualifiedJavaType> importedTypes = new TreeSet<>();
        FullyQualifiedJavaType parameterType = this.getBOFullyQualifiedType();
        importedTypes.add(parameterType);

        // 生成方法
        Method method = new Method();
        // 设置返回类型
        method.setReturnType(FullyQualifiedJavaType.getBooleanPrimitiveInstance());
        // 设置为public
        method.setVisibility(JavaVisibility.PUBLIC);
        // 设置方法名
        method.setName("save");
        // 设置参数
        method.addParameter(new Parameter(parameterType, this.getBOParamName()));

        context.getCommentGenerator().addDataServiceSaveMethodComment(method, introspectedTable);
        interfaze.addImportedTypes(importedTypes);
        interfaze.addMethod(method);
    }

    /**
     * 获取bo参数名
     *
     * @return bo参数名
     */
    private String getBOParamName() {
        String boName = introspectedTable.getFullyQualifiedTable().getDomainObjectName().substring(1);
        return String.valueOf(Character.toLowerCase(boName.charAt(0))) + boName.substring(1);
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
     * 获取对应BO的全限定名
     *
     * @return PO的全限定名
     */
    private FullyQualifiedJavaType getBOFullyQualifiedType() {
        String boTargetPackage = context.getBusinessModelGeneratorConfiguration().getTargetPackage();
        String boFullyQualified = boTargetPackage + "." + getBOClassName();
        return new FullyQualifiedJavaType(boFullyQualified);
    }

}
