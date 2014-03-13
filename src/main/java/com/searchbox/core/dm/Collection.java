/*******************************************************************************
 * Copyright Searchbox - http://www.searchbox.com
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package com.searchbox.core.dm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.searchbox.core.SearchAttribute;
import com.searchbox.core.engine.SearchEngine;

public class Collection {

    /**
     */
    @SearchAttribute
    protected String name;

    @SearchAttribute
    protected String description;

    protected List<Field> fields = new ArrayList<Field>();

    @SearchAttribute
    protected SearchEngine<?, ?> searchEngine;

    @SearchAttribute
    String idFieldName;

    public Collection() {

    }

    public Collection(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this,
                ToStringStyle.SHORT_PREFIX_STYLE);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Field> getFields() {
        return fields;
    }

    public void setFields(List<Field> fields) {
        this.fields = fields;
    }

    public SearchEngine<?, ?> getSearchEngine() {
        return searchEngine;
    }

    public void setSearchEngine(SearchEngine<?, ?> searchEngine) {
        this.searchEngine = searchEngine;
    }

    /**
     * @return the idFieldName
     */
    public String getIdFieldName() {
        return idFieldName;
    }

    /**
     * @param idFieldName
     *            the idFieldName to set
     */
    public void setIdFieldName(String idFieldName) {
        this.idFieldName = idFieldName;
    }

    public static class FieldMap extends HashMap<String, List<Object>> {
        public void put(String key, Object number) {
            List<Object> current = get(key);
            if (current == null) {
                current = new ArrayList<Object>();
                super.put(key, current);
            }
            current.add(number);
        }
    }
}
