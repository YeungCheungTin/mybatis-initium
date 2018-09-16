package org.mybatis.generator.config.xml;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXParseException;

import java.util.List;

import static org.mybatis.generator.internal.util.messages.Messages.getString;

/**
 * @author Jeff Butler
 */
public class ParserErrorHandler implements ErrorHandler {
    private List<String> warnings;

    private List<String> errors;

    public ParserErrorHandler(List<String> warnings, List<String> errors) {
        this.warnings = warnings;
        this.errors = errors;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.xml.sax.ErrorHandler#warning(org.xml.sax.SAXParseException)
     */
    public void warning(SAXParseException exception) {
        warnings.add(getString("Warning.7", Integer.toString(exception.getLineNumber()), exception.getMessage()));
    }

    /*
     * (non-Javadoc)
     *
     * @see org.xml.sax.ErrorHandler#error(org.xml.sax.SAXParseException)
     */
    public void error(SAXParseException exception) {
        errors.add(getString("RuntimeError.4", Integer.toString(exception.getLineNumber()), exception.getMessage()));
    }

    /*
     * (non-Javadoc)
     *
     * @see org.xml.sax.ErrorHandler#fatalError(org.xml.sax.SAXParseException)
     */
    public void fatalError(SAXParseException exception) {
        errors.add(getString("RuntimeError.4", Integer.toString(exception.getLineNumber()), exception.getMessage()));
    }
}
