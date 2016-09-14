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

import java.util.Iterator;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class SVNEntityProcessor. This reads and process the
 * values returned by the DataSource instance.
 *
 * 
 */
public class SVNEntityProcessor extends EntityProcessorBase {
	
	public static final String QUERY = "query";

	/** The Constant LOG. */
	private static final Logger LOG = LoggerFactory
			.getLogger(EntityProcessorBase.class);

	/** The data source. */
	protected DataSource<Iterator<Map<String, Object>>> dataSource;

	/* (non-Javadoc)
	 * @see org.apache.solr.handler.dataimport.EntityProcessorBase#init(org.apache.solr.handler.dataimport.Context)
	 */
	
    @Override
    @SuppressWarnings("unchecked")
	public void init(Context context) {
		super.init(context);
		this.dataSource = (DataSource<Iterator<Map<String, Object>>>)context.getDataSource();
		String query = context.getEntityAttribute(QUERY);
		if (query == null){
			throw new DataImportHandlerException(SEVERE,  "SolrEntityProcessor: parameter '" + QUERY + "' is required");
		}
		LOG.info("Processing: " + query);
		initQuery(query);
	}

	/**
	 * Inits the query. Reads the datasource data and instances 
	 * the iterator for {@link nextRow} function.
	 *
	 * @param q the resource url 
	 */
	protected void initQuery(String q) {
		try {
			DataImporter.QUERY_COUNT.get().incrementAndGet();
			rowIterator = dataSource.getData(q);
		} catch (Exception e) {
			String message = "The query failed '" + q + "'" +  e.getMessage();
			wrapAndThrow(SEVERE, e, message);
		}
	}

	/**
	 * Return the next row in interator
	 * @see org.apache.solr.handler.dataimport.EntityProcessorBase#nextRow()
	 */
	@Override
	public Map<String, Object> nextRow() {
		Map<String, Object> next = getNext();
		if (next != null)
			LOG.debug("Processing: " + next.get("filePath"));
		return next;
	}
}