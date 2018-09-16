package org.mybatis.generator.codegen.mybatis3.javamapper.elements;

import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.Interface;
import org.mybatis.generator.api.dom.java.JavaVisibility;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.java.Parameter;
import org.mybatis.generator.config.CandidateKey;
import org.mybatis.generator.internal.util.IntrospectedColumnUtils;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * @author YangXiangTian
 */
public class UpdateByConditonMethodGenerator extends AbstractJavaMapperMethodGenerator {

    public UpdateByConditonMethodGenerator() {
        super();
    }

    @Override
    public void addInterfaceElements(Interface interfaze) {
        if (introspectedTable.getTableConfiguration().isDefineCandidateKey()) {
            // 获取候选码组
            List<CandidateKey> candidateKeys = introspectedTable.getTableConfiguration().getUpdateConfiguration().getCandidateKeys();
            // 获取所有的字段名
            List<String> actualColumnNames = IntrospectedColumnUtils.getActualColumnNames(introspectedTable.getAllColumns());
            // 获取所有字段对应的属性名
            Map<String, String> javaPropertyNames = IntrospectedColumnUtils.getJavaPropertyNames(introspectedTable.getAllColumns());
            // 为每一组候选码生成一个方法
            for (CandidateKey candidateKey : candidateKeys) {
                String methodSuffix = candidateKey.getMethodSuffix(actualColumnNames, javaPropertyNames);
                if (methodSuffix != null) {
                    createMethod(interfaze, methodSuffix);
                }
            }
        } else {
            createMethod(interfaze, null);
        }
    }

    private void createMethod(Interface interfaze, String methodSuffix) {
        Method method = new Method();
        // 设置访问权限
        method.setVisibility(JavaVisibility.PUBLIC);
        // 设置方法名
        String methodName = introspectedTable.getUpdateByConditionStatementId();
        if (methodSuffix != null) {
            methodName = methodName.replaceFirst("Condition", methodSuffix);
        }
        method.setName(methodName);
        Set<FullyQualifiedJavaType> importedTypes = new TreeSet<>();
        // 设置参数类型
        FullyQualifiedJavaType parameterType = new FullyQualifiedJavaType(introspectedTable.getBaseRecordType());
        // 引包
        importedTypes.add(parameterType);
        // 设置参数名称
        method.addParameter(new Parameter(parameterType, "record"));
        // 设置返回值类型
        method.setReturnType(FullyQualifiedJavaType.getIntInstance());
        // 设置注释
        context.getCommentGenerator().addGeneralMethodComment(method, introspectedTable);
        // 把引入和方法写入接口中
        interfaze.addImportedTypes(importedTypes);
        interfaze.addMethod(method);
    }

}
