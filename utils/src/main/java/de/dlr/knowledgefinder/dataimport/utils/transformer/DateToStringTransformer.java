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
import java.util.Date;
import java.util.Locale;
import java.util.Map;

import org.apache.solr.handler.dataimport.Context;
import org.apache.solr.handler.dataimport.DataImporter;
import org.apache.solr.handler.dataimport.Transformer;

public class DateToStringTransformer extends Transformer {
	
	public static final String SRC_COL = "srcColName";
	public static final String FORMATING_PATTERN = "datePattern";
	public static final String LOCALE_FIELD = "locale";

	/*
	 * @see
	 * org.apache.solr.handler.dataimport.Transformer#transformRow(java.util.
	 * Map, org.apache.solr.handler.dataimport.Context)
	 */
	@Override
	public Object transformRow(Map<String, Object> row, Context context) {

		for (Map<String, String> map : context.getAllEntityFields()) {

			String column = map.get(DataImporter.COLUMN);
			String pattern = map.get(FORMATING_PATTERN);
			String srcField = map.get(SRC_COL);
			String localeString = map.get(LOCALE_FIELD);
			Locale convertingLocale;
			
			if(pattern == null){
				continue;
			}
			if(srcField == null){
				srcField = column;
			}
			
			if(localeString == null){
				convertingLocale = Locale.getDefault();
			}else{
				convertingLocale = new Locale(localeString);
			}
			
			Object columnContent = row.get(srcField);
			if(columnContent instanceof Date){
				row.put(column, new SimpleDateFormat(pattern, convertingLocale).format((Date)columnContent));
			}
		}
		return row;
	}

}
