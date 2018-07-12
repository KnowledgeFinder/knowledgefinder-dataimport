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

package de.dlr.knowledgefinder.dataimport.utils.datapicker;


import static org.easymock.EasyMock.expect;
import static org.powermock.api.easymock.PowerMock.createMock;
import static org.powermock.api.easymock.PowerMock.expectNew;
import static org.powermock.api.easymock.PowerMock.mockStatic;
import static org.powermock.api.easymock.PowerMock.replay;
import static org.powermock.api.easymock.PowerMock.verify;

import java.io.File;

import junit.framework.TestCase;

import org.easymock.EasyMock;
import org.easymock.Mock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNWCClient;

import de.dlr.knowledgefinder.dataimport.utils.crawler.SVNCrawlerFactoryImpl;
import de.dlr.knowledgefinder.dataimport.utils.crawler.SVNPropertyCrawler;
import de.dlr.knowledgefinder.dataimport.utils.datapicker.SNVDataPickerLocal;
import de.dlr.knowledgefinder.dataimport.utils.parser.SVNParser;
import de.dlr.knowledgefinder.dataimport.utils.parser.SVNParserMapFactory;


@RunWith(PowerMockRunner.class)
@PrepareForTest(SNVDataPickerLocal.class)
public class SVNDataPickerLocalCreateTest extends TestCase{
	
	@Mock
	File file = createMock(File.class);
	
	@Test
	@PrepareForTest(SVNClientManager.class)
	public void testCreateClient(){
		SVNClientManager manager = createMock(SVNClientManager.class);
		SVNWCClient client = createMock(SVNWCClient.class);	

		mockStatic(SVNClientManager.class);
		expect(SVNClientManager.newInstance()).andReturn(manager).times(1);
		expect(manager.getWCClient()).andReturn(client).times(1);
		
		replay(SVNClientManager.class, manager);
		SNVDataPickerLocal.createSVNDataPicker(file);
		verify(SVNClientManager.class, manager);
	}
	
	@Test
	public void testCreateParser() throws Exception{
		SVNParserMapFactory parserFactory = createMock(SVNParserMapFactory.class);
		SVNParser<?> parser = createMock(SVNParser.class);

		// Parser
		expectNew(SVNParserMapFactory.class).andReturn(parserFactory).times(1);
		EasyMock.<SVNParser<?>>expect(parserFactory.createParser(file)).andReturn(parser).times(1);
		
		replay(SVNParserMapFactory.class, parserFactory);
		SNVDataPickerLocal.createSVNDataPicker(file);
		verify(SVNParserMapFactory.class, parserFactory);
	}
	
	@Test
	public void testCreateCrawler() throws Exception {
		SVNCrawlerFactoryImpl crawlerFactory = createMock(SVNCrawlerFactoryImpl.class);
		SVNPropertyCrawler<?> crawler = createMock(SVNPropertyCrawler.class);
		
		// Crawler
		expectNew(SVNCrawlerFactoryImpl.class).andReturn(crawlerFactory).times(1);
		EasyMock.<SVNPropertyCrawler<?>>expect(crawlerFactory.createCrawler(file)).andReturn(crawler).times(1);
		
		replay(SVNCrawlerFactoryImpl.class, crawlerFactory);
		SNVDataPickerLocal.createSVNDataPicker(file);
		verify(SVNCrawlerFactoryImpl.class, crawlerFactory);
	}
}
