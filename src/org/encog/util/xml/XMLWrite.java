/*
 * Encog Artificial Intelligence Framework v2.x
 * Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 * 
 * Copyright 2008-2009, Heaton Research Inc., and individual contributors.
 * See the copyright.txt in the distribution for a full listing of 
 * individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.encog.util.xml;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;

import org.encog.EncogError;
import org.encog.neural.NeuralNetworkError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

public class XMLWrite {
	
	private final OutputStream output;
	private final TransformerHandler outputXML;
	private final AttributesImpl attributes;
	private final Stack<String> tagStack;
	
	/**
	 * The logging object.
	 */
	@SuppressWarnings("unused")
	final private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	public XMLWrite(final OutputStream os)
	{
		try {
			this.output = os;
			final StreamResult streamResult = new StreamResult(os);
			final SAXTransformerFactory tf = 
				(SAXTransformerFactory) TransformerFactory
					.newInstance();
			// SAX2.0 ContentHandler.
			this.outputXML = tf.newTransformerHandler();
			final Transformer serializer = this.outputXML.getTransformer();
			serializer.setOutputProperty(OutputKeys.ENCODING, "ISO-8859-1");

			serializer.setOutputProperty(OutputKeys.INDENT, "yes");
			this.outputXML.setResult(streamResult);
			
			this.attributes = new AttributesImpl();
			this.tagStack = new Stack<String>();

		} catch (final TransformerConfigurationException e) {
			if( logger.isErrorEnabled())
			{
				logger.error("Exception",e);
			}
			throw new EncogError(e);
		} 
	}
	
	public void close()
	{
		try {
			this.output.close();
		} catch (IOException e) {
			throw new EncogError(e);
		}
	}
	
	public void addAttribute(String name, String value)
	{
		this.attributes.addAttribute("", "", name, "CDATA", value);
	}
	
	public void beginTag(String name)
	{
		try {
			this.outputXML.startElement("", "", name, this.attributes);
			this.tagStack.push(name);
			this.attributes.clear();
		} catch (SAXException e) {
			throw new EncogError(e);
		}		
	}
	
	public void endTag()
	{
		try {
			if( this.tagStack.isEmpty() )
			{
				throw new NeuralNetworkError("Can't create end tag, no beginning tag.");
			}
			String tag = this.tagStack.pop();
			this.outputXML.endElement("", "", tag);
			this.attributes.clear();
		} catch (SAXException e) {
			throw new EncogError(e);
		}
	}
	
	public void beginDocument()
	{
		try {
			this.outputXML.startDocument();
		} catch (SAXException e) {
			throw new EncogError(e);
		}
	}
	public void endDocument()
	{
		try {
			this.outputXML.endDocument();
		} catch (SAXException e) {
			throw new EncogError(e);
		}
	}
	
	public void addText(String text)
	{
		try {
			this.outputXML.characters(text.toCharArray(), 0, text.length());
		} catch (SAXException e) {
			throw new EncogError(e);
		}
	}
	
	public void addCDATA(String text)
	{
		try {
			this.outputXML.startCDATA();
			this.addText(text);
			this.outputXML.endCDATA();
		} catch (SAXException e) {
			throw(new EncogError(e));
		}
		
	}
	
	public void addProperty(String name, String value)
	{
		beginTag(name);
		addText(value);
		endTag();
	}

	public void addProperty(String name, int i) {
		addProperty(name,""+i);
		
	}
}
