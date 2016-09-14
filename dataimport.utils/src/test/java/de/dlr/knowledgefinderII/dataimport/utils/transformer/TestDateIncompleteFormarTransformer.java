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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import org.apache.solr.handler.dataimport.Context;
import org.apache.solr.handler.dataimport.VariableResolver;
import org.apache.solr.util.AbstractSolrTestCase;
import org.junit.BeforeClass;

import de.dlr.knowledgefinderII.dataimport.utils.transformer.DateIncompleteFormatTransformer;



public class TestDateIncompleteFormarTransformer extends AbstractSolrTestCase  {

	private static String REGEX = "(?<year>\\d{4})-?(?<month>\\d{2})?-?(?<day>\\d{2})?";
	private static Map<String, Date> valuesTestStart;
	private static Map<String, Date> valuesTestEnd;

	@BeforeClass
	public static void initEndDate() {
		valuesTestEnd = new HashMap<String, Date>();
		
		TimeZone GMT = TimeZone.getTimeZone("GMT");
		Locale locale = Locale.ROOT;
		Calendar cal = Calendar.getInstance(GMT, locale);
		int milisecond = cal.getMaximum(Calendar.MILLISECOND);
		
		cal.set(Calendar.MILLISECOND, milisecond);
		cal.set(2014, Calendar.DECEMBER, 31, 23, 59, 59);
		valuesTestEnd.put("2014", cal.getTime());
		// Leap year
		cal.set(2016, Calendar.FEBRUARY, 29, 23, 59, 59);
		valuesTestEnd.put("2016-02", cal.getTime());
		cal.set(2014, Calendar.DECEMBER, 10, 23, 59, 59);
		valuesTestEnd.put("2014-12-10", cal.getTime());
		
		// valuesTestEnd.put("noMatch", null);

	}
	
	/**
	 * Inits the start date.
	 */
	@BeforeClass
	public static void initStartDate(){
		valuesTestStart = new HashMap<String, Date>();
		
		TimeZone UTC = TimeZone.getTimeZone("GMT");
		Locale locale = Locale.ROOT;
		Calendar cal = Calendar.getInstance(UTC, locale);
		int milisecond = cal.getMinimum(Calendar.MILLISECOND);
		cal.set(Calendar.MILLISECOND, milisecond);
		cal.set(2014, Calendar.JANUARY, 01, 00, 00, 00);
		valuesTestStart.put("2014", cal.getTime());
		cal.set(2014, Calendar.DECEMBER, 01, 00, 00, 00);
		valuesTestStart.put("2014-12", cal.getTime());
		cal.set(2014, Calendar.DECEMBER, 10, 00, 00, 00);
		valuesTestStart.put("2014-12-10", cal.getTime());
		
		// valuesTestStart.put("noMatch", null);
	}
	
	/**
	 * Test end date.
	 */
	@SuppressWarnings("unchecked")
	public void testEndDate()  {
		List<Map<String, String>> fields = new ArrayList<Map<String, String>>();
		fields.add(createMap("column", "target",
				DateIncompleteFormatTransformer.DATE_INCOMPLETE_REGEX, REGEX,
				DateIncompleteFormatTransformer.SRC_COL_NAME, "origin",
				DateIncompleteFormatTransformer.DATE_TIME_PARSE, DateIncompleteFormatTransformer.DATE_END
		));
		
		Context context = getContext(null, new VariableResolver(), null, Context.FULL_DUMP,
				fields, null);
 
		for (String value: valuesTestEnd.keySet()){
			Date date = valuesTestEnd.get(value);
			Map<String, Object> row = createMap("origin", value);
			new DateIncompleteFormatTransformer().transformRow(row, context);
			Date oldDate = (Date) row.get("target");
			assertEquals(date, oldDate);
		}
	}
	
