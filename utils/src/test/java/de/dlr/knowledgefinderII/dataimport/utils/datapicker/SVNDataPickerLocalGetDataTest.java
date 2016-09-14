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

package de.dlr.knowledgefinderII.dataimport.utils.datapicker;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.tmatesoft.svn.core.SVNDepth;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.wc.SVNInfo;
import org.tmatesoft.svn.core.wc.SVNPropertyData;
import org.tmatesoft.svn.core.wc.SVNRevision;

import de.dlr.knowledgefinderII.dataimport.utils.datapicker.SNVDataPickerLocal;
import de.dlr.knowledgefinderII.dataimport.utils.datapicker.SVNDataPicker;
import de.dlr.knowledgefinderII.dataimport.utils.datapicker.SVNDataPickerException;
import de.dlr.knowledgefinderII.dataimport.utils.datapicker.SVNDataPickerFactory;
import de.dlr.knowledgefinderII.dataimport.utils.parser.SVNParserException;


@RunWith(PowerMockRunner.class)
@PrepareForTest(SNVDataPickerLocal.class)
public class SVNDataPickerLocalGetDataTest extends SVNDataPickerGetDataTest<File>{
	
	private SVNRevision wRevision = SVNRevision.WORKING;
	
	@SuppressWarnings("unchecked")
	public void setUp() throws SVNException, SVNParserException{
		Mockito.when(mockClient.doInfo(Mockito.any(File.class), Mockito.eq(wRevision))).thenReturn(Mockito.mock(SVNInfo.class));
		Mockito.when(mockCrawler.iterator()).thenReturn(results.entrySet().iterator());
		Mockito.when(mockParser.parse(Mockito.any(List.class), Mockito.any(SVNInfo.class))).thenReturn(Mockito.mock(Map.class));

		picker = (SVNDataPicker<File>) SVNDataPickerFactory.createSVNDataPicker(file);
		
		Mockito.doNothing().when(mockClient).doGetProperty(file, null,
				wRevision, wRevision, SVNDepth.INFINITY, mockCrawler, null);

		picker.setSVNWCClient(mockClient);
		picker.setSVNParser(mockParser);
		picker.setSVNPropertyCrawler( mockCrawler);
	}
	
	@Test
	public void testCallDoGetProperty() throws SVNException, SVNDataPickerException{
		picker.getData();
		
		Mockito.verify(mockClient).doGetProperty(file, null, wRevision, 
				wRevision, SVNDepth.INFINITY, mockCrawler, null);
	}
	
	@Test(expected = SVNDataPickerException.class)
	public void testExceptionDoGetProperty() throws SVNDataPickerException, SVNException {
		SVNException exception = Mockito.mock(SVNException.class);
		Mockito.doThrow(exception).when(mockClient).doGetProperty(file, null, wRevision, 
				wRevision, SVNDepth.INFINITY, mockCrawler, null);
		picker.getData();
	}
	
	@Test
	public void testDataResults() throws SVNException, SVNDataPickerException, SVNParserException{
		Collection<Map<String, Object>> valuesPicker = picker.getData();
		assertEquals(valuesPicker.size(), results.size());
		for(File item : results.keySet()){
			List<SVNPropertyData> values = results.get(item);
			Mockito.verify(mockClient, Mockito.times(1)).doInfo(item, wRevision);
			Mockito.verify(mockParser, Mockito.times(1)).parse(Mockito.eq(values), Mockito.any(SVNInfo.class));
		}
	}
	
	@Test
	public void testDataSVNExceptions() throws SVNException, SVNDataPickerException{
		SVNException exception = Mockito.mock(SVNException.class);
		Mockito.doThrow(exception).when(mockClient).doInfo(Mockito.any(File.class), Mockito.eq(wRevision));

		Collection<Map<String, Object>> valuesPicker = picker.getData();
		assertEquals(valuesPicker.size(), 0);
	}
	
	
	@SuppressWarnings("unchecked")
	protected Map<File, List<SVNPropertyData>> createMapResults(){
		Map<File, List<SVNPropertyData>> results = new HashMap<File, List<SVNPropertyData>>();
		
		int items = 10;
		for(int i=0; i<items; i++){
			File file = Mockito.mock(File.class);
			results.put(file, Mockito.mock(List.class));
		}
		return results;
	}
}

