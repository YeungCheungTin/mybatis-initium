package org.mybatis.generator.codegen.mybatis3.model;

import org.apache.commons.lang3.StringUtils;
import org.mybatis.generator.api.CommentGenerator;
import org.mybatis.generator.api.FullyQualifiedTable;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.dom.java.CompilationUnit;
import org.mybatis.generator.api.dom.java.Field;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.JavaVisibility;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.java.Parameter;
import org.mybatis.generator.api.dom.java.TopLevelClass;
import org.mybatis.generator.codegen.AbstractJavaGenerator;
import org.mybatis.generator.config.BusinessModelGeneratorConfiguration;
import org.mybatis.generator.config.CandidateKey;

import java.util.ArrayList;
import java.util.List;

import static org.mybatis.generator.internal.util.messages.Messages.getString;

/**
 * @author Sky Yeung
 */
public class BaseDataServiceImplGenerator extends AbstractJavaGenerator {

    private static final String TAB = "    ";

    @Override
    public List<CompilationUnit> getCompilationUnits() {
        BusinessModelGeneratorConfiguration businessModelGeneratorConfiguration = context.getBusinessModelGeneratorConfiguration();
        if (businessModelGeneratorConfiguration == null || !businessModelGeneratorConfiguration.isGenerateDataService()) {
            return new ArrayList<>();
        }
        FullyQualifiedTable table = introspectedTable.getFullyQualifiedTable();
        progressCallback.startTask(getString("Progress.8", table.toString()));
        CommentGenerator commentGenerator = context.getCommentGenerator();

        // 生成dataServiceImpl
        FullyQualifiedJavaType type = new FullyQualifiedJavaType(introspectedTable.getDataServiceImplType());
        // 设置为类
        TopLevelClass topLevelClass = new TopLevelClass(type);
        // 设置为public
        topLevelClass.setVisibility(JavaVisibility.PUBLIC);
        // 设置lombok的@Slf4j注解
        topLevelClass.addAnnotation("@Slf4j");
        topLevelClass.addImportedType("lombok.extern.slf4j.Slf4j");
        // 设置spring的@service注解
        topLevelClass.addAnnotation("@Service(\"" + this.getSpringBeanName(introspectedTable.getDataServiceType()) + "\")");
        topLevelClass.addImportedType("org.springframework.stereotype.Service");
        // 设置注释
        commentGenerator.addDataServiceComment(topLevelClass, introspectedTable);


        // 实现dataService
        FullyQualifiedJavaType dataServiceType = new FullyQualifiedJavaType(introspectedTable.getDataServiceType());
        topLevelClass.addSuperInterface(dataServiceType);
        topLevelClass.addImportedType(dataServiceType);

        // 注入dao
        Field daoField = this.buildDaoField();
        topLevelClass.addField(daoField);
        topLevelClass.addImportedType(daoField.getType());
        topLevelClass.addImportedType("javax.annotation.Resource");

        // 添加save方法
        Method saveMethod = this.buildSaveMethod();
        topLevelClass.addMethod(saveMethod);

        // 添加get方法
        List<CandidateKey> candidateKeys = introspectedTable.getTableConfiguration().getUpdateConfiguration().getCandidateKeys();
        if (candidateKeys != null && candidateKeys.size() > 0) {
            for (CandidateKey candidateKey : candidateKeys) {
                List<IntrospectedColumn> primaryKeyColumns = introspectedTable.getPrimaryKeyColumns(candidateKey);
                Method queryByUniqueKeyMethod = this.buildQueryByUniqueKeyMethod(topLevelClass, primaryKeyColumns);
                topLevelClass.addMethod(queryByUniqueKeyMethod);
            }
        }

        // 生成类
        List<CompilationUnit> answer = new ArrayList<>();
        answer.add(topLevelClass);
        return answer;
    }

    /**
     * 生成dao属性
     */
    private Field buildDaoField() {
        FullyQualifiedJavaType daoType = new FullyQualifiedJavaType(introspectedTable.getMyBatis3JavaMapperType());
        String daoParamName = this.getDaoParamName();

        Field field = new Field();
        field.setVisibility(JavaVisibility.PRIVATE);
        field.setType(daoType);
        field.setName(daoParamName);
        field.addAnnotation("@Resource");

        return field;
    }

