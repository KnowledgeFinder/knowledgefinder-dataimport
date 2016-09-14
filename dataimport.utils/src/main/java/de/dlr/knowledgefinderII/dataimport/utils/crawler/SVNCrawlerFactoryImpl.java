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

import java.io.File;

import org.tmatesoft.svn.core.SVNURL;


/**
 * A factory for creating SVNURLCrawler objects.
 *
 * 
 */
public class SVNCrawlerFactoryImpl implements SVNCrawlerFactory {

	/* (non-Javadoc)
	 * @see de.dlr.xps.server.handler.dataimport.crawler.SVNCrawlerFactory#createCrawler(java.io.File)
	 */
	public SVNPropertyCrawler<File> createCrawler(File fileQuery) {
		return SVNFilePropertyCrawler.createSVNFilePropertyCrawler();
	}

	/* (non-Javadoc)
	 * @see de.dlr.xps.server.handler.dataimport.crawler.SVNCrawlerFactory#createCrawler(org.tmatesoft.svn.core.SVNURL)
	 */
	public SVNPropertyCrawler<SVNURL> createCrawler(SVNURL svnUrlEncoded) {
		return SVNURLPropertyCrawler.createSVNURLPropertyCrawler();
	}
}
