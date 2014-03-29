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
package com.searchbox.framework.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.searchbox.framework.model.PresetEntity;
import com.searchbox.framework.model.SearchboxEntity;

public interface PresetRepository extends
    CrudRepository<PresetEntity, Long> {

  public PresetEntity findPresetDefinitionBySearchboxAndSlug(
      SearchboxEntity searchbox, String slug);

  public List<PresetEntity> findAllBySearchbox(SearchboxEntity searchbox);
  
  public List<PresetEntity> findAllBySearchboxAndVisible(
      SearchboxEntity searchbox, Boolean visible);

  public PresetEntity findPresetDefinitionBySlug(String slug);
  

}
