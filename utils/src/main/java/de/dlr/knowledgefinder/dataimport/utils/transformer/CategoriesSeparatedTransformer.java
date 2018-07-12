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

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.apache.solr.core.SolrResourceLoader;
import org.apache.solr.handler.dataimport.Context;
import org.apache.solr.handler.dataimport.DataImporter;
import org.apache.solr.handler.dataimport.Transformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.dlr.knowledgefinder.dataimport.utils.fileparser.FileParserException;
import de.dlr.knowledgefinder.dataimport.utils.fileparser.category.CategoryParser;
import de.dlr.knowledgefinder.dataimport.utils.fileparser.category.CategoryParserFactory;

/**
 * The Class CategoriesSeparatedTransformer. A {@link Transformer}
 * implementation which creates columns for each category group read from file.
 * 
 * 
 * @see {@link CategoryParser}
 * @see {@link FileParserException}
 */
public class CategoriesSeparatedTransformer extends Transformer {

	private static final Logger LOG = LoggerFactory.getLogger(CategoriesSeparatedTransformer.class);
	private static final Map<String, Map<String, String>> loadedFiles = new TreeMap<String, Map<String, String>>(
			String.CASE_INSENSITIVE_ORDER);
	public static final String CATEGORIES_FILE_FIELD = "categories";
	public static final String CATEGORIES_SPLIT_PREFIX_FIELD = "categories_split_prefix";

	private Context context;
	private CategoryParserFactory factory;

	public CategoriesSeparatedTransformer() {
		factory = new CategoryParserFactory();
	}

	/*
	 * @see
	 * org.apache.solr.handler.dataimport.Transformer#transformRow(java.util
	 * .Map, org.apache.solr.handler.dataimport.Context)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Object transformRow(Map<String, Object> row, Context ctx) {
		context = ctx;

		for (Map<String, String> map : context.getAllEntityFields()) {
			String fileField = map.get(CATEGORIES_FILE_FIELD);
			String splitFieldPrefix = map.get(CATEGORIES_SPLIT_PREFIX_FIELD);

			if (splitFieldPrefix != null && fileField != null) {
				Map<String, String> parentNameValues = initializeNameValues(fileField);
				String[] keys = parentNameValues.keySet().toArray(new String[parentNameValues.size()]);
				Arrays.sort(keys, String.CASE_INSENSITIVE_ORDER);

				String column = map.get(DataImporter.COLUMN);

				Object tmpVal = row.get(column);

				List<Object> values = new ArrayList<Object>();

				if (tmpVal == null)
					return row;
				if (tmpVal instanceof Object[]) {
					values.addAll(Arrays.asList((Object[]) tmpVal));
				} else if(tmpVal instanceof List){
					
					values = (List<Object>)tmpVal;
				}else {
					values.add(tmpVal);
				}

				Map<String, List<String>> parentValues = createParentChildValuePairs(parentNameValues, keys, values);

				for (String parent : parentValues.keySet()) {
					List<String> results = parentValues.get(parent);
					Object[] arrayResult = results.toArray(new Object[results.size()]);
					row.put(splitFieldPrefix + parent, arrayResult);
				}
			}
		}
		return row;
	}

	// file -> StringCategory -> parent name
	private Map<String, String> initializeNameValues(String filename) {
		Map<String, String> loadedFile = loadedFiles.get(filename);

		if (loadedFile == null) {
			SolrResourceLoader loader = context.getSolrCore().getResourceLoader();

			Map<String, String> names = new TreeMap<String, String>(String.CASE_INSENSITIVE_ORDER);

			try {
				InputStream is = loader.openResource(filename);
				CategoryParser catParser = factory.getParser(filename);
				for (Entry<String, Object> entry : catParser.getAncestorsField(is, "name").entrySet()) {
					names.put(entry.getKey(), entry.getValue().toString());
				}
			} catch (FileParserException | IOException e) {
				LOG.error(e.getLocalizedMessage(), e);
			}
			loadedFiles.put(filename, names);
		}
		return loadedFiles.get(filename);
	}

	private Map<String, List<String>> createParentChildValuePairs(Map<String, String> parentNameValues, String[] keys,
			List<Object> values) {
		Map<String, List<String>> parentValues = new TreeMap<String, List<String>>(String.CASE_INSENSITIVE_ORDER);
		for (Object value : values) {
			String valStr = value.toString();
			String parentValue = parentNameValues.get(valStr);
			if (parentValue == null) {
				LOG.warn("Parent not found for category: " + value);
			} else {
				List<String> listResult = parentValues.get(parentValue);
				if (listResult == null) {
					listResult = new ArrayList<String>();
				}

				int keyIndex = Arrays.binarySearch(keys, valStr, String.CASE_INSENSITIVE_ORDER);
				listResult.add(keys[keyIndex]);
				parentValues.put(parentValue, listResult);
			}
		}
		return parentValues;
	}

}