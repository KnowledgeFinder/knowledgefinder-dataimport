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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import junit.framework.AssertionFailedError;
import junit.framework.TestCase;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.tmatesoft.svn.core.SVNErrorCode;
import org.tmatesoft.svn.core.SVNErrorMessage;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNURL;

import de.dlr.knowledgefinderII.dataimport.utils.datapicker.SVNDataPicker;
import de.dlr.knowledgefinderII.dataimport.utils.datapicker.SVNDataPickerException;
import de.dlr.knowledgefinderII.dataimport.utils.datapicker.SVNDataPickerFactory;
import de.dlr.knowledgefinderII.dataimport.utils.parser.SVNParserMapFactory;

/**
 * The Class TestSVNDataSourceRemote.
 *
 * 
 */
@RunWith(PowerMockRunner.class)
public class TestSVNDataSourceRemote extends TestCase {
	
	/** The data source. */
	private SVNDataSource dataSource = new SVNDataSource();
	
	/** The context. */
	private Context context;
	
	/** The init props. */
	private Properties initProps = new Properties();

	/** The query. */
	private String query;
	
	/** The svn url. */
	private SVNURL svnUrl;
	
	/** The username. */
	private String username;
	
	/** The password. */
	private String password;
	
	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	@Before
	public void setUp() throws IOException{
		query = "file/does/not/exists/in/system";
		svnUrl = EasyMock.createMock(SVNURL.class);
		username = "testUser";
		password = "testPass";
		
		Map<String, String> usernameField = new HashMap<String, String>();
		usernameField.put("username", username);
		usernameField.put("password", password);
		context = AbstractDataImportHandlerTestCase
					.getContext(null, null, dataSource, Context.FULL_DUMP,
							new ArrayList<Map<String, String>>(), usernameField);
	}
	
	/**
	 * Test remote picker.
	 *
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws SVNDataPickerException the SVN data picker exception
	 * @throws SVNException the SVN exception
	 */
	@Test
	@PrepareForTest({SVNDataPickerFactory.class, SVNURL.class})
	public void testRemotePicker() throws IOException, SVNDataPickerException, SVNException {
		PowerMock.mockStatic(SVNURL.class);
		EasyMock.expect(SVNURL.parseURIEncoded(query)).andReturn(svnUrl).times(1);
		
		PowerMock.mockStatic(SVNDataPickerFactory.class);
		Collection<Map<String, Object>> result = Collections.emptyList();
		
		SVNDataPicker<?> picker = PowerMock.createMock(SVNDataPicker.class);
		EasyMock.expect(picker.getData()).andReturn(result).times(1);
		
		EasyMock.<SVNDataPicker<?>>expect(SVNDataPickerFactory.createSVNDataPicker(svnUrl, username, password)).andReturn(picker).times(1);
		
		PowerMock.replay(picker);
		PowerMock.replay(SVNDataPickerFactory.class);
		PowerMock.replay(SVNURL.class);
		
		dataSource.init(context, initProps);
		Iterator<Map<String, Object>> data = dataSource.getData(query);
		
		PowerMock.verify(picker);
		PowerMock.verify(SVNParserMapFactory.class);
		PowerMock.verify(SVNURL.class);
		assertEquals(data, result.iterator());
	}
	
	/**
	 * Test svn exception.
	 *
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws SVNDataPickerException the SVN data picker exception
	 * @throws SVNException the SVN exception
	 */
	@Test(expected=DataImportHandlerException.class)
	@PrepareForTest({SVNDataPickerFactory.class, SVNURL.class})
	public void testSVNException() throws IOException, SVNDataPickerException, SVNException {
		SVNException exception = new SVNException(SVNErrorMessage.create(SVNErrorCode.UNKNOWN));
		PowerMock.mockStatic(SVNURL.class);
		EasyMock.expect(SVNURL.parseURIEncoded(query)).andThrow(exception);

		PowerMock.mockStatic(SVNDataPickerFactory.class);
		SVNDataPicker<?> picker = PowerMock.createMock(SVNDataPicker.class);
		
		// never called
		EasyMock.expect(picker.getData()).andThrow(new AssertionFailedError()).anyTimes();
		EasyMock.<SVNDataPicker<?>>expect(SVNDataPickerFactory.createSVNDataPicker(svnUrl, username, password)).andThrow(new AssertionFailedError()).anyTimes();
		
		PowerMock.replay(picker);
		PowerMock.replay(SVNDataPickerFactory.class);
		PowerMock.replay(SVNURL.class);
		
		dataSource.init(context, initProps);
		dataSource.getData(query);
		
		PowerMock.verify(picker);
		PowerMock.verify(SVNParserMapFactory.class);
		PowerMock.verify(SVNURL.class);
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
		PowerMock.mockStatic(SVNURL.class);
		EasyMock.expect(SVNURL.parseURIEncoded(query)).andReturn(svnUrl).times(1);

		PowerMock.mockStatic(SVNDataPickerFactory.class);
		SVNDataPicker<?> picker = PowerMock.createMock(SVNDataPicker.class);
		
		// never called
		EasyMock.expect(picker.getData()).andThrow(exception).times(1);
		EasyMock.<SVNDataPicker<?>>expect(SVNDataPickerFactory.createSVNDataPicker(svnUrl, username, password)).andReturn(picker).times(1);
		
		PowerMock.replay(picker);
		PowerMock.replay(SVNDataPickerFactory.class);
		PowerMock.replay(SVNURL.class);
		
		dataSource.init(context, initProps);
		dataSource.getData(query);
		
		PowerMock.verify(picker);
		PowerMock.verify(SVNParserMapFactory.class);
		PowerMock.verify(SVNURL.class);
	}
}