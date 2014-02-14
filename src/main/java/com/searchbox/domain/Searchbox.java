package com.searchbox.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;

import org.hibernate.annotations.Sort;
import org.hibernate.annotations.SortType;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.tostring.RooToString;

import com.searchbox.core.dm.Preset;

@RooJavaBean
@RooToString
@RooJpaActiveRecord(finders = { "findSearchboxesBySlugEquals",
		"findSearchboxesBySlugLike" })
public class Searchbox {

	public Searchbox(String name, String description) {
		this.name = name;
		this.description = description;
	}

	/**
     */
	private String slug;

	/**
     */
	private String name;

	/**
     */
	private String description;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "searchbox", orphanRemoval = true, cascade=CascadeType.ALL)
//	@Sort(type = SortType.NATURAL)
	private List<PresetDefinition> presets = new ArrayList<PresetDefinition>();

	public void addPresetDefinition(PresetDefinition preset) {
		this.presets.add(preset);
	}
}
