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
package de.dlr.knowledgefinder.dataimport.utils.crawler;

import java.io.File;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.modules.junit4.PowerMockRunner;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.wc.SVNPropertyData;

import de.dlr.knowledgefinder.dataimport.utils.crawler.SVNURLPropertyCrawler;
import junit.framework.TestCase;


/**
 * The Class SVNURLPropertyCrawlerTest.
 *
 * 
 */
@RunWith(PowerMockRunner.class)
public class SVNURLPropertyCrawlerTest extends TestCase {

	/** The crawler. */
	private SVNURLPropertyCrawler crawler;
	
	/** The property. */
	private SVNPropertyData property;

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	@Before
	public void setUp(){
		crawler = SVNURLPropertyCrawler.createSVNURLPropertyCrawler();
		property = Mockito.mock(SVNPropertyData.class);
	}
	
	/**
	 * Test handle property svnurl.
	 *
	 * @throws SVNException the SVN exception
	 */
	@Test
	public void testHandlePropertySVNURL() throws SVNException{
		SVNURL url = Mockito.mock(SVNURL.class);
		assertTrue(crawler.getSvnURLProperties().isEmpty());
		crawler.handleProperty(url, property);
		assertFalse(crawler.getSvnURLProperties().isEmpty());
		assertTrue(crawler.getProperties(url).contains(property));
		assertEquals(crawler.getSvnURLProperties().size(), 1);
		crawler.handleProperty(url, property);
		assertEquals(crawler.getSvnURLProperties().size(), 1);
	}
	
	/**
	 * Test handle property file.
	 *
	 * @throws SVNException the SVN exception
	 */
	@Test(expected = UnsupportedOperationException.class)
	public void testHandlePropertyFile() throws SVNException{
		File file = Mockito.mock(File.class);
		crawler.handleProperty(file, property);
	}
	
	/**
	 * Test handle property long.
	 *
	 * @throws SVNException the SVN exception
	 */
	@Test(expected = UnsupportedOperationException.class)
	public void testHandlePropertyLong() throws SVNException{
		long value = 12321;
		crawler.handleProperty(value, property);
	}
}
