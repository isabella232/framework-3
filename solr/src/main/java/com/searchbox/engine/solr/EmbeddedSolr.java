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
package com.searchbox.engine.solr;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;
import org.apache.solr.core.CoreContainer;
import org.apache.solr.core.CoreDescriptor;
import org.apache.solr.core.SolrCore;
import org.apache.solr.schema.CopyField;
import org.apache.solr.schema.IndexSchema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import com.searchbox.core.SearchAttribute;
import com.searchbox.core.dm.Collection;
import com.searchbox.core.dm.Field;

public class EmbeddedSolr extends SolrSearchEngine implements InitializingBean,
    DisposableBean {

  private static final Logger LOGGER = LoggerFactory
      .getLogger(EmbeddedSolr.class);

  @SearchAttribute
  private String solrHome;

  @SearchAttribute
  private String dataDir;

  private static CoreContainer coreContainer = null;

  public EmbeddedSolr() {
    super();
  }

  public EmbeddedSolr(String name, String solrHome) {
    super(name);
    this.solrHome = solrHome;
  }

  @Override
  public void afterPropertiesSet() throws Exception {
    if (EmbeddedSolr.coreContainer == null) {
      try {
        LOGGER.info("Embedded solr.solr.home is: " + this.solrHome);
        EmbeddedSolr.coreContainer = new CoreContainer(this.solrHome);
        EmbeddedSolr.coreContainer.load();
      } catch (Exception e) {
        LOGGER.error("Could not start search engine", e);
      }
    }
  }

  @Override
  public void destroy() throws Exception {
    coreContainer.shutdown();
  }

  @Override
  protected SolrServer getSolrServer(Collection collection) {
    return new EmbeddedSolrServer(coreContainer, collection.getName());
  }

  public String getDataDir() {
    return dataDir;
  }

  public void setDataDir(String dataDir) {
    this.dataDir = dataDir;
  }

  public String getSolrHome() {
    return solrHome;
  }

  public void setSolrHome(String solrHome) {
    this.solrHome = solrHome;
  }

  @Override
  protected boolean updateDataModel(Collection collection, Map<Field, Set<String>> copyFields) {
    for (Entry<Field, Set<String>> copyField : copyFields.entrySet()) {
      this.addCopyFields(collection, copyField.getKey(), copyField.getValue());
    }
    return true;
  }

  private boolean addCopyFields(Collection collection, Field field, Set<String> copyFields) {
    SolrCore core = coreContainer.getCore(collection.getName());
    IndexSchema schema = core.getLatestSchema();

    for (CopyField copyField : schema.getCopyFieldsList(field.getKey())) {
      copyFields.remove(copyField.getDestination().getName());
    }

    Map<String, java.util.Collection<String>> copyFieldsMap =
        new HashMap<String, java.util.Collection<String>>();
    copyFieldsMap.put(field.getKey(), copyFields);
    schema = schema.addCopyFields(copyFieldsMap);

    core.setLatestSchema(schema);

    return true;
  }

  @Override
  public void reloadEngine(Collection collection) {
    try {
      coreContainer.reload(collection.getName());
    } catch (Exception e) {
      LOGGER.warn(e.getMessage());
    }
  }

  @Override
  public void register(Collection collection) {

    String coreInstanceDir = this.solrHome;

    Properties properties = new Properties();

    if (this.dataDir != null) {
      File dataDir = new File(this.dataDir);
      if (dataDir.exists()) {
        try {
          FileUtils.deleteDirectory(dataDir);
        } catch (IOException e) {
          LOGGER.error("Could not delete DataDir: " + dataDir);
        }
      }
      properties.setProperty("dataDir", this.dataDir);
    } else {
      properties.setProperty("dataDir",
          coreInstanceDir + "/" + collection.getName() + "/data/");
    }

    CoreDescriptor dcore = new CoreDescriptor(coreContainer,
        collection.getName(), coreInstanceDir, properties);

    try {
      SolrCore core = coreContainer.create(dcore);
      coreContainer.register(core, false);

      LOGGER.info("Solr Core config: " + core.getConfigResource());
      LOGGER.info("Solr SchemaResource: " + core.getSchemaResource());
      LOGGER.info("Solr Data dir: " + core.getDataDir());
    } catch (Exception e) {
      LOGGER.warn(e.getMessage());
    }
  }
}
