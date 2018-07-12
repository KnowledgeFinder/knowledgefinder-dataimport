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

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

public class CategoryParserJSONTest {

	private HashMap<String, Object> createInput(int levels, boolean firstLvl) {
		HashMap<String, Object> input = new HashMap<String, Object>();
		
			if (!firstLvl) {
				input.put("id", "IDString" +  ":" + levels);
				input.put("name", "nameString" +  ":" + levels);
			}
			Object[] categories = null;
			if (levels > 0) {
				categories = new Object[3];
				for (int j = 0; j < 3; j++) {
					categories[j] = createInput(levels - 1, false);
				}
			}
			input.put("categories", categories);

		

		return input;
	}

	private HashMap<String, Set<String>> createExpectedOutput(int levels,
			Set<String> path, boolean firstLvl) {
		HashMap<String, Set<String>> expectedOutput = new HashMap<String, Set<String>>();
		Set<String> localPath = new HashSet<String>(path);
		if (levels >= 0&&!firstLvl)
		{
				localPath.add("IDString" + ":" + levels);
				expectedOutput.put("nameString" + ":" + levels, localPath);
				expectedOutput.putAll(createExpectedOutput(levels - 1,
						localPath, false));
	}
		return expectedOutput;
	}

	@Test
	public void testExtractCategories() {
		CategoryParserJSON parser = new CategoryParserJSON() {
			@Override
			protected Map<String, Set<String>> extractCategoriesRecursive(
					String field, int startLevel, Map<String, Object> category,
					Map<String, Set<String>> currents, Set<String> path) {
				HashMap<String, Set<String>> output = new HashMap<String, Set<String>>();

				return output;
			}
		};
		Map<String, Set<String>> expectedOutput = createExpectedOutput(0, new HashSet<String>(), true);
		HashMap<String, Object> input = createInput(1, true);

		Map<String, Set<String>> output = parser.extractCategories(input,
				"foo", 0);

		assertEquals(expectedOutput, output);
	}

	@Test
	public void testExtractCategoriesRecursive() {
		CategoryParserJSON parser = new CategoryParserJSON();
		HashMap<String, Object> input = createInput(0, false);
		Map<String, Set<String>> expectedOutput = createExpectedOutput(0, new HashSet<String>(),false);
		
		Map<String, Set<String>> output = parser.extractCategoriesRecursive(
				"id", -1, input, new HashMap<String, Set<String>>(),
				new HashSet<String>());

		assertEquals(expectedOutput, output);
	}

	@Test
	public void testExtractCategoriesRecursive_startLevel1() {
		CategoryParserJSON parser = new CategoryParserJSON();
		Map<String, Object> input = createInput(0, false);
		Map<String, Set<String>> expectedOutput = createExpectedOutput(-1, new HashSet<String>(),false);

		Map<String, Set<String>> output = parser.extractCategoriesRecursive(
				"id", 1, input, new HashMap<String, Set<String>>(),
				new HashSet<String>());

		assertEquals(expectedOutput, output);
	}
	
	@Test
	public void testExtractCategoriesRecursive_Depth4() {
		CategoryParserJSON parser = new CategoryParserJSON();
		HashMap<String, Object> input = createInput(4, false);
		Map<String, Set<String>> expectedOutput = createExpectedOutput(4, new HashSet<String>(),false);
		
		Map<String, Set<String>> output = parser.extractCategoriesRecursive(
				"id", -1, input, new HashMap<String, Set<String>>(),
				new HashSet<String>());

		assertEquals(expectedOutput, output);
	}

}
