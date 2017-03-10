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
package de.dlr.knowledgefinderII.dataimport.utils.fileparser.category;

import java.io.InputStream;
import java.util.Map;
import java.util.Set;

import de.dlr.knowledgefinderII.dataimport.utils.fileparser.FileParserException;



/**
 * The Interface CategoryParser. Provides functionality for parsing 
 * a InputStream from a file to a map with the category name and all of the ancestors ids.
 *
 * 
 */
public interface CategoryParser {

	/**
	 * Parser. Function to parser the stream in a map. "name category": ids
	 *
	 * @param is the stream from a file with the information of the categories.
	 * @param field the field to read (for example id or name)
	 * @param startLevel the start level in tree
	 * @return the map category name -> id and ancestors ids.
	 * @throws FileParserException the file parser exception
	 */
	public Map<String, Set<String>> parser(InputStream is, String field, int startLevel) throws FileParserException;
	
	/**
	 * Parser. Function to parser the stream in a map. "name category": "ancestor field"
	 *
	 * @param is the stream from a file with the information of the categories.
	 * @param field the field to read (for example id or name)
	 * @return the map category name -> ancestor field
	 * @throws FileParserException the file parser exception
	 */
	public Map<String, Object> getAncestorsField(InputStream is, String field) throws FileParserException;
}
