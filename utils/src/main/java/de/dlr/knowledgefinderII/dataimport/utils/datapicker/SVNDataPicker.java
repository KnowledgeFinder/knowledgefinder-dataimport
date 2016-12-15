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
import java.util.Map;

import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNWCClient;

import de.dlr.knowledgefinderII.dataimport.utils.crawler.SVNPropertyCrawler;
import de.dlr.knowledgefinderII.dataimport.utils.parser.SVNParser;



/**
 * The Interface SVNDataPicker.
 *
 * 
 * @param <T> the generic type for the {@link SVNPropertyCrawler}
 */
public interface SVNDataPicker<T> {
	
	/**
	 * Gets the data. Return the read data in an iterable object of maps (Id, data).
	 *
	 * @return the data
	 * @throws SVNDataPickerException the SVN data picker exception
	 * @see {@link SVNPropertyCrawler}
	 * @see {@link SVNParser}
	 */
	public Collection<Map<String, Object>> getData() throws SVNDataPickerException;
	
	/**
	 * Close the svn connection.
	 * @see {@link SVNClientManager#dispose()}
	 */
	public void close();

	/**
	 * Sets the SVN client manager.
	 *
	 * @param manager the new SVN client manager
	 */
	public void setSVNClientManager(SVNClientManager manager);

	/**
	 * Sets the SVNWC client.
	 *
	 * @param client the new SVNWC client
	 */
	public void setSVNWCClient(SVNWCClient client);

	/**
	 * Sets the svn parser.
	 *
	 * @param parser the parser
	 */
	public void setSVNParser(SVNParser<Map<String, Object>> parser);
	
	/**
	 * Sets the SVN property crawler.
	 *
	 * @param crawler the new SVN property crawler
	 */
	public void setSVNPropertyCrawler(SVNPropertyCrawler<T> crawler);

}
