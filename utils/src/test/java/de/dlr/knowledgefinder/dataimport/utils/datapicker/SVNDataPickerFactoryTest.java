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


import static org.easymock.EasyMock.expect;
import static org.powermock.api.easymock.PowerMock.createMock;
import static org.powermock.api.easymock.PowerMock.mockStatic;
import static org.powermock.api.easymock.PowerMock.replay;
import static org.powermock.api.easymock.PowerMock.verify;

import java.io.File;

import junit.framework.TestCase;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.tmatesoft.svn.core.SVNURL;

import de.dlr.knowledgefinder.dataimport.utils.datapicker.SNVDataPickerLocal;
import de.dlr.knowledgefinder.dataimport.utils.datapicker.SNVDataPickerRemote;
import de.dlr.knowledgefinder.dataimport.utils.datapicker.SVNDataPicker;
import de.dlr.knowledgefinder.dataimport.utils.datapicker.SVNDataPickerFactory;


/**
 * The Class SVNDataPickerFactoryTest.
 *
 * 
 */
@RunWith(PowerMockRunner.class)
public class SVNDataPickerFactoryTest extends TestCase {
	
	/**
	 * Test create svn data picker local.
	 */
	@PrepareForTest(SNVDataPickerLocal.class)
	@Test
	public void testCreateSVNDataPickerLocal(){
		File f = createMock(File.class);
		SNVDataPickerLocal local = createMock(SNVDataPickerLocal.class);

		mockStatic(SNVDataPickerLocal.class);
		expect(SNVDataPickerLocal.createSVNDataPicker(f)).andReturn(local);
		replay(SNVDataPickerLocal.class);
		
		SVNDataPicker<?> pickerInstance = SVNDataPickerFactory.createSVNDataPicker(f);
		
		verify(SNVDataPickerLocal.class);
		assertTrue(pickerInstance instanceof SNVDataPickerLocal);
	}
	
	/**
	 * Test create svn data picker remote.
	 */
	@PrepareForTest(SNVDataPickerRemote.class)
	@Test
	public void testCreateSVNDataPickerRemote(){
		SVNURL f = createMock(SVNURL.class);
		SNVDataPickerRemote remote = createMock(SNVDataPickerRemote.class);
		String username = "username";
		String password = "password";
		
		mockStatic(SNVDataPickerRemote.class);
		expect(SNVDataPickerRemote.createSVNDataPicker(f, username, password)).andReturn(remote);
		replay(SNVDataPickerRemote.class);
		
		SVNDataPicker<?> pickerInstance = SVNDataPickerFactory.createSVNDataPicker(f, username, password);
		
		verify(SNVDataPickerRemote.class);
		assertTrue(pickerInstance instanceof SNVDataPickerRemote);
	}
}
