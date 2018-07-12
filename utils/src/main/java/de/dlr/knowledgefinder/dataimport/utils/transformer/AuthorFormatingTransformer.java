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
 * The Class AuthorFormatingTransformer extends {@link Transformer} to parsing a
 * list of author dictionaries and generates a formated author entry
 *
 * 
 *         
 */
public class AuthorFormatingTransformer extends Transformer {

	public static final String BOOL_FORMAT_DATA_FIELD = "formatAuthor";
	public static final String SRC_COL = "srcColName";

	/*
	 * @see
	 * org.apache.solr.handler.dataimport.Transformer#transformRow(java.util.
	 * Map, org.apache.solr.handler.dataimport.Context)
	 */
	@Override
	public Object transformRow(Map<String, Object> row, Context context) {
		for (Map<String, String> map : context.getAllEntityFields()) {
			String boolStr = map.get(BOOL_FORMAT_DATA_FIELD);
			String columnField = map.get(DataImporter.COLUMN);
			String srcCol = map.get(SRC_COL);
			
			if(srcCol == null){
				srcCol = columnField;
			}

			if (boolStr == null || !boolStr.equalsIgnoreCase("true") || row.get(srcCol) == null)
				continue;

			Object currentValueOfTheSourceField = row.get(srcCol);
			List<Object> listOfCurrentValues = new ArrayList<Object>();

			if (currentValueOfTheSourceField == null)
				return row;

			if (currentValueOfTheSourceField instanceof Object[])
				listOfCurrentValues.addAll(Arrays.asList((Object[]) currentValueOfTheSourceField));
			else
				listOfCurrentValues.add(currentValueOfTheSourceField);

			List<String> authors = new ArrayList<String>();
			for (Object value : listOfCurrentValues) {
				if (value instanceof Map<?, ?>) {

					String author = createAuthorString(value);

					if (author.length() > 0)
						authors.add(author);
				}
			}
			Object[] arrayResult = authors.toArray(new Object[authors.size()]);
			row.put(columnField, arrayResult);
		}

		return row;
	}


	private String createAuthorString(Object value) {
		Map<String, Object> authorValueMap = new TreeMap<String, Object>(String.CASE_INSENSITIVE_ORDER);
		@SuppressWarnings("unchecked")
		Map<? extends String, ? extends Object> castedValue = (Map<? extends String, ? extends Object>) value;
		authorValueMap.putAll(castedValue);

		String author = "";
		if (authorValueMap.containsKey("firstName"))
			author += authorValueMap.get("firstName").toString();
		if (author.length() == 1)
			author += ".";

		if (authorValueMap.containsKey("lastName")) {
			if (author.length() > 0 && authorValueMap.get("lastName").toString().length() > 0)
				author += " ";
			author += authorValueMap.get("lastName").toString();
		}

		if (author.length() == 0)
			author = authorValueMap.get("organization").toString();
		else if (authorValueMap.containsKey("organization")
				&& authorValueMap.get("organization").toString().length() > 0) {
			author += " (";
			author += authorValueMap.get("organization").toString();
			author += ")";
		}
		return author;
	}

}