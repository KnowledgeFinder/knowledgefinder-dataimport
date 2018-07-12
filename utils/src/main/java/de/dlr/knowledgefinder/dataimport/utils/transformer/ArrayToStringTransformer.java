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

import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.solr.handler.dataimport.Context;
import org.apache.solr.handler.dataimport.DataImporter;
import org.apache.solr.handler.dataimport.Transformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class ArrayToStringTransformer. A {@link Transformer} implementation which
 * builds a single string from an string array. Can also work if a single string is given
 * 
 * 
 */
public class ArrayToStringTransformer extends Transformer {

	private static final Logger LOG = LoggerFactory.getLogger(ArrayToStringTransformer.class);

	public static final String ARRAY_SOURCE_DATA_FIELD = "concatArrayFromSource";

	/*
	 * @see
	 * org.apache.solr.handler.dataimport.Transformer#transformRow(java.util.
	 * Map, org.apache.solr.handler.dataimport.Context)
	 */
	@Override
	public Object transformRow(Map<String, Object> row, Context context) {

		for (Map<String, String> map : context.getAllEntityFields()) {

			String column = map.get(DataImporter.COLUMN);
			String source = map.get(ARRAY_SOURCE_DATA_FIELD);

			if (source != null && row.containsKey(source) && row.get(source) != null) {
				try {
					String result = "";
					if (row.get(source) instanceof Object[]) {
						Object[] array = (Object[]) row.get(source);
						result = StringUtils.join(array, ", ");
					} else
						result = row.get(source).toString();
					row.put(column, result);
				} catch (Exception e) {
					LOG.warn(e.toString());
				}
			}
		}
		return row;
	}

}