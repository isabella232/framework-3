// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package com.searchbox.domain.app;

import com.searchbox.domain.app.Preset;
import com.searchbox.domain.app.SearchElementDefinition;
import com.searchbox.domain.app.SearchElementDefinitionAttribute;
import java.util.List;

privileged aspect SearchElementDefinition_Roo_JavaBean {
    
    public Preset SearchElementDefinition.getPreset() {
        return this.preset;
    }
    
    public void SearchElementDefinition.setPreset(Preset preset) {
        this.preset = preset;
    }
    
    public Integer SearchElementDefinition.getPosition() {
        return this.position;
    }
    
    public void SearchElementDefinition.setPosition(Integer position) {
        this.position = position;
    }
    
    public Class<?> SearchElementDefinition.getSearchElementClass() {
        return this.searchElementClass;
    }
    
    public void SearchElementDefinition.setSearchElementClass(Class<?> searchElementClass) {
        this.searchElementClass = searchElementClass;
    }
    
    public void SearchElementDefinition.setAttributes(List<SearchElementDefinitionAttribute> attributes) {
        this.attributes = attributes;
    }
    
}
