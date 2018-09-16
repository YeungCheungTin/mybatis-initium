package org.mybatis.generator.config.xml;

import org.mybatis.generator.codegen.XmlConstants;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;

import java.io.InputStream;

/**
 * @author Jeff Butler
 */
public class ParserEntityResolver implements EntityResolver {

    /**
     *
     */
    public ParserEntityResolver() {
        super();
    }

    /*
     * (non-Javadoc)
     *
     * @see org.xml.sax.EntityResolver#resolveEntity(java.lang.String,
     * java.lang.String)
     */
    public InputSource resolveEntity(String publicId, String systemId) {
        if (XmlConstants.IBATOR_CONFIG_PUBLIC_ID.equalsIgnoreCase(publicId)) {
            InputStream is = getClass().getClassLoader().getResourceAsStream("org/mybatis/generator/config/xml/ibator-config_1_0.dtd");

            return new InputSource(is);
        } else if (XmlConstants.MYBATIS_GENERATOR_CONFIG_PUBLIC_ID.equalsIgnoreCase(publicId)) {
            InputStream is = getClass().getClassLoader().getResourceAsStream("org/mybatis/generator/config/xml/mybatis-generator-config_1_0.dtd");
            return new InputSource(is);
        } else {
            return null;
        }
    }
}
