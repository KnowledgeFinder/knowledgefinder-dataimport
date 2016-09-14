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

package de.dlr.knowledgefinderII.dataimport.utils.parser;



import static org.easymock.EasyMock.expect;
import static org.powermock.api.easymock.PowerMock.createMock;
import static org.powermock.api.easymock.PowerMock.mockStatic;
import static org.powermock.api.easymock.PowerMock.replay;
import static org.powermock.api.easymock.PowerMock.verify;

import java.io.File;
import java.util.Map;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.tmatesoft.svn.core.SVNURL;

import de.dlr.knowledgefinderII.dataimport.utils.parser.SVNParser;
import de.dlr.knowledgefinderII.dataimport.utils.parser.SVNParserMap;
import de.dlr.knowledgefinderII.dataimport.utils.parser.SVNParserMapFactory;

@RunWith(PowerMockRunner.class)
public class SVNParserMapFactoryTest extends TestCase{
	
	private SVNParserMapFactory factory;

	@Before
	public void setUp(){
		factory = new SVNParserMapFactory();
	}

	
	/**
	 * Test create parser file.
	 *
	 * @throws Exception the exception
	 */
	@PrepareForTest(SVNParserMap.class)
	@Test
	public void testCreateParserFile() throws Exception{
		File f = createMock(File.class);
		SVNParserMap fileParser = createMock(SVNParserMap.class);

		mockStatic(SVNParserMap.class);
		expect(SVNParserMap.createSVNParserMap()).andReturn(fileParser);
		replay(SVNParserMap.class);
		
		SVNParser<Map<String, Object>> parserInstance = factory.createParser(f);
		verify(SVNParserMap.class);
		assertTrue(parserInstance instanceof SVNParserMap);
	}

	@PrepareForTest(SVNParserMap.class)
	@Test
	public void testCreateParserSVNURL() throws Exception{
		SVNURL svnUrl = createMock(SVNURL.class);
		SVNParserMap fileParser = createMock(SVNParserMap.class);

		mockStatic(SVNParserMap.class);
		expect(SVNParserMap.createSVNParserMap()).andReturn(fileParser);
		replay(SVNParserMap.class);
		
		SVNParser<Map<String, Object>> parserInstance = factory.createParser(svnUrl);
		verify(SVNParserMap.class);
		assertTrue(parserInstance instanceof SVNParserMap);
	}
}
