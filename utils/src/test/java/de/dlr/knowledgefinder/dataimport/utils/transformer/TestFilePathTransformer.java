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
import org.junit.BeforeClass;
import org.junit.Test;

public class TestFilePathTransformer extends AbstractSolrTestCase {

	private static Map<String, Object> filePaths;
	private static Map<String, String> resultPaths;

	/**
	 * Inits the start date.
	 */
	@BeforeClass
	public static void initDates() {
		filePaths = new TreeMap<String, Object>();
		filePaths.put("path1", "PATH/ELEMENT1.xml");
		filePaths.put("path2", "PATH/");
		filePaths.put("path3", "PATH/ELEMENT1");
		filePaths.put("path4", null);
		filePaths.put("path5", "test.xml.string.xml");

		resultPaths = new TreeMap<String, String>();
		resultPaths.put(filePaths.get("path1").toString(), "PREFIX/ELEMENT1.pdf");
		resultPaths.put(filePaths.get("path3").toString(), "PREFIX%20TEST/PATH/ELEMENT1");
		resultPaths.put(filePaths.get("path5").toString(), "test.xml.string.pdf");
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testFilePathRelatedTransformation() throws Exception {
		List<Map<String, String>> fields = new ArrayList<Map<String, String>>();
		fields.add(createMap("column", "target", FilePathTransformer.PATH_PREFIX_FIELD, "PREFIX/",
				FilePathTransformer.OLD_SUFFIX_FIELD, ".xml", FilePathTransformer.SUFFIX_FIELD, ".pdf",
				FilePathTransformer.FILE_NAME_COLUMN, "file"));

		Context context = getContext(null, new VariableResolver(), null, Context.FULL_DUMP, fields, null);

		Map<String, Object> row = createMap("target", filePaths.get("path1"), "file", "ELEMENT1.xml");
		new FilePathTransformer().transformRow(row, context);
		assertEquals(resultPaths.get(filePaths.get("path1").toString()), row.get("target"));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testNoFile() throws Exception {
		List<Map<String, String>> fields = new ArrayList<Map<String, String>>();
		fields.add(createMap("column", "target", FilePathTransformer.PATH_PREFIX_FIELD, "PREFIX",
				FilePathTransformer.OLD_SUFFIX_FIELD, ".xml", FilePathTransformer.SUFFIX_FIELD, ".pdf",
				FilePathTransformer.FILE_NAME_COLUMN, "file"));

		Context context = getContext(null, new VariableResolver(), null, Context.FULL_DUMP, fields, null);

		Map<String, Object> row = createMap("target", filePaths.get("path1"), "file", null);
		new FilePathTransformer().transformRow(row, context);
		assertEquals(filePaths.get("path1"), row.get("target"));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testTargetColumn() throws Exception {
		List<Map<String, String>> fields = new ArrayList<Map<String, String>>();
		fields.add(createMap("column", "target", FilePathTransformer.PATH_PREFIX_FIELD, "PREFIX/",
				FilePathTransformer.OLD_SUFFIX_FIELD, ".xml", FilePathTransformer.SUFFIX_FIELD, ".pdf",
				FilePathTransformer.TARGET_COLUMN_FIELD, "newTarget", FilePathTransformer.FILE_NAME_COLUMN, "file"));

		Context context = getContext(null, new VariableResolver(), null, Context.FULL_DUMP, fields, null);

		Map<String, Object> row = createMap("target", filePaths.get("path1"), "file", "ELEMENT1.xml");
		new FilePathTransformer().transformRow(row, context);
		assertEquals(resultPaths.get(filePaths.get("path1").toString()), row.get("newTarget"));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testFilePathTransformation() throws Exception {
		List<Map<String, String>> fields = new ArrayList<Map<String, String>>();
		fields.add(createMap("column", "target", FilePathTransformer.PATH_PREFIX_FIELD, "PREFIX%20TEST/"));

		Context context = getContext(null, new VariableResolver(), null, Context.FULL_DUMP, fields, null);

		Map<String, Object> row = createMap("target", filePaths.get("path3"));
		new FilePathTransformer().transformRow(row, context);
		assertEquals(resultPaths.get(filePaths.get("path3").toString()), row.get("target"));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testNoSlashInPrefix() throws Exception {
		List<Map<String, String>> fields = new ArrayList<Map<String, String>>();
		fields.add(createMap("column", "target", FilePathTransformer.PATH_PREFIX_FIELD, "PREFIX%20TEST"));

		Context context = getContext(null, new VariableResolver(), null, Context.FULL_DUMP, fields, null);

		Map<String, Object> row = createMap("target", filePaths.get("path3"));
		new FilePathTransformer().transformRow(row, context);
		assertEquals(resultPaths.get(filePaths.get("path3").toString()), row.get("target"));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testNoFilePath() throws Exception {
		List<Map<String, String>> fields = new ArrayList<Map<String, String>>();
		fields.add(createMap("column", "target", FilePathTransformer.PATH_PREFIX_FIELD, "PREFIX%20TEST/"));

		Context context = getContext(null, new VariableResolver(), null, Context.FULL_DUMP, fields, null);

		Map<String, Object> row = createMap("target", filePaths.get("path4"));
		new FilePathTransformer().transformRow(row, context);
		assertEquals(null, row.get("target"));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testLeadingAndTrailingWhiteSpacesInPrefix() throws Exception {
		List<Map<String, String>> fields = new ArrayList<Map<String, String>>();
		fields.add(createMap("column", "target", FilePathTransformer.PATH_PREFIX_FIELD, " PREFIX%20TEST/ "));

		Context context = getContext(null, new VariableResolver(), null, Context.FULL_DUMP, fields, null);

		Map<String, Object> row = createMap("target", filePaths.get("path3"));
		new FilePathTransformer().transformRow(row, context);
		assertEquals(resultPaths.get(filePaths.get("path3").toString()), row.get("target"));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testAppendSuffix() {
		List<Map<String, String>> fields = new ArrayList<Map<String, String>>();
		fields.add(createMap("column", "target", FilePathTransformer.PATH_PREFIX_FIELD, "PREFIX/",
				FilePathTransformer.SUFFIX_FIELD, ".pdf"));
	
		Context context = getContext(null, new VariableResolver(), null, Context.FULL_DUMP, fields, null);
	
		Map<String, Object> row = createMap("target", "ELEMENT1");
		new FilePathTransformer().transformRow(row, context);
		assertEquals(resultPaths.get(filePaths.get("path1").toString()), row.get("target"));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testEmptyOldSuffix() throws Exception {
		List<Map<String, String>> fields = new ArrayList<Map<String, String>>();
		fields.add(createMap("column", "target", FilePathTransformer.PATH_PREFIX_FIELD, "PREFIX/",
				FilePathTransformer.OLD_SUFFIX_FIELD, "", FilePathTransformer.SUFFIX_FIELD, ".pdf"));
	
		Context context = getContext(null, new VariableResolver(), null, Context.FULL_DUMP, fields, null);
		
		Map<String, Object> row = createMap("target", "ELEMENT1");
		new FilePathTransformer().transformRow(row, context);
		assertEquals(resultPaths.get(filePaths.get("path1").toString()), row.get("target"));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testNullOldSuffix() throws Exception {
		List<Map<String, String>> fields = new ArrayList<Map<String, String>>();
		fields.add(createMap("column", "target", FilePathTransformer.PATH_PREFIX_FIELD, "PREFIX/",
				FilePathTransformer.OLD_SUFFIX_FIELD, null, FilePathTransformer.SUFFIX_FIELD, ".pdf"));
	
		Context context = getContext(null, new VariableResolver(), null, Context.FULL_DUMP, fields, null);
		
		Map<String, Object> row = createMap("target", "ELEMENT1");
		new FilePathTransformer().transformRow(row, context);
		assertEquals(resultPaths.get(filePaths.get("path1").toString()), row.get("target"));
	}
	
	@Test
	@SuppressWarnings("unchecked")
	public void testNoSuffix() throws Exception {
		List<Map<String, String>> fields = new ArrayList<Map<String, String>>();
		fields.add(createMap("column", "target", FilePathTransformer.PATH_PREFIX_FIELD, "PREFIX%20TEST/PATH/"));
	
		Context context = getContext(null, new VariableResolver(), null, Context.FULL_DUMP, fields, null);
		
		Map<String, Object> row = createMap("target", "ELEMENT1");
		new FilePathTransformer().transformRow(row, context);
		assertEquals(resultPaths.get(filePaths.get("path3").toString()), row.get("target"));
	}
	
	@Test
	@SuppressWarnings("unchecked")
	public void testMultipleOcurrenceOfSuffix() throws Exception {
		List<Map<String, String>> fields = new ArrayList<Map<String, String>>();
		fields.add(createMap("column", "target", FilePathTransformer.PATH_PREFIX_FIELD, "",
				FilePathTransformer.OLD_SUFFIX_FIELD, ".xml", FilePathTransformer.SUFFIX_FIELD, ".pdf"));
	
		Context context = getContext(null, new VariableResolver(), null, Context.FULL_DUMP, fields, null);
		
		Map<String, Object> row = createMap("target", filePaths.get("path5").toString());
		new FilePathTransformer().transformRow(row, context);
		assertEquals(resultPaths.get(filePaths.get("path5").toString()), row.get("target"));
	}
	
}