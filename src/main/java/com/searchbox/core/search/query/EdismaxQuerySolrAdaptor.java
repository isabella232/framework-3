package com.searchbox.core.search.query;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.util.ClientUtils;
import org.apache.solr.common.params.DisMaxParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.searchbox.core.SearchAdapter;
import com.searchbox.core.SearchAdapter.Time;
import com.searchbox.core.SearchAdapterMethod;
import com.searchbox.core.dm.FieldAttribute;
import com.searchbox.core.dm.FieldAttribute.USE;
import com.searchbox.engine.solr.SolrSearchEngine;

@SearchAdapter
public class EdismaxQuerySolrAdaptor {
	
	private static final Logger LOGGER = LoggerFactory
			.getLogger(EdismaxQuerySolrAdaptor.class);
	
	@SearchAdapterMethod(execute=Time.PRE)
	public void setDefaultQuery(SolrQuery query){
		query.setParam("defType", "edismax");
		query.set(DisMaxParams.ALTQ, "*:*");
	}
	
	
	@SearchAdapterMethod(execute=Time.PRE)
	public void setQueryFields(SolrSearchEngine searchEngine, EdismaxQuery SearchElement,
			SolrQuery query, FieldAttribute fieldAttribute) {
		LOGGER.debug("Checking Field Attr for EdismaxQuery -- Field: {} ", fieldAttribute.getField().getKey());

		if(fieldAttribute.getSearchable()){
			LOGGER.debug("Found searchable field: {}", fieldAttribute.getField().getKey());
			Float boost = (fieldAttribute.getBoost()!=null)?fieldAttribute.getBoost():1.0f;
			String key = searchEngine.getKeyForField(fieldAttribute, USE.SEARCH);
			String currentFields = query.get(DisMaxParams.QF);
			query.set(DisMaxParams.QF, 
					((currentFields!=null && !currentFields.isEmpty())?currentFields+" ":"")+
							((key==null)?fieldAttribute.getField().getKey():key)+"^"+boost);
		}
	}

	@SearchAdapterMethod(execute=Time.POST)
	public void udpateElementQuery(EdismaxQuery searchElement, SolrQuery query) {
		searchElement.setQuery(query.getQuery());			
	}

	@SearchAdapterMethod(execute=Time.PRE)
	public void getQueryCondition(EdismaxQuery.Condition condition, SolrQuery query) {
		query.setQuery(ClientUtils.escapeQueryChars(condition.getQuery()));
	}
}