	/**
	 * Test start date.
	 */
	@SuppressWarnings("unchecked")
	public void testStartDate() {
		List<Map<String, String>> fields = new ArrayList<Map<String, String>>();
		fields.add(createMap("column", "target",
				DateIncompleteFormatTransformer.DATE_INCOMPLETE_REGEX, REGEX,
				DateIncompleteFormatTransformer.SRC_COL_NAME, "origin",
				DateIncompleteFormatTransformer.DATE_TIME_PARSE, DateIncompleteFormatTransformer.DATE_START
		));
		
		Context context = getContext(null, new VariableResolver(), null, Context.FULL_DUMP,
				fields, null);
 
		for (String value: valuesTestStart.keySet()){
			Date date = valuesTestStart.get(value);
			Map<String, Object> row = createMap("origin", value);
			new DateIncompleteFormatTransformer().transformRow(row, context);
			Date oldDate = (Date) row.get("target");
			assertEquals(date, oldDate);
		}
	}
	
	/**
	 * Test start date same column.
	 */
	@SuppressWarnings("unchecked")
	public void testStartDateSameColumn() {
		List<Map<String, String>> fields = new ArrayList<Map<String, String>>();
		fields.add(createMap("column", "target",
				DateIncompleteFormatTransformer.DATE_INCOMPLETE_REGEX, REGEX,
				DateIncompleteFormatTransformer.DATE_TIME_PARSE, DateIncompleteFormatTransformer.DATE_START
		));
		
		Context context = getContext(null, new VariableResolver(), null, Context.FULL_DUMP,
				fields, null);
 
		for (String value: valuesTestStart.keySet()){
			Date date = valuesTestStart.get(value);
			Map<String, Object> row = createMap("target", value);
			new DateIncompleteFormatTransformer().transformRow(row, context);
			assertEquals(date, row.get("target"));
		}
	}
	
	/**
	 * Test end date same column.
	 */
	@SuppressWarnings("unchecked")
	public void testEndDateSameColumn() {
		List<Map<String, String>> fields = new ArrayList<Map<String, String>>();
		fields.add(createMap("column", "target",
				DateIncompleteFormatTransformer.DATE_INCOMPLETE_REGEX, REGEX,
				DateIncompleteFormatTransformer.DATE_TIME_PARSE, DateIncompleteFormatTransformer.DATE_END
		));
		
		Context context = getContext(null, new VariableResolver(), null, Context.FULL_DUMP,
				fields, null);
 
		for (String value: valuesTestEnd.keySet()){
			Date date = valuesTestEnd.get(value);
			Map<String, Object> row = createMap("target", value);
			new DateIncompleteFormatTransformer().transformRow(row, context);
			assertEquals(date, row.get("target"));
		}
	}
	
	/**
	 * Test start date no match.
	 */
	@SuppressWarnings("unchecked")
	public void testStartDateNoMatch() {
		List<Map<String, String>> fields = new ArrayList<Map<String, String>>();
		fields.add(createMap("column", "target",
				DateIncompleteFormatTransformer.DATE_INCOMPLETE_REGEX, REGEX,
				DateIncompleteFormatTransformer.DATE_TIME_PARSE, DateIncompleteFormatTransformer.DATE_START
		));
		
		Context context = getContext(null, new VariableResolver(), null, Context.FULL_DUMP,
				fields, null);
		
		String value = "noMatch";
		Map<String, Object> row = createMap("target", value);
		new DateIncompleteFormatTransformer().transformRow(row, context);
		assertEquals("noMatch", row.get("target"));
	}
	
	/**
	 * Test end date no match.
	 */
	@SuppressWarnings("unchecked")
	public void testEndDateNoMatch() {
		List<Map<String, String>> fields = new ArrayList<Map<String, String>>();
		fields.add(createMap("column", "target",
				DateIncompleteFormatTransformer.DATE_INCOMPLETE_REGEX, REGEX,
				DateIncompleteFormatTransformer.DATE_TIME_PARSE, DateIncompleteFormatTransformer.DATE_END
		));
		
		Context context = getContext(null, new VariableResolver(), null, Context.FULL_DUMP,
				fields, null);
 
		String value = "noMatch";
		Map<String, Object> row = createMap("target", value);
		new DateIncompleteFormatTransformer().transformRow(row, context);
		assertEquals("noMatch", row.get("target"));
	}
}
