// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package com.searchbox.domain.app;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Version;

privileged aspect FieldDefinition_Roo_Jpa_Entity {
    
    declare @type: FieldDefinition: @Entity;
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long FieldDefinition.id;
    
    @Version
    @Column(name = "version")
    private Integer FieldDefinition.version;
    
    public Long FieldDefinition.getId() {
        return this.id;
    }
    
    public void FieldDefinition.setId(Long id) {
        this.id = id;
    }
    
    public Integer FieldDefinition.getVersion() {
        return this.version;
    }
    
    public void FieldDefinition.setVersion(Integer version) {
        this.version = version;
    }
    
}
