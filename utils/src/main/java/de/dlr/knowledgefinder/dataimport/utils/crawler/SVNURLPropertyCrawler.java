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
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.wc.SVNPropertyData;


/**
 * This class implements the {@link SVNPropertyCrawler} interface and thus
 * allows for crawling all files (along with their properties) from a local (or
 * remote) SVN repository.
 * <p>
 * Note: this implementation first crawls the repository, and saves all urls and
 * their properties to the repository-global hash map {@link SNVURL}- {@link List} of {@link #svnFileProperties}.
 * 
 * 
 * @see {@link SVNPropertyCrawler}
 * 
 */

public final class SVNURLPropertyCrawler implements SVNPropertyCrawler<SVNURL> {

	/** Map that holds the svn properties for each url. */
	// NOTE: this is a HashMap, because SVNURL does not implement comparable!
	private Map<SVNURL, List<SVNPropertyData>> svnURLProperties = new HashMap<SVNURL, List<SVNPropertyData>>();

	public static SVNURLPropertyCrawler createSVNURLPropertyCrawler(){
		return new SVNURLPropertyCrawler();
	}
	
	/* (non-Javadoc)
	 * @see org.tmatesoft.svn.core.wc.ISVNPropertyHandler#handleProperty(org.tmatesoft.svn.core.SVNURL, org.tmatesoft.svn.core.wc.SVNPropertyData)
	 */
	public void handleProperty(SVNURL url, SVNPropertyData property)
			throws SVNException {
		List<SVNPropertyData> propertyList;

		if (svnURLProperties.containsKey(url)) {
			propertyList = svnURLProperties.get(url);
		} else {
			propertyList = new ArrayList<SVNPropertyData>();
			svnURLProperties.put(url, propertyList);
		}
		propertyList.add(property);
	}
	
	/* (non-Javadoc)
	 * @see org.tmatesoft.svn.core.wc.ISVNPropertyHandler#handleProperty(long, org.tmatesoft.svn.core.wc.SVNPropertyData)
	 */
	public void handleProperty(long revision, SVNPropertyData property)
			throws SVNException {
		throw new UnsupportedOperationException(
				"handleProperty(long, SVNPropertyData) is not implemented");
	}

	/* (non-Javadoc)
	 * @see org.tmatesoft.svn.core.wc.ISVNPropertyHandler#handleProperty(java.io.File, org.tmatesoft.svn.core.wc.SVNPropertyData)
	 */
	public void handleProperty(File path, SVNPropertyData property)
			throws SVNException {
		throw new UnsupportedOperationException(
				"handleProperty(File, SVNPropertyData) is not implemented");
	}

	/**
	 * Returns properties of given URL.
	 * 
	 * @param url a {@link SVNURL} instance
	 * @return the properties of the svn url (this list may be empty, but not
	 *         <code>null</code>)
	 */
	public List<SVNPropertyData> getProperties(SVNURL url) {
		if (svnURLProperties.containsKey(url)) {
			return Collections.unmodifiableList(svnURLProperties.get(url));
		} else {
			// empty array
			return new ArrayList<SVNPropertyData>();
		}
	}

	/**
	 * Returns a map that contains a list of {@link SVNPropertyData} for each
	 * {@link SVNURL} in the repository.
	 * 
	 * @return a map that contains a list of {@link SVNPropertyData} for each
	 *         {@link SVNURL} in the repository.
	 */
	public Map<SVNURL, List<SVNPropertyData>> getSvnURLProperties() {
		return Collections.unmodifiableMap(svnURLProperties);
	}

	/* (non-Javadoc)
	 * @see de.dlr.xps.server.handler.dataimport.crawler.SVNPropertyCrawler#iterator()
	 */
	public Iterator<Entry<SVNURL, List<SVNPropertyData>>> iterator() {
		return getSvnURLProperties().entrySet().iterator();
	}
}
