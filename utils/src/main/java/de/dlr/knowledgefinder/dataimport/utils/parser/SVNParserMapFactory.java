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
package de.dlr.knowledgefinder.dataimport.utils.parser;

import java.io.File;
import java.util.Map;

import org.tmatesoft.svn.core.SVNURL;

/**
 * A factory for creating SVNParserMap objects.
 *
 * 
 */
public class SVNParserMapFactory implements SVNParserFactory {

	/* (non-Javadoc)
	 * @see de.dlr.xps.server.handler.dataimport.parser.SVNParserFactory#createParser(java.io.File)
	 */
	@Override
	public SVNParser<Map<String, Object>> createParser(File fileQuery) {
		return SVNParserMap.createSVNParserMap();
	}

	/* (non-Javadoc)
	 * @see de.dlr.xps.server.handler.dataimport.parser.SVNParserFactory#createParser(org.tmatesoft.svn.core.SVNURL)
	 */
	@Override
	public SVNParser<Map<String, Object>> createParser(SVNURL svnUrlEncoded) {
		return SVNParserMap.createSVNParserMap();
	}
}
