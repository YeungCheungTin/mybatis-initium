package org.mybatis.generator.config.xml;

import org.apache.commons.lang3.StringUtils;
import org.mybatis.generator.config.BusinessModelGeneratorConfiguration;
import org.mybatis.generator.config.CandidateKey;
import org.mybatis.generator.config.ColumnOverride;
import org.mybatis.generator.config.ColumnRenamingRule;
import org.mybatis.generator.config.CommentGeneratorConfiguration;
import org.mybatis.generator.config.Configuration;
import org.mybatis.generator.config.Context;
import org.mybatis.generator.config.GeneratedKey;
import org.mybatis.generator.config.IgnoredColumn;
import org.mybatis.generator.config.JDBCConnectionConfiguration;
import org.mybatis.generator.config.JavaClientGeneratorConfiguration;
import org.mybatis.generator.config.JavaModelGeneratorConfiguration;
import org.mybatis.generator.config.JavaTypeResolverConfiguration;
import org.mybatis.generator.config.ModelType;
import org.mybatis.generator.config.PluginConfiguration;
import org.mybatis.generator.config.PropertyHolder;
import org.mybatis.generator.config.SqlMapGeneratorConfiguration;
import org.mybatis.generator.config.TableConfiguration;
import org.mybatis.generator.config.UpdateConfiguration;
import org.mybatis.generator.exception.XMLParserException;
import org.mybatis.generator.internal.ObjectFactory;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static org.mybatis.generator.internal.util.StringUtility.isTrue;
import static org.mybatis.generator.internal.util.StringUtility.stringHasValue;
import static org.mybatis.generator.internal.util.messages.Messages.getString;

/**
 * This class parses configuration files into the new Configuration API
 *
 * @author Jeff Butler
 */
public class MyBatisGeneratorConfigurationParser {
    private Properties properties;

    MyBatisGeneratorConfigurationParser(Properties properties) {
        super();
        if (properties == null) {
            this.properties = System.getProperties();
        } else {
            this.properties = properties;
        }
    }

