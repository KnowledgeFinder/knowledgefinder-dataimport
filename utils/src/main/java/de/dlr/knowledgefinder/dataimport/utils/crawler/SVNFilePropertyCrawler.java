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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.wc.SVNPropertyData;


/**
 * This class implements the {@link SVNPropertyCrawler} interface and thus
 * allows for crawling all files (along with their properties) from a local (or
 * remote) SVN repository.
 * <p>
 * Note: this implementation first crawls the repository, and saves all files and
 * their properties to the repository-global hash map {@link File}- {@link List} of {@link #svnFileProperties}.
 * 
 * 
 * @see {@link SVNPropertyCrawler}
 * 
 */

public final class SVNFilePropertyCrawler implements SVNPropertyCrawler<File> {
	
	/** Map that holds the svn properties for each file path. */
	private Map<File, List<SVNPropertyData>> svnURLProperties = new TreeMap<File, List<SVNPropertyData>>();

	public static SVNFilePropertyCrawler createSVNFilePropertyCrawler(){
		return new SVNFilePropertyCrawler();
	}
	
	/* (non-Javadoc)
	 * @see org.tmatesoft.svn.core.wc.ISVNPropertyHandler#handleProperty(org.tmatesoft.svn.core.SVNURL, org.tmatesoft.svn.core.wc.SVNPropertyData)
	 */
	public void handleProperty(SVNURL url, SVNPropertyData property)
			throws SVNException {
		throw new UnsupportedOperationException(
				"handleProperty(SVNURL, SVNPropertyData) is not implemented");
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
		
		List<SVNPropertyData> propertyList;

		if (svnURLProperties.containsKey(path)) {
			propertyList = svnURLProperties.get(path);
		} else {
			propertyList = new ArrayList<SVNPropertyData>();
			svnURLProperties.put(path, propertyList);
		}
		propertyList.add(property);
	}

	/**
	 * Returns properties of given path.
	 * 
	 * @param path a {@link File} instance
	 * @return the properties of the directory (this list may be empty, but not
	 *         <code>null</code>)
	 */
	public List<SVNPropertyData> getProperties(File path) {
		if (svnURLProperties.containsKey(path)) {
			return Collections.unmodifiableList(svnURLProperties.get(path));
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
	public Map<File, List<SVNPropertyData>> getSvnURLProperties() {
		return Collections.unmodifiableMap(svnURLProperties);
	}

	/* (non-Javadoc)
	 * @see de.dlr.xps.server.handler.dataimport.crawler.SVNPropertyCrawler#iterator()
	 */
	public Iterator<Entry<File, List<SVNPropertyData>>> iterator() {
		return getSvnURLProperties().entrySet().iterator();
	}
}
