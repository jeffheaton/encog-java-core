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
import java.io.IOException;
import java.io.InputStream;
import java.util.StringTokenizer;

import org.encog.neural.persist.persistors.PersistorUtil;
import org.encog.parse.tags.Tag.Type;
import org.encog.parse.tags.read.ReadXML;
import org.encog.parse.tags.write.WriteXML;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PersistReader {

	public final static String ATTRIBUTE_NAME = "name";

	private ReadXML in;
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
			this.in = new ReadXML(this.fileInput);
		} catch (FileNotFoundException e) {
			throw new PersistError(e);
		}
	}
	
	public void close()
	{
		try
		{
		this.fileInput.close();
		}
		catch(IOException e)
		{
			throw new PersistError(e);
		}
		
	}

	/**
	 * Advance to the objects collection.
	 */
	public void advanceObjectsCollection()
	{
		while (this.in.readToTag()) {
			Type type = this.in.getTag().getType();
			if (type == Type.BEGIN
					&& this.in.getTag().getName().equals("Objects")) {
				return;
			}
		}
		
	}
	
	/**
	 * Advance to the specified object.
	 * @param name The name of the object looking for.
	 * @return The beginning element of the object found.
	 */
	public boolean advance(String name) {
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
	private boolean advanceObjects(String name) {
		
		while (this.in.readToTag() ) {
			Type type = this.in.getTag().getType();
			if (type == Type.BEGIN) {
				String elementName = this.in.getTag().getAttributeValue("name");

				if (elementName != null && elementName.equals(name))
					return true;
				else
					skipObject(this.in.getTag().getName());
			}
		}
		return false;
	}
	
	private void skipObject(String name)
	{
		while (this.in.readToTag() ) {
			Type type = this.in.getTag().getType();
			if (type == Type.END) {
				if( this.in.getTag().getName().equals(name))
					return;
			}
		}
	}
	
	/**
	 * Read the specific object, search through the objects
	 * until its found.
	 * @param name The name of the object you are looking for.
	 * @return The object found, null if not found.
	 */
	public EncogPersistedObject readObject(String name) {
		
		// did we find the object?
		if ( advance(name) ) {
			String objectType = this.in.getTag().getName();
			Persistor persistor = PersistorUtil.createPersistor(objectType);
			if (persistor == null) {
				throw new PersistError("Do now know how to load: " + objectType);
			}
			return persistor.load(this.in);
		} else
			return null;
	}

	public boolean readNextTag(String name) {
		

		while (this.in.readToTag() ) {
			Type type = this.in.getTag().getType();
			if (type == Type.BEGIN) {
				if( in.getTag().getName().equals(name))
				{
					return true;
				}
				else
					skipObject(this.in.getTag().getName());
			}
		}
		return false;
	}
	
	public String readNextText(String name)
	{
		StringBuilder result = new StringBuilder();
		return result.toString();
	}

	public String readValue(String name) {

		StringTokenizer tok = new StringTokenizer(name, ".");
		while(tok.hasMoreTokens())
		{
			String subName = tok.nextToken();
			if( !readNextTag(subName) )
				return null;
		}
		
		return readNextText(this.in.getTag().getName());
	}
	
	/**
	 * Save all objects to the specified steam, skip the one
	 * specified by the skip parameter.  Do not attempt to 
	 * understand the structure, just copy.
	 * @param out The XML writer to save the objects to.
	 * @param skip The object to skip.
	 */
	public void saveTo(WriteXML out,String skip)
	{
		advanceObjectsCollection();

		while( this.in.readToTag() )
		{
			Type type = this.in.getTag().getType();
			if (type == Type.BEGIN )
			{
				String name = in.getTag().getAttributeValue(ATTRIBUTE_NAME);
				if( name.equals(skip))
				{
					skipObject(this.in.getTag().getName());
				}
				else
					copyXML(out);
			}
		}
	}
	
	private void copyAttributes(WriteXML out)
	{
		for( String key: this.in.getTag().getAttributes().keySet())
		{
			out.addAttribute(key, this.in.getTag().getAttributeValue(key));
		}		
	}
	

	private void copyXML(WriteXML out) {
		StringBuilder text = new StringBuilder();
		int depth = 0;
		int ch;
		copyAttributes(out);
		String contain = in.getTag().getName(); 
		
		out.beginTag(contain);
		
		while( (ch=this.in.read()) !=-1 ) {
			Type type = in.getTag().getType();
			
			if( ch==0 )
			{
			if( type==Type.BEGIN)
			{
				if( text.length()>0 )
				{
					out.addText(text.toString());
					text.setLength(0);
				}
				
				copyAttributes(out);
				out.beginTag(in.getTag().getName());
				depth++;
			}
			else if( type==Type.END)
			{
				if( text.length()>0 )
				{
					out.addText(text.toString());
					text.setLength(0);
				}
				
				if( !in.getTag().getName().equals(contain))
				{
					out.endTag();
				}
				else if( depth==0)
						return;
				depth--;
			}
			}
			else
			{
				text.append((char)ch);
			}
			
		}
		
		out.endTag();		
	}
}
