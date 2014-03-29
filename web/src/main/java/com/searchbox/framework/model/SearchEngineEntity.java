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
package com.searchbox.framework.model;

import java.util.Set;
import java.util.TreeSet;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import com.searchbox.core.engine.SearchEngine;

@Entity
public class SearchEngineEntity<K extends SearchEngine<?,?>> 
  extends BeanFactoryEntity<Long> 
  implements ParametrizedBeanFactory<K>, Comparable<SearchEngineEntity<K>> {
  
  @OneToMany(fetch=FetchType.LAZY, mappedBy = "searchEngine", orphanRemoval = true, 
      cascade ={CascadeType.MERGE, CascadeType.REFRESH})
  @LazyCollection(LazyCollectionOption.TRUE)
  private Set<CollectionEntity<?>> collections; 
  
  private Class<?> clazz;
  
  protected String name;
  
  public SearchEngineEntity(){
    this.collections = new TreeSet<>();
  }

  public Set<CollectionEntity<?>> getCollections() {
    return collections;
  }

  public void setCollections(Set<CollectionEntity<?>> collections) {
    this.collections = collections;
  }

  public String getName() {
    return name;
  }

  public SearchEngineEntity<?> setName(String name) {
    this.name = name;
    return this;
  }

  public Class<?> getClazz() {
    return clazz;
  }

  public SearchEngineEntity<?> setClazz(Class<?> clazz) {
    this.clazz = clazz;
    return this;
  }

  @SuppressWarnings("unchecked")
  @Override
  public K build() {
    if (this.getClazz() == null) {
      throw new MissingClassAttributeException();
    }
    return (K) super.build(this.getClazz());
  }

  @Override
  public int compareTo(SearchEngineEntity<K> o) {
    return this.getName().compareTo(o.getName());
  }
  
  public SearchEngineEntity<?> setAttribute(String name, Object value) {
    this.getAttributes().add(new AttributeEntity()
      .setName(name)
      .setValue(value)
      .setType(value.getClass()));
    return this;
  }

  @Override
  public String toString() {
    return "SearchEngineEntity [clazz=" + clazz + ", name=" + name + "]";
  }
  
  
}
