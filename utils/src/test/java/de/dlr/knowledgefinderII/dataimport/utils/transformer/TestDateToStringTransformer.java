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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import org.apache.solr.handler.dataimport.Context;
import org.apache.solr.handler.dataimport.VariableResolver;
import org.apache.solr.util.AbstractSolrTestCase;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestDateToStringTransformer extends AbstractSolrTestCase {

	private static Map<String, Date> inputData;
	private static Map<String, String> outputData;

	@BeforeClass
	public static void initDates() {

		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss:S");
		inputData = new HashMap<String, Date>();
		try {
			inputData.put("date1", format.parse("2015-10-25 22:00:10:465"));
		} catch (ParseException e) {
			e.printStackTrace();
		}

		outputData = new HashMap<String, String>();
		outputData.put("date1", "2015-10-25");
		outputData.put("date11", "25-10-2015 22:00");
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testSimpleFormating() throws Exception {
		List<Map<String, String>> fields = new ArrayList<Map<String, String>>();
		fields.add(createMap("column", "target", DateToStringTransformer.FORMATING_PATTERN, "yyyy-MM-dd",
				DateToStringTransformer.LOCALE_FIELD, "en"));

		Context context = getContext(null, new VariableResolver(), null, Context.FULL_DUMP, fields, null);

		Map<String, Object> row = createMap("target", inputData.get("date1"));
		new DateToStringTransformer().transformRow(row, context);
		assertThat((String) row.get("target"), is(outputData.get("date1")));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testOtherFormatingOption() throws Exception {
		List<Map<String, String>> fields = new ArrayList<Map<String, String>>();
		fields.add(createMap("column", "target", DateToStringTransformer.FORMATING_PATTERN, "dd-MM-yyyy HH:mm",
				DateToStringTransformer.LOCALE_FIELD, "en"));

		Context context = getContext(null, new VariableResolver(), null, Context.FULL_DUMP, fields, null);

		Map<String, Object> row = createMap("target", inputData.get("date1"));
		new DateToStringTransformer().transformRow(row, context);
		assertThat((String) row.get("target"), is(outputData.get("date11")));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testInputIsNotADate() throws Exception {
		List<Map<String, String>> fields = new ArrayList<Map<String, String>>();
		fields.add(createMap("column", "target", DateToStringTransformer.FORMATING_PATTERN, "dd-MM-yyyy HH:mm",
				DateToStringTransformer.LOCALE_FIELD, "en"));

		Context context = getContext(null, new VariableResolver(), null, Context.FULL_DUMP, fields, null);

		Map<String, Object> row = createMap("target", "not a date");
		new DateToStringTransformer().transformRow(row, context);
		assertThat((String) row.get("target"), is("not a date"));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testOtherSrcColumn() throws Exception {
		List<Map<String, String>> fields = new ArrayList<Map<String, String>>();
		fields.add(createMap("column", "target", DateToStringTransformer.FORMATING_PATTERN, "dd-MM-yyyy HH:mm",
				DateToStringTransformer.LOCALE_FIELD, "en",DateToStringTransformer.SRC_COL, "src"));

		Context context = getContext(null, new VariableResolver(), null, Context.FULL_DUMP, fields, null);

		Map<String, Object> row = createMap("target", null, "src", inputData.get("date1"));
		new DateToStringTransformer().transformRow(row, context);
		assertThat((String) row.get("target"), is(outputData.get("date11")));
	}
}
