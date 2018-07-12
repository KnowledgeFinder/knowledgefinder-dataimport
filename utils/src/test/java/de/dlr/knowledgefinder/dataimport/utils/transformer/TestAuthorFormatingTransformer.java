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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.solr.handler.dataimport.Context;
import org.apache.solr.handler.dataimport.VariableResolver;
import org.apache.solr.util.AbstractSolrTestCase;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.mockito.Mockito;

import de.dlr.knowledgefinder.dataimport.utils.transformer.AuthorFormatingTransformer;



/**
 * The Class TestGenerateIdTransformer.
 *
 * 
 */

public class TestAuthorFormatingTransformer extends AbstractSolrTestCase  {
    
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
    
    private static Map<String,Object> authorDicts;
    private static Map<String, Object[]> formatedAuthors;

    /**
     * Inits the start date.
     */
    @BeforeClass
    public static void initDates(){
        authorDicts = new TreeMap<String, Object>();

        Map<String, String> author1 = new HashMap<String, String>();
        author1.put("firstName", "firstName1");
        author1.put("lastName", "lastName1");
        author1.put("organization", "dlr");
        
        Map<String, String> author2 = new HashMap<String, String>();
        author2.put("firstname", "firstName2");
        author2.put("lastname", "lastName2");
        author2.put("organization", "mpi");
        
        Map<String, String> author3 = new HashMap<String, String>();
        author3.put("firstname", "firstName3");
        
        Map<String, String> author4 = new HashMap<String, String>();
        author4.put("lastname", "lastName4");
        
        Map<String, String> author5 = new HashMap<String, String>();
        author5.put("organization", "dlr");
        
        Map<String, String> author6 = new HashMap<String, String>();
        author6.put("firstname", "firstName6");
        author6.put("lastname", "lastName6");
        
        Map<String, String> author7 = new HashMap<String, String>();
        author7.put("firstname", "firstName7");
        author7.put("organization", "dlr");
        
        Map<String, String> author8 = new HashMap<String, String>();
        author8.put("lastname", "lastName8");
        author8.put("organization", "dlr");       
        
        authorDicts.put("test1", new Object[]{author1});
        authorDicts.put("test2", new Object[]{author2});
        authorDicts.put("test3", new Object[]{author3});
        authorDicts.put("test4", new Object[]{author4});
        authorDicts.put("test5", new Object[]{author5});
        authorDicts.put("test6", new Object[]{author6});
        authorDicts.put("test7", new Object[]{author7});
        authorDicts.put("test8", new Object[]{author8});
        authorDicts.put("test9", new Object[]{author1, author2});
        
        formatedAuthors = new TreeMap<String,Object[]>();
        formatedAuthors.put("result1", new Object[]{"firstName1 lastName1 (dlr)"});
        formatedAuthors.put("result2", new Object[]{"firstName2 lastName2 (mpi)"});
        formatedAuthors.put("result3", new Object[]{"firstName3"});
        formatedAuthors.put("result4", new Object[]{"lastName4"});
        formatedAuthors.put("result5", new Object[]{"dlr"});
        formatedAuthors.put("result6", new Object[]{"firstName6 lastName6"});
        formatedAuthors.put("result7", new Object[]{"firstName7 (dlr)"});
        formatedAuthors.put("result8", new Object[]{"lastName8 (dlr)"});
        formatedAuthors.put("result9", new Object[]{"firstName1 lastName1 (dlr)", "firstName2 lastName2 (mpi)"});
    }
   
    
    @SuppressWarnings("unchecked")
    private void test(int testnumber) throws Exception {
        List<Map<String, String>> fields = new ArrayList<Map<String, String>>();
        fields.add(createMap("column", "target",
                AuthorFormatingTransformer.BOOL_FORMAT_DATA_FIELD, "true"
        ));
        
        Context context = getContext(null, new VariableResolver(), null, Context.FULL_DUMP,
                fields, null);
        
        Context fakeContext = Mockito.spy(context);
        Mockito.doReturn(h.getCore()).when(fakeContext).getSolrCore();
    
        String strTestnumber = String.valueOf(testnumber);
        
        Map<String, Object> row = createMap("target", authorDicts.get("test" + strTestnumber));
        new AuthorFormatingTransformer().transformRow(row, fakeContext);

        Object[] result = (Object[]) row.get("target");
        Arrays.sort(result);
        
        assertEquals(formatedAuthors.get("result" + strTestnumber).length, result.length);
        assertArrayEquals(formatedAuthors.get("result" + strTestnumber), result);
    }
    
    public void testAuthorFormating() throws Exception {
        test(1);
    }
    
    public void testAuthorFormatingDifferent() throws Exception {
        test(2);
    }
    
    public void testOnlyFirstname() throws Exception {
        test(3);
    }
    
    public void testOnlyLastname() throws Exception {
        test(4);
    }
    
    public void testOnlyOrganization() throws Exception {
        test(5);
    }
    
    public void testFirstnameLastname() throws Exception {
        test(6);
    }
    
    public void testFirstnameOrganization() throws Exception {
        test(7);
    }
    
    public void testLastnameOrganization() throws Exception {
        test(8);
    }
    
    public void testmultipleAuthors() throws Exception {
        test(9);
    }
    
}