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

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.tmatesoft.svn.core.SVNPropertyValue;
import org.tmatesoft.svn.core.wc.SVNInfo;
import org.tmatesoft.svn.core.wc.SVNPropertyData;


/**
 * The Class Parser. Implements {@link SVNPaser}. Return a 
 * {@code <Map<String, Object>>} with the properties and
 * the SNV url.
 *
 * 
 */
public class SVNParserMap implements SVNParser<Map<String, Object>> {

	/** The Constant SVN_PROPERTY_NAME. */
	public static final String SVN_PROPERTY_NAME = "datafinder:json";
	private ObjectMapper mapper;

	private SVNParserMap(){
		mapper = new ObjectMapper();
		mapper.configure(DeserializationConfig.Feature.USE_JAVA_ARRAY_FOR_JSON_ARRAY, true);
	}
	
	/**
	 * Creates a new parser map.
	 *
	 * @return the SVN parser map
	 */
	public static SVNParserMap createSVNParserMap(){
		return new SVNParserMap();
	}
	
	/**
	 * Sets the object mapper.
	 *
	 * @param mapper the new object mapper
	 */
	public void setObjectMapper(ObjectMapper mapper){
		this.mapper = mapper;
	}
	

	/**
	 * Gets the object mapper.
	 *
	 * @return the object mapper
	 */
	public ObjectMapper getObjectMapper() {
		return this.mapper;
	}
	
	/**
	 * Parses the properties and SNV url in a map.
	 *
	 * @param svnPropertyList the svn property list
	 * @param svnInfo the svn info
	 * @return the properties and the SVN url in a {@link Map}
	 * @throws SVNParserException the SVN parser exception
	 * @throws SVNPropertyNotFoundException if "datafinder:json" is not found in the properties.
	 * 
	 * @see {@link ObjectMapper#readValue(byte[], Class)
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> parse(List<SVNPropertyData> svnPropertyList, SVNInfo svnInfo)
			throws SVNPropertyNotFoundException, SVNParserException {
		
		Map<String, Object> result = null;
		for (SVNPropertyData svnPropertyData : svnPropertyList) {
		    /*if (svnPropertyData.getName().equals("datafinder:____LINK____")){
		        result =
		    }*/
			if (svnPropertyData.getName().equals(SVN_PROPERTY_NAME)) {
				result = new TreeMap<String, Object>();
				SVNPropertyValue jsonPropertyValue = svnPropertyData.getValue();
				byte[] svnJsonData = SVNPropertyValue.getPropertyAsBytes(jsonPropertyValue);
				try {
					result = mapper.readValue(svnJsonData, result.getClass());
				} catch (JsonProcessingException e) {
					throw new SVNParserException(e.getMessage());
				} catch (IOException e) {
					throw new SVNParserException(e.getMessage());
				}
				// there needs to be a property title
				if (!result.containsKey("title"))
				    result = null;
				break;
			}
		}
		
		// Property not found, maybe a folder?
		if (result == null){
			String message = "Could not find svn property: '" + SVN_PROPERTY_NAME +"' for " + svnInfo.getURL().toString();
			throw new SVNPropertyNotFoundException(message);
		}
		
		result.put("filePath", simpleFilePath(svnInfo));
		
		return result;
	}
	
	private String simpleFilePath(SVNInfo svnInfo){
		final String repository = svnInfo.getRepositoryRootURL().toString();
		return svnInfo.getURL().toString().replaceFirst(repository + "/?", "");
	}

}
