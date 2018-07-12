/*******************************************************************************
 * Copyright 2016 DLR - German Aerospace Center
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package de.dlr.knowledgefinder.dataimport.utils.transformer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.solr.handler.dataimport.Context;
import org.apache.solr.handler.dataimport.DataImporter;
import org.apache.solr.handler.dataimport.Transformer;

/**
 * The Class DictToStringTransformer extends {@link Transformer} to parsing a
 * dictionary and generates a formated string entry
 *
 * 
 */
public class DictToStringTransformer extends Transformer {

	public static final String FORMAT_DATA_FIELD = "formatDictPattern";
	public static final String SOURCE = "srcColName";

	/*
	 * @see
	 * org.apache.solr.handler.dataimport.Transformer#transformRow(java.util.
	 * Map, org.apache.solr.handler.dataimport.Context)
	 */
	@Override
	public Object transformRow(Map<String, Object> row, Context context) {
		for (Map<String, String> map : context.getAllEntityFields()) {

			String formatPatternString = map.get(FORMAT_DATA_FIELD);
			String targetField = map.get(DataImporter.COLUMN);
			String sourceField = map.get(SOURCE);

			if (sourceField == null)
				sourceField = targetField;

			Object origData = row.get(sourceField);

			if (formatPatternString == null || origData == null)
				continue;

			List<Object> values = new ArrayList<Object>();
			if (origData instanceof Object[])
				values.addAll(Arrays.asList((Object[]) origData));
			else
				values.add(origData);

			String[] formatPattern = formatPatternString.split("[\\{\\}]");
			List<String> formatedData = new ArrayList<String>();
			for (Object value : values) {

				if (value instanceof Map<?, ?>) {
					addStringToFormatedList(formatedData, formatPattern, value);
				}
			}
			Object[] arrayResult = formatedData.toArray(new Object[formatedData.size()]);
			row.put(targetField, arrayResult);
		}
		return row;
	}

	@SuppressWarnings("unchecked")
	private void addStringToFormatedList(List<String> formatedData, String[] formatPattern, Object value) {
		
		Map<String, Object> valueMap = new TreeMap<String, Object>(String.CASE_INSENSITIVE_ORDER);
		valueMap.putAll((Map<? extends String, ? extends Object>) value);

		String formatedEntry = "";
		for (String formatPart : formatPattern)
			if (valueMap.containsKey(formatPart)) {
				formatedEntry += valueMap.get(formatPart).toString();
			} else {
				formatedEntry += formatPart;
			}
		if (formatedEntry.length() > 0)
			formatedData.add(formatedEntry);
	}

}