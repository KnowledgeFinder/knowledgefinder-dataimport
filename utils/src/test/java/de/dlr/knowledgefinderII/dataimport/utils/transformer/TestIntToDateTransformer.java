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
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.solr.handler.dataimport.Context;
import org.apache.solr.handler.dataimport.VariableResolver;
import org.apache.solr.util.AbstractSolrTestCase;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.mockito.Mockito;

import de.dlr.knowledgefinderII.dataimport.utils.transformer.IntToDateTransformer;


/**
 * The Class TestIntToDateTransformer.
 *
 * 
 */
public class TestIntToDateTransformer extends AbstractSolrTestCase  {
	
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
	
	private static Map<String,Object> dates;
	private static Map<String, Date> formatedDates;

	/**
	 * Inits the start date.
	 */
	@BeforeClass
	public static void initDates(){
		dates = new TreeMap<String, Object>();
		dates.put("date1", "1335425321");
		dates.put("date6", null);
		
		formatedDates = new TreeMap<String,Date>();
		formatedDates.put("1335425321", new Date(1335425321000L));
	}
	
	@SuppressWarnings("unchecked")
	public void testDateTransformation() throws Exception {
		List<Map<String, String>> fields = new ArrayList<Map<String, String>>();
		fields.add(createMap("column", "target",
				IntToDateTransformer.BOOL_CONVERT_DATA_FIELD, "true"
		));
		
		Context context = getContext(null, new VariableResolver(), null, Context.FULL_DUMP,
				fields, null);
		
		Context fakeContext = Mockito.spy(context);
		Mockito.doReturn(h.getCore()).when(fakeContext).getSolrCore();
				
		Map<String, Object> row = createMap("target", dates.get("date1"));
		new IntToDateTransformer().transformRow(row, fakeContext);
		assertEquals(formatedDates.get(dates.get("date1").toString()), row.get("target"));
	}
	
	@SuppressWarnings("unchecked")
	public void testNoDate() throws Exception {
		List<Map<String, String>> fields = new ArrayList<Map<String, String>>();
		fields.add(createMap("column", "target",
				IntToDateTransformer.BOOL_CONVERT_DATA_FIELD, "true"
		));
		
		Context context = getContext(null, new VariableResolver(), null, Context.FULL_DUMP,
				fields, null);
		
		Context fakeContext = Mockito.spy(context);
		Mockito.doReturn(h.getCore()).when(fakeContext).getSolrCore();
				
		Map<String, Object> row = createMap("target", dates.get("date6"));
		new IntToDateTransformer().transformRow(row, fakeContext);
		assertEquals(null, row.get("target"));
	}
}