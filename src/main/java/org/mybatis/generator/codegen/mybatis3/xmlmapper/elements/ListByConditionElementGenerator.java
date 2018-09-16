package org.mybatis.generator.codegen.mybatis3.xmlmapper.elements;

import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.XmlElement;

/**
 * @author YangXiangTian
 */
public class ListByConditionElementGenerator extends QueryByConditionElementGenerator {

    public ListByConditionElementGenerator() {
        super();
    }

    @Override
    protected void addLimit(XmlElement answer, StringBuilder sb) {
    }

    @Override
    protected void setId(XmlElement answer) {
        answer.addAttribute(new Attribute("id", introspectedTable.getListByConditionStatementId()));
    }
}
