// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package com.searchbox.domain;

import com.searchbox.domain.DefinitionAttribute;
import com.searchbox.domain.PresetDefinition;
import com.searchbox.domain.SearchElementDefinition;
import java.util.List;
import org.springframework.context.ApplicationContext;

privileged aspect SearchElementDefinition_Roo_JavaBean {
    
    public Class<?> SearchElementDefinition.getClazz() {
        return this.clazz;
    }
    
    public void SearchElementDefinition.setClazz(Class<?> clazz) {
        this.clazz = clazz;
    }
    
    public Integer SearchElementDefinition.getPosition() {
        return this.position;
    }
    
    public void SearchElementDefinition.setPosition(Integer position) {
        this.position = position;
    }
    
    public PresetDefinition SearchElementDefinition.getPreset() {
        return this.preset;
    }
    
    public void SearchElementDefinition.setPreset(PresetDefinition preset) {
        this.preset = preset;
    }
    
    public void SearchElementDefinition.setAttributes(List<DefinitionAttribute> attributes) {
        this.attributes = attributes;
    }
    
    public ApplicationContext SearchElementDefinition.getContext() {
        return this.context;
    }
    
    public void SearchElementDefinition.setContext(ApplicationContext context) {
        this.context = context;
    }
    
}
