package org.mybatis.generator.config;

import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.Document;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.codegen.XmlConstants;
import org.mybatis.generator.exception.InvalidConfigurationException;

import java.util.ArrayList;
import java.util.List;

import static org.mybatis.generator.internal.util.StringUtility.stringHasValue;
import static org.mybatis.generator.internal.util.messages.Messages.getString;

/**
 * 
 * @author Jeff Butler
 */
public class Configuration {

    private List<Context> contexts;
    private List<String> classPathEntries;

    public Configuration() {
        super();
        contexts = new ArrayList<>();
        classPathEntries = new ArrayList<>();
    }

    public void addClasspathEntry(String entry) {
        classPathEntries.add(entry);
    }

    /**
     * @return Returns the classPathEntries.
     */
    public List<String> getClassPathEntries() {
        return classPathEntries;
    }

    /**
     * This method does a simple validate, it makes sure that all required
     * fields have been filled in and that all implementation classes exist and
     * are of the proper type. It does not do any more complex operations such
     * as: validating that database tables exist or validating that named
     * columns exist
     */
    public void validate() throws InvalidConfigurationException {
        List<String> errors = new ArrayList<>();

        for (String classPathEntry : classPathEntries) {
            if (!stringHasValue(classPathEntry)) {
                errors.add(getString("ValidationError.19")); 
                // only need to state this error once
                break;
            }
        }

        if (contexts.size() == 0) {
            errors.add(getString("ValidationError.11")); 
        } else {
            for (Context context : contexts) {
                context.validate(errors);
            }
        }

        if (errors.size() > 0) {
            throw new InvalidConfigurationException(errors);
        }
    }

    public List<Context> getContexts() {
        return contexts;
    }

    public void addContext(Context context) {
        contexts.add(context);
    }

    public Context getContext(String id) {
        for (Context context : contexts) {
            if (id.equals(context.getId())) {
                return context;
            }
        }

        return null;
    }

    /**
     * Builds an XML representation of this configuration. This can be used to
     * persist a programtically generated configuration.
     * 
     * @return the XML representation of this configuration
     */
    public Document toDocument() {
        // note that this method will not reconstruct a properties
        // element - that element is only used in XML parsing

        Document document = new Document(XmlConstants.MYBATIS_GENERATOR_CONFIG_PUBLIC_ID, XmlConstants.MYBATIS_GENERATOR_CONFIG_SYSTEM_ID);
        XmlElement rootElement = new XmlElement("generatorConfiguration"); 
        document.setRootElement(rootElement);

        for (String classPathEntry : classPathEntries) {
            XmlElement cpeElement = new XmlElement("classPathEntry"); 
            cpeElement.addAttribute(new Attribute("location", classPathEntry)); 
            rootElement.addElement(cpeElement);
        }

        for (Context context : contexts) {
            rootElement.addElement(context.toXmlElement());
        }

        return document;
    }
}
