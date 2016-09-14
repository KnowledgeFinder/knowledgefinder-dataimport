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

import java.util.List;

import org.tmatesoft.svn.core.wc.SVNInfo;
import org.tmatesoft.svn.core.wc.SVNPropertyData;


/**
 * The Interface Parser. It defines a interface to parse {@link SVNPropertyData} list and
 * {@link SVNInfo} into another class T.
 *
 * 
 * @param <T> the generic type to parser
 */
public interface SVNParser<T> {

	/**
	 * Parses the fileSVN with 
	 *
	 * @param svnPropertyList the svn property list
	 * @param svnInfo the svn info
	 * @return the property list and some extra info from svn info in object of class T
	 * @throws SVNParserException the SVN parser exception if something wrong happens when parsing.
	 */
	public T parse(final List<SVNPropertyData> svnPropertyList, final SVNInfo svnInfo) throws SVNParserException;
}
