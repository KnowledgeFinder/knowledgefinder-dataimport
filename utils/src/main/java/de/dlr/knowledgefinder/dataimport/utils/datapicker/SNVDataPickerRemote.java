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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.solr.handler.component.SearchHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tmatesoft.svn.core.SVNAuthenticationException;
import org.tmatesoft.svn.core.SVNDepth;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNInfo;
import org.tmatesoft.svn.core.wc.SVNPropertyData;
import org.tmatesoft.svn.core.wc.SVNRevision;
import org.tmatesoft.svn.core.wc.SVNWCClient;
import org.tmatesoft.svn.core.wc.SVNWCUtil;

import de.dlr.knowledgefinder.dataimport.utils.crawler.SVNCrawlerFactoryImpl;
import de.dlr.knowledgefinder.dataimport.utils.crawler.SVNPropertyCrawler;
import de.dlr.knowledgefinder.dataimport.utils.parser.SVNParser;
import de.dlr.knowledgefinder.dataimport.utils.parser.SVNParserException;
import de.dlr.knowledgefinder.dataimport.utils.parser.SVNParserMapFactory;
import de.dlr.knowledgefinder.dataimport.utils.parser.SVNPropertyNotFoundException;

/**
 * The Class SNVDataPickerRemote. Implements the interface
 * SVNDataPicker for reading the properties of a remote SVN repository.
 * 
 *
 * 
 * @see {@link SVNPropertyCrawler}
 * @see {@link SVNParser}
 */
public final class SNVDataPickerRemote implements SVNDataPicker<SVNURL> {

	/** The log. */
	private static final Logger LOG = LoggerFactory.getLogger(SNVDataPickerLocal.class);
	
	/** The svnurl. */
	private SVNURL svnurl;
	
	
	SearchHandler test;
	/** The svn crawler. */
	private SVNPropertyCrawler<SVNURL> svnCrawler;
	
	/** The parser. */
	private SVNParser<Map<String, Object>> parser;

	/** The manager. */
	private SVNClientManager manager;

	/** The client. */
	private SVNWCClient client;

	
	/**
	 * Instantiates a new SNV data picker remote.
	 *
	 * @param svnurl the svnurl
	 * @param username the username
	 * @param password the password
	 */
	public static SVNDataPicker<SVNURL> createSVNDataPicker(SVNURL svnurl,
			String username, String password) {
		return new SNVDataPickerRemote(svnurl, username, password);
	}
	
	private SNVDataPickerRemote(SVNURL svnurl) {
		this(svnurl, null, null);
	}

	private SNVDataPickerRemote(SVNURL svnurl, String username, String password) {
		this.svnurl = svnurl;
		parser = new SVNParserMapFactory().createParser(svnurl);
		svnCrawler = new SVNCrawlerFactoryImpl().createCrawler(svnurl);
		manager = SVNClientManager.newInstance(SVNWCUtil.createDefaultOptions(true), username, password);
		client = manager.getWCClient();
	}

	/* (non-Javadoc)
	 * @see de.dlr.xps.server.handler.dataimport.datapicker.SVNDataPicker#getData()
	 */
	public Collection<Map<String, Object>> getData() throws SVNDataPickerException{
		List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();

		try {
			client.doGetProperty(svnurl, null, SVNRevision.HEAD,
				SVNRevision.HEAD,  SVNDepth.INFINITY, svnCrawler);
		} catch (SVNAuthenticationException e) {
			throw new SVNDataPickerException(e);
		} catch (Exception e) {
			throw new SVNDataPickerException(e);
		}
		
		Iterator<Entry<SVNURL, List<SVNPropertyData>>> iterator = svnCrawler.iterator();
		
		while (iterator.hasNext()) {
			Entry<SVNURL, List<SVNPropertyData>> fileEntry = iterator.next();
			try {
				SVNInfo svnInfo = client.doInfo(fileEntry.getKey(), SVNRevision.HEAD, SVNRevision.HEAD);
				List<SVNPropertyData> properties = fileEntry.getValue();
				Map<String, Object> fileMap = parser.parse(properties, svnInfo);
				resultList.add(fileMap);
			} catch(SVNPropertyNotFoundException e){
				LOG.warn(e.getLocalizedMessage(), e);
			} catch (SVNParserException e) {
				LOG.warn(e.getLocalizedMessage(), e);
			} catch (SVNException e) {
				LOG.warn(e.getLocalizedMessage(), e);
			}
		}
		return resultList;
	}
	
	/* (non-Javadoc)
	 * @see de.dlr.xps.server.handler.dataimport.datapicker.SVNDataPicker#close()
	 */
	public void close(){
		manager.dispose();
	}

	/* (non-Javadoc)
	 * @see de.dlr.xps.server.handler.dataimport.datapicker.SVNDataPicker#setSVNClientManager(org.tmatesoft.svn.core.wc.SVNClientManager)
	 */
	@Override
	public void setSVNClientManager(SVNClientManager manager) {
		this.manager = manager;
	}

	/* (non-Javadoc)
	 * @see de.dlr.xps.server.handler.dataimport.datapicker.SVNDataPicker#setSVNWCClient(org.tmatesoft.svn.core.wc.SVNWCClient)
	 */
	@Override
	public void setSVNWCClient(SVNWCClient client) {
		this.client = client;
	}

	/* (non-Javadoc)
	 * @see de.dlr.xps.server.handler.dataimport.datapicker.SVNDataPicker#setSVNParser(de.dlr.xps.server.handler.dataimport.parser.SVNParser)
	 */
	@Override
	public void setSVNParser(SVNParser<Map<String, Object>> parser) {
		this.parser = parser;
	}

	/* (non-Javadoc)
	 * @see de.dlr.xps.server.handler.dataimport.datapicker.SVNDataPicker#setSVNPropertyCrawler(de.dlr.xps.server.handler.dataimport.crawler.SVNPropertyCrawler)
	 */
	@Override
	public void setSVNPropertyCrawler(SVNPropertyCrawler<SVNURL> crawler) {
		this.svnCrawler = crawler;
	}
}
