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
package de.dlr.knowledgefinder.dataimport.utils.fileparser;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import de.dlr.knowledgefinder.dataimport.utils.fileparser.category.CategoryParser;

/**
 * The Class CategoryParserJSON. Implements CategoryParser interface. It reads a
 * JSON file with the structure:
 * <p>
 * categories: <br/>
 * - name, id, [categories]: <br/>
 * ------ - name, id, [categories]: <br/>
 * --------------- - name, id, [categories]: <br/>
 * ... <br/>
 * </p>
 * The first level categories will not be inserted in the map.
 *
 * 
 */
public class CategoryParserJSON implements CategoryParser {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.dlr.xps.server.handler.dataimport.transformer.CategoryParser#parser
	 * (java.io.InputStream)
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Set<String>> parser(InputStream is, String field,
			int startLevel) throws FileParserException {
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(
				DeserializationConfig.Feature.USE_JAVA_ARRAY_FOR_JSON_ARRAY,
				true);

		HashMap<String, Object> read = null;
		try {
			read = (HashMap<String, Object>) mapper.readValue(is, Map.class);
		} catch (JsonParseException e) {
			throw new FileParserException(e);
		} catch (JsonMappingException e) {
			throw new FileParserException(e);
		} catch (IOException e) {
			throw new FileParserException(e);
		}
		Map<String, Set<String>> categories = extractCategories(read, field,
				startLevel);
		return categories;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.dlr.xps.server.handler.dataimport.transformer.CategoryParser#parser
	 * (java.io.InputStream)
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> getAncestorsField(InputStream is, String field)
			throws FileParserException {
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(
				DeserializationConfig.Feature.USE_JAVA_ARRAY_FOR_JSON_ARRAY,
				true);

		HashMap<String, Object> read = null;
		try {
			read = (HashMap<String, Object>) mapper.readValue(is, Map.class);
		} catch (JsonParseException e) {
			throw new FileParserException(e);
		} catch (JsonMappingException e) {
			throw new FileParserException(e);
		} catch (IOException e) {
			throw new FileParserException(e);
		}
		Map<String, Object> categories = extractAncestorField(read, field);
		return categories;
	}

	/**
	 * categories -> [ancestors,..]
	 * 
	 * @param read
	 * @param field
	 * @return categories -> ancestor field (parent)
	 * 
	 */
	protected Map<String, Object> extractAncestorField(
			HashMap<String, Object> read, String field) {

		Map<String, Object> currents = new HashMap<String, Object>();

		if (read != null) {
			Object[] categories = (Object[]) read.get("categories");
			// initial categories:
			for (Object category : categories) {
				@SuppressWarnings("unchecked")
				Map<String, Object> mapCategory = (Map<String, Object>) category;
				currents.putAll(extractAncestorFieldRecursive(field,
						mapCategory, new HashMap<String, Object>(), null));
			}
		}

		return currents;
	}

	protected Map<String, Object> extractAncestorFieldRecursive(String field,
			Map<String, Object> category, Map<String, Object> currents,
			Map<String, Object> parent) {

		Object[] categories = (Object[]) category.get("categories");
		String name = (String) category.get("name");

		if (parent != null) {
			currents.put(name, parent.get(field));
		}

		if (categories == null) {
			return currents;
		} else {
			for (Object cat : categories) {
				@SuppressWarnings("unchecked")
				Map<String, Object> catValue = (Map<String, Object>) cat;
				currents.putAll(extractAncestorFieldRecursive(field, catValue,
						currents, category));
			}
		}

		return currents;
	}

	/**
	 * Extract categories recursive.
	 *
	 * @param category
	 *            the category
	 * @param currents
	 *            the currents values
	 * @param path
	 *            the "field" path (list of ancestors "field")
	 * @return the map
	 */
	protected Map<String, Set<String>> extractCategoriesRecursive(String field,
			int startLevel, Map<String, Object> category,
			Map<String, Set<String>> currents, Set<String> path) {

		Object[] categories = (Object[]) category.get("categories");
		String id = (String) category.get(field);
		String name = (String) category.get("name");

		path.add(id);
		// Skip "the x-th level" categories
		if (path.size() > startLevel) {
			currents.put(name, Collections.unmodifiableSet(path));
		}

		if (categories == null) {
			return currents;
		} else {
			for (Object cat : categories) {
				@SuppressWarnings("unchecked")
				Map<String, Object> catValue = (Map<String, Object>) cat;
				currents.putAll(extractCategoriesRecursive(field, startLevel,
						catValue, currents, new HashSet<String>(path)));
			}
		}
		return currents;
	}

	/**
	 * Extract categories.
	 *
	 * @param read
	 *            the read
	 * @return the map
	 */
	protected Map<String, Set<String>> extractCategories(
			HashMap<String, Object> read, String field, int startlevel) {
		
		Map<String, Set<String>> result = new HashMap<String, Set<String>>();

		if (read != null) {
			Object[] categories = (Object[]) read.get("categories");
			// initial categories:
			for (Object category : categories) {
				@SuppressWarnings("unchecked")
				Map<String, Object> mapCategory = (Map<String, Object>) category;
				result.putAll(extractCategoriesRecursive(field, startlevel,
						mapCategory, new HashMap<String, Set<String>>(),
						new HashSet<String>()));
			}
		}
		return result;
	}
}
