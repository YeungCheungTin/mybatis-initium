package org.mybatis.generator.codegen.mybatis3.model;

import org.mybatis.generator.api.CommentGenerator;
import org.mybatis.generator.api.FullyQualifiedTable;
import org.mybatis.generator.api.dom.java.CompilationUnit;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.Interface;
import org.mybatis.generator.api.dom.java.JavaVisibility;
import org.mybatis.generator.codegen.AbstractJavaGenerator;
import org.mybatis.generator.codegen.mybatis3.javamapper.elements.AbstractJavaMapperMethodGenerator;
import org.mybatis.generator.codegen.mybatis3.model.method.QueryByUniqueKeyMethodGenerator;
import org.mybatis.generator.codegen.mybatis3.model.method.SaveMethodGenerator;
import org.mybatis.generator.config.BusinessModelGeneratorConfiguration;

import java.util.ArrayList;
import java.util.List;

import static org.mybatis.generator.internal.util.messages.Messages.getString;

/**
 * dataService接口生成
 *
 * @author YangXiangTian
 */
public class BaseDataServiceGenerator extends AbstractJavaGenerator {

    @Override
    public List<CompilationUnit> getCompilationUnits() {
        BusinessModelGeneratorConfiguration businessModelGeneratorConfiguration = context.getBusinessModelGeneratorConfiguration();
        if (businessModelGeneratorConfiguration == null || !businessModelGeneratorConfiguration.isGenerateDataService()) {
            return new ArrayList<>();
        }
        FullyQualifiedTable table = introspectedTable.getFullyQualifiedTable();
        progressCallback.startTask(getString("Progress.8", table.toString()));
        CommentGenerator commentGenerator = context.getCommentGenerator();

        // 生成dataService
        FullyQualifiedJavaType type = new FullyQualifiedJavaType(introspectedTable.getDataServiceType());
        // 设置为接口
        Interface interfaze = new Interface(type);
        // 设置为public
        interfaze.setVisibility(JavaVisibility.PUBLIC);
        // 设置注释
        commentGenerator.addDataServiceComment(interfaze, introspectedTable);
        // 添加方法
        this.addSaveMethod(interfaze);
        this.addQueryByUniqueKeyMethod(interfaze);
        // 生成接口
        List<CompilationUnit> answer = new ArrayList<>();
        answer.add(interfaze);
        return answer;
    }

    private void addQueryByUniqueKeyMethod(Interface interfaze) {
        AbstractJavaMapperMethodGenerator methodGenerator = new QueryByUniqueKeyMethodGenerator();
        initializeAndExecuteGenerator(methodGenerator, interfaze);
    }

    private void addSaveMethod(Interface interfaze) {
        AbstractJavaMapperMethodGenerator methodGenerator = new SaveMethodGenerator();
        initializeAndExecuteGenerator(methodGenerator, interfaze);
    }


    private void initializeAndExecuteGenerator(AbstractJavaMapperMethodGenerator methodGenerator, Interface interfaze) {
        methodGenerator.setContext(context);
        methodGenerator.setIntrospectedTable(introspectedTable);
        methodGenerator.setProgressCallback(progressCallback);
        methodGenerator.setWarnings(warnings);
        methodGenerator.addInterfaceElements(interfaze);
    }

}
