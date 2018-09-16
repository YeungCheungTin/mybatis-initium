package org.mybatis.generator.api.dom;

import org.mybatis.generator.api.JavaFormatter;
import org.mybatis.generator.api.dom.java.CompilationUnit;
import org.mybatis.generator.config.Context;

/**
 * This class is the default formatter for generated Java.  This class will use the
 * built in formatting of the DOM classes directly.
 * 
 * @author Jeff Butler
 *
 */
public class DefaultJavaFormatter implements JavaFormatter {
    protected Context context;
    
    public String getFormattedContent(CompilationUnit compilationUnit) {
        return compilationUnit.getFormattedContent();
    }

    public void setContext(Context context) {
        this.context = context;
    }
}
