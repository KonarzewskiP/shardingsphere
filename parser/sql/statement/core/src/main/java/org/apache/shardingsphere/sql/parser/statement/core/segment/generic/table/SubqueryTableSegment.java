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

package org.apache.shardingsphere.sql.parser.statement.core.segment.generic.table;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.apache.shardingsphere.sql.parser.statement.core.segment.dml.expr.subquery.SubquerySegment;
import org.apache.shardingsphere.sql.parser.statement.core.segment.generic.AliasSegment;
import org.apache.shardingsphere.sql.parser.statement.core.segment.generic.PivotSegment;
import org.apache.shardingsphere.sql.parser.statement.core.value.identifier.IdentifierValue;

import java.util.Optional;

/**
 * Subquery table segment.
 */
@RequiredArgsConstructor
@Getter
public final class SubqueryTableSegment implements TableSegment {
    
    private final int startIndex;
    
    private final int stopIndex;
    
    private final SubquerySegment subquery;
    
    @Setter
    private AliasSegment alias;
    
    @Setter
    private PivotSegment pivot;
    
    @Override
    public Optional<String> getAliasName() {
        return null == alias ? Optional.empty() : Optional.ofNullable(alias.getIdentifier().getValue());
    }
    
    @Override
    public Optional<IdentifierValue> getAlias() {
        return Optional.ofNullable(alias).map(AliasSegment::getIdentifier);
    }
    
    /**
     * Get pivot segment.
     *
     * @return pivot segment
     */
    public Optional<PivotSegment> getPivot() {
        return Optional.ofNullable(pivot);
    }
    
    @Override
    public int getStartIndex() {
        return startIndex;
    }
    
    @Override
    public int getStopIndex() {
        return null == alias ? stopIndex : alias.getStopIndex();
    }
    
    /**
     * Get alias segment.
     *
     * @return alias segment
     */
    public Optional<AliasSegment> getAliasSegment() {
        return Optional.ofNullable(alias);
    }
}
