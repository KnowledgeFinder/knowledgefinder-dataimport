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

import java.util.Date;
import java.util.Map;

import org.apache.solr.handler.dataimport.Context;
import org.apache.solr.handler.dataimport.DataImporter;
import org.apache.solr.handler.dataimport.Transformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class IntToDateTransformer. A {@link Transformer} implementation which
 * finds the latest date in a list of dates.
 * 
 * 
 */
public class IntToDateTransformer extends Transformer {

	private static final Logger LOG = LoggerFactory.getLogger(IntToDateTransformer.class);
	private static final long secondToMilisecondMultiplier = 1000;
	public static final String BOOL_CONVERT_DATA_FIELD = "convertIntToDate";

	/*
	 * @see
	 * org.apache.solr.handler.dataimport.Transformer#transformRow(java.util.
	 * Map, org.apache.solr.handler.dataimport.Context)
	 */
	@Override
	public Object transformRow(Map<String, Object> row, Context context) {

		for (Map<String, String> map : context.getAllEntityFields()) {

			String column = map.get(DataImporter.COLUMN);
			String boolStr = map.get(BOOL_CONVERT_DATA_FIELD);

			if (boolStr == null || !boolStr.equalsIgnoreCase("true") || row.get(column) == null)
				continue;

			try{
				int dateInSeconds = Integer.parseInt(row.get(column).toString());
				Date transformedDate = new Date(dateInSeconds * secondToMilisecondMultiplier);
				row.put(column, transformedDate);
			} catch (Exception e) {
				LOG.warn(e.getMessage());
			}
		}
		return row;
	}
}