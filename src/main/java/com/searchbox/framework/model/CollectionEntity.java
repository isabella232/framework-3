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

import java.lang.reflect.Method;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.searchbox.collection.SynchronizedCollection;
import com.searchbox.core.dm.Collection;
import com.searchbox.core.dm.Field;
import com.searchbox.core.dm.MultiCollection;
import com.searchbox.core.dm.SearchableCollection;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public class CollectionEntity<K extends Collection> extends
    BeanFactoryEntity<Long> implements ParametrizedBeanFactory<K>,
    Comparable<CollectionEntity<K>> {

  private static final Logger LOGGER = LoggerFactory
      .getLogger(CollectionEntity.class);

  private Class<?> clazz;

  private String name;

  private String description;

  private Boolean autoStart = false;

  private String idFieldName;

  @ManyToOne(fetch=FetchType.LAZY)
  @LazyCollection(LazyCollectionOption.TRUE)
  private SearchEngineEntity<?> searchEngine;

  @OneToMany(fetch=FetchType.LAZY, mappedBy = "collection", cascade = CascadeType.ALL)
  @LazyCollection(LazyCollectionOption.TRUE)
  private Set<PresetEntity> presets;

  @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  @LazyCollection(LazyCollectionOption.TRUE)
  private Set<FieldEntity> fields;

  @Transient
  private Date lastJobDate;

  @Transient
  private String lastJobStatus;

  @Transient
  private int jobCount;

  public CollectionEntity() {
    this.fields = new TreeSet<FieldEntity>();
    this.presets = new TreeSet<PresetEntity>();
    // TODO infer clazz thru reflection
  }

  public boolean isRunnable(){
    return SynchronizedCollection.class.isAssignableFrom(this.clazz);
  }

  public Date getLastJobDate() {
    return lastJobDate;
  }

  public void setLastJobDate(Date lastJobDate) {
    this.lastJobDate = lastJobDate;
  }

  public String getLastJobStatus() {
    return lastJobStatus;
  }

  public void setLastJobStatus(String lastJobStatus) {
    this.lastJobStatus = lastJobStatus;
  }

  public int getJobCount() {
    return jobCount;
  }

  public void setJobCount(int i) {
    this.jobCount = i;
  }

  public String getName() {
    return name;
  }

  public CollectionEntity<K> setName(String name) {
    this.name = name;
    return this;
  }

  public Set<FieldEntity> getFields() {
    return fields;
  }

  public void setFields(Set<FieldEntity> fields) {
    this.fields = fields;
  }

  @SuppressWarnings("unchecked")
  public K build() {
    if (this.getClazz() == null) {
      throw new MissingClassAttributeException();
    }
    LOGGER.info("Building Class for {}", this.getClazz());
    K collection = (K) super.build(this.getClazz());
    if (SearchableCollection.class.isAssignableFrom(collection.getClass())) {
      ((SearchableCollection) collection).setSearchEngine(this.searchEngine
          .build());
    }
    return collection;
  }

  public Class<?> getClazz() {
    return clazz;
  }

  @SuppressWarnings("unchecked")
  public CollectionEntity<K> setClazz(Class<?> clazz) {
    this.clazz = clazz;
    if (!MultiCollection.class.isAssignableFrom(clazz)) {
      try {
        Method method = clazz.getMethod("GET_FIELDS");
        if (method != null) {
          for (Field field :  (List<Field>) method.invoke(null)) {
            FieldEntity fieldDef = new FieldEntity(field.getClazz(),
                field.getKey());
            LOGGER.debug("Created FieldDef[{},{}]", field.getClazz()
                .getSimpleName(), field.getKey());
            if (!this.fields.contains(fieldDef)) {
              LOGGER.trace("Adding FieldDef[{},{}]", field.getClazz()
                  .getSimpleName(), field.getKey());
              this.fields.add(fieldDef);
            }
          }
        }
      } catch (Exception e) {
        LOGGER.warn("Could not use GET_FIELD method on collection: {}", name);
      }
    }
    return this;
  }

  public String getDescription() {
    return description;
  }

  public CollectionEntity<K> setDescription(String description) {
    this.description = description;
    return this;
  }

  public CollectionEntity<K> setAttribute(String name, Object value) {
    this.getAttributes().add(
        new AttributeEntity().setName(name).setValue(value));
    return this;
  }

  public Boolean getAutoStart() {
    return autoStart;
  }

  public CollectionEntity<?> setAutoStart(Boolean autoStart) {
    this.autoStart = autoStart;
    return this;
  }

  public SearchEngineEntity<?> getSearchEngine() {
    return searchEngine;
  }

  public CollectionEntity<?> setSearchEngine(SearchEngineEntity<?> searchEngine) {
    this.searchEngine = searchEngine;
    searchEngine.getCollections().add(this);
    return this;
  }

  public String getIdFieldName() {
    return idFieldName;
  }

  public CollectionEntity<?> setIdFieldName(String idFieldName) {
    this.idFieldName = idFieldName;
    return this;
  }

  public Set<PresetEntity> getPresets() {
    return presets;
  }

  public CollectionEntity<?> setPresets(Set<PresetEntity> presetEntity) {
    this.presets = presetEntity;
    return this;
  }

  public FieldEntity getField(String key) {
    for (FieldEntity field : this.getFields()) {
      if (field.getKey().equals(key)) {
        return field;
      }
    }
    return null;
  }

  @Override
  public int compareTo(CollectionEntity<K> o) {
    return this.getName().compareTo(o.getName());
  }

  @Override
  public String toString() {
    return "CollectionEntity [clazz=" + clazz + ", name=" + name
        + ", autoStart=" + autoStart + ", idFieldName=" + idFieldName + "]";
  }
}
