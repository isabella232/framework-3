// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package com.searchbox.domain.app;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.transaction.annotation.Transactional;

privileged aspect Preset_Roo_Jpa_ActiveRecord {
    
    @PersistenceContext
    transient EntityManager Preset.entityManager;
    
    public static final List<String> Preset.fieldNames4OrderClauseFilter = java.util.Arrays.asList("slug", "label", "description", "global", "visible", "position", "snippetTemplate", "viewTemplate", "metaTemplate", "searchbox", "fields", "searchElements", "collection", "spells");
    
    public static final EntityManager Preset.entityManager() {
        EntityManager em = new Preset().entityManager;
        if (em == null) throw new IllegalStateException("Entity manager has not been injected (is the Spring Aspects JAR configured as an AJC/AJDT aspects library?)");
        return em;
    }
    
    public static long Preset.countPresets() {
        return entityManager().createQuery("SELECT COUNT(o) FROM Preset o", Long.class).getSingleResult();
    }
    
    public static List<Preset> Preset.findAllPresets() {
        return entityManager().createQuery("SELECT o FROM Preset o", Preset.class).getResultList();
    }
    
    public static List<Preset> Preset.findAllPresets(String sortFieldName, String sortOrder) {
        String jpaQuery = "SELECT o FROM Preset o";
        if (fieldNames4OrderClauseFilter.contains(sortFieldName)) {
            jpaQuery = jpaQuery + " ORDER BY " + sortFieldName;
            if ("ASC".equalsIgnoreCase(sortOrder) || "DESC".equalsIgnoreCase(sortOrder)) {
                jpaQuery = jpaQuery + " " + sortOrder;
            }
        }
        return entityManager().createQuery(jpaQuery, Preset.class).getResultList();
    }
    
    public static Preset Preset.findPreset(Long id) {
        if (id == null) return null;
        return entityManager().find(Preset.class, id);
    }
    
    public static List<Preset> Preset.findPresetEntries(int firstResult, int maxResults) {
        return entityManager().createQuery("SELECT o FROM Preset o", Preset.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
    }
    
    public static List<Preset> Preset.findPresetEntries(int firstResult, int maxResults, String sortFieldName, String sortOrder) {
        String jpaQuery = "SELECT o FROM Preset o";
        if (fieldNames4OrderClauseFilter.contains(sortFieldName)) {
            jpaQuery = jpaQuery + " ORDER BY " + sortFieldName;
            if ("ASC".equalsIgnoreCase(sortOrder) || "DESC".equalsIgnoreCase(sortOrder)) {
                jpaQuery = jpaQuery + " " + sortOrder;
            }
        }
        return entityManager().createQuery(jpaQuery, Preset.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
    }
    
    @Transactional
    public void Preset.persist() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.persist(this);
    }
    
    @Transactional
    public void Preset.remove() {
        if (this.entityManager == null) this.entityManager = entityManager();
        if (this.entityManager.contains(this)) {
            this.entityManager.remove(this);
        } else {
            Preset attached = Preset.findPreset(this.id);
            this.entityManager.remove(attached);
        }
    }
    
    @Transactional
    public void Preset.flush() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.flush();
    }
    
    @Transactional
    public void Preset.clear() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.clear();
    }
    
    @Transactional
    public Preset Preset.merge() {
        if (this.entityManager == null) this.entityManager = entityManager();
        Preset merged = this.entityManager.merge(this);
        this.entityManager.flush();
        return merged;
    }
    
}
