package org.mybatis.generator.codegen.mybatis3.xmlmapper.elements;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.codegen.mybatis3.MyBatis3FormattingUtilities;

/**
 * @author YangXiangTian
 */
public class QueryByConditionElementGenerator extends AbstractXmlElementGenerator {

    public QueryByConditionElementGenerator() {
        super();
    }

    protected void setId(XmlElement answer) {
        answer.addAttribute(new Attribute("id", introspectedTable.getQueryByConditonStatementId()));
    }

    @Override
    public void addElements(XmlElement parentElement) {
        XmlElement answer = new XmlElement("select");
        // 设置方法名
        setId(answer);
        // 设置返回值
        answer.addAttribute(new Attribute("resultMap", introspectedTable.getBaseResultMapId()));

        // 设置参数类型
        String parameterType = introspectedTable.getBaseRecordType();
        answer.addAttribute(new Attribute("parameterType", parameterType));

        StringBuilder sb = new StringBuilder();
        sb.append("select ");
        answer.addElement(new TextElement(sb.toString()));

        answer.addElement(getBaseColumnListElement());

        sb.setLength(0);
        sb.append("from ");
        sb.append(introspectedTable.getAliasedFullyQualifiedTableNameAtRuntime());
        answer.addElement(new TextElement(sb.toString()));

        // where标签
        XmlElement whereElement = new XmlElement("where");
        answer.addElement(whereElement);
        for (IntrospectedColumn introspectedColumn : introspectedTable.getBaseColumns()) {
            XmlElement isNotNullElement = new XmlElement("if");
            sb.setLength(0);
            sb.append(introspectedColumn.getJavaProperty());
            sb.append(" != null");
            isNotNullElement.addAttribute(new Attribute("test", sb.toString()));
            whereElement.addElement(isNotNullElement);

            sb.setLength(0);
            sb.append("and ");
            sb.append(MyBatis3FormattingUtilities.getEscapedColumnName(introspectedColumn));
            sb.append(" = ");
            sb.append(MyBatis3FormattingUtilities.getParameterClause(introspectedColumn));

            isNotNullElement.addElement(new TextElement(sb.toString()));
        }
        addLimit(answer, sb);
        parentElement.addElement(answer);
    }

    /**
     * 添加limit 1
     */
    protected void addLimit(XmlElement answer, StringBuilder sb) {
        if (sb == null) {
            sb = new StringBuilder();
        }
        sb.setLength(0);
        sb.append("limit 1");
        answer.addElement(new TextElement(sb.toString()));
    }

}
