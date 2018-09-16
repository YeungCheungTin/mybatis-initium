package org.mybatis.generator.codegen.mybatis3.xmlmapper.elements;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.codegen.mybatis3.MyBatis3FormattingUtilities;
import org.mybatis.generator.config.CandidateKey;
import org.mybatis.generator.internal.util.IntrospectedColumnUtils;

import java.util.List;
import java.util.Map;

/**
 * @author YangXiangTian
 */
public class UpdateByConditonElementGenerator extends AbstractXmlElementGenerator {

    public UpdateByConditonElementGenerator() {
        super();
    }

    @Override
    public void addElements(XmlElement parentElement) {
        // 检查生成、更新时间，设置警告
        introspectedTable.validateCreateUpdateTime();
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
                    createMethodElement(parentElement, methodSuffix, candidateKey);
                }
            }
        } else {
            createMethodElement(parentElement, null, null);
        }

    }

    private void createMethodElement(XmlElement parentElement, String methodSuffix, CandidateKey candidateKey) {
        if (candidateKey == null) {
            return;
        }
        XmlElement answer = new XmlElement("update");
        // 设置方法名
        String methodName = introspectedTable.getUpdateByConditionStatementId();
        if (methodSuffix != null) {
            methodName = methodName.replaceFirst("Condition", methodSuffix);
        }
        answer.addAttribute(new Attribute("id", methodName));
        // 设置参数类型
        String parameterType = introspectedTable.getBaseRecordType();
        answer.addAttribute(new Attribute("parameterType", parameterType));

        StringBuilder sb = new StringBuilder();
        sb.append("update ");
        sb.append(introspectedTable.getFullyQualifiedTableNameAtRuntime());
        answer.addElement(new TextElement(sb.toString()));

        XmlElement dynamicElement = new XmlElement("set");
        answer.addElement(dynamicElement);

        for (IntrospectedColumn introspectedColumn : introspectedTable.getNonPrimaryKeyColumns(candidateKey)) {
            if (candidateKey.isIgnoreColumn(introspectedColumn)) {
                sb.setLength(0);
            } else if (introspectedTable.isCreateTimeColumn(introspectedColumn)) {
                sb.setLength(0);
            } else if (introspectedTable.isUpdateTimeColumn(introspectedColumn)) {
                sb.setLength(0);
                sb.append(MyBatis3FormattingUtilities.getEscapedColumnName(introspectedColumn));
                sb.append(" = ");
                sb.append("now(),");
                dynamicElement.addElement(new TextElement(sb.toString()));
            } else {
                XmlElement isNotNullElement = new XmlElement("if");
                sb.setLength(0);
                sb.append(introspectedColumn.getJavaProperty());
                sb.append(" != null");
                isNotNullElement.addAttribute(new Attribute("test", sb.toString()));
                dynamicElement.addElement(isNotNullElement);

                sb.setLength(0);
                sb.append(MyBatis3FormattingUtilities.getEscapedColumnName(introspectedColumn));
                sb.append(" = ");
                sb.append(MyBatis3FormattingUtilities.getParameterClause(introspectedColumn));
                sb.append(',');

                isNotNullElement.addElement(new TextElement(sb.toString()));
            }
        }

        createWhereElement(answer, candidateKey);

        parentElement.addElement(answer);
    }

    private void createWhereElement(XmlElement answer, CandidateKey candidateKey) {
        StringBuilder sb = new StringBuilder();
        sb.append("where ");
        for (IntrospectedColumn introspectedColumn : introspectedTable.getPrimaryKeyColumns(candidateKey)) {
            sb.append(MyBatis3FormattingUtilities.getEscapedColumnName(introspectedColumn));
            sb.append(" = ");
            sb.append(MyBatis3FormattingUtilities.getParameterClause(introspectedColumn));
            sb.append(" and ");
        }
        sb.deleteCharAt(sb.lastIndexOf(" "));
        sb.deleteCharAt(sb.lastIndexOf("d"));
        sb.deleteCharAt(sb.lastIndexOf("n"));
        sb.deleteCharAt(sb.lastIndexOf("a"));
        answer.addElement(new TextElement(sb.toString()));
    }
}
