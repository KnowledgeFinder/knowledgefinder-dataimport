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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.solr.handler.dataimport.Context;
import org.apache.solr.handler.dataimport.VariableResolver;
import org.apache.solr.util.AbstractSolrTestCase;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.mockito.Mockito;

import de.dlr.knowledgefinder.dataimport.utils.transformer.ArrayToStringTransformer;


/**
 * The Class TestIntToDateTransformer.
 *
 * 
 */
public class TestArrayToStringTransformer extends AbstractSolrTestCase  {
	
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
	
	private static Map<String,Object> array;
	private static Map<String, String> jointArray;

	/**
	 * Inits the start date.
	 */
	@BeforeClass
	public static void initDates(){
		array = new TreeMap<String, Object>();
		array.put("test1", new Object[]{"entry1"});
		array.put("test2", new Object[]{"entry3", "entry2"});
		array.put("test3", "entry1");
		
		jointArray = new TreeMap<String,String>();
		jointArray.put("result1", "entry1");
		jointArray.put("result2", "entry3, entry2");
		jointArray.put("result3", "entry1");
	}
	
	 @SuppressWarnings("unchecked")
	    private void test(int testnumber) throws Exception {
	        List<Map<String, String>> fields = new ArrayList<Map<String, String>>();
	        fields.add(createMap("column", "target",
	                ArrayToStringTransformer.ARRAY_SOURCE_DATA_FIELD, "target"
	        ));
	        
	        Context context = getContext(null, new VariableResolver(), null, Context.FULL_DUMP,
	                fields, null);
	        
	        Context fakeContext = Mockito.spy(context);
	        Mockito.doReturn(h.getCore()).when(fakeContext).getSolrCore();
	    
	        String strTestnumber = String.valueOf(testnumber);
	        
	        Map<String, Object> row = createMap("target", array.get("test" + strTestnumber));
	        new ArrayToStringTransformer().transformRow(row, fakeContext);
	        
	        assertEquals(jointArray.get("result" + strTestnumber), row.get("target"));
	    }
	    
	    public void testArrayWithOneEntryTransformation() throws Exception {
	        test(1);
	    }
	    
	    public void testArrayWithMulitEntryTransformation() throws Exception {
	        test(2);
	    }
	    
	    public void testStringAsInput() throws Exception {
            test(3);
        }
	    
	    @SuppressWarnings("unchecked")
        public void testDifferentSourceAndTargetFields() throws Exception {
	        List<Map<String, String>> fields = new ArrayList<Map<String, String>>();
            fields.add(createMap("column", "target",
                    ArrayToStringTransformer.ARRAY_SOURCE_DATA_FIELD, "source"
            ));
            
            Context context = getContext(null, new VariableResolver(), null, Context.FULL_DUMP,
                    fields, null);
            
            Context fakeContext = Mockito.spy(context);
            Mockito.doReturn(h.getCore()).when(fakeContext).getSolrCore();
            
            Map<String, Object> row = createMap("source", array.get("test1"));
            new ArrayToStringTransformer().transformRow(row, fakeContext);
            
            assertEquals(jointArray.get("result1"), row.get("target"));
	    }
}