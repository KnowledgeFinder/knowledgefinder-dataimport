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

package de.dlr.knowledgefinderII.dataimport.utils.crawler;



import static org.easymock.EasyMock.expect;
import static org.powermock.api.easymock.PowerMock.createMock;
import static org.powermock.api.easymock.PowerMock.mockStatic;
import static org.powermock.api.easymock.PowerMock.replay;
import static org.powermock.api.easymock.PowerMock.verify;

import java.io.File;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.tmatesoft.svn.core.SVNURL;

import de.dlr.knowledgefinderII.dataimport.utils.crawler.SVNCrawlerFactory;
import de.dlr.knowledgefinderII.dataimport.utils.crawler.SVNCrawlerFactoryImpl;
import de.dlr.knowledgefinderII.dataimport.utils.crawler.SVNFilePropertyCrawler;
import de.dlr.knowledgefinderII.dataimport.utils.crawler.SVNPropertyCrawler;
import de.dlr.knowledgefinderII.dataimport.utils.crawler.SVNURLPropertyCrawler;


/**
 * The Class SVNCrawlerFactoryTest.
 *
 * 
 */
@RunWith(PowerMockRunner.class)
public class SVNCrawlerFactoryTest extends TestCase {
	
	/** The factory. */
	private SVNCrawlerFactory factory;

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	@Before
	public void setUp(){
		factory = new SVNCrawlerFactoryImpl();
	}
	
	/**
	 * Test create crawler file.
	 *
	 * @throws Exception the exception
	 */
	@PrepareForTest(SVNFilePropertyCrawler.class)
	public void testCreateCrawlerFile() throws Exception{
		File f = createMock(File.class);
		SVNFilePropertyCrawler fileCrawler = createMock(SVNFilePropertyCrawler.class);

		mockStatic(SVNFilePropertyCrawler.class);
		expect(SVNFilePropertyCrawler.createSVNFilePropertyCrawler()).andReturn(fileCrawler);
		replay(SVNFilePropertyCrawler.class);
		
		SVNPropertyCrawler<?> crawlerInstance = factory.createCrawler(f);
		verify(SVNFilePropertyCrawler.class);
		assertTrue(crawlerInstance instanceof SVNFilePropertyCrawler);
	}

	/**
	 * Test create crawler svnurl.
	 */
	@PrepareForTest(SVNURLPropertyCrawler.class)
	public void testCreateCrawlerSVNURL(){
		SVNURL svnUrl = createMock(SVNURL.class);
		SVNURLPropertyCrawler urlCrawler = createMock(SVNURLPropertyCrawler.class);

		mockStatic(SVNURLPropertyCrawler.class);
		expect(SVNURLPropertyCrawler.createSVNURLPropertyCrawler()).andReturn(urlCrawler);
		replay(SVNURLPropertyCrawler.class);
		
		SVNPropertyCrawler<?> crawlerInstance = factory.createCrawler(svnUrl);
		
		verify(SVNURLPropertyCrawler.class);
		assertTrue(crawlerInstance instanceof SVNURLPropertyCrawler);		
	}
}
