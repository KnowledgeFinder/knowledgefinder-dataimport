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


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import junit.framework.TestCase;

import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNURL;

import de.dlr.knowledgefinderII.dataimport.utils.datapicker.SVNDataPicker;
import de.dlr.knowledgefinderII.dataimport.utils.datapicker.SVNDataPickerException;
import de.dlr.knowledgefinderII.dataimport.utils.datapicker.SVNDataPickerFactory;
import de.dlr.knowledgefinderII.dataimport.utils.parser.SVNParserMapFactory;


/**
 * The Class TestSVNDataSourceLocal.
 *
 * 
 */
@RunWith(PowerMockRunner.class)
public class TestSVNDataSourceLocal extends TestCase {
	
	/** The fields. */
	private List<Map<String, String>> fields = new ArrayList<Map<String, String>>();
	
	/** The data source. */
	private SVNDataSource dataSource = new SVNDataSource();
	
	/** The variable resolver. */
	private VariableResolver variableResolver = new VariableResolver();
	
	/** The context. */
	private Context context = AbstractDataImportHandlerTestCase
			.getContext(null, variableResolver, dataSource, Context.FULL_DUMP,
					fields, null);
	
	/** The init props. */
	private Properties initProps = new Properties();

	/** The test folder. */
	@Rule
	public TemporaryFolder testFolder = new TemporaryFolder();
	
	/** The query. */
	private String query;
	
	/** The file. */
	private File file;
	
	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	@Before
	public void setUp() throws IOException{
		testFolder.create();
		File folder = testFolder.newFolder();
		query = folder.getAbsolutePath();
		file = new File(query);
	}
	
	/* (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	@After
	public void tearDown(){
		file.delete();
		testFolder.delete();
	}
	
	/**
	 * Test local picker.
	 *
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws SVNDataPickerException the SVN data picker exception
	 */
	@Test
	@PrepareForTest(SVNDataPickerFactory.class)
	public void testLocalPicker() throws IOException, SVNDataPickerException {
		PowerMock.mockStatic(SVNDataPickerFactory.class);
		Collection<Map<String, Object>> result = Collections.emptyList();
		
		SVNDataPicker<?> picker = PowerMock.createMock(SVNDataPicker.class);
		EasyMock.expect(picker.getData()).andReturn(result).times(1);
		
		EasyMock.<SVNDataPicker<?>>expect(SVNDataPickerFactory.createSVNDataPicker(file)).andReturn(picker).times(1);
		
		PowerMock.replay(picker);
		PowerMock.replay(SVNDataPickerFactory.class);
		
		dataSource.init(context, initProps);
		Iterator<Map<String, Object>> data = dataSource.getData(query);
		
		PowerMock.verify(picker);
		PowerMock.verify(SVNParserMapFactory.class);
		assertEquals(data, result.iterator());
	}

	/**
	 * Test svn data picker exception.
	 *
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws SVNDataPickerException the SVN data picker exception
	 * @throws SVNException the SVN exception
	 */
	@Test(expected=DataImportHandlerException.class)
	@PrepareForTest({SVNDataPickerFactory.class, SVNURL.class})
	public void testSVNDataPickerException() throws IOException, SVNDataPickerException, SVNException {
		SVNDataPickerException exception = new SVNDataPickerException(new Exception("Error"));

		PowerMock.mockStatic(SVNDataPickerFactory.class);
		SVNDataPicker<?> picker = PowerMock.createMock(SVNDataPicker.class);
		
		EasyMock.expect(picker.getData()).andThrow(exception).times(1);
		EasyMock.<SVNDataPicker<?>>expect(SVNDataPickerFactory.createSVNDataPicker(file)).andReturn(picker).times(1);
		
		PowerMock.replay(picker);
		PowerMock.replay(SVNDataPickerFactory.class);
		
		dataSource.init(context, initProps);
		dataSource.getData(query);
		
		PowerMock.verify(picker);
		PowerMock.verify(SVNParserMapFactory.class);
	}
	
}