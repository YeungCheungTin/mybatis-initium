package org.mybatis.generator.internal;

import org.apache.commons.lang3.StringUtils;
import org.mybatis.generator.api.CommentGenerator;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.java.CompilationUnit;
import org.mybatis.generator.api.dom.java.Field;
import org.mybatis.generator.api.dom.java.InnerClass;
import org.mybatis.generator.api.dom.java.InnerEnum;
import org.mybatis.generator.api.dom.java.JavaElement;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.java.Parameter;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.config.MergeConstants;
import org.mybatis.generator.config.PropertyRegistry;
import org.mybatis.generator.internal.util.StringUtility;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import static org.mybatis.generator.internal.util.StringUtility.isTrue;

/**
 * @author Jeff Butler
 */
public class DefaultCommentGenerator implements CommentGenerator {

    private Properties properties;
    private boolean suppressDate;
    private boolean suppressAllComments;

    public DefaultCommentGenerator() {
        super();
        properties = new Properties();
        suppressDate = false;
        suppressAllComments = false;
    }

    /**
     * java文件注释，在import上方的对于文件的注释，实际使用中不需要
     */
    public void addJavaFileComment(CompilationUnit compilationUnit) {
        if (suppressAllComments) {
            compilationUnit.addFileCommentLine("/*");
            compilationUnit.addFileCommentLine(" * @author " + System.getenv().get("USERNAME"));
            compilationUnit.addFileCommentLine(" * @date " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
            compilationUnit.addFileCommentLine(" */");
        }
    }

    /**
     * Mybatis的Mapper.xml文件里面的注释，实际使用中并不需要，注释掉里面的内容
     */
    public void addComment(XmlElement xmlElement) {
    }

    public void addRootComment(XmlElement rootElement) {
    }

    public void addConfigurationProperties(Properties properties) {
        this.properties.putAll(properties);
        suppressDate = isTrue(properties.getProperty(PropertyRegistry.COMMENT_GENERATOR_SUPPRESS_DATE));
        suppressAllComments = isTrue(properties.getProperty(PropertyRegistry.COMMENT_GENERATOR_SUPPRESS_ALL_COMMENTS));
    }

    /**
     * This method adds the custom javadoc tag for. You may do nothing if you do
     * not wish to include the Javadoc tag - however, if you do not include the
     * Javadoc tag then the Java merge capability of the eclipse plugin will
     * break.
     *
     * @param javaElement the java element
     */
    private void addJavadocTag(JavaElement javaElement, boolean markAsDoNotDelete) {
        javaElement.addJavaDocLine(" *");
        StringBuilder sb = new StringBuilder();
        sb.append(" * ");
        sb.append(MergeConstants.NEW_ELEMENT_TAG);
        if (markAsDoNotDelete) {
            sb.append(" do_not_delete_during_merge");
        }
        String s = getDateString();
        if (s != null) {
            sb.append(' ');
            sb.append(s);
        }
        javaElement.addJavaDocLine(sb.toString());
    }

    private String getDateString() {
        if (suppressDate) {
            return null;
        } else {
            return new Date().toString();
        }
    }

    /**
     * 类的注释（也就是类名上方的注释）
     */
    public void addClassComment(JavaElement element, IntrospectedTable introspectedTable) {
        element.addJavaDocLine("/**");
        element.addJavaDocLine(" * " + introspectedTable.getTableConfiguration().getTableName() + "表对应实体");
        String author = introspectedTable.getContext().getAuthor();
        if (StringUtility.stringHasValue(author)) {
            element.addJavaDocLine(" * ");
            element.addJavaDocLine(" * @author " + author);
        }
        element.addJavaDocLine(" */");
    }

    /**
     * dataService类的注释
     */
    public void addDataServiceComment(JavaElement element, IntrospectedTable introspectedTable) {
        String remark = StringUtils.isNotBlank(introspectedTable.getTableRemark()) ? introspectedTable.getTableRemark() + "数据操作服务" :
                introspectedTable.getTableConfiguration().getTableName() + "表数据操作服务";
        element.addJavaDocLine("/**");
        element.addJavaDocLine(" * " + remark);
        String author = introspectedTable.getContext().getAuthor();
        if (StringUtility.stringHasValue(author)) {
            element.addJavaDocLine(" * ");
            element.addJavaDocLine(" * @author " + author);
        }
        element.addJavaDocLine(" */");
    }

    /**
     * 添加注解
     */
    public void addClassAnnotation(JavaElement element) {
        element.addAnnotation("@Getter");
        element.addAnnotation("@Setter");
        element.addAnnotation("@Builder");
        element.addAnnotation("@ToString");
        element.addAnnotation("@NoArgsConstructor");
        element.addAnnotation("@AllArgsConstructor");
    }

    /**
     * 枚举注释
     */
    public void addEnumComment(InnerEnum innerEnum, IntrospectedTable introspectedTable) {
        if (suppressAllComments) {
            return;
        }
        StringBuilder sb = new StringBuilder();
        innerEnum.addJavaDocLine("/**");
        innerEnum.addJavaDocLine(" * This enum was generated by MyBatis Generator.");
        sb.append(" * This enum corresponds to the database table ");
        sb.append(introspectedTable.getFullyQualifiedTable());
        innerEnum.addJavaDocLine(sb.toString());
        addJavadocTag(innerEnum, false);
        innerEnum.addJavaDocLine(" */");
    }

    /**
     * po中各个属性的注释
     */
    public void addFieldComment(Field field, IntrospectedTable introspectedTable, IntrospectedColumn introspectedColumn) {
        if (suppressAllComments) {
            return;
        }
        field.addJavaDocLine("/**");
        field.addJavaDocLine(" * " + introspectedColumn.getRemarks());
        field.addJavaDocLine(" */");
    }

    public void addFieldComment(Field field, IntrospectedTable introspectedTable) {
    }

    /**
     * dao方法注释
     */
    public void addGeneralMethodComment(Method method, IntrospectedTable introspectedTable) {
        method.addJavaDocLine("/**");
        for (Parameter parameter : method.getParameters()) {
            method.addJavaDocLine(" * @param " + parameter.getName());
        }
        method.addJavaDocLine(" */");
    }

    /**
     * dataService 保存方法注释
     */
    public void addDataServiceSaveMethodComment(Method method, IntrospectedTable introspectedTable) {
        if (method.getParameters() == null || method.getParameters().size() != 1) {
            return;
        }
        // save 方法应该有且只有一个参数
        Parameter parameter = method.getParameters().get(0);
        String remark = this.getTableObjectRemark(introspectedTable.getTableRemark());
        method.addJavaDocLine("/**");
        method.addJavaDocLine(" * 保存" + remark);
        method.addJavaDocLine(" * ");
        method.addJavaDocLine(" * @param " + parameter.getName() + " " + remark);
        method.addJavaDocLine(" * @return 保存结果");
        method.addJavaDocLine(" */");
    }

    /**
     * dataServie 唯一索引查询方法注释
     */
    public void addDataServiceQueryMethodComment(Method method, List<IntrospectedColumn> introspectedColumns, IntrospectedTable introspectedTable) {
        method.addJavaDocLine("/**");
        method.addJavaDocLine(" * 获取" + this.getTableObjectRemark(introspectedTable.getTableRemark()));
        method.addJavaDocLine(" * ");
        for (IntrospectedColumn introspectedColumn : introspectedColumns) {
            method.addJavaDocLine(" * @param " + introspectedColumn.getJavaProperty() + " " + introspectedColumn.getRemarks());
        }
        method.addJavaDocLine(" * @return " + this.getTableObjectRemark(introspectedTable.getTableRemark()));
        method.addJavaDocLine(" */");
    }

    private String getTableObjectRemark(String tableRemark) {
        String remark = tableRemark;
        if (StringUtils.substring(remark, remark.length() - 1).equals("表")) {
            remark = StringUtils.substring(remark, 0, remark.length() - 1) + "信息";
        } else if (StringUtils.isNotBlank(remark)) {
            remark += "信息";
        }
        return remark;
    }

    /**
     * Model里面的Getter方法，暂时不需要
     */
    public void addGetterComment(Method method, IntrospectedTable introspectedTable, IntrospectedColumn introspectedColumn) {
        /*StringBuilder sb = new StringBuilder();

        method.addJavaDocLine("/**");
        method.addJavaDocLine(" * This method was generated by MyBatis Generator.");

        sb.append(" * This method returns the value of the database column ");
        sb.append(introspectedTable.getFullyQualifiedTable());
        sb.append('.');
        sb.append(introspectedColumn.getActualColumnName());
        method.addJavaDocLine(sb.toString());

        method.addJavaDocLine(" *");

        sb.setLength(0);
        sb.append(" * @return the value of ");
        sb.append(introspectedTable.getFullyQualifiedTable());
        sb.append('.');
        sb.append(introspectedColumn.getActualColumnName());
        method.addJavaDocLine(sb.toString());

        addJavadocTag(method, false);*/

        //method.addJavaDocLine(" */");
    }

    /**
     * Model里面的Setter方法，暂时不需要
     */
    public void addSetterComment(Method method, IntrospectedTable introspectedTable, IntrospectedColumn introspectedColumn) {
        /*StringBuilder sb = new StringBuilder();

        method.addJavaDocLine("/**"); 
        method.addJavaDocLine(" * This method was generated by MyBatis Generator."); 

        sb.append(" * This method sets the value of the database column "); 
        sb.append(introspectedTable.getFullyQualifiedTable());
        sb.append('.');
        sb.append(introspectedColumn.getActualColumnName());
        method.addJavaDocLine(sb.toString());

        method.addJavaDocLine(" *"); 

        Parameter parm = method.getParameters().get(0);
        sb.setLength(0);
        sb.append(" * @param "); 
        sb.append(parm.getName());
        sb.append(" the value for "); 
        sb.append(introspectedTable.getFullyQualifiedTable());
        sb.append('.');
        sb.append(introspectedColumn.getActualColumnName());
        method.addJavaDocLine(sb.toString());

        addJavadocTag(method, false);*/

        //method.addJavaDocLine(" */");
    }

    public void addClassComment(InnerClass innerClass, IntrospectedTable introspectedTable, boolean markAsDoNotDelete) {
        innerClass.addJavaDocLine("/**");
        innerClass.addJavaDocLine(" * @Description ");
        innerClass.addJavaDocLine(" * @version 1.0");
        innerClass.addJavaDocLine(" * @Date " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        innerClass.addJavaDocLine(" */");
    }
}
