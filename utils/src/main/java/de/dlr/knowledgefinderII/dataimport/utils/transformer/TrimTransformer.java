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

import java.util.Map;

import org.apache.solr.handler.dataimport.Context;
import org.apache.solr.handler.dataimport.DataImporter;
import org.apache.solr.handler.dataimport.Transformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * The Class TrimTransformer. A {@link Transformer} implementation which deletes starting and trailing white spaces.  
 * 
 * 
 */
public class TrimTransformer extends Transformer {

	private static final Logger LOG = LoggerFactory.getLogger(TrimTransformer.class);
	public static final String TRIM_DATA_FIELD = "trim";
	
	/* 
	 * @see org.apache.solr.handler.dataimport.Transformer#transformRow(java.util.Map, org.apache.solr.handler.dataimport.Context)
	 */
	@Override
	public Object transformRow(Map<String, Object> row, Context context) {
		
		for (Map<String, String> map : context.getAllEntityFields()) {
			
			String column = map.get(DataImporter.COLUMN);
			String doTrim = map.get(TRIM_DATA_FIELD);
			
			if (Boolean.parseBoolean(doTrim) == true){
    			try {
    			    if (row.get(column) instanceof Object[]) {
    			    	Object[] array = (Object[]) row.get(column);
    			    	String[] result = new String[array.length];
    			        for(int i = 0; i < array.length; i++){
    			        	result[i] = array[i].toString().trim();
    			        }
    			        row.put(column, result);
                    }
    			    else
    			    	row.put(column, row.get(column).toString().trim());
    			} catch (Exception e) {
    				LOG.warn(e.toString());
    			}
			}
		}
		return row;
	}
	
	
}