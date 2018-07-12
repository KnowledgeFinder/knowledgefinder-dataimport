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
package de.dlr.knowledgefinder.dataimport.utils.transformer;

import java.util.Map;

import org.apache.solr.handler.dataimport.Context;
import org.apache.solr.handler.dataimport.DataImporter;
import org.apache.solr.handler.dataimport.Transformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FilePathTransformer extends Transformer {

	private static final Logger LOG = LoggerFactory.getLogger(FilePathTransformer.class);
	public static final String PATH_PREFIX_FIELD = "filePrefix";
	public static final String SUFFIX_FIELD = "fileSuffix";
	public static final String OLD_SUFFIX_FIELD = "oldFileSuffix";
	public static final String TARGET_COLUMN_FIELD = "dst";
	public static final String FILE_NAME_COLUMN = "srcColName";

	/*
	 * @see
	 * org.apache.solr.handler.dataimport.Transformer#transformRow(java.util.
	 * Map, org.apache.solr.handler.dataimport.Context)
	 */
	@Override
	public Object transformRow(Map<String, Object> row, Context context) {
		for (Map<String, String> map : context.getAllEntityFields()) {
			String column = map.get(DataImporter.COLUMN);
			String pathPrefix = map.get(PATH_PREFIX_FIELD);
			String targetColumn = map.get(TARGET_COLUMN_FIELD);
			String suffix = map.get(SUFFIX_FIELD);
			String oldSuffix = map.get(OLD_SUFFIX_FIELD);
			String fileCol = map.get(FILE_NAME_COLUMN);

			fileCol = fileCol == null ? column : fileCol;

			if (pathPrefix == null || row.get(fileCol) == null)
				continue;

			String fileName = row.get(fileCol).toString();

			if (oldSuffix != null && !oldSuffix.equalsIgnoreCase("")) {
				fileName = fileName.replaceAll(oldSuffix + "[ \t]*$", suffix);
			} else if (suffix != null) {
				fileName += suffix;
			}

			pathPrefix = pathPrefix.trim();
			fileName = fileName.trim();
			if (pathPrefix != "" && !pathPrefix.endsWith("/"))
				pathPrefix += "/";
			if (targetColumn == null)
				row.put(column, pathPrefix + fileName);
			else
				row.put(targetColumn, pathPrefix + fileName);
			LOG.info(pathPrefix + fileName);

		}
		return row;
	}

}
