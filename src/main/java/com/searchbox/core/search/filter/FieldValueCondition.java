package com.searchbox.core.search.filter;

import org.springframework.core.convert.converter.Converter;

import com.searchbox.core.SearchCondition;
import com.searchbox.core.SearchConverter;
import com.searchbox.core.search.AbstractSearchCondition;

@SearchCondition(urlParam="ff")
public class FieldValueCondition extends AbstractSearchCondition {

	String fieldName;
	String value;
	Boolean taged;
	
	public FieldValueCondition(String field, String value) {
		this.fieldName = field;
		this.value = value;
		this.taged = false;
	}
	
	public FieldValueCondition(String field, String value, Boolean taged) {
		this.fieldName = field;
		this.value = value;
		this.taged = taged;
	}

	public String getFieldName() {
		return fieldName;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
	public Boolean getTaged() {
		return taged;
	}

	public void setTaged(Boolean taged) {
		this.taged = taged;
	}

	/** Format of FieldValueFacet is key[value]d * where d is a class shortcut */
	@SearchConverter
	public static class FieldValueConditionConverter
			implements Converter<String, FieldValueCondition> {
		@Override
		public FieldValueCondition convert(String source) {
			String cfield = source.split("\\[")[0];
			String cvalue = source.split("\\[")[1].split("]")[0];
			//TODO Problem here, the facet will not be sticky if not forced... :/
			return new FieldValueCondition(cfield, cvalue, true);
		}
	}
}
