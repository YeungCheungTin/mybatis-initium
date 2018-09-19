package org.mybatis.generator.config;

import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.XmlElement;

import java.util.List;

import static org.mybatis.generator.internal.util.StringUtility.stringHasValue;
import static org.mybatis.generator.internal.util.messages.Messages.getString;

/**
 * @author Sky Yeung 
 */
public class BusinessModelGeneratorConfiguration extends PropertyHolder {

    private String targetPackage;

    private String targetProject;

    private List<String> ignoreColumns;

    public BusinessModelGeneratorConfiguration() {
        super();
    }

    public XmlElement toXmlElement() {
        XmlElement answer = new XmlElement("businessModelGenerator"); 

        if (targetPackage != null) {
            answer.addAttribute(new Attribute("targetPackage", targetPackage)); 
        }

        if (targetProject != null) {
            answer.addAttribute(new Attribute("targetProject", targetProject)); 
        }

        addPropertyXmlElements(answer);

        return answer;
    }

    public void validate(List<String> errors, String contextId) {
        if (!stringHasValue(targetProject)) {
            errors.add(getString("ValidationError.0", contextId)); 
        }

        if (!stringHasValue(targetPackage)) {
            errors.add(getString("ValidationError.12", "BusinessModelGenerator", contextId));
        }
    }

    public String getTargetPackage() {
        return targetPackage;
    }

    public String getTargetProject() {
        return targetProject;
    }

    public List<String> getIgnoreColumns() {
        return ignoreColumns;
    }

    public void setTargetPackage(String targetPackage) {
        this.targetPackage = targetPackage;
    }

    public void setTargetProject(String targetProject) {
        this.targetProject = targetProject;
    }

    public void setIgnoreColumns(List<String> ignoreColumns) {
        this.ignoreColumns = ignoreColumns;
    }

    public boolean isGenerateDataService() {
        String generateDataService = getProperty("generateDataService");
        return Boolean.valueOf(generateDataService);
    }

    public String getDataServiePackage() {
        return getProperty("dataServicePackage");
    }
}
