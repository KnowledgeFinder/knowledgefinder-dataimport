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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

import org.apache.solr.handler.dataimport.Context;
import org.apache.solr.handler.dataimport.DataImporter;
import org.apache.solr.handler.dataimport.Transformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class SelectLatestDateTransformer. A {@link Transformer} implementation
 * which finds the latest date in a list of dates.
 * 
 * 
 */
public class SelectLatestDateTransformer extends Transformer {

	private static final Logger LOG = LoggerFactory.getLogger(SelectLatestDateTransformer.class);
	public static final String DATA_LIST_FIELD = "dataTimeSources";

	/*
	 * @see
	 * org.apache.solr.handler.dataimport.Transformer#transformRow(java.util.
	 * Map, org.apache.solr.handler.dataimport.Context)
	 */
	@Override
	public Object transformRow(Map<String, Object> row, Context context) {

		for (Map<String, String> map : context.getAllEntityFields()) {

			String column = map.get(DataImporter.COLUMN);
			String sourcesStr = map.get(DATA_LIST_FIELD);

			if (sourcesStr == null)
				continue;

			String[] sources = sourcesStr.split(",");
			ArrayList<Date> dates = new ArrayList<Date>();
			for (int is = 0; is < sources.length; is++) {
				try {
					Object tmpVal = row.get(sources[is].trim());
					if (tmpVal != null && tmpVal.toString().length() > 0)
						if (tmpVal instanceof Date)
							dates.add((Date) tmpVal);
						else
							dates.add(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ENGLISH)
									.parse(tmpVal.toString()));
				} catch (Exception e) {
					LOG.warn(e.getMessage());
				}
			}
			if (dates.size() > 0) {
				Collections.sort(dates);
				row.put(column, dates.get(dates.size() - 1));
			} else {
				LOG.warn("There are no valid dates set");
			}

		}
		return row;
	}
}