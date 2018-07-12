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
package de.dlr.knowledgefinder.dataimport.utils.datapicker;

import java.io.File;

import org.tmatesoft.svn.core.SVNURL;

/**
 * A factory for creating SVNDataPicker objects.
 *
 * 
 */
public class SVNDataPickerFactory {
	
	/**
	 * Creates a new SVNDataPicker object. (Local)
	 *
	 * @param file the file
	 * @return the SVN data picker
	 */
	public static SVNDataPicker<?> createSVNDataPicker(File file){
		return SNVDataPickerLocal.createSVNDataPicker(file);
	}
	
	/**
	 * Creates a new SVNDataPicker object. (Remote)
	 *
	 * @param svnurl the svnurl
	 * @param username the username
	 * @param password the password
	 * @return the SVN data picker
	 */
	public static SVNDataPicker<?> createSVNDataPicker(SVNURL svnurl, String username, String password){
		return SNVDataPickerRemote.createSVNDataPicker(svnurl, username, password);
	}
}
