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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.solr.handler.dataimport.Context;
import org.apache.solr.handler.dataimport.DataImportHandlerException;
import org.apache.solr.handler.dataimport.VariableResolver;
import org.apache.solr.util.AbstractSolrTestCase;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;

import de.dlr.knowledgefinder.dataimport.utils.transformer.ExcludeValuesTransformer;


/**
 * The Class TestExcludeValuesTransformer.
 *
 * 
 */
public class TestExcludeValuesTransformer extends AbstractSolrTestCase  {
	
	/**
	 * Before class.
	 *
	 * @throws Exception the exception
	 */
	@BeforeClass
	public static void beforeClass() throws Exception {
		initCore("solrconfig-minimal.xml", "schema-minimal.xml", getFile("solr").getAbsolutePath());
	}
	
	/**
	 * After class.
	 *
	 * @throws Exception the exception
	 */
	@AfterClass
	public static void afterClass() throws Exception {
		deleteCore();
	}
	
	/**
	 * Test transform row same column.
	 *
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	@SuppressWarnings("unchecked")
	public void testTransformRowSameColumn() throws IOException {
		List<Map<String, String>> fields = new ArrayList<Map<String, String>>();
		fields.add(createMap("column", "origin",
				ExcludeValuesTransformer.EXCLUDE_FIELD, "resource.txt"
		));
		
		Context context = getContext(null, new VariableResolver(), null, Context.FULL_DUMP,
				fields, null);
		
		Context fakeContext = Mockito.spy(context);
		Mockito.doReturn(h.getCore()).when(fakeContext).getSolrCore();
		Map<String, Object> row = createMap("origin", "value1");
		new ExcludeValuesTransformer().transformRow(row, fakeContext);
		assertEquals(row.get("origin"), null);
	}
	
	
	/**
	 * Test transform row simple exclude.
	 *
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	@SuppressWarnings("unchecked")
	public void testTransformRowSimpleExclude() throws IOException {
		List<Map<String, String>> fields = new ArrayList<Map<String, String>>();
		fields.add(createMap("column", "target",
				ExcludeValuesTransformer.EXCLUDE_FIELD, "resource.txt",
				ExcludeValuesTransformer.SRC_COL_NAME, "origin"
		));
		
		Context context = getContext(null, new VariableResolver(), null, Context.FULL_DUMP,
				fields, null);
		
		Context fakeContext = Mockito.spy(context);
		Mockito.doReturn(h.getCore()).when(fakeContext).getSolrCore();
		Map<String, Object> row = createMap("origin", "value1");
		new ExcludeValuesTransformer().transformRow(row, fakeContext);
		assertEquals(row.get("target"), null);
	}
	
	/**
	 * Test transform row simple not exclude.
	 *
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	@SuppressWarnings("unchecked")
	public void testTransformRowSimpleNotExclude() throws IOException {
		List<Map<String, String>> fields = new ArrayList<Map<String, String>>();
		fields.add(createMap("column", "target",
				ExcludeValuesTransformer.EXCLUDE_FIELD, "resource.txt",
				ExcludeValuesTransformer.SRC_COL_NAME, "origin"
		));
		
		Context context = getContext(null, new VariableResolver(), null, Context.FULL_DUMP,
				fields, null);
		
		Context fakeContext = Mockito.spy(context);
		Mockito.doReturn(h.getCore()).when(fakeContext).getSolrCore();
		Map<String, Object> row = createMap("origin", "value1xxxx");
		new ExcludeValuesTransformer().transformRow(row, fakeContext);
		assertEquals(row.get("target"), "value1xxxx");
	}
	
	/**
	 * Test transform row array all excluded.
	 *
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	@SuppressWarnings("unchecked")
	public void testTransformRowArrayAllExcluded() throws IOException {
		List<Map<String, String>> fields = new ArrayList<Map<String, String>>();
		fields.add(createMap("column", "target",
				ExcludeValuesTransformer.EXCLUDE_FIELD, "resource.txt",
				ExcludeValuesTransformer.SRC_COL_NAME, "origin"
		));
		
		Context context = getContext(null, new VariableResolver(), null, Context.FULL_DUMP,
				fields, null);
		
		Context fakeContext = Mockito.spy(context);
		Mockito.doReturn(h.getCore()).when(fakeContext).getSolrCore();
		Object[] value = new Object[]{"value1", "value2", "value3"}; 
		Map<String, Object> row = createMap("origin", value);
		new ExcludeValuesTransformer().transformRow(row, fakeContext);
		assertEquals(row.get("target"), null);
	}
	
	/**
	 * Test transform row array one excluded.
	 *
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	@SuppressWarnings("unchecked")
	public void testTransformRowArrayOneExcluded() throws IOException {
		List<Map<String, String>> fields = new ArrayList<Map<String, String>>();
		fields.add(createMap("column", "target",
				ExcludeValuesTransformer.EXCLUDE_FIELD, "resource.txt",
				ExcludeValuesTransformer.SRC_COL_NAME, "origin"
		));
		
		Context context = getContext(null, new VariableResolver(), null, Context.FULL_DUMP,
				fields, null);
		
		Context fakeContext = Mockito.spy(context);
		Mockito.doReturn(h.getCore()).when(fakeContext).getSolrCore();
		Object[] value = new Object[]{"value1xxx", "value2", "value3xxx"}; 
		Map<String, Object> row = createMap("origin", value);
		new ExcludeValuesTransformer().transformRow(row, fakeContext);
		String[] target = (String[]) row.get("target");
		String[] valueEx = new String[]{"value1xxx", "value3xxx"};
		Arrays.sort(target);
		Arrays.sort(value);
		assertTrue(Arrays.equals(target, valueEx));
	}
	
	
	/**
	 * Test transform row io error.
	 *
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	@Test(expected=DataImportHandlerException.class)
	@SuppressWarnings("unchecked")
	public void testTransformRowIOError() throws IOException {
		List<Map<String, String>> fields = new ArrayList<Map<String, String>>();
		fields.add(createMap("column", "target",
				ExcludeValuesTransformer.EXCLUDE_FIELD, "resource-not-found.txt",
				ExcludeValuesTransformer.SRC_COL_NAME, "origin"
		));
		
		Context context = getContext(null, new VariableResolver(), null, Context.FULL_DUMP,
				fields, null);
		
		Context fakeContext = Mockito.spy(context);
		Mockito.doReturn(h.getCore()).when(fakeContext).getSolrCore();
		Map<String, Object> row = createMap("origin", "value");
		new ExcludeValuesTransformer().transformRow(row, fakeContext);
		assertEquals(row.containsKey("target"), false);
	}
}
