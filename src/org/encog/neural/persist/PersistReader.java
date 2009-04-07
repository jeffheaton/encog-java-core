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
package org.encog.neural.persist;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.StringTokenizer;

import org.encog.neural.persist.persistors.PersistorUtil;
import org.encog.util.xml.XMLElement;
import org.encog.util.xml.XMLRead;
import org.encog.util.xml.XMLWrite;
import org.encog.util.xml.XMLElement.XMLElementType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PersistReader {

	public final static String ATTRIBUTE_NAME = "name";

	private XMLRead in;
	private InputStream fileInput;
	
	/**
	 * The logging object.
	 */
	@SuppressWarnings("unused")
	final private Logger logger = LoggerFactory.getLogger(this.getClass());

	public PersistReader(String filename) {
		this(new File(filename));
	}

	public PersistReader(File filename) {
		try {
			this.fileInput = new FileInputStream(filename);
			this.in = new XMLRead(this.fileInput);
		} catch (FileNotFoundException e) {
			throw new PersistError(e);
		}
	}

	/**
	 * Advance to the objects collection.
	 */
	public void advanceObjectsCollection()
	{
		XMLElement element;

		while ((element = this.in.get()) != null) {
			XMLElementType type = element.getType();
			if (type == XMLElementType.start
					&& element.getText().equals("Objects")) {
				return;
			}
		}
		
	}
	
	/**
	 * Advance to the specified object.
	 * @param name The name of the object looking for.
	 * @return The beginning element of the object found.
	 */
	public XMLElement advance(String name) {
		advanceObjectsCollection();
		return advanceObjects(name);
	}

	/**
	 * Once you are in the objects collection, advance
	 * to a specific object.
	 * @param name The name of the object to advance to.
	 * @return The beginning tag of that object if its found,
	 * null otherwise.
	 */
	private XMLElement advanceObjects(String name) {
		XMLElement element;

		while ((element = this.in.get()) != null) {
			XMLElementType type = element.getType();
			if (type == XMLElementType.start) {
				String elementName = element.getAttributes().get("name");

				if (elementName != null && elementName.equals(name))
					return element;
				else
					this.in.skipObject(element);
			}
		}

		return null;
	}
	
	/**
	 * Read the specific object, search through the objects
	 * until its found.
	 * @param name The name of the object you are looking for.
	 * @return The object found, null if not found.
	 */
	public EncogPersistedObject readObject(String name) {
		XMLElement element = advance(name);
		// did we find the object?
		if (element != null) {
			String objectType = element.getText();
			Persistor persistor = PersistorUtil.createPersistor(objectType);
			if (persistor == null) {
				throw new PersistError("Do now know how to load: " + objectType);
			}
			return persistor.load(element, this.in);
		} else
			return null;
	}

	public XMLElement readNextTag(String name) {
		XMLElement element;

		while ((element = this.in.get()) != null) {
			XMLElementType type = element.getType();
			if (type == XMLElementType.start) {
				if( element.getText().equals(name))
				{
					return element;
				}
				else
					this.in.skipObject(element);
			}
		}
		return null;
	}
	
	public String readNextText(XMLElement start)
	{
		StringBuilder result = new StringBuilder();
		XMLElement element;

		while ((element = this.in.get()) != null) {
			XMLElementType type = element.getType();
			if (type == XMLElementType.end) {
				if( element.getText().equals(start.getText()))
				{
					break;
				}
			}
			else if( type==XMLElementType.text)
			{
				result.append(element.getText());
			}
		}
		return result.toString();
	}

	public String readValue(String name) {
		XMLElement element = null;
		StringTokenizer tok = new StringTokenizer(name, ".");
		while(tok.hasMoreTokens())
		{
			String subName = tok.nextToken();
			element = readNextTag(subName);
			if( element==null )
				return null;
		}
		
		if( element==null )
			return null;
		else
			return readNextText(element);
	}
	
	/**
	 * Save all objects to the specified steam, skip the one
	 * specified by the skip parameter.  Do not attempt to 
	 * understand the structure, just copy.
	 * @param out The XML writer to save the objects to.
	 * @param skip The object to skip.
	 */
	public void saveTo(XMLWrite out,String skip)
	{
		XMLElement element;
		advanceObjectsCollection();
		while ((element = this.in.get()) != null) {
			XMLElementType type = element.getType();
			if (type == XMLElementType.start)
			{
				String name = element.getAttributes().get(ATTRIBUTE_NAME);
				if( name.equals(skip))
				{
					this.in.skipObject(element);
				}
				else
					copyXML(element,out);
			}
		}
	}
	
	private void copyAttributes(XMLElement object,XMLWrite out)
	{
		for( String key: object.getAttributes().keySet())
		{
			out.addAttribute(key, object.getAttributes().get(key));
		}
	}
	

	private void copyXML(XMLElement object,XMLWrite out) {
		
		int depth = 0;
		copyAttributes(object,out);
		out.beginTag(object.getText());
		
		XMLElement element;
		while ((element = this.in.get()) != null) {
			XMLElementType type = element.getType();
			
			switch(type)
			{
				case start:
					copyAttributes(element,out);
					out.beginTag(element.getText());
					depth++;
					break;
				case end:
					if( !element.getText().equals(object.getText()))
					{
						out.endTag();
					}
					else if( depth==0)
							return;
					depth--;
					break;
				case text:
					out.addText(element.getText());
					break;
			}
		}
		
		out.endTag();		
	}
}
