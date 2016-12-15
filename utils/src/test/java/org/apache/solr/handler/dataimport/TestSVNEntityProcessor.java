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
package org.apache.solr.handler.dataimport;

import static org.apache.solr.handler.dataimport.DataImportHandlerException.SEVERE;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import junit.framework.AssertionFailedError;
import junit.framework.TestCase;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.modules.junit4.PowerMockRunner;

/**
 * The Class TestSVNEntityProcessor.
 *
 * 
 */
@RunWith(PowerMockRunner.class)
public class TestSVNEntityProcessor extends TestCase {

	/** The query. */
	private String query;

	/** The attrs. */
	private Map<String, String> attrs;

	/*
	 * (non-Javadoc)
	 * 
	 * @see junit.framework.TestCase#setUp()
	 */
	@Before
	@SuppressWarnings("unchecked")
	public void setUp() throws IOException {
		File tmpdir = File.createTempFile("test", "tmp", null);
		tmpdir.delete();
		tmpdir.mkdir();
		tmpdir.deleteOnExit();
		query = tmpdir.getAbsolutePath();
		attrs = AbstractDataImportHandlerTestCase.createMap(
				SVNEntityProcessor.QUERY, query);
	}

	/**
	 * Test simple. Need to call init() for a good configuration via super.init()
	 *
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	@Test
	@SuppressWarnings("unchecked")
	public void testInitQuery() throws IOException {

		// simple datasource, return a list
		DataSource<Iterator<Map<String, Object>>> datasource = EasyMock
				.createMock(DataSource.class);

		Context c = AbstractDataImportHandlerTestCase.getContext(null,
				new VariableResolver(), datasource, Context.FULL_DUMP,
				Collections.EMPTY_LIST, attrs);

		SVNEntityProcessor svnEntityProcessor = new SVNEntityProcessor();
		List<Map<String, Object>> result = createDataSourceResponse();

		EasyMock.expect(datasource.getData(query)).andReturn(result.iterator())
				.times(1);

		PowerMock.replay(datasource);
		svnEntityProcessor.init(c);
		PowerMock.verify(datasource);

		Iterator<Map<String, Object>> iterator = result.iterator();
		while (true) {
			Map<String, Object> f = svnEntityProcessor.nextRow();
			if (f == null)
				break; // no more elements
			assertEquals(f, iterator.next());
		}

	}

	@Test
	@SuppressWarnings("unchecked")
	public void testInit() throws IOException {
		SVNEntityProcessor svnEntityProcessor = new SVNEntityProcessor() {
			@Override
			protected void initQuery(String q) {
			}
		};
		// simple datasource, return a list
		DataSource<Iterator<Map<String, Object>>> datasource = EasyMock
				.createMock(DataSource.class);

		Context c = AbstractDataImportHandlerTestCase.getContext(null,
				new VariableResolver(), datasource, Context.FULL_DUMP,
				Collections.EMPTY_LIST, attrs);

		svnEntityProcessor.init(c);

		assertEquals(svnEntityProcessor.dataSource, datasource);

	}

	@Test(expected = DataImportHandlerException.class)
	@SuppressWarnings("unchecked")
	public void testInit_NullQuery() throws IOException {
		SVNEntityProcessor svnEntityProcessor = new SVNEntityProcessor();
		if (attrs.containsKey(SVNEntityProcessor.QUERY))
			attrs.remove(SVNEntityProcessor.QUERY);
		// simple datasource, return a list
		DataSource<Iterator<Map<String, Object>>> datasource = EasyMock
				.createMock(DataSource.class);

		Context c = AbstractDataImportHandlerTestCase.getContext(null,
				new VariableResolver(), datasource, Context.FULL_DUMP,
				Collections.EMPTY_LIST, attrs);

		svnEntityProcessor.init(c);

	}

	/**
	 * Test data source exception. It calls init() for a proper configuration of
	 * the class via the super.init() call
	 */
	@SuppressWarnings("unchecked")
	@Test(expected = DataImportHandlerException.class)
	public void testInitQuery_DataImportException() {

		// simple datasource, return a list
		DataSource<Iterator<Map<String, Object>>> datasource = EasyMock
				.createMock(DataSource.class);

		Context c = AbstractDataImportHandlerTestCase.getContext(null,
				new VariableResolver(), datasource, Context.FULL_DUMP,
				Collections.EMPTY_LIST, attrs);

		SVNEntityProcessor svnEntityProcessor = new SVNEntityProcessor();

		EasyMock.expect(datasource.getData(query))
				.andThrow(new DataImportHandlerException(SEVERE)).times(1);
		EasyMock.expect(datasource.getData(query))
				.andThrow(new AssertionFailedError()).anyTimes();

		PowerMock.replay(datasource);
		svnEntityProcessor.init(c);
		PowerMock.verify(datasource);
	}

	/**
	 * Creates the data source response.
	 *
	 * @return the list
	 */
	@SuppressWarnings("unchecked")
	private List<Map<String, Object>> createDataSourceResponse() {
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		result.add(AbstractDataImportHandlerTestCase.createMap("Property1",
				"valueA", "property2", "value2A"));
		result.add(AbstractDataImportHandlerTestCase.createMap("Property1",
				"valueB", "property2", "value2B"));
		result.add(AbstractDataImportHandlerTestCase.createMap("Property1",
				"valueC", "property2", "value2C"));
		result.add(AbstractDataImportHandlerTestCase.createMap("Property1",
				"valueD", "property2", "value2D"));
		return result;
	}
}