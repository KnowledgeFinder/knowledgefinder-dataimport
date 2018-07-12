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
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.apache.solr.handler.dataimport.Context;
import org.apache.solr.handler.dataimport.VariableResolver;
import org.apache.solr.util.AbstractSolrTestCase;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.mockito.Mockito;

import de.dlr.knowledgefinder.dataimport.utils.transformer.CategoriesSeparatedTransformer;


/**
 * The Class TestCategoriesSeparatedTransformer.
 *
 * 
 */
public class TestCategoriesSeparatedTransformer extends AbstractSolrTestCase  {
	
    private static HashSet<String> cat2;
    private static HashSet<String> cat1;
    private static HashSet<String> cat22;
    
	@BeforeClass
	public static void beforeClass() throws Exception {
		initCore("solrconfig-minimal.xml", "schema-minimal.xml", getFile("solr").getAbsolutePath());
	}
	
	@AfterClass
	public static void afterClass() throws Exception {
		deleteCore();
	}
	
	@BeforeClass
	public static void initResult() {
	    cat1 = new HashSet<String>();
        cat2 = new HashSet<String>();
        cat22 = new HashSet<String>();
        
        cat1.add("a1");
        cat1.add("B1");
        cat1.add("c1");
        
        cat2.add("a2");
        cat2.add("b2");
        cat2.add("c2");
        
        cat22.add("c22");
        cat22.add("c23");
        cat22.add("c24");

	}
	
	@SuppressWarnings("unchecked")
	public void testGetNameSimple() throws Exception {
        List<Map<String, String>> fields = new ArrayList<Map<String, String>>();
        fields.add(createMap("column", "origin",
                CategoriesSeparatedTransformer.CATEGORIES_FILE_FIELD, "categories.json",
                CategoriesSeparatedTransformer.CATEGORIES_SPLIT_PREFIX_FIELD, "prefix_"
        ));
		
		Context context = getContext(null, new VariableResolver(), null, Context.FULL_DUMP,
				fields, null);
		
		Context fakeContext = Mockito.spy(context);
		Mockito.doReturn(h.getCore()).when(fakeContext).getSolrCore();
		
		Map<String, Object> row = createMap("origin", "a1");
		new CategoriesSeparatedTransformer().transformRow(row, fakeContext);
        
        Object[] rest1 = (Object[]) row.get("prefix_" + "category1");
        Object[] expected1 = {"a1"};
        Arrays.sort(rest1);
        
        assertEquals(rest1.length, expected1.length);
        assertTrue(Arrays.equals(rest1, expected1));
	}
	
	@SuppressWarnings("unchecked")
    public void testGetNameList() throws Exception {
        List<Map<String, String>> fields = new ArrayList<Map<String, String>>();
        fields.add(createMap("column", "origin",
                CategoriesSeparatedTransformer.CATEGORIES_FILE_FIELD, "categories.json",
                CategoriesSeparatedTransformer.CATEGORIES_SPLIT_PREFIX_FIELD, "prefix_"
        ));
        
        Context context = getContext(null, new VariableResolver(), null, Context.FULL_DUMP,
                fields, null);
        
        Context fakeContext = Mockito.spy(context);
        Mockito.doReturn(h.getCore()).when(fakeContext).getSolrCore();
        
        String[] categories = new String[]{"a1", "B1", "c1", "a2", "b2", "c2", "c22", "c23", "c24"};
        Map<String, Object> row = createMap("origin", categories);
        new CategoriesSeparatedTransformer().transformRow(row, fakeContext);
        
        Object[] rest1 = (Object[]) row.get("prefix_" + "category1");
        Object[] expected1 = cat1.toArray();
        Arrays.sort(rest1);
        Arrays.sort(expected1);
        assertEquals(rest1.length, expected1.length);
        assertTrue(Arrays.equals(rest1, expected1));
        
        Object[] rest2 = (Object[]) row.get("prefix_" + "category2");
        Object[] expected2 = cat2.toArray();
        Arrays.sort(rest2);
        Arrays.sort(expected2);
        assertEquals(rest2.length, expected2.length);
        assertTrue(Arrays.equals(rest2, expected2));
        
        Object[] rest22 = (Object[]) row.get("prefix_" + "c2");
        Object[] expected22 = cat22.toArray();
        Arrays.sort(rest22);
        Arrays.sort(expected22);
        assertEquals(rest22.length, expected22.length);
        assertTrue(Arrays.equals(rest22, expected22));
    }
	
	@SuppressWarnings("unchecked")
    public void testGetNameNotFound() throws Exception {
        List<Map<String, String>> fields = new ArrayList<Map<String, String>>();
        fields.add(createMap("column", "origin",
                CategoriesSeparatedTransformer.CATEGORIES_FILE_FIELD, "categories.json",
                CategoriesSeparatedTransformer.CATEGORIES_SPLIT_PREFIX_FIELD, "prefix_"
        ));
        
        Context context = getContext(null, new VariableResolver(), null, Context.FULL_DUMP,
                fields, null);
        
        Context fakeContext = Mockito.spy(context);
        Mockito.doReturn(h.getCore()).when(fakeContext).getSolrCore();
        
        Map<String, Object> row = createMap("origin", "categoryXXX");
        String rowBefore = row.toString();
        new CategoriesSeparatedTransformer().transformRow(row, fakeContext);
        
        assertEquals(rowBefore, row.toString());
    }
	
	@SuppressWarnings("unchecked")
    public void testCaseInsensitve() throws Exception {
        List<Map<String, String>> fields = new ArrayList<Map<String, String>>();
        fields.add(createMap("column", "origin",
                CategoriesSeparatedTransformer.CATEGORIES_FILE_FIELD, "categories.json",
                CategoriesSeparatedTransformer.CATEGORIES_SPLIT_PREFIX_FIELD, "prefix_"
        ));
        
        Context context = getContext(null, new VariableResolver(), null, Context.FULL_DUMP,
                fields, null);
        
        Context fakeContext = Mockito.spy(context);
        Mockito.doReturn(h.getCore()).when(fakeContext).getSolrCore();
        
        String[] categories = new String[]{"A1", "b1", "c1"};
        Map<String, Object> row = createMap("origin", categories);
        new CategoriesSeparatedTransformer().transformRow(row, fakeContext);
        
        Object[] rest1 = (Object[]) row.get("prefix_" + "category1");
        Object[] expected1 = cat1.toArray();
        Arrays.sort(rest1);
        Arrays.sort(expected1);
        assertEquals(rest1.length, expected1.length);
        assertTrue(Arrays.equals(rest1, expected1));
    }
}
