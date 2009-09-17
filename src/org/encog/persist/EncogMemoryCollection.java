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
package org.encog.persist;

import java.util.HashMap;
import java.util.Map;
import org.encog.parse.tags.read.ReadXML;
import org.encog.persist.location.PersistenceLocation;
import org.encog.persist.persistors.PersistorUtil;

/**
 * A memory based collection of Encog objects.  Does not use the more complex temp
 * file structure like EncogPersistedCollection, but also can't handle gigantic files.
 * This class can load and save from/to Encog EG files.
 */
public class EncogMemoryCollection {

	/**
	 * The contents of this collection.
	 */
	private final Map<String,EncogPersistedObject> contents = new HashMap<String,EncogPersistedObject>();

	/**
	 * Populated during a load, the file version.
	 */
	private int fileVersion;
	
	/**
	 * Populated during a load, the Encog version that created this file.
	 */
	private String encogVersion;
	
	/**
	 * Populated during a load, the platform that this was loaded to.
	 */
	private String platform;
	
	/**
	 * Load the contents of a location.
	 * @param location The location to load from.
	 */
	public void load(PersistenceLocation location)
	{
		PersistReader reader = null;
		
		try {
			reader = new PersistReader(location);
			Map<String,String> header = reader.readHeader();
			if( header!=null )
			{
				this.fileVersion = Integer.parseInt(header.get("fileVersion"));
				this.encogVersion = header.get("encogVersion");
				this.platform = header.get("platform");
			}
			reader.advanceObjectsCollection();
			ReadXML in = reader.getXMLInput();

			while (in.readToTag()) {
				if (in.is(PersistReader.TAG_OBJECTS, false)) {
					break;
				}			

				final String type = in.getTag().getName();
				final String name = in.getTag().getAttributeValue("name");

				final Persistor persistor = PersistorUtil
						.createPersistor(type);
				
				if (persistor == null) {
					throw new PersistError("Do not know how to load: " + type);
				}
				EncogPersistedObject obj = persistor.load(in);
				this.contents.put(name,obj);
			}		
		}
		finally
		{
			if( reader!=null ) {
				reader.close();	
			}
		}

	}
	
	/**
	 * Save the contents of this collection to a location. 
	 * @param location The location to save to.
	 */
	public void save(PersistenceLocation location)
	{
		PersistWriter writer = null;
		
		writer = new PersistWriter(location);
		try
		{
			writer.begin();
			writer.writeHeader();
			writer.beginObjects();
			for(EncogPersistedObject obj: this.contents.values())
			{
				writer.writeObject(obj);
			}
			writer.endObjects();
		}
		finally
		{
			writer.end();
			writer.close();
		}
	}

	/**
	 * @return The objects that were loaded from this file.
	 */
	public Map<String, EncogPersistedObject> getContents() {
		return contents;
	}
		
	/**
	 * @return The Encog file version.
	 */
	public int getFileVersion() {
		return fileVersion;
	}

	/**
	 * @return The version of Encog that this file was created with.
	 */
	public String getEncogVersion() {
		return encogVersion;
	}

	/**
	 * @return The platform that this file was created on.
	 */
	public String getPlatform() {
		return platform;
	}
}
