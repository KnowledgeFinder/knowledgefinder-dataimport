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
package de.dlr.knowledgefinder.dataimport.utils.parser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.modules.junit4.PowerMockRunner;
import org.tmatesoft.svn.core.SVNPropertyValue;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.wc.SVNInfo;
import org.tmatesoft.svn.core.wc.SVNPropertyData;

import de.dlr.knowledgefinder.dataimport.utils.parser.SVNParserException;
import de.dlr.knowledgefinder.dataimport.utils.parser.SVNParserMap;
import de.dlr.knowledgefinder.dataimport.utils.parser.SVNPropertyNotFoundException;
import junit.framework.TestCase;

/**
 * The Class SVNParserMapTest.
 *
 * 
 */
@RunWith(PowerMockRunner.class)
public class SVNParserMapTest extends TestCase {

	/** The parser. */
	private SVNParserMap parser;
	
	/** The svn url. */
	private SVNURL svnUrl;
	
	/** The svninfo. */
	private SVNInfo svninfo;

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	@Before
	public void setUp(){
		parser = SVNParserMap.createSVNParserMap();
		svninfo = Mockito.mock(SVNInfo.class);
		svnUrl = Mockito.mock(SVNURL.class);
		Mockito.when(svninfo.getURL()).thenReturn(svnUrl);
		Mockito.when(svninfo.getRepositoryRootURL()).thenReturn(svnUrl);
	}
	
	/**
	 * Test svn property not found exception.
	 *
	 * @throws SVNPropertyNotFoundException the SVN property not found exception
	 * @throws SVNParserException the SVN parser exception
	 */
	@Test(expected=SVNPropertyNotFoundException.class)
	public void testSVNPropertyNotFoundException() throws SVNPropertyNotFoundException, SVNParserException {
		// empty list
		List<SVNPropertyData> svnPropertyList = new ArrayList<SVNPropertyData>();
		parser.parse(svnPropertyList, svninfo);
	}
	
	/**
	 * Test json processing exception.
	 *
	 * @throws JsonParseException the json parse exception
	 * @throws JsonMappingException the json mapping exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws SVNParserException the SVN parser exception
	 */
	@Test(expected=SVNParserException.class)
	public void testJsonProcessingException() throws JsonParseException, JsonMappingException, IOException, SVNParserException {
		List<SVNPropertyData> svnPropertyList = new ArrayList<SVNPropertyData>();
		SVNPropertyValue jsonPropertyValue = Mockito.mock(SVNPropertyValue.class);
		byte[] svnJsonData = SVNPropertyValue.getPropertyAsBytes(jsonPropertyValue);
		
		SVNPropertyData data = new SVNPropertyData(SVNParserMap.SVN_PROPERTY_NAME, jsonPropertyValue, null);
		svnPropertyList.add(data);
		
		ObjectMapper mapper = Mockito.mock(ObjectMapper.class);
		JsonProcessingException exception = Mockito.mock(JsonProcessingException.class);
		Mockito.when(mapper.readValue(svnJsonData, TreeMap.class)).thenThrow(exception);

		parser.setObjectMapper(mapper);
		
		parser.parse(svnPropertyList, svninfo);
	}
	
	/**
	 * Test io exception.
	 *
	 * @throws JsonParseException the json parse exception
	 * @throws JsonMappingException the json mapping exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws SVNParserException the SVN parser exception
	 */
	@Test(expected=SVNParserException.class)
	public void testIOException() throws JsonParseException, JsonMappingException, IOException, SVNParserException {
		List<SVNPropertyData> svnPropertyList = new ArrayList<SVNPropertyData>();
		SVNPropertyValue jsonPropertyValue = Mockito.mock(SVNPropertyValue.class);
		byte[] svnJsonData = SVNPropertyValue.getPropertyAsBytes(jsonPropertyValue);
		
		SVNPropertyData data = new SVNPropertyData(SVNParserMap.SVN_PROPERTY_NAME, jsonPropertyValue, null);
		svnPropertyList.add(data);
		
		ObjectMapper mapper = Mockito.mock(ObjectMapper.class);
		IOException exception = Mockito.mock(IOException.class);
		Mockito.when(mapper.readValue(svnJsonData, TreeMap.class)).thenThrow(exception);

		parser.setObjectMapper(mapper);
		
		parser.parse(svnPropertyList, svninfo);
	}
	
	/**
	 * Test file path in results.
	 *
	 * @throws JsonParseException the json parse exception
	 * @throws JsonMappingException the json mapping exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws SVNParserException the SVN parser exception
	 */
	@Test
	public void testFilePathInResults() throws JsonParseException, JsonMappingException, IOException, SVNParserException {
		List<SVNPropertyData> svnPropertyList = new ArrayList<SVNPropertyData>();
		SVNPropertyValue jsonPropertyValue = Mockito.mock(SVNPropertyValue.class);
		byte[] svnJsonData = SVNPropertyValue.getPropertyAsBytes(jsonPropertyValue);
		
		SVNPropertyData data = new SVNPropertyData(SVNParserMap.SVN_PROPERTY_NAME, jsonPropertyValue, null);
		svnPropertyList.add(data);
		
		ObjectMapper mapper = Mockito.mock(ObjectMapper.class);
		
		TreeMap<String, Object> resultReadValue = new TreeMap<String, Object>();
		resultReadValue.put("title", null);
		Mockito.when(mapper.readValue(svnJsonData, TreeMap.class)).thenReturn(resultReadValue);
		parser.setObjectMapper(mapper);
		
		Map<String, Object> result = parser.parse(svnPropertyList, svninfo);
		
		assertTrue(result.containsKey("filePath"));
	}

	/**
	 * Test results.
	 *
	 * @throws JsonParseException the json parse exception
	 * @throws JsonMappingException the json mapping exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws SVNParserException the SVN parser exception
	 */
	@Test
	public void testResults() throws JsonParseException, JsonMappingException, IOException, SVNParserException {
		List<SVNPropertyData> svnPropertyList = new ArrayList<SVNPropertyData>();
		
		SVNPropertyValue jsonPropertyValue = SVNPropertyValue.create("{\"title\":\"\",\"key1\":\"value1, value2\"}");
		byte[] svnJsonData = SVNPropertyValue.getPropertyAsBytes(jsonPropertyValue);
		SVNPropertyData data = new SVNPropertyData(SVNParserMap.SVN_PROPERTY_NAME, jsonPropertyValue, null);
		
		
		SVNPropertyValue fakePropertyValue = SVNPropertyValue.create("{\"key2\":\"value1, value2\"}");
		byte[] svnfakeData = SVNPropertyValue.getPropertyAsBytes(fakePropertyValue);
		SVNPropertyData dataFake = new SVNPropertyData("nodata", fakePropertyValue, null);

		svnPropertyList.add(data);
		svnPropertyList.add(dataFake);

		ObjectMapper mapperSpy = Mockito.spy(parser.getObjectMapper());
		parser.setObjectMapper(mapperSpy);
		
		Map<String, Object> result = parser.parse(svnPropertyList, svninfo);
		Mockito.verify(mapperSpy, Mockito.times(1)).readValue(svnJsonData, TreeMap.class);
		Mockito.verify(mapperSpy, Mockito.never()).readValue(svnfakeData, TreeMap.class);

		assertTrue(result.containsKey("filePath"));
		assertTrue(result.containsKey("key1"));
		assertEquals(result.get("key1"), "value1, value2");
		assertEquals(result.keySet().size(), 3);
	}
}
