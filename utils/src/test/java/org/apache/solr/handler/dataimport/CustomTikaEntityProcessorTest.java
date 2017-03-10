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
package org.apache.solr.handler.dataimport;

import static org.easymock.EasyMock.*;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import org.apache.solr.core.SolrCore;
import org.apache.solr.core.SolrResourceLoader;
import org.apache.tika.config.TikaConfig;
import org.apache.tika.exception.TikaException;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.xml.sax.SAXException;

@RunWith(PowerMockRunner.class)
public class CustomTikaEntityProcessorTest {

	@Test
	@PrepareForTest( { SolrCore.class })
	@Ignore(value="The constructor of the TikaConfig have to be catched")
	public void testFirstInit() throws TikaException, IOException, SAXException {
		Context c = PowerMock.createMock( Context.class);
		SolrCore solrCore = PowerMock.createMock(SolrCore.class);
		SolrResourceLoader loader = PowerMock.createMock(SolrResourceLoader.class);
		
		expect(c.getResolvedEntityAttribute("tikaConfig")).andReturn("someFile");
		expect(c.getSolrCore()).andReturn(solrCore);
		expect(c.getResolvedEntityAttribute("format")).andReturn("text");
		expect(c.getResolvedEntityAttribute("htmlMapper")).andReturn("default");
		expect(c.getResolvedEntityAttribute("parser")).andReturn("someParser");
		
		expect(solrCore.getResourceLoader()).andReturn(loader);
		
		expect(loader.getConfigDir()).andReturn("/some/path/");
		
		PowerMock.replay(c);
		PowerMock.replay(solrCore);
		PowerMock.replay(loader);
		
		CustomTikaEntityProcessor processor = new CustomTikaEntityProcessor();
		
		processor.firstInit(c);
		
		assertThat(processor.tikaConfig, is(new TikaConfig(new File("/some/path/", "someFile"))));
		assertThat(processor.format, is("text"));
		assertThat(processor.htmlMapper, is("default"));
		assertThat(processor.parser, is("someParser"));
	}

}