    Configuration parseConfiguration(Element rootNode) throws XMLParserException {
        Configuration configuration = new Configuration();
        NodeList nodeList = rootNode.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node childNode = nodeList.item(i);
            if (childNode.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }
            if ("properties".equals(childNode.getNodeName())) {
                parseProperties(configuration, childNode);
            } else if ("classPathEntry".equals(childNode.getNodeName())) {
                parseClassPathEntry(configuration, childNode);
            } else if ("context".equals(childNode.getNodeName())) {
                parseContext(configuration, childNode);
            }
        }
        return configuration;
    }

    private void parseProperties(Configuration configuration, Node node) throws XMLParserException {
        Properties attributes = parseAttributes(node);
        String resource = attributes.getProperty("resource");
        String url = attributes.getProperty("url");

        if (!stringHasValue(resource) && !stringHasValue(url)) {
            throw new XMLParserException(getString("RuntimeError.14"));
        }

        if (stringHasValue(resource) && stringHasValue(url)) {
            throw new XMLParserException(getString("RuntimeError.14"));
        }

        URL resourceUrl;

        try {
            if (stringHasValue(resource)) {
                resourceUrl = ObjectFactory.getResource(resource);
                if (resourceUrl == null) {
                    throw new XMLParserException(getString("RuntimeError.15", resource));
                }
            } else {
                resourceUrl = new URL(url);
            }

            InputStream inputStream = resourceUrl.openConnection().getInputStream();

            properties.load(inputStream);
            inputStream.close();
        } catch (IOException e) {
            if (stringHasValue(resource)) {
                throw new XMLParserException(getString("RuntimeError.16", resource));
            } else {
                throw new XMLParserException(getString("RuntimeError.17", url));
            }
        }
    }

    private void parseContext(Configuration configuration, Node node) {

        Properties attributes = parseAttributes(node);
        String defaultModelType = attributes.getProperty("defaultModelType");
        String targetRuntime = attributes.getProperty("targetRuntime");
        String introspectedColumnImpl = attributes.getProperty("introspectedColumnImpl");
        String id = attributes.getProperty("id");
        String author = attributes.getProperty("author");

        ModelType mt = defaultModelType == null ? null : ModelType.getModelType(defaultModelType);

        Context context = new Context(mt);
        context.setId(id);
        context.setAuthor(author);
        if (stringHasValue(introspectedColumnImpl)) {
            context.setIntrospectedColumnImpl(introspectedColumnImpl);
        }
        if (stringHasValue(targetRuntime)) {
            context.setTargetRuntime(targetRuntime);
        }

        configuration.addContext(context);

        NodeList nodeList = node.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node childNode = nodeList.item(i);

            if (childNode.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }

            if ("property".equals(childNode.getNodeName())) {
                parseProperty(context, childNode);
            } else if ("plugin".equals(childNode.getNodeName())) {
                parsePlugin(context, childNode);
            } else if ("globalPackage".equals(childNode.getNodeName())) {
                parseGlobalPackage(context, childNode);
            } else if ("commentGenerator".equals(childNode.getNodeName())) {
                parseCommentGenerator(context, childNode);
            } else if ("jdbcConnection".equals(childNode.getNodeName())) {
                parseJdbcConnection(context, childNode);
            } else if ("javaModelGenerator".equals(childNode.getNodeName())) {
                parseJavaModelGenerator(context, childNode);
            } else if ("businessModelGenerator".equals(childNode.getNodeName())) {
                parseBusinessModelGenerator(context, childNode);
            } else if ("javaTypeResolver".equals(childNode.getNodeName())) {
                parseJavaTypeResolver(context, childNode);
            } else if ("sqlMapGenerator".equals(childNode.getNodeName())) {
                parseSqlMapGenerator(context, childNode);
            } else if ("javaClientGenerator".equals(childNode.getNodeName())) {
                parseJavaClientGenerator(context, childNode);
            } else if ("table".equals(childNode.getNodeName())) {
                parseTable(context, childNode);
            }
        }
    }

    private void parseGlobalPackage(Context context, Node globalPackageNode) {
        Properties properties = parseAttributes(globalPackageNode);
        String value = properties.getProperty("value");
        if (StringUtils.isBlank(value)) {
            context.setGlobalPackage(StringUtils.EMPTY);
        } else {
            context.setGlobalPackage(value);
        }
    }

    private void parseSqlMapGenerator(Context context, Node node) {
        SqlMapGeneratorConfiguration sqlMapGeneratorConfiguration = new SqlMapGeneratorConfiguration();

        context.setSqlMapGeneratorConfiguration(sqlMapGeneratorConfiguration);

        Properties attributes = parseAttributes(node);
        String targetPackage = attributes.getProperty("targetPackage");
        if (StringUtils.isBlank(targetPackage)) {
            targetPackage = context.getGlobalPackage() + ".dao";
        }
        String targetProject = attributes.getProperty("targetProject");

        sqlMapGeneratorConfiguration.setTargetPackage(targetPackage);
        sqlMapGeneratorConfiguration.setTargetProject(targetProject);

        NodeList nodeList = node.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node childNode = nodeList.item(i);

            if (childNode.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }

            if ("property".equals(childNode.getNodeName())) {
                parseProperty(sqlMapGeneratorConfiguration, childNode);
            }
        }
    }

    private void parseTable(Context context, Node node) {
        TableConfiguration tc = new TableConfiguration(context);
        context.addTableConfiguration(tc);

        Properties attributes = parseAttributes(node);
        String catalog = attributes.getProperty("catalog");
        String schema = attributes.getProperty("schema");
        String tableName = attributes.getProperty("tableName");
        String domainObjectName = attributes.getProperty("domainObjectName");
        String alias = attributes.getProperty("alias");
        String enableInsert = attributes.getProperty("enableInsert");
        String enableSelectByPrimaryKey = attributes.getProperty("enableSelectByPrimaryKey");
        String enableSelectByExample = attributes.getProperty("enableSelectByExample");
        String enableUpdateByPrimaryKey = attributes.getProperty("enableUpdateByPrimaryKey");
        String enableDeleteByPrimaryKey = attributes.getProperty("enableDeleteByPrimaryKey");
        String enableDeleteByExample = attributes.getProperty("enableDeleteByExample");
        String enableCountByExample = attributes.getProperty("enableCountByExample");
        String enableUpdateByExample = attributes.getProperty("enableUpdateByExample");
        String selectByPrimaryKeyQueryId = attributes.getProperty("selectByPrimaryKeyQueryId");
        String selectByExampleQueryId = attributes.getProperty("selectByExampleQueryId");
        String modelType = attributes.getProperty("modelType");
        String escapeWildcards = attributes.getProperty("escapeWildcards");
        String delimitIdentifiers = attributes.getProperty("delimitIdentifiers");
        String delimitAllColumns = attributes.getProperty("delimitAllColumns");

        if (stringHasValue(catalog)) {
            tc.setCatalog(catalog);
        }

        if (stringHasValue(schema)) {
            tc.setSchema(schema);
        }

        if (stringHasValue(tableName)) {
            tc.setTableName(tableName);
        }

        if (stringHasValue(domainObjectName)) {
            tc.setDomainObjectName(domainObjectName);
        }

        if (stringHasValue(alias)) {
            tc.setAlias(alias);
        }

        if (stringHasValue(enableInsert)) {
            tc.setInsertStatementEnabled(isTrue(enableInsert));
        }

        if (stringHasValue(enableSelectByPrimaryKey)) {
            tc.setSelectByPrimaryKeyStatementEnabled(isTrue(enableSelectByPrimaryKey));
        }

        if (stringHasValue(enableSelectByExample)) {
            tc.setSelectByExampleStatementEnabled(isTrue(enableSelectByExample));
        }

        if (stringHasValue(enableUpdateByPrimaryKey)) {
            tc.setUpdateByPrimaryKeyStatementEnabled(isTrue(enableUpdateByPrimaryKey));
        }

        if (stringHasValue(enableDeleteByPrimaryKey)) {
            tc.setDeleteByPrimaryKeyStatementEnabled(isTrue(enableDeleteByPrimaryKey));
        }

        if (stringHasValue(enableDeleteByExample)) {
            tc.setDeleteByExampleStatementEnabled(isTrue(enableDeleteByExample));
        }

        if (stringHasValue(enableCountByExample)) {
            tc.setCountByExampleStatementEnabled(isTrue(enableCountByExample));
        }

        if (stringHasValue(enableUpdateByExample)) {
            tc.setUpdateByExampleStatementEnabled(isTrue(enableUpdateByExample));
        }

        if (stringHasValue(selectByPrimaryKeyQueryId)) {
            tc.setSelectByPrimaryKeyQueryId(selectByPrimaryKeyQueryId);
        }

        if (stringHasValue(selectByExampleQueryId)) {
            tc.setSelectByExampleQueryId(selectByExampleQueryId);
        }

        if (stringHasValue(modelType)) {
            tc.setConfiguredModelType(modelType);
        }

        if (stringHasValue(escapeWildcards)) {
            tc.setWildcardEscapingEnabled(isTrue(escapeWildcards));
        }

        if (stringHasValue(delimitIdentifiers)) {
            tc.setDelimitIdentifiers(isTrue(delimitIdentifiers));
        }

        if (stringHasValue(delimitAllColumns)) {
            tc.setAllColumnDelimitingEnabled(isTrue(delimitAllColumns));
        }

        NodeList nodeList = node.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node childNode = nodeList.item(i);

            if (childNode.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }

            if ("property".equals(childNode.getNodeName())) {
                parseProperty(tc, childNode);
            } else if ("columnOverride".equals(childNode.getNodeName())) {
                parseColumnOverride(tc, childNode);
            } else if ("ignoreColumn".equals(childNode.getNodeName())) {
                parseIgnoreColumn(tc, childNode);
            } else if ("generatedKey".equals(childNode.getNodeName())) {
                parseGeneratedKey(tc, childNode);
            } else if ("columnRenamingRule".equals(childNode.getNodeName())) {
                parseColumnRenamingRule(tc, childNode);
            } else if ("update".equals(childNode.getNodeName())) {
                parseUpdate(tc, childNode);
            } else if ("createTime".equals(childNode.getNodeName())) {
                parseCreateTime(tc, childNode);
            } else if ("updateTime".equals(childNode.getNodeName())) {
                parseUpdateTime(tc, childNode);
            }
        }
    }

    /**
     * 处理update节点
     *
     * @param tc         table配置
     * @param updateNode update节点
     */
    private void parseUpdate(TableConfiguration tc, Node updateNode) {
        NodeList candidateKeysNodeList = updateNode.getChildNodes();
        UpdateConfiguration updateConfiguration = new UpdateConfiguration();
        for (int i = 0; i < candidateKeysNodeList.getLength(); i++) {
            Node candidateKeysNode = candidateKeysNodeList.item(i);
            if (candidateKeysNode.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }
            if ("candidateKeys".equals(candidateKeysNode.getNodeName())) {
                parseCandidateKeys(updateConfiguration, candidateKeysNode);
            }
        }
        tc.setUpdateConfiguration(updateConfiguration);
    }

    /**
     * 处理更新时间节点
     *
     * @param tc             table配置
     * @param updateTimeNode 更新时间节点
     */
    private void parseUpdateTime(TableConfiguration tc, Node updateTimeNode) {
        Properties properties = parseAttributes(updateTimeNode);
        String updateTime = properties.getProperty("updateTime");
        if (stringHasValue(updateTime)) {
            tc.setUpdateTimeColumn(updateTime);
        }
    }

    /**
     * 处理生成时间节点
     *
     * @param tc             table配置
     * @param createTimeNode 生成时间节点
     */
    private void parseCreateTime(TableConfiguration tc, Node createTimeNode) {
        Properties properties = parseAttributes(createTimeNode);
        String createTime = properties.getProperty("createTime");
        if (stringHasValue(createTime)) {
            tc.setCreateTimeColumn(createTime);
        }
    }

    /**
     * 处理候选码组
     *
     * @param updateConfiguration update配置
     * @param candidateKeysNode   candidateKeys节点
     */
    private void parseCandidateKeys(UpdateConfiguration updateConfiguration, Node candidateKeysNode) {
        List<CandidateKey> candidateKeys = new ArrayList<>();
        NodeList candidateKeyNodeList = candidateKeysNode.getChildNodes();
        for (int i = 0; i < candidateKeyNodeList.getLength(); i++) {
            Node candidateKeyNode = candidateKeyNodeList.item(i);
            if (candidateKeyNode.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }
            if ("candidateKey".equals(candidateKeyNode.getNodeName())) {
                CandidateKey candidateKey = new CandidateKey();
                parseCandidateKey(candidateKeyNode, candidateKey);
                candidateKeys.add(candidateKey);
            }
        }
        updateConfiguration.setCandidateKeys(candidateKeys);
    }

    /**
     * 处理候选码
     *
     * @param candidateKeyNode 候选码节点
     * @param candidateKey     待生成的候选码
     */
    private void parseCandidateKey(Node candidateKeyNode, CandidateKey candidateKey) {
        if (candidateKey == null) {
            return;
        }
        Properties candidateKeyAttr = parseAttributes(candidateKeyNode);
        String candidateKeyName = candidateKeyAttr.getProperty("name");
        List<String> columns = new ArrayList<>();
        List<String> ignores = new ArrayList<>();
        NodeList candidateColumnList = candidateKeyNode.getChildNodes();
        for (int i = 0; i < candidateColumnList.getLength(); i++) {
            Node candidateColumn = candidateColumnList.item(i);
            if (candidateColumn.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }
            if ("column".equals(candidateColumn.getNodeName())) {
                Properties attributes = parseAttributes(candidateColumn);
                String name = attributes.getProperty("name");
                if (stringHasValue(name)) {
                    columns.add(name);
                } else {
                    columns.add("");
                }
            } else if ("ignore".equals(candidateColumn.getNodeName())) {
                Properties attributes = parseAttributes(candidateColumn);
                String name = attributes.getProperty("name");
                if (stringHasValue(name)) {
                    ignores.add(name);
                } else {
                    ignores.add("");
                }
            }
        }
        candidateKey.setName(candidateKeyName);
        candidateKey.setColumnNames(columns);
        candidateKey.setIgnoreColumns(ignores);
    }

    private void parseColumnOverride(TableConfiguration tc, Node node) {
        Properties attributes = parseAttributes(node);
        String column = attributes.getProperty("column");
        String property = attributes.getProperty("property");
        String javaType = attributes.getProperty("javaType");
        String jdbcType = attributes.getProperty("jdbcType");
        String typeHandler = attributes.getProperty("typeHandler");
        String delimitedColumnName = attributes.getProperty("delimitedColumnName");

        ColumnOverride co = new ColumnOverride(column);

        if (stringHasValue(property)) {
            co.setJavaProperty(property);
        }

        if (stringHasValue(javaType)) {
            co.setJavaType(javaType);
        }

        if (stringHasValue(jdbcType)) {
            co.setJdbcType(jdbcType);
        }

        if (stringHasValue(typeHandler)) {
            co.setTypeHandler(typeHandler);
        }

        if (stringHasValue(delimitedColumnName)) {
            co.setColumnNameDelimited(isTrue(delimitedColumnName));
        }

        NodeList nodeList = node.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node childNode = nodeList.item(i);

            if (childNode.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }

            if ("property".equals(childNode.getNodeName())) {
                parseProperty(co, childNode);
            }
        }

        tc.addColumnOverride(co);
    }

    private void parseGeneratedKey(TableConfiguration tc, Node node) {
        Properties attributes = parseAttributes(node);

        String column = attributes.getProperty("column");
        boolean identity = isTrue(attributes.getProperty("identity"));
        String sqlStatement = attributes.getProperty("sqlStatement");
        String type = attributes.getProperty("type");

        GeneratedKey gk = new GeneratedKey(column, sqlStatement, identity, type);

        tc.setGeneratedKey(gk);
    }

    private void parseIgnoreColumn(TableConfiguration tc, Node node) {
        Properties attributes = parseAttributes(node);
        String column = attributes.getProperty("column");
        String delimitedColumnName = attributes.getProperty("delimitedColumnName");

        IgnoredColumn ic = new IgnoredColumn(column);

        if (stringHasValue(delimitedColumnName)) {
            ic.setColumnNameDelimited(isTrue(delimitedColumnName));
        }

        tc.addIgnoredColumn(ic);
    }

    private void parseColumnRenamingRule(TableConfiguration tc, Node node) {
        Properties attributes = parseAttributes(node);
        String searchString = attributes.getProperty("searchString");
        String replaceString = attributes.getProperty("replaceString");

        ColumnRenamingRule crr = new ColumnRenamingRule();

        crr.setSearchString(searchString);

        if (stringHasValue(replaceString)) {
            crr.setReplaceString(replaceString);
        }

        tc.setColumnRenamingRule(crr);
    }

    private void parseJavaTypeResolver(Context context, Node node) {
        JavaTypeResolverConfiguration javaTypeResolverConfiguration = new JavaTypeResolverConfiguration();

        context.setJavaTypeResolverConfiguration(javaTypeResolverConfiguration);

        Properties attributes = parseAttributes(node);
        String type = attributes.getProperty("type");

        if (stringHasValue(type)) {
            javaTypeResolverConfiguration.setConfigurationType(type);
        }

        NodeList nodeList = node.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node childNode = nodeList.item(i);

            if (childNode.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }

            if ("property".equals(childNode.getNodeName())) {
                parseProperty(javaTypeResolverConfiguration, childNode);
            }
        }
    }

    private void parsePlugin(Context context, Node node) {
        PluginConfiguration pluginConfiguration = new PluginConfiguration();

        context.addPluginConfiguration(pluginConfiguration);

        Properties attributes = parseAttributes(node);
        String type = attributes.getProperty("type");

        pluginConfiguration.setConfigurationType(type);

        NodeList nodeList = node.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node childNode = nodeList.item(i);

            if (childNode.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }

            if ("property".equals(childNode.getNodeName())) {
                parseProperty(pluginConfiguration, childNode);
            }
        }
    }

    private void parseJavaModelGenerator(Context context, Node node) {
        JavaModelGeneratorConfiguration javaModelGeneratorConfiguration = new JavaModelGeneratorConfiguration();

        context.setJavaModelGeneratorConfiguration(javaModelGeneratorConfiguration);

        Properties attributes = parseAttributes(node);
        String targetPackage = attributes.getProperty("targetPackage");
        if (StringUtils.isBlank(targetPackage)) {
            targetPackage = context.getGlobalPackage() + ".po";
        }
        String targetProject = attributes.getProperty("targetProject");

        javaModelGeneratorConfiguration.setTargetPackage(targetPackage);
        javaModelGeneratorConfiguration.setTargetProject(targetProject);

        NodeList nodeList = node.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node childNode = nodeList.item(i);

            if (childNode.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }

            if ("property".equals(childNode.getNodeName())) {
                parseProperty(javaModelGeneratorConfiguration, childNode);
            }
        }
    }

    private void parseBusinessModelGenerator(Context context, Node node) {
        BusinessModelGeneratorConfiguration javaModelGeneratorConfiguration = new BusinessModelGeneratorConfiguration();

        context.setBusinessModelGeneratorConfiguration(javaModelGeneratorConfiguration);

        Properties attributes = parseAttributes(node);
        String targetPackage = attributes.getProperty("targetPackage");
        if (StringUtils.isBlank(targetPackage)) {
            targetPackage = context.getGlobalPackage() + ".bo";
        }
        String targetProject = attributes.getProperty("targetProject");


        javaModelGeneratorConfiguration.setTargetPackage(targetPackage);
        javaModelGeneratorConfiguration.setTargetProject(targetProject);

        NodeList nodeList = node.getChildNodes();
        List<String> ignoreColumns = new ArrayList<>();
        javaModelGeneratorConfiguration.setIgnoreColumns(ignoreColumns);
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node childNode = nodeList.item(i);
            if (childNode.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }
            if ("property".equals(childNode.getNodeName())) {
                parseProperty(javaModelGeneratorConfiguration, childNode);
            } else if ("ignore".equals(childNode.getNodeName())) {
                Properties ignoreProperty = parseAttributes(childNode);
                String name = ignoreProperty.getProperty("name");
                if (stringHasValue(name)) {
                    ignoreColumns.add(name);
                }
            }
        }
    }

    private void parseJavaClientGenerator(Context context, Node node) {
        JavaClientGeneratorConfiguration javaClientGeneratorConfiguration = new JavaClientGeneratorConfiguration();

        context.setJavaClientGeneratorConfiguration(javaClientGeneratorConfiguration);

        Properties attributes = parseAttributes(node);
        String type = attributes.getProperty("type");
        String targetPackage = attributes.getProperty("targetPackage");
        if (StringUtils.isBlank(targetPackage)) {
            targetPackage = context.getGlobalPackage() + ".dao";
        }
        String targetProject = attributes.getProperty("targetProject");
        String implementationPackage = attributes.getProperty("implementationPackage");

        javaClientGeneratorConfiguration.setConfigurationType(type);
        javaClientGeneratorConfiguration.setTargetPackage(targetPackage);
        javaClientGeneratorConfiguration.setTargetProject(targetProject);
        javaClientGeneratorConfiguration.setImplementationPackage(implementationPackage);

        NodeList nodeList = node.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node childNode = nodeList.item(i);

            if (childNode.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }

            if ("property".equals(childNode.getNodeName())) {
                parseProperty(javaClientGeneratorConfiguration, childNode);
            }
        }
    }

    private void parseJdbcConnection(Context context, Node node) {
        JDBCConnectionConfiguration jdbcConnectionConfiguration = new JDBCConnectionConfiguration();

        context.setJdbcConnectionConfiguration(jdbcConnectionConfiguration);

        Properties attributes = parseAttributes(node);
        String driverClass = attributes.getProperty("driverClass");
        String connectionURL = attributes.getProperty("connectionURL");
        String userId = attributes.getProperty("userId");
        String password = attributes.getProperty("password");

        jdbcConnectionConfiguration.setDriverClass(driverClass);
        jdbcConnectionConfiguration.setConnectionURL(connectionURL);

        if (stringHasValue(userId)) {
            jdbcConnectionConfiguration.setUserId(userId);
        }

        if (stringHasValue(password)) {
            jdbcConnectionConfiguration.setPassword(password);
        }

        NodeList nodeList = node.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node childNode = nodeList.item(i);

            if (childNode.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }

            if ("property".equals(childNode.getNodeName())) {
                parseProperty(jdbcConnectionConfiguration, childNode);
            }
        }
    }

    private void parseClassPathEntry(Configuration configuration, Node node) {
        Properties attributes = parseAttributes(node);

        configuration.addClasspathEntry(attributes.getProperty("location"));
    }

    private void parseProperty(PropertyHolder propertyHolder, Node node) {
        Properties attributes = parseAttributes(node);

        String name = attributes.getProperty("name");
        String value = attributes.getProperty("value");

        propertyHolder.addProperty(name, value);
    }

    private Properties parseAttributes(Node node) {
        Properties attributes = new Properties();
        NamedNodeMap nnm = node.getAttributes();
        for (int i = 0; i < nnm.getLength(); i++) {
            Node attribute = nnm.item(i);
            String value = parsePropertyTokens(attribute.getNodeValue());
            attributes.put(attribute.getNodeName(), value);
        }

        return attributes;
    }

    private String parsePropertyTokens(String string) {
        final String OPEN = "${";
        final String CLOSE = "}";

        String newString = string;
        if (newString != null) {
            int start = newString.indexOf(OPEN);
            int end = newString.indexOf(CLOSE);

            while (start > -1 && end > start) {
                String prepend = newString.substring(0, start);
                String append = newString.substring(end + CLOSE.length());
                String propName = newString.substring(start + OPEN.length(), end);
                String propValue = properties.getProperty(propName);
                if (propValue != null) {
                    newString = prepend + propValue + append;
                }

                start = newString.indexOf(OPEN, end);
                end = newString.indexOf(CLOSE, end);
            }
        }

        return newString;
    }

    private void parseCommentGenerator(Context context, Node node) {
        CommentGeneratorConfiguration commentGeneratorConfiguration = new CommentGeneratorConfiguration();

        context.setCommentGeneratorConfiguration(commentGeneratorConfiguration);

        Properties attributes = parseAttributes(node);
        String type = attributes.getProperty("type");

        if (stringHasValue(type)) {
            commentGeneratorConfiguration.setConfigurationType(type);
        }

        NodeList nodeList = node.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node childNode = nodeList.item(i);

            if (childNode.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }

            if ("property".equals(childNode.getNodeName())) {
                parseProperty(commentGeneratorConfiguration, childNode);
            }
        }
    }
}
