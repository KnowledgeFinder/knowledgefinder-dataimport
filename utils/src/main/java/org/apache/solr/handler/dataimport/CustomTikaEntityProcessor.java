/* 
 * CustomTikaEntityProcessor.java
 * 
 * Created: 27.11.2014 Anja Sonneneberg <anja.sonnenberg@dlr.de>
 * 
 */
/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * Based on the class TikaEntityProcessor from the Apache Solr project. Home of the project is http://lucene.apache.org/.
 */
package org.apache.solr.handler.dataimport;

import org.apache.commons.io.IOUtils;
import org.apache.tika.config.TikaConfig;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.parser.html.HtmlMapper;
import org.apache.tika.parser.html.IdentityHtmlMapper;
import org.apache.tika.sax.BodyContentHandler;
import org.apache.tika.sax.ContentHandlerDecorator;
import org.apache.tika.sax.XHTMLContentHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import static org.apache.solr.handler.dataimport.DataImportHandlerException.SEVERE;
import static org.apache.solr.handler.dataimport.DataImportHandlerException.wrapAndThrow;
import static org.apache.solr.handler.dataimport.DataImporter.COLUMN;
import static org.apache.solr.handler.dataimport.XPathEntityProcessor.URL;

import org.apache.solr.handler.dataimport.TikaEntityProcessor;

public class CustomTikaEntityProcessor extends TikaEntityProcessor {
    protected TikaConfig tikaConfig;
    private static final Logger LOG = LoggerFactory.getLogger(CustomTikaEntityProcessor.class);
    protected String format = "text";
    private boolean done = false;
    protected String parser;
    static final String AUTO_PARSER = "org.apache.tika.parser.AutoDetectParser";
    protected String htmlMapper;

    @Override
    protected void firstInit(Context context) {
        try {
            String tikaConfigFile = context.getResolvedEntityAttribute("tikaConfig");
            if (tikaConfigFile == null) {
                ClassLoader classLoader = context.getSolrCore().getResourceLoader().getClassLoader();
                tikaConfig = new TikaConfig(classLoader);
            } else {
                File configFile = new File(tikaConfigFile);
                if (!configFile.isAbsolute()) {
                    configFile = new File(context.getSolrCore().getResourceLoader().getConfigDir(), tikaConfigFile);
                }
                tikaConfig = new TikaConfig(configFile);
            }
        } catch (Exception e) {
            wrapAndThrow(SEVERE, e, "Unable to load Tika Config");
        }

        format = context.getResolvedEntityAttribute("format");
        if (format == null)
            format = "text";
        if (!"html".equals(format) && !"xml".equals(format) && !"text".equals(format) && !"none".equals(format))
            throw new DataImportHandlerException(SEVERE, "'format' can be one of text|html|xml|none");

        htmlMapper = context.getResolvedEntityAttribute("htmlMapper");
        if (htmlMapper == null)
            htmlMapper = "default";
        if (!"default".equals(htmlMapper) && !"identity".equals(htmlMapper))
            throw new DataImportHandlerException(SEVERE, "'htmlMapper', if present, must be 'default' or 'identity'");

        parser = context.getResolvedEntityAttribute("parser");
        if (parser == null) {
            parser = AUTO_PARSER;
        }
        done = false;
    }

