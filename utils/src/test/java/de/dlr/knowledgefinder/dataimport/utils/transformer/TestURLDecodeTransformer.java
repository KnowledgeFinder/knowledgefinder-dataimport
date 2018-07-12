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
import static org.hamcrest.CoreMatchers.is;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.solr.handler.dataimport.Context;
import org.apache.solr.handler.dataimport.VariableResolver;
import org.apache.solr.util.AbstractSolrTestCase;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestURLDecodeTransformer extends AbstractSolrTestCase {

	private static Map<String, String> strings;
	private static Map<String, String> formatedStrings;

	@BeforeClass
	public static void initDates() {
		strings = new HashMap<String, String>();
		strings.put("string1", "thisIsAStringWithoutSpaces");
		strings.put("string2", "this is a string with spaces");
		strings.put("string3", "this%20is%20a%20string");
		strings.put("string4", "containing#Special$Char?");
		strings.put("string5", "containing%23Special%24Char%2F");

		formatedStrings = new HashMap<String, String>();
		formatedStrings.put("string1", "thisIsAStringWithoutSpaces");
		formatedStrings.put("string2", "this is a string with spaces");
		formatedStrings.put("string3", "this is a string");
		formatedStrings.put("string4", "containing#Special$Char?");
		formatedStrings.put("string5", "containing#Special$Char/");
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testSimpleString() throws Exception {
		List<Map<String, String>> fields = new ArrayList<Map<String, String>>();
		fields.add(createMap("column", "target", URLDecodeTransformer.URL_DATA_FIELD, "true"));

		Context context = getContext(null, new VariableResolver(), null, Context.FULL_DUMP, fields, null);

		Map<String, Object> row = createMap("target", strings.get("string1"));
		new URLDecodeTransformer().transformRow(row, context);

		assertEquals(formatedStrings.get("string1"), row.get("target"));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testSpacesInString() throws Exception {
		List<Map<String, String>> fields = new ArrayList<Map<String, String>>();
		fields.add(createMap("column", "target", URLDecodeTransformer.URL_DATA_FIELD, "true"));

		Context context = getContext(null, new VariableResolver(), null, Context.FULL_DUMP, fields, null);

		Map<String, Object> row = createMap("target", strings.get("string2"));
		new URLDecodeTransformer().transformRow(row, context);

		assertEquals(formatedStrings.get("string2"), row.get("target"));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testEncodedSpacesInString() throws Exception {
		List<Map<String, String>> fields = new ArrayList<Map<String, String>>();
		fields.add(createMap("column", "target", URLDecodeTransformer.URL_DATA_FIELD, "true"));

		Context context = getContext(null, new VariableResolver(), null, Context.FULL_DUMP, fields, null);

		Map<String, Object> row = createMap("target", strings.get("string3"));
		new URLDecodeTransformer().transformRow(row, context);

		assertEquals(formatedStrings.get("string3"), row.get("target"));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testSpecialCharacters() throws Exception {
		List<Map<String, String>> fields = new ArrayList<Map<String, String>>();
		fields.add(createMap("column", "target", URLDecodeTransformer.URL_DATA_FIELD, "true"));

		Context context = getContext(null, new VariableResolver(), null, Context.FULL_DUMP, fields, null);

		Map<String, Object> row = createMap("target", strings.get("string4"));
		new URLDecodeTransformer().transformRow(row, context);
		
		assertEquals(formatedStrings.get("string4"), row.get("target"));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testEncodedSpecialCharacters() throws Exception {
		List<Map<String, String>> fields = new ArrayList<Map<String, String>>();
		fields.add(createMap("column", "target", URLDecodeTransformer.URL_DATA_FIELD, "true"));

		Context context = getContext(null, new VariableResolver(), null, Context.FULL_DUMP, fields, null);

		Map<String, Object> row = createMap("target", strings.get("string5"));
		new URLDecodeTransformer().transformRow(row, context);
		
		assertEquals(formatedStrings.get("string5"), row.get("target"));
	}
	@Test
	@SuppressWarnings("unchecked")
	public void testArrayInput() throws Exception {
		List<Map<String, String>> fields = new ArrayList<Map<String, String>>();
		fields.add(createMap("column", "target", URLDecodeTransformer.URL_DATA_FIELD, "true"));

		Context context = getContext(null, new VariableResolver(), null, Context.FULL_DUMP, fields, null);

		Map<String, Object> row = createMap("target", new Object[] 
				{ strings.get("string5"), strings.get("string3") });
		new URLDecodeTransformer().transformRow(row, context);
		
		assertThat(new Object[]{formatedStrings.get("string5"),formatedStrings.get("string3")}, is(row.get("target")));
	}
}