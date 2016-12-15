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



/**
 * A factory for creating CategoryParser objects.
 *
 * 
 */
public class CategoryParserFactory {

	private CategoryParserJSON jsonParser;

	public CategoryParserFactory() {
		// problem with class loader in Solr
		jsonParser = new CategoryParserJSON();
	}
	
	/**
	 * Gets the parser for JSON files.
	 *
	 * @param filename the filename
	 * @return the parser for this kind of file
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 */
	public CategoryParser getParser(String filename) {
		int index = filename.lastIndexOf("/");
		String file = index < 0 ? filename : filename.substring(index);
		String extension = file.substring(file.indexOf(".")); 
		
		switch(extension){
			case ".json": return jsonParser;
			default: throw new UnsupportedOperationException("Unsupported file: " + extension);
		}
	}
}
