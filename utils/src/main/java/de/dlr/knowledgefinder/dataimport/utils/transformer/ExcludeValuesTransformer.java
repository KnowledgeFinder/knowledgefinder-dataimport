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

import static org.apache.solr.handler.dataimport.DataImportHandlerException.SEVERE;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.lucene.analysis.CharArraySet;
import org.apache.solr.core.SolrCore;
import org.apache.solr.core.SolrResourceLoader;
import org.apache.solr.handler.dataimport.Context;
import org.apache.solr.handler.dataimport.DataImporter;
import org.apache.solr.handler.dataimport.Transformer;
import static org.apache.solr.handler.dataimport.DataImportHandlerException.wrapAndThrow;;

/**
 * The Class ExcludeValuesTransformer. A {@link Transformer} implementation
 * which removes the matched values defined in an exclude file.
 * 
 * 
 * @see {@link UniqueIdGenerator}
 */
public class ExcludeValuesTransformer extends Transformer {

	private static final Map<String, CharArraySet> loadedFiles = new TreeMap<String, CharArraySet>();
	public static final String EXCLUDE_FIELD = "exclude";
	public static final String SRC_COL_NAME = "srcColName";

	private Context context;

	/*
	 * @see
	 * org.apache.solr.handler.dataimport.Transformer#transformRow(java.util.
	 * Map, org.apache.solr.handler.dataimport.Context)
	 */
	@Override
	public Object transformRow(Map<String, Object> row, Context ctx) {

		context = ctx;

		for (Map<String, String> map : context.getAllEntityFields()) {
			String idField = map.get(EXCLUDE_FIELD);
			String column = map.get(DataImporter.COLUMN);
			String srcCol = map.get(SRC_COL_NAME);

			srcCol = srcCol == null ? column : srcCol;

			if (idField != null) {
				CharArraySet excludeValues = null;
				try {
					excludeValues = initializeExcludeValues(idField);
				} catch (IOException e) {
					wrapAndThrow(SEVERE, e);
				}

				Object currentRowValue = row.get(srcCol);

				if (currentRowValue == null)
					return row;

				if (currentRowValue instanceof Object[]) {

					putNotExcludedArrayValues(row, column, excludeValues, currentRowValue);
					
				} else {

					if (excludeValues.contains(currentRowValue)) {
						row.put(column, null);
					} else {
						row.put(column, currentRowValue);
					}
				}
			}
		}
		return row;
	}

	private CharArraySet initializeExcludeValues(String filename) throws IOException {
	
		CharArraySet loadedFile = loadedFiles.get(filename);
		if (loadedFile == null) {
			SolrCore core = context.getSolrCore();
	
			SolrResourceLoader loader = core.getResourceLoader();
			List<String> lines = Collections.emptyList();
			lines = loader.getLines(filename);
			loadedFiles.put(filename, new CharArraySet(lines, true));
		}
		return loadedFiles.get(filename);
	}

	private void putNotExcludedArrayValues(Map<String, Object> row, String column, CharArraySet excludeValues,
			Object currentRowValue) {
		
		List<String> results = new ArrayList<String>();
		List<Object> values = new ArrayList<Object>();
		String[] arrayResult = null;
		values.addAll(Arrays.asList((Object[]) currentRowValue));

		for (Object value : values) {
			String valStr = value.toString();
			if (!excludeValues.contains(valStr.toCharArray())) {
				results.add(valStr);
			}
		}

		if (results.size() > 0) {
			arrayResult = results.toArray(new String[results.size()]);
		}

		row.put(column, arrayResult);
	}
}