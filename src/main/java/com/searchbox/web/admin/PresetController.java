package com.searchbox.web.admin;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.searchbox.core.dm.Preset;
import com.searchbox.core.search.SearchCondition;
import com.searchbox.core.search.SearchElement;
import com.searchbox.core.search.SearchResult;
import com.searchbox.domain.PresetDefinition;
import com.searchbox.domain.Searchbox;
import com.searchbox.service.ApplicationConversionService;
import com.searchbox.service.SearchService;
import com.searchbox.web.Layout;

@Controller
@RequestMapping("/admin")
@Layout("admin")
public class PresetController {
	
	private static Logger logger = LoggerFactory.getLogger(PresetController.class);

	@Autowired
	ConversionService conversionService;
	
	@Autowired
	ApplicationConversionService searchComponentService;
	
	@Autowired
	SearchService searchService;
	
	@RequestMapping("/")
	public ModelAndView search(@RequestParam(value="searchbox", required=false) String searchboxSlug, 
			HttpServletRequest request) {
		logger.info("No Slug given, select default Preset");
		return search(searchboxSlug, "", request);
	}
	
	@RequestMapping("/{presetSlug}")
	public ModelAndView search(
			@RequestParam(value = "searchbox", required = false) String searchboxSlug,
			@PathVariable String presetSlug, HttpServletRequest request) {

		// That should come from the searchbox param/filter
		List<Searchbox> searchboxes = Searchbox.findAllSearchboxes();
		Searchbox searchbox = searchboxes.get(0);
		for (Searchbox sb : searchboxes) {
			if (sb.getSlug().equals(searchboxSlug)) {
				searchbox = sb;
			}
		}
//
//		if (searchbox == null) {
//			ModelAndView model = new ModelAndView("search/searchbox");
//			model.addObject("searchboxes", searchboxes);
//			return model;
//		}

		List<Preset> presets = new ArrayList<Preset>();
		Preset currentPreset = null;
		PresetDefinition currentPresetDefinition = null;
		for (PresetDefinition pdef : searchbox.getPresets()) {
			Preset pset = pdef.getElement();

			if (pset.getSlug().equals(presetSlug)) {
				currentPreset = pset;
				currentPresetDefinition = pdef;
			}
			presets.add(pset);
		}

		if (currentPreset == null && presets.size() > 0) {
			currentPreset = presets.get(0);
			currentPresetDefinition = searchbox.getPresets().get(0);
		}

		List<SearchCondition> conditions = new ArrayList<SearchCondition>();

		for (String param : searchComponentService.getSearchConditionParams()) {
			if (request.getParameterValues(param) != null) {
				for (String value : request.getParameterValues(param)) {
					if (value != null && !value.isEmpty()) {
						try {
							SearchCondition cond = (SearchCondition) conversionService
									.convert(value, searchComponentService
											.getSearchConditionClass(param));
							conditions.add(cond);
						} catch (Exception e) {
							logger.error("Could not convert " + value, e);
						}
					}
				}
			}
		}

		SearchResult result = new SearchResult();

		if (currentPreset != null) {
			for (SearchElement element : searchService.execute(currentPreset,
					conditions)) {
				logger.debug("Adding to result view element["
						+ element.getPosition() + "] = " + element.getLabel());
				result.addElement(element);
			}
		}

		ModelAndView model = new ModelAndView("admin/preset");
		model.addObject("result", result);
		model.addObject("presets", presets);
		model.addObject("searchboxes", searchboxes);
		model.addObject("currentSearchbox", searchbox);
		model.addObject("currentPreset", currentPreset);
		model.addObject("currentPresetDefinition", currentPresetDefinition);
		

		return model;
	}
}
