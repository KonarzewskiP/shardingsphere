/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.shardingsphere.infra.check;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.shardingsphere.infra.metadata.ShardingSphereMetaData;
import org.apache.shardingsphere.infra.metadata.rule.ShardingSphereRuleMetaData;
import org.apache.shardingsphere.infra.metadata.user.Grantee;
import org.apache.shardingsphere.infra.rule.ShardingSphereRule;
import org.apache.shardingsphere.infra.spi.ShardingSphereServiceLoader;
import org.apache.shardingsphere.infra.spi.ordered.OrderedSPIRegistry;
import org.apache.shardingsphere.sql.parser.sql.common.statement.SQLStatement;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

/**
 * SQL check engine.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SQLCheckEngine {
    
    static {
        ShardingSphereServiceLoader.register(SQLChecker.class);
    }
    
    /**
     * Check schema.
     *
     * @param schemaName schema name
     * @param metaData meta data
     * @param globalRuleMetaData global rule meta data
     * @param grantee grantee
     * @return check result
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static boolean check(final String schemaName, final ShardingSphereMetaData metaData, final ShardingSphereRuleMetaData globalRuleMetaData, final Grantee grantee) {
        for (Entry<ShardingSphereRule, SQLChecker> entry : OrderedSPIRegistry.getRegisteredServices(getRules(metaData, globalRuleMetaData), SQLChecker.class).entrySet()) {
            boolean checkResult = entry.getValue().check(schemaName, grantee, entry.getKey());
            if (!checkResult) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * Check SQL.
     * 
     * @param sqlStatement SQL statement
     * @param parameters SQL parameters
     * @param metaData meta data
     * @param globalRuleMetaData global rule meta data
     * @param grantee grantee
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static void check(final SQLStatement sqlStatement, final List<Object> parameters, 
                             final ShardingSphereMetaData metaData, final ShardingSphereRuleMetaData globalRuleMetaData, final Grantee grantee) {
        for (Entry<ShardingSphereRule, SQLChecker> entry : OrderedSPIRegistry.getRegisteredServices(getRules(metaData, globalRuleMetaData), SQLChecker.class).entrySet()) {
            SQLCheckResult checkResult = entry.getValue().check(sqlStatement, parameters, metaData, grantee, entry.getKey());
            if (!checkResult.isPassed()) {
                throw new SQLCheckException(checkResult.getErrorMessage());
            }
        }
    }
    
    private static Collection<ShardingSphereRule> getRules(final ShardingSphereMetaData metaData, final ShardingSphereRuleMetaData globalRuleMetaData) {
        Collection<ShardingSphereRule> result = new LinkedList<>(metaData.getRuleMetaData().getRules());
        result.addAll(globalRuleMetaData.getRules());
        return result;
    }
}
