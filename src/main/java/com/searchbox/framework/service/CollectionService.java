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
package com.searchbox.framework.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.searchbox.collection.SynchronizedCollection;
import com.searchbox.core.dm.Collection;
import com.searchbox.core.dm.FieldAttribute;
import com.searchbox.core.dm.SearchableCollection;
import com.searchbox.core.engine.ManagedSearchEngine;
import com.searchbox.core.engine.SearchEngine;
import com.searchbox.framework.event.SearchboxReady;
import com.searchbox.framework.model.CollectionEntity;
import com.searchbox.framework.model.FieldAttributeEntity;
import com.searchbox.framework.model.PresetEntity;
import com.searchbox.framework.repository.CollectionRepository;

@Service
public class CollectionService implements ApplicationListener<SearchboxReady>,
    InitializingBean {

  private static final Logger LOGGER = LoggerFactory
      .getLogger(CollectionService.class);

  public static final String UPDATE_DM_ON_START = "searchbox.dm.update.onstart";
  public static final Boolean UPDATE_DM_ON_START_DEFAULT = false;

  @Autowired
  CollectionRepository repository;

  @Resource
  Environment env;

  @Autowired
  private JobExplorer jobExplorer;

  private Boolean updateDmOnStart;

  public CollectionService() {
    updateDmOnStart = false;
  }

  @Override
  public void afterPropertiesSet() throws Exception {
    this.updateDmOnStart = env.getProperty(UPDATE_DM_ON_START, Boolean.class,
        UPDATE_DM_ON_START_DEFAULT);
  }

  public Map<String, String> synchronizeData(CollectionEntity<?> collectiondef) {
    Map<String, String> result = new HashMap<String, String>();
    Collection collection = collectiondef.build();
    if (SynchronizedCollection.class.isAssignableFrom(collection.getClass())) {
      LOGGER.info("Starting Data synchronization for \"" + collection.getName()
          + "\"");
      try {
        ((SynchronizedCollection) collection).synchronize();
      } catch (Exception e) {
        result.put("status", "KO");
        result.put("message", e.getMessage());
        return result;
      }
      result.put("status", "OK");
      result.put("message", "Synchronization started");
    } else {
      result.put("status", "KO");
      result.put("message", "Collection is not synchronizable");
    }
    return result;
  }

  @Transactional
  public Map<String, String> synchronizeDm(CollectionEntity<?> collectionEntity) {

    Map<String, String> result = new HashMap<String, String>();
    Collection collection = collectionEntity.build();

    if (SearchableCollection.class.isAssignableFrom(collection.getClass())) {
      SearchEngine<?, ?> engine = ((SearchableCollection) collection)
          .getSearchEngine();

      if (ManagedSearchEngine.class.isAssignableFrom(engine.getClass())) {
        LOGGER.info("Register Searchengine Configuration for \""
            + collection.getName() + "\"");
        ((ManagedSearchEngine) engine).register(collection);
      }

      if (SynchronizedCollection.class.isAssignableFrom(collection.getClass())) {
        LOGGER.info("Starting DM synchronization for \"" + collection.getName()
            + "\"");
        for (PresetEntity presetDef : collectionEntity.getPresets()) {
          List<FieldAttribute> fieldAttributes = new ArrayList<FieldAttribute>();
          for (FieldAttributeEntity fieldAttr : presetDef.getFieldAttributes()) {
            fieldAttributes.add(fieldAttr.build());
          }
          ((ManagedSearchEngine) engine).updateDataModel(collection,
              fieldAttributes);
        }
      }
    }
    return result;
  }

  @Override
  @Transactional
  public void onApplicationEvent(SearchboxReady event) {

    LOGGER.info("Searchbox is ready. Loading autoStart collections");

    Iterable<CollectionEntity<?>> collectionDefs = repository.findAll();
    for (CollectionEntity<?> collectionDef : collectionDefs) {
      if (this.updateDmOnStart) {
        synchronizeDm(collectionDef);
        if (collectionDef.getAutoStart()) {
          synchronizeData(collectionDef);
        }
      }
    }
  }

  /**
   * Returns a new object which specifies the the wanted result page.
   *
   * @param pageIndex
   *          The index of the wanted result page
   * @return
   */
  private Pageable constructPageSpecification(int pageIndex) {
    Pageable pageSpecification = new PageRequest(pageIndex, 10, sortByName());
    return pageSpecification;
  }

  /**
   * Returns a Sort object which sorts collection in ascending order by using
   * the name.
   *
   * @return
   */
  private Sort sortByName() {
    return new Sort(Sort.Direction.ASC, "name");
  }

  public List<CollectionEntity<?>> findAll() {
    return this.findAll(0);
  }

  public List<CollectionEntity<?>> findAll(int pageIndex) {
    LOGGER.debug("Listing all collections for page: " + pageIndex);
    Page<CollectionEntity<?>> requestedPage = repository
        .findAll(constructPageSpecification(pageIndex));
    for (CollectionEntity<?> collection : requestedPage.getContent()) {
      setJobStatus(collection);
    }
    return requestedPage.getContent();
  }

  private void setJobStatus(CollectionEntity<?> collection) {
    if (collection.isRunnable()) {
      // This will retrieve the latest job instance:
      List<JobInstance> jobInstances = jobExplorer.getJobInstances(
          collection.getName(), 0, 1);
      if (!jobInstances.isEmpty()) {
        // This will retrieve the latest job execution:
        List<JobExecution> jobExecutions = jobExplorer
            .getJobExecutions(jobInstances.get(0));

        if (!jobExecutions.isEmpty()) {
          collection.setJobCount(jobExecutions.size());
          collection.setLastJobDate(jobExecutions.get(0).getCreateTime());
          collection.setLastJobStatus(jobExecutions.get(0).getStatus()
              .toString());
        }
      }
    }
  }
}
