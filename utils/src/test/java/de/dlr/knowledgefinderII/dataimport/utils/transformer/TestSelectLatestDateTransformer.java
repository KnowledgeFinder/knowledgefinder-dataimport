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

import static org.apache.solr.handler.dataimport.AbstractDataImportHandlerTestCase.createMap;
import static org.apache.solr.handler.dataimport.AbstractDataImportHandlerTestCase.getContext;
import static org.hamcrest.CoreMatchers.is;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.HashMap;


import org.apache.solr.handler.dataimport.Context;
import org.apache.solr.handler.dataimport.VariableResolver;
import org.apache.solr.util.AbstractSolrTestCase;
import org.junit.BeforeClass;

import de.dlr.knowledgefinderII.dataimport.utils.transformer.SelectLatestDateTransformer;


/**
 * The Class TestSelectLatestDateTransformer.
 *
 * 
 */
public class TestSelectLatestDateTransformer extends AbstractSolrTestCase  {
	
	private static Map<String,String> dates;
	private static Map<String, Date> formatedDates;

	@BeforeClass
	public static void initDates(){
		dates = new HashMap<String, String>();
		dates.put("date1", "2012-08-30T13:03:25Z");
		dates.put("date2", "2012-08-30T13:02:05Z");
		dates.put("date3", "2012-08-30T13:03:25Z");
		dates.put("date4", "30.08.2012");
		dates.put("date5", null);
		dates.put("date6", null);
		
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ENGLISH);
		formatedDates = new HashMap<String,Date>();
		try {
		formatedDates.put("2012-08-30T13:03:25Z", dateFormat.parse("2012-08-30T13:03:25Z"));
		formatedDates.put("2012-08-30T13:02:05Z", dateFormat.parse("2012-08-30T13:02:05Z"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("unchecked")
	public void testTwoDifferentDates() throws Exception {
		List<Map<String, String>> fields = new ArrayList<Map<String, String>>();
		fields.add(createMap("column", "target",
				SelectLatestDateTransformer.DATA_LIST_FIELD, "date2,date1"
		));
		fields.add(createMap("column", "date1"));
		fields.add(createMap("column", "date2"));
		
		Context context = getContext(null, new VariableResolver(), null, Context.FULL_DUMP,
				fields, null);

				
		Map<String, Object> row = new HashMap<String, Object>(dates);
		new SelectLatestDateTransformer().transformRow(row, context);
		System.out.println(row.get("target").getClass().toString());
		assertEquals(formatedDates.get(dates.get("date1").toString()), row.get("target"));
	}
	
	@SuppressWarnings("unchecked")
	public void testTwoEqualDates() throws Exception {
		List<Map<String, String>> fields = new ArrayList<Map<String, String>>();
		fields.add(createMap("column", "target",
				SelectLatestDateTransformer.DATA_LIST_FIELD, "date1,date3"
		));
		fields.add(createMap("column", "date1"));
		fields.add(createMap("column", "date3"));
		
		Context context = getContext(null, new VariableResolver(), null, Context.FULL_DUMP,
				fields, null);
 
		Map<String, Object> row = new HashMap<String, Object>(dates);
		new SelectLatestDateTransformer().transformRow(row, context);
		assertEquals(formatedDates.get(dates.get("date1").toString()), row.get("target"));
	}
	
	@SuppressWarnings("unchecked")
	public void testOnlyOneDateSet() throws Exception {
		List<Map<String, String>> fields = new ArrayList<Map<String, String>>();
		fields.add(createMap("column", "target",
				SelectLatestDateTransformer.DATA_LIST_FIELD, "date1,date5"
		));
		fields.add(createMap("column", "date1"));
		fields.add(createMap("column", "date5"));
		
		Context context = getContext(null, new VariableResolver(), null, Context.FULL_DUMP,
				fields, null);
 	
		Map<String, Object> row = new HashMap<String, Object>(dates);
		new SelectLatestDateTransformer().transformRow(row, context);
		assertThat((Date)row.get("target"), is(formatedDates.get(dates.get("date1").toString())));
	}
	
	@SuppressWarnings("unchecked")
	public void testNoDatesSet() throws Exception {
		List<Map<String, String>> fields = new ArrayList<Map<String, String>>();
		fields.add(createMap("column", "target",
				SelectLatestDateTransformer.DATA_LIST_FIELD, "date6,date5"
		));
		fields.add(createMap("column", "date6"));
		fields.add(createMap("column", "date5"));
		
		Context context = getContext(null, new VariableResolver(), null, Context.FULL_DUMP,
				fields, null);

		Map<String, Object> row = new HashMap<String, Object>(dates);
		new SelectLatestDateTransformer().transformRow(row, context);
		assertEquals(null, row.get("target"));
	}
	
	@SuppressWarnings("unchecked")
	public void testDifferentTimeFormat() throws Exception {
		List<Map<String, String>> fields = new ArrayList<Map<String, String>>();
		fields.add(createMap("column", "target",
				SelectLatestDateTransformer.DATA_LIST_FIELD, "date1,date4"
		));
		fields.add(createMap("column", "date1"));
		fields.add(createMap("column", "date4"));
		
		Context context = getContext(null, new VariableResolver(), null, Context.FULL_DUMP,
				fields, null);
 	
		Map<String, Object> row = new HashMap<String, Object>(dates);
		new SelectLatestDateTransformer().transformRow(row, context);
		assertEquals(formatedDates.get(dates.get("date1").toString()), row.get("target"));
	}
}