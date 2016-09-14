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

package de.dlr.knowledgefinderII.dataimport.utils.fileparser;


/**
 * The Class FileParserException.
 *
 * 
 */
public class FileParserException extends Exception {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 6500349631409035733L;
	
	/**
	 * Instantiates a new file parser exception.
	 *
	 * @param e another {@link Throwable}
	 */
	public FileParserException(Throwable e) {
		super(e);
	}

	/**
	 * Instantiates a new file parser exception.
	 *
	 * @param string a string
	 */
	
	public FileParserException(String string) {
		super(string);
	}
}
