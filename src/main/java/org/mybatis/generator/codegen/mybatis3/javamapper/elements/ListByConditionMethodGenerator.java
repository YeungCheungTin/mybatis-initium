package org.mybatis.generator.codegen.mybatis3.javamapper.elements;

import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.Interface;
import org.mybatis.generator.api.dom.java.JavaVisibility;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.java.Parameter;

import java.util.Set;
import java.util.TreeSet;

/**
 * listByConditon方法生成器
 *
 * @author YangXiangTian
 */
public class ListByConditionMethodGenerator extends AbstractJavaMapperMethodGenerator {

    public ListByConditionMethodGenerator() {
        super();
    }

    @Override
    public void addInterfaceElements(Interface interfaze) {
        Method method = new Method();

        // 设置访问权限
        method.setVisibility(JavaVisibility.PUBLIC);

        // 设置方法名
        method.setName(introspectedTable.getListByConditionStatementId());

        Set<FullyQualifiedJavaType> importedTypes = new TreeSet<>();

        // 设置参数类型
        FullyQualifiedJavaType parameterType = new FullyQualifiedJavaType(introspectedTable.getBaseRecordType());
        importedTypes.add(parameterType);
        // 设置参数名称
        method.addParameter(new Parameter(parameterType, "record"));

        // 设置返回值类型
        FullyQualifiedJavaType returnType = FullyQualifiedJavaType.getNewListInstance();
        FullyQualifiedJavaType listType = new FullyQualifiedJavaType(introspectedTable.getBaseRecordType());
        returnType.addTypeArgument(listType);
        method.setReturnType(returnType);
        // 引入list和list的泛型
        importedTypes.add(listType);
        importedTypes.add(returnType);
        
        // 设置注释
        context.getCommentGenerator().addGeneralMethodComment(method, introspectedTable);

        if (context.getPlugins().clientUpdateByPrimaryKeySelectiveMethodGenerated(method, interfaze, introspectedTable)) {
            interfaze.addImportedTypes(importedTypes);
            interfaze.addMethod(method);
        }
    }
}
