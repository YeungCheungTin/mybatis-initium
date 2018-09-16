/*
 *  Copyright 2006 The Apache Software Foundation
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.mybatis.generator.internal;

import org.mybatis.generator.api.DAOMethodNameCalculator;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.internal.rules.Rules;

/**
 * @author Jeff Butler
 */
public class ExtendedDAOMethodNameCalculator implements DAOMethodNameCalculator {

    public String getInsertMethodName(IntrospectedTable introspectedTable) {
        return "insert" + introspectedTable.getFullyQualifiedTable().getDomainObjectName();
    }

    /**
     * 1. if this will be the only updateByPrimaryKey, then the result should be
     * updateByPrimaryKey. 2. If the other method is enabled, but there are
     * seperate base and blob classes, then the method name should be
     * updateByPrimaryKey 3. Else the method name should be
     * updateByPrimaryKeyWithoutBLOBs
     */
    public String getUpdateByPrimaryKeyWithoutBLOBsMethodName(
            IntrospectedTable introspectedTable) {
        StringBuilder sb = new StringBuilder();

        sb.append("update"); 
        sb.append(introspectedTable.getFullyQualifiedTable()
                .getDomainObjectName());

        Rules rules = introspectedTable.getRules();

        if (!rules.generateUpdateByPrimaryKeyWithBLOBs()) {
            sb.append("ByPrimaryKey"); 
        } else if (rules.generateRecordWithBLOBsClass()) {
            sb.append("ByPrimaryKey"); 
        } else {
            sb.append("ByPrimaryKeyWithoutBLOBs"); 
        }

        return sb.toString();
    }

    /**
     * 1. if this will be the only updateByPrimaryKey, then the result should be
     * updateByPrimaryKey. 2. If the other method is enabled, but there are
     * seperate base and blob classes, then the method name should be
     * updateByPrimaryKey 3. Else the method name should be
     * updateByPrimaryKeyWithBLOBs
     */
    public String getUpdateByPrimaryKeyWithBLOBsMethodName(
            IntrospectedTable introspectedTable) {
        StringBuilder sb = new StringBuilder();
        sb.append("update"); 
        sb.append(introspectedTable.getFullyQualifiedTable()
                .getDomainObjectName());

        Rules rules = introspectedTable.getRules();

        if (!rules.generateUpdateByPrimaryKeyWithoutBLOBs()) {
            sb.append("ByPrimaryKey"); 
        } else if (rules.generateRecordWithBLOBsClass()) {
            sb.append("ByPrimaryKey"); 
        } else {
            sb.append("ByPrimaryKeyWithBLOBs"); 
        }

        return sb.toString();
    }

    public String getDeleteByExampleMethodName(IntrospectedTable introspectedTable) {
        return "delete" + introspectedTable.getFullyQualifiedTable().getDomainObjectName() + "ByExample";
    }

    public String getDeleteByPrimaryKeyMethodName(IntrospectedTable introspectedTable) {
        return "delete" + introspectedTable.getFullyQualifiedTable().getDomainObjectName() + "ByPrimaryKey";
    }

    /**
     * 1. if this will be the only selectByExample, then the result should be
     * selectByExample. 2. Else the method name should be
     * selectByExampleWithoutBLOBs
     */
    public String getSelectByExampleWithoutBLOBsMethodName(
            IntrospectedTable introspectedTable) {
        StringBuilder sb = new StringBuilder();
        sb.append("select"); 
        sb.append(introspectedTable.getFullyQualifiedTable()
                .getDomainObjectName());
        sb.append("ByExample"); 

        Rules rules = introspectedTable.getRules();

        if (rules.generateSelectByExampleWithBLOBs()) {
            sb.append("WithoutBLOBs"); 
        }

        return sb.toString();
    }

    /**
     * 1. if this will be the only selectByExample, then the result should be
     * selectByExample. 2. Else the method name should be
     * selectByExampleWithBLOBs
     */
    public String getSelectByExampleWithBLOBsMethodName(
            IntrospectedTable introspectedTable) {
        StringBuilder sb = new StringBuilder();
        sb.append("select"); 
        sb.append(introspectedTable.getFullyQualifiedTable()
                .getDomainObjectName());
        sb.append("ByExample"); 

        Rules rules = introspectedTable.getRules();

        if (rules.generateSelectByExampleWithoutBLOBs()) {
            sb.append("WithBLOBs"); 
        }

        return sb.toString();
    }

    public String getSelectByPrimaryKeyMethodName(IntrospectedTable introspectedTable) {
        return "select" + introspectedTable.getFullyQualifiedTable().getDomainObjectName() + "ByPrimaryKey";
    }

    public String getUpdateByPrimaryKeySelectiveMethodName(IntrospectedTable introspectedTable) {

        return "update" + introspectedTable.getFullyQualifiedTable().getDomainObjectName() + "ByPrimaryKeySelective";
    }

    public String getCountByExampleMethodName(IntrospectedTable introspectedTable) {
        return "count" + introspectedTable.getFullyQualifiedTable().getDomainObjectName() + "ByExample";
    }

    public String getUpdateByExampleSelectiveMethodName(IntrospectedTable introspectedTable) {
        return "update" + introspectedTable.getFullyQualifiedTable().getDomainObjectName() + "ByExampleSelective";
    }

    public String getUpdateByExampleWithBLOBsMethodName(IntrospectedTable introspectedTable) {
        StringBuilder sb = new StringBuilder();
        sb.append("update"); 
        sb.append(introspectedTable.getFullyQualifiedTable()
                .getDomainObjectName());

        Rules rules = introspectedTable.getRules();

        if (!rules.generateUpdateByExampleWithoutBLOBs()) {
            sb.append("ByExample"); 
        } else if (rules.generateRecordWithBLOBsClass()) {
            sb.append("ByExample"); 
        } else {
            sb.append("ByExampleWithBLOBs"); 
        }

        return sb.toString();
    }

    public String getUpdateByExampleWithoutBLOBsMethodName(IntrospectedTable introspectedTable) {
        StringBuilder sb = new StringBuilder();
        sb.append("update"); 
        sb.append(introspectedTable.getFullyQualifiedTable().getDomainObjectName());

        Rules rules = introspectedTable.getRules();

        if (!rules.generateUpdateByExampleWithBLOBs()) {
            sb.append("ByExample"); 
        } else if (rules.generateRecordWithBLOBsClass()) {
            sb.append("ByExample"); 
        } else {
            sb.append("ByExampleWithoutBLOBs"); 
        }

        return sb.toString();
    }

    public String getInsertSelectiveMethodName(IntrospectedTable introspectedTable) {
        return "insert" + introspectedTable.getFullyQualifiedTable().getDomainObjectName() + "Selective";
    }
}
