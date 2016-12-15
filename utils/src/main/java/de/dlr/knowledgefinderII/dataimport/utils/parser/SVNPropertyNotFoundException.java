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
package de.dlr.knowledgefinderII.dataimport.utils.parser;


/** 
 * The class <code>SVNPropertyNotFoundException</code> 
 * indicates that a SVN property could not be found.
 *
 * @see {@link SVNParserException}
 * @see {@link SVNParser}
 * 
 * 
 */
public class SVNPropertyNotFoundException extends SVNParserException {

	/**
	 * Serial version
	 */
	private static final long serialVersionUID = 6703504798896825000L;

	public SVNPropertyNotFoundException(String message) {
		super(message);
	}
}
