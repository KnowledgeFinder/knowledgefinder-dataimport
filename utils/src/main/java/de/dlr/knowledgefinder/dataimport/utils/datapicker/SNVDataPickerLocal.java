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

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tmatesoft.svn.core.SVNDepth;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNInfo;
import org.tmatesoft.svn.core.wc.SVNPropertyData;
import org.tmatesoft.svn.core.wc.SVNRevision;
import org.tmatesoft.svn.core.wc.SVNWCClient;

import de.dlr.knowledgefinder.dataimport.utils.crawler.SVNCrawlerFactoryImpl;
import de.dlr.knowledgefinder.dataimport.utils.crawler.SVNPropertyCrawler;
import de.dlr.knowledgefinder.dataimport.utils.parser.SVNParser;
import de.dlr.knowledgefinder.dataimport.utils.parser.SVNParserException;
import de.dlr.knowledgefinder.dataimport.utils.parser.SVNParserMapFactory;
import de.dlr.knowledgefinder.dataimport.utils.parser.SVNPropertyNotFoundException;



/**
 * The Class SNVDataPickerLocal. It implements the interface SVNDataPicker and
 * reads from a local path the properties.
 * of a SVN repository.
 *
 * @see {@link SVNPropertyCrawler}
 * @see {@link SVNParser}
 * 
 */
public final class SNVDataPickerLocal implements SVNDataPicker<File> {

	/** The log. */
	private static final Logger LOG = LoggerFactory.getLogger(SNVDataPickerLocal.class);

	/** The parser. */
	private SVNParser<Map<String, Object>> parser;
	
	/** The svn crawler. */
	private SVNPropertyCrawler<File> svnCrawler;
	
	/** The file. */
	private File file;

	/** The manager. */
	private SVNClientManager manager;

	/** The client. */
	private SVNWCClient client;
	
	
	/**
	 * Instantiates a new SNV data picker local.
	 *
	 * @param file the file (root)
	 */
	public static SNVDataPickerLocal createSVNDataPicker(File file){
		return new SNVDataPickerLocal(file);
	}

	private SNVDataPickerLocal(File file) {
		this.file = file;
		parser = new SVNParserMapFactory().createParser(file);
		svnCrawler = new SVNCrawlerFactoryImpl().createCrawler(file);
		manager = SVNClientManager.newInstance();
		client = manager.getWCClient();

	}
	
	/* (non-Javadoc)
	 * @see de.dlr.xps.server.handler.dataimport.datapicker.SVNDataPicker#getData()
	 */
	public Collection<Map<String, Object>> getData() throws SVNDataPickerException{
		List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
		try {
			client.doGetProperty(file, null, SVNRevision.WORKING,
					SVNRevision.WORKING, SVNDepth.INFINITY, svnCrawler, null);
		} catch (SVNException e) {
			throw new SVNDataPickerException(e);
		}
		
		Iterator<Entry<File, List<SVNPropertyData>>> iterator = svnCrawler.iterator();
		while (iterator.hasNext()) {
			Entry<File, List<SVNPropertyData>> fileEntry = iterator.next();
			try {
				SVNInfo svnInfo = client.doInfo(fileEntry.getKey(), SVNRevision.WORKING);
				try {
					List<SVNPropertyData> properties = fileEntry.getValue();
					Map<String, Object> fileMap = parser.parse(properties, svnInfo);
					resultList.add(fileMap);
				} catch(SVNPropertyNotFoundException e){
					LOG.warn(e.getLocalizedMessage());
				} catch (SVNParserException e) {
					LOG.warn(e.getLocalizedMessage(), e);
				}
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
	 * @see de.dlr.xps.server.handler.dataimport.datapicker.SVNDataPicker#setSVNWCClient(org.tmatesoft.svn.core.wc.SVNWCClient)
	 */
	public void setSVNWCClient(SVNWCClient clt) {
		client = clt;
	}

	/* (non-Javadoc)
	 * @see de.dlr.xps.server.handler.dataimport.datapicker.SVNDataPicker#setSVNPropertyCrawler(de.dlr.xps.server.handler.dataimport.crawler.SVNPropertyCrawler)
	 */
	public void setSVNPropertyCrawler(SVNPropertyCrawler<File> svnCrw) {
		svnCrawler = svnCrw;
	}

	/* (non-Javadoc)
	 * @see de.dlr.xps.server.handler.dataimport.datapicker.SVNDataPicker#setSVNParser(de.dlr.xps.server.handler.dataimport.parser.SVNParser)
	 */
	public void setSVNParser(SVNParser<Map<String, Object>> prs) {
		parser = prs;
	}

	/* (non-Javadoc)
	 * @see de.dlr.xps.server.handler.dataimport.datapicker.SVNDataPicker#setSVNClientManager(org.tmatesoft.svn.core.wc.SVNClientManager)
	 */
	public void setSVNClientManager(SVNClientManager mng) {
		manager = mng;
	}
}
