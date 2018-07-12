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

import org.apache.solr.handler.dataimport.Context;
import org.apache.solr.handler.dataimport.DataImporter;
import org.apache.solr.handler.dataimport.Transformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class URLDecodeTransformer extends Transformer {

	private static final Logger LOG = LoggerFactory.getLogger(URLDecodeTransformer.class);
	public static final String URL_DATA_FIELD = "urlDecode";

	/*
	 * @see
	 * org.apache.solr.handler.dataimport.Transformer#transformRow(java.util.
	 * Map, org.apache.solr.handler.dataimport.Context)
	 */
	@Override
	public Object transformRow(Map<String, Object> row, Context context) {
		for (Map<String, String> map : context.getAllEntityFields()) {

			String column = map.get(DataImporter.COLUMN);
			String doDecode = map.get(URL_DATA_FIELD);

			if (doDecode != null && Boolean.parseBoolean(doDecode) == true) {
				try {
					if (row.get(column) instanceof Object[]) {
						Object[] array = (Object[]) row.get(column);
						String[] result = new String[array.length];
						for (int i = 0; i < array.length; i++) {
							result[i] = urlDecode(array[i].toString());
						}
						row.put(column, result);
					} else
						row.put(column, urlDecode(row.get(column).toString()));
				} catch (Exception e) {
					LOG.warn(e.toString());
				}
			}
		}
		return row;
	}
	
	private String urlDecode(String input) {
		String urlDecoded = "";
		try {
			urlDecoded = java.net.URLDecoder.decode(input, "UTF-8");
		} catch (Exception e) {
			LOG.error("Failed to parse url: " + input);
			e.printStackTrace();
		}
		return urlDecoded;
	}

}