    @Override
    public Map<String, Object> nextRow() {
        if (done)
            return null;
        Map<String, Object> row = new HashMap<>();
        String filePath = context.getResolvedEntityAttribute(URL);
        
        /*
         * Changed from original source
         * Required for later change
         */
        @SuppressWarnings("unchecked")
        DataSource<InputStream> dataSource = context.getDataSource();
        
        /*
         * Changed from original source
         * When dataSource is an InputStreamReader, create a new InputStream to handle this
         * 
         */
        InputStream is = null;
        if(InputStream.class.isInstance(dataSource)){
        	is = dataSource.getData(filePath);
        }
        else{
        	try {
				is = new FileInputStream(new File(filePath));
			} catch (FileNotFoundException e) {
				LOG.warn("Unable to create InputStream of " + filePath);
			}
        }
       
        ContentHandler contentHandler = null;
        Metadata metadata = new Metadata();
        
        /*
         * Changed from original source
         * metadata is not able to determine the PDF fileformat without the filepath
         * see also: http://stackoverflow.com/questions/5507565/extracting-text-from-documents-of-unknown-content-type
         */
        metadata.set(Metadata.RESOURCE_NAME_KEY, filePath);
        
        
        
        StringWriter sw = new StringWriter();
        try {
            if ("html".equals(format)) {
                contentHandler = getHtmlHandler(sw);
            } else if ("xml".equals(format)) {
                contentHandler = getXmlContentHandler(sw);
            } else if ("text".equals(format)) {
                contentHandler = getTextContentHandler(sw);
            } else if ("none".equals(format)) {
                contentHandler = new DefaultHandler();
            }
        } catch (TransformerConfigurationException e) {
            wrapAndThrow(SEVERE, e, "Unable to create content handler");
        }
        Parser tikaParser = null;
        if (parser.equals(AUTO_PARSER)) {
            tikaParser = new AutoDetectParser(tikaConfig);
        } else {
            tikaParser = context.getSolrCore().getResourceLoader().newInstance(parser, Parser.class);
        }        
        
        try {
            ParseContext context = new ParseContext();//here
            /*
             * Changed from original source
             * makes it possible to index the content files contained in zip files
             * see also: https://issues.apache.org/jira/browse/SOLR-2332 and https://issues.apache.org/jira/secure/attachment/12469108/SOLR-2332.patch
             */
            context.set(Parser.class, tikaParser);
            if ("identity".equals(htmlMapper)) {
                context.set(HtmlMapper.class, IdentityHtmlMapper.INSTANCE);
            }
            tikaParser.parse(is, contentHandler, metadata, context);
        } catch (Exception e) {
            /*
             * Changed from original source
             * print to log that file can't be read, instead of throwing error and stopping indexing
             */
            //wrapAndThrow(SEVERE, e, "Unable to read content");
            LOG.warn("Unable to read content of " + filePath);
        }
        IOUtils.closeQuietly(is);
        for (Map<String, String> field : context.getAllEntityFields()) {
            if (!"true".equals(field.get("meta")))
                continue;
            String col = field.get(COLUMN);
            String s = metadata.get(col);
            if (s != null)
                row.put(col, s);
        }
        if (!"none".equals(format))
            row.put("text", sw.toString());
        done = true;
        return row;
    }

    private static ContentHandler getHtmlHandler(Writer writer) throws TransformerConfigurationException {
        SAXTransformerFactory factory = (SAXTransformerFactory) TransformerFactory.newInstance();
        TransformerHandler handler = factory.newTransformerHandler();
        handler.getTransformer().setOutputProperty(OutputKeys.METHOD, "html");
        handler.setResult(new StreamResult(writer));
        return new ContentHandlerDecorator(handler) {
            @Override
            public void startElement(String uri, String localName, String name, Attributes atts) throws SAXException {
                if (XHTMLContentHandler.XHTML.equals(uri)) {
                    uri = null;
                }
                if (!"head".equals(localName)) {
                    super.startElement(uri, localName, name, atts);
                }
            }

            @Override
            public void endElement(String uri, String localName, String name) throws SAXException {
                if (XHTMLContentHandler.XHTML.equals(uri)) {
                    uri = null;
                }
                if (!"head".equals(localName)) {
                    super.endElement(uri, localName, name);
                }
            }

            @Override
            public void startPrefixMapping(String prefix, String uri) {/* no op */
            }

            @Override
            public void endPrefixMapping(String prefix) {/* no op */
            }
        };
    }

    private static ContentHandler getTextContentHandler(Writer writer) {
        return new BodyContentHandler(writer);
    }

    private static ContentHandler getXmlContentHandler(Writer writer) throws TransformerConfigurationException {
        SAXTransformerFactory factory = (SAXTransformerFactory) TransformerFactory.newInstance();
        TransformerHandler handler = factory.newTransformerHandler();
        handler.getTransformer().setOutputProperty(OutputKeys.METHOD, "xml");
        handler.setResult(new StreamResult(writer));
        return handler;
    }

}