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
package de.dlr.knowledgefinderII.dataimport.utils.transformer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.solr.handler.dataimport.Context;
import org.apache.solr.handler.dataimport.DataImporter;
import org.apache.solr.handler.dataimport.Transformer;
/**
 * The Class FormatingStringTransformer extends {@link Transformer} to format a
 * string
 * 
 * {*} = matches whole string
 *
 * 
 */
public class FormatingStringTransformer extends Transformer {

	public static final String FORMAT_DATA_FIELD = "formatStringPattern";
	public static final String SOURCE = "srcColName";

	/*
	 * @see
	 * org.apache.solr.handler.dataimport.Transformer#transformRow(java.util
	 * .Map, org.apache.solr.handler.dataimport.Context)
	 */
	@Override
	public Object transformRow(Map<String, Object> row, Context context) {
		for (Map<String, String> map : context.getAllEntityFields()) {

			String formatPatternString = map.get(FORMAT_DATA_FIELD);
			String sourceField = map.get(SOURCE);
			String targetField = map.get(DataImporter.COLUMN);

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

			List<String> formatedData = new ArrayList<String>();
			for (Object value : values) {
				String formatedEntry = formatPatternString.replaceAll("\\{\\*}", value.toString());
				if (formatedEntry.length() > 0)
					formatedData.add(formatedEntry);
			}
			Object[] arrayResult = formatedData.toArray(new Object[formatedData.size()]);
			row.put(targetField, arrayResult);
		}
		return row;
	}

}