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
import java.io.OutputStream;
import java.util.StringTokenizer;

import org.encog.neural.persist.persistors.PersistorUtil;
import org.encog.util.xml.XMLElement;
import org.encog.util.xml.XMLRead;
import org.encog.util.xml.XMLWrite;
import org.encog.util.xml.XMLElement.XMLElementType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PersistReader {

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

	public XMLElement advance(String name) {
		XMLElement element;

		while ((element = this.in.get()) != null) {
			XMLElementType type = element.getType();
			if (type == XMLElementType.start
					&& element.getText().equals("Objects")) {
				return advanceObjects(name);
			}
		}

		return null;
	}

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

}
