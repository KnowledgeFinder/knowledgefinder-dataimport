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

import static org.apache.solr.handler.dataimport.AbstractDataImportHandlerTestCase.createMap;
import static org.apache.solr.handler.dataimport.AbstractDataImportHandlerTestCase.getContext;
import static org.hamcrest.CoreMatchers.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.solr.handler.dataimport.Context;
import org.apache.solr.handler.dataimport.VariableResolver;
import org.apache.solr.util.AbstractSolrTestCase;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestDictToStringTransformer extends AbstractSolrTestCase {

	private static Map<String, String> formatingData;

	@BeforeClass
	public static void initDates() {

		formatingData = new TreeMap<String, String>();
		formatingData.put("name", "help");
		formatingData.put("value", "test");
		formatingData.put("empty", "");
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testSimpleFormating() throws Exception {
		List<Map<String, String>> fields = new ArrayList<Map<String, String>>();
		fields.add(createMap("column", "target", DictToStringTransformer.FORMAT_DATA_FIELD, "{name}"));

		Context context = getContext(null, new VariableResolver(), null, Context.FULL_DUMP, fields, null);

		
		
		Map<String, Object> row = createMap("target", formatingData );
		new DictToStringTransformer().transformRow(row, context);
		assertThat((Object[])row.get("target"),is(new Object[]{"help"}));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testUnknownKey() throws Exception {
		List<Map<String, String>> fields = new ArrayList<Map<String, String>>();
		fields.add(createMap("column", "target", DictToStringTransformer.FORMAT_DATA_FIELD, "{name} {unknown}"));

		Context context = getContext(null, new VariableResolver(), null, Context.FULL_DUMP, fields, null);

		Map<String, Object> row = createMap("target", formatingData);
		new DictToStringTransformer().transformRow(row, context);
		assertThat((Object[])row.get("target"), is(new Object[]{"help unknown"}));
	}
	
	@Test
	@SuppressWarnings("unchecked")
	public void testEmptyKey() throws Exception {
		List<Map<String, String>> fields = new ArrayList<Map<String, String>>();
		fields.add(createMap("column", "target", DictToStringTransformer.FORMAT_DATA_FIELD, "{name} {empty}"));

		Context context = getContext(null, new VariableResolver(), null, Context.FULL_DUMP, fields, null);

		Map<String, Object> row = createMap("target", formatingData);
		new DictToStringTransformer().transformRow(row, context);
		assertThat((Object[])row.get("target"), is(new Object[]{"help "}));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testTwoPartFormat() throws Exception {
		List<Map<String, String>> fields = new ArrayList<Map<String, String>>();
		fields.add(createMap("column", "target", DictToStringTransformer.FORMAT_DATA_FIELD, "{name} {value}"));

		Context context = getContext(null, new VariableResolver(), null, Context.FULL_DUMP, fields, null);

		Map<String, Object> row = createMap("target", formatingData);
		new DictToStringTransformer().transformRow(row, context);
		assertThat((Object[])row.get("target"), is(new Object[]{"help test"}));
	}
}