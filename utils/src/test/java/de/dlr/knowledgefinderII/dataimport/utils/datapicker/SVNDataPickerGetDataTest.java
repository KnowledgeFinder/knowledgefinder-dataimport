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
package de.dlr.knowledgefinderII.dataimport.utils.datapicker;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.easymock.Mock;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNInfo;
import org.tmatesoft.svn.core.wc.SVNPropertyData;
import org.tmatesoft.svn.core.wc.SVNWCClient;

import de.dlr.knowledgefinderII.dataimport.utils.crawler.SVNPropertyCrawler;
import de.dlr.knowledgefinderII.dataimport.utils.datapicker.SVNDataPicker;
import de.dlr.knowledgefinderII.dataimport.utils.datapicker.SVNDataPickerException;
import de.dlr.knowledgefinderII.dataimport.utils.parser.SVNParser;
import de.dlr.knowledgefinderII.dataimport.utils.parser.SVNParserException;
import de.dlr.knowledgefinderII.dataimport.utils.parser.SVNPropertyNotFoundException;


/**
 * The Abstract Class SVNDataPickerGetDataTest.
 *
 * 
 * @param <T> the generic type
 */
public abstract class SVNDataPickerGetDataTest<T> extends TestCase {

	
	/** The file. */
	protected @Mock T file;
	
	/** The picker. */
	protected SVNDataPicker<T> picker;
	
	/** The mock client. */
	protected SVNWCClient mockClient;
	
	/** The mock crawler. */
	protected SVNPropertyCrawler<T> mockCrawler;
	
	/** The mock parser. */
	protected SVNParser<Map<String, Object>> mockParser;

	/** The results. */
	protected Map<T, List<SVNPropertyData>> results;
	
	/**
	 * Inits the mockers and run the setup.
	 *
	 * @throws SVNException the SVN exception
	 * @throws SVNDataPickerException the SVN data picker exception
	 * @throws SVNParserException the SVN parser exception
	 */
    @Before
	public void init() throws SVNException, SVNDataPickerException, SVNParserException{
		results = createMapResults();
		mockCrawler = (SVNPropertyCrawler<T>) Mockito.mock(SVNPropertyCrawler.class);
		mockParser = (SVNParser<Map<String,Object>>)Mockito.mock(SVNParser.class);
		mockClient = Mockito.mock(SVNWCClient.class);
		setUp();
	}
	
	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	abstract public void setUp() throws SVNException, SVNDataPickerException, SVNParserException;

	/**
	 * Test call do get property.
	 *
	 * @throws SVNException the SVN exception
	 * @throws SVNDataPickerException the SVN data picker exception
	 */
	@Ignore
	abstract public void testCallDoGetProperty() throws SVNException, SVNDataPickerException;
	
	/**
	 * Test exception do get property.
	 *
	 * @throws SVNDataPickerException the SVN data picker exception
	 * @throws SVNException the SVN exception
	 */
	@Ignore
	abstract public void testExceptionDoGetProperty() throws SVNDataPickerException, SVNException;
	
	/**
	 * Test data results.
	 *
	 * @throws SVNException the SVN exception
	 * @throws SVNDataPickerException the SVN data picker exception
	 * @throws SVNParserException the SVN parser exception
	 */
	@Ignore
	public abstract void testDataResults() throws SVNException, SVNDataPickerException, SVNParserException;
	
	/**
	 * Test data svn exceptions.
	 *
	 * @throws SVNException the SVN exception
	 * @throws SVNDataPickerException the SVN data picker exception
	 */
	@Ignore
	public abstract void testDataSVNExceptions() throws SVNException, SVNDataPickerException;

	
	
	/**
	 * Test data svn property not found exceptions.
	 *
	 * @throws SVNPropertyNotFoundException the SVN property not found exception
	 * @throws SVNParserException the SVN parser exception
	 * @throws SVNDataPickerException the SVN data picker exception
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testDataSVNPropertyNotFoundExceptions() throws SVNPropertyNotFoundException, SVNParserException, SVNDataPickerException{
		SVNPropertyNotFoundException exception = Mockito.mock(SVNPropertyNotFoundException.class);
		Mockito.doThrow(exception).when(mockParser).parse(Mockito.any(List.class), Mockito.any(SVNInfo.class));

		Collection<Map<String, Object>> valuesPicker = picker.getData();
		assertEquals(valuesPicker.size(), 0);
	}
	
	/**
	 * Test data svn parser exceptions.
	 *
	 * @throws SVNPropertyNotFoundException the SVN property not found exception
	 * @throws SVNParserException the SVN parser exception
	 * @throws SVNDataPickerException the SVN data picker exception
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testDataSVNParserExceptions() throws SVNPropertyNotFoundException, SVNParserException, SVNDataPickerException{
		SVNParserException exception = Mockito.mock(SVNParserException.class);
		Mockito.doThrow(exception).when(mockParser).parse(Mockito.any(List.class), Mockito.any(SVNInfo.class));

		Collection<Map<String, Object>> valuesPicker = picker.getData();
		assertEquals(valuesPicker.size(), 0);
	}

	/**
	 * Test close.
	 *
	 * @throws SVNPropertyNotFoundException the SVN property not found exception
	 * @throws SVNParserException the SVN parser exception
	 * @throws SVNDataPickerException the SVN data picker exception
	 */
	@Test
	public void testClose() throws SVNPropertyNotFoundException, SVNParserException, SVNDataPickerException{
		SVNClientManager mockManager = Mockito.mock(SVNClientManager.class);
		picker.setSVNClientManager(mockManager);
		picker.close();
		Mockito.verify(mockManager, Mockito.times(1)).dispose();
	}
	
	
	/**
	 * Creates the map results.
	 *
	 * @return the map
	 */
	abstract protected Map<T, List<SVNPropertyData>> createMapResults();
}
