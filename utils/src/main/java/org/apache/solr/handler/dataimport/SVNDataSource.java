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
package org.apache.solr.handler.dataimport;

import static org.apache.solr.handler.dataimport.DataImportHandlerException.SEVERE;
import static org.apache.solr.handler.dataimport.DataImportHandlerException.wrapAndThrow;

import java.io.File;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNURL;

import de.dlr.knowledgefinder.dataimport.utils.datapicker.SVNDataPicker;
import de.dlr.knowledgefinder.dataimport.utils.datapicker.SVNDataPickerException;
import de.dlr.knowledgefinder.dataimport.utils.datapicker.SVNDataPickerFactory;


/**
 * The Class SVNDataSource. A {@link Datasource} to read the properties from 
 * a SVN repository using a {@link SVNDataPicker}.
 *
 * 
 * @see {@link SVNDataPicker}
 */
public class SVNDataSource extends DataSource<Iterator<Map<String, Object>>> {

	/** The log. */
	Logger LOG = LoggerFactory.getLogger(SVNDataSource.class);

	/** The context. */
	private Context context;

	/** The picker. */
	private SVNDataPicker<?> picker;

	/**
	 * Instantiates a new SVN data source.
	 */
	public SVNDataSource() {
	}

	/* (non-Javadoc)
	 * @see org.apache.solr.handler.dataimport.DataSource#init(org.apache.solr.handler.dataimport.Context, java.util.Properties)
	 */
	@Override
	public void init(Context context, Properties initProps) {
		this.context = context;
	}

	/* (non-Javadoc)
	 * @see org.apache.solr.handler.dataimport.DataSource#close()
	 */
	@Override
	public void close() {
		// close connection ??
		// close files ??
		if(picker != null)
			picker.close();
	}

	/* (non-Javadoc)
	 * @see org.apache.solr.handler.dataimport.DataSource#getData(java.lang.String)
	 */
	
	@Override
	public Iterator<Map<String, Object>> getData(String query) {
		File fileQuery = new File(query);
		
		// Local repository
		if (fileQuery.exists()) {
			picker = SVNDataPickerFactory.createSVNDataPicker(fileQuery);
		// remote
		} else {
			final String username = context.getEntityAttribute(USERNAME);
			final String password = context.getEntityAttribute(PASSWORD);
			
			SVNURL svnUrlEncoded = null;
			try{
				svnUrlEncoded = SVNURL.parseURIEncoded(query);
			} catch (SVNException e) {
				LOG.error(e.getErrorMessage().toString(), e);
				wrapAndThrow(SEVERE, e);
			}
			picker = SVNDataPickerFactory.createSVNDataPicker(svnUrlEncoded, username, password);
		}
		
		try {
			Iterable<Map<String, Object>> data = picker.getData();
			return data.iterator();
		} catch (SVNDataPickerException e){
			LOG.error(e.getMessage().toString(), e);
			wrapAndThrow(SEVERE, e);
			return null;
		}
	}

	/** The Constant USERNAME. */
	public static final String USERNAME = "username";
	
	/** The Constant PASSWORD. */
	public static final String PASSWORD = "password";

}
