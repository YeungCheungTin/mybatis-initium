package org.mybatis.generator.api;

import org.mybatis.generator.api.dom.java.CompilationUnit;
import org.mybatis.generator.api.dom.java.Field;
import org.mybatis.generator.api.dom.java.InnerClass;
import org.mybatis.generator.api.dom.java.InnerEnum;
import org.mybatis.generator.api.dom.java.JavaElement;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.xml.XmlElement;

import java.util.List;
import java.util.Properties;

/**
 * Implementations of this interface are used to generate comments for the
 * various artifacts.
 *
 * @author Jeff Butler
 */
public interface CommentGenerator {

    void addConfigurationProperties(Properties properties);

    void addFieldComment(Field field, IntrospectedTable introspectedTable, IntrospectedColumn introspectedColumn);

    void addFieldComment(Field field, IntrospectedTable introspectedTable);

    void addClassComment(JavaElement element, IntrospectedTable introspectedTable);

    void addDataServiceComment(JavaElement element, IntrospectedTable introspectedTable);

    void addClassAnnotation(JavaElement element);

    void addClassComment(InnerClass innerClass, IntrospectedTable introspectedTable, boolean markAsDoNotDelete);

    void addEnumComment(InnerEnum innerEnum, IntrospectedTable introspectedTable);

    void addGeneralMethodComment(Method method, IntrospectedTable introspectedTable);

    void addDataServiceSaveMethodComment(Method method, IntrospectedTable introspectedTable);

    void addDataServiceQueryMethodComment(Method method, List<IntrospectedColumn> introspectedColumns, IntrospectedTable introspectedTable);

    void addGetterComment(Method method, IntrospectedTable introspectedTable, IntrospectedColumn introspectedColumn);

    void addSetterComment(Method method, IntrospectedTable introspectedTable, IntrospectedColumn introspectedColumn);

    void addJavaFileComment(CompilationUnit compilationUnit);

    void addComment(XmlElement xmlElement);

    void addRootComment(XmlElement rootElement);

}