    /**
     * 获取spring bean 名称, 类名首字母小写
     *
     * @param fullyQualifiedName 全限定名
     */
    private String getSpringBeanName(String fullyQualifiedName) {
        String[] fullyQualifiedNameSplit = StringUtils.split(fullyQualifiedName, ".");
        String className = fullyQualifiedNameSplit[fullyQualifiedNameSplit.length - 1];
        return String.valueOf(Character.toLowerCase(className.charAt(0))) + className.substring(1);
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
     * 获取BO类名
     *
     * @return BO类名
     */
    private String getBOClassName() {
        return introspectedTable.getFullyQualifiedTable().getDomainObjectName().substring(1) + "BO";
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
     * 获取dao参数名
     *
     * @return dao参数名
     */
    private String getDaoParamName() {
        String daoClassName = introspectedTable.getFullyQualifiedTable().getDomainObjectName().substring(1) + "Dao";
        return String.valueOf(Character.toLowerCase(daoClassName.charAt(0))) + daoClassName.substring(1);
    }

    /**
     * 获取BO名称(方法后缀)
     */
    private String getBOName() {
        return introspectedTable.getFullyQualifiedTable().getDomainObjectName().substring(1);
    }

    private Method buildQueryByUniqueKeyMethod(TopLevelClass topLevelClass, List<IntrospectedColumn> primaryKeyColumns) {
        FullyQualifiedJavaType poFullyQualifiedType = this.getPOFullyQualifiedType();
        FullyQualifiedJavaType boFullyQualifiedType = this.getBOFullyQualifiedType();

        topLevelClass.addImportedType(poFullyQualifiedType);

        Method method = new Method();
        method.setVisibility(JavaVisibility.PUBLIC);
        method.setReturnType(boFullyQualifiedType);
        method.setName("get" + this.getBOName());
        // 添加@Override注解
        method.addAnnotation("@Override");
        // 设置参数
        for (IntrospectedColumn primaryKeyColumn : primaryKeyColumns) {
            FullyQualifiedJavaType paramType = primaryKeyColumn.getFullyQualifiedJavaType();
            String paramName = primaryKeyColumn.getJavaProperty();
            method.addParameter(new Parameter(paramType, paramName));
        }
        StringBuilder sb = new StringBuilder();
        // 参数判断
        sb.append("if (");
        for (IntrospectedColumn primaryKeyColumn : primaryKeyColumns) {
            if (primaryKeyColumn.getFullyQualifiedJavaType().getFullyQualifiedName().equals("java.lang.String")) {
                topLevelClass.addImportedType("org.apache.commons.lang3.StringUtils");
                sb.append("StringUtils.isBlank(").append(primaryKeyColumn.getJavaProperty()).append(") || ");
            } else {
                sb.append(primaryKeyColumn.getJavaProperty()).append(" == null || ");
            }
        }
        // 去除最后的" || "
        sb.setLength(sb.length() - 4);
        sb.append(") {\n");
        sb.append(TAB).append(TAB).append(TAB);
        sb.append("return null;\n");
        sb.append(TAB).append(TAB);
        sb.append("}\n");
        sb.append(TAB).append(TAB);

        // 查询数据库
        sb.append(this.getPOClassName()).append(" po = ").append(this.getDaoParamName()).append(".queryByCondition(").append(this.getPOClassName())
                .append(".builder()");
        for (IntrospectedColumn primaryKeyColumn : primaryKeyColumns) {
            sb.append(".").append(primaryKeyColumn.getJavaProperty()).append("(").append(primaryKeyColumn.getJavaProperty()).append(")");
        }
        sb.append(".build());\n");
        sb.append(TAB).append(TAB);
        sb.append("return ").append(this.getBOClassName()).append(".builder().build().buildByPO(po);");

        method.addBodyLine(sb.toString());
        return method;
    }

    private Method buildSaveMethod() {
        FullyQualifiedJavaType boFullyQualifiedType = this.getBOFullyQualifiedType();
        Method method = new Method();
        method.setVisibility(JavaVisibility.PUBLIC);
        method.setReturnType(FullyQualifiedJavaType.getBooleanPrimitiveInstance());
        method.setName("save");
        // 添加@Override注解
        method.addAnnotation("@Override");
        // 设置参数
        method.addParameter(new Parameter(boFullyQualifiedType, this.getBOName()));
        // 设置方法体
        String sb = "if (" + this.getBOName() + " == null) {\n" +
                TAB + TAB + TAB + "return false;\n" +
                TAB + TAB + "}\n" +
                TAB + TAB + "return " + this.getDaoParamName() + ".save(" + this.getBOName() + ".buildPO()) == 1;";
        method.addBodyLine(sb);
        return method;
    }


}
