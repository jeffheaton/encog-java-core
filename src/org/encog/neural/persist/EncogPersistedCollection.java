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
import java.util.ArrayList;
import java.util.List;

import org.encog.neural.networks.BasicNetwork;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An EncogPersistedCollection holds a collection of EncogPersistedObjects. This
 * allows the various neural networks and some data sets to be persisted. They
 * are persisted to an XML form.
 * 
 * @author jheaton
 * 
 */
public class EncogPersistedCollection {

	public final static String GENERAL_ERROR = "Malformed XML near tag: ";
	
	public static final String TYPE_TEXT = "TextData";
	public static final String TYPE_PROPERTY = "PropertyData";
	public static final String TYPE_BASIC_NET = "BasicNetwork";
	public static final String TYPE_BASIC_LAYER = "BasicLayer";
	public static final String TYPE_TRAINING = "TrainingData";
	public static final String TYPE_WEIGHTED_SYNAPSE = "WeightedSynapse"; 
	
	public final static String ATTRIBUTE_NAME = "name";
	public final static String ATTRIBUTE_DESCRIPTION = "description";
	
	private File filePrimary;
	private File fileTemp;
	private PersistWriter writer;
	
	/**
	 * The logging object.
	 */
	@SuppressWarnings("unused")
	final private static Logger logger = LoggerFactory.getLogger(EncogPersistedCollection.class);
	
	public EncogPersistedCollection(String filename)
	{
		this(new File(filename));
	}
	
	public EncogPersistedCollection(File file)
	{
		this.filePrimary = file;
		String f = file.getAbsolutePath();
		int index = f.lastIndexOf('.');
		if( index!=-1)
			f = f.substring(0,index);
		f+=".tmp";
		this.fileTemp = new File(f);
		
		if( this.filePrimary.exists())
			buildDirectory();
		else
			create();
	}
	
	
	/**
	 * The platform this collection was created on.
	 */
	private String platform;

	/**
	 * The version of the persisted file.
	 */
	private int fileVersion;
	
	private final List<DirectoryEntry> directory = new ArrayList<DirectoryEntry>();

	/**
	 * The version of Encog.
	 */
	private String encogVersion;
	
	public void buildDirectory()
	{
		PersistReader reader = new PersistReader(this.filePrimary);
		List<DirectoryEntry> d = reader.buildDirectory();
		this.directory.clear();
		this.directory.addAll(d);
		reader.close();
	}
	
	public void create()
	{
			PersistWriter writer = new PersistWriter(this.filePrimary);
			writer.begin();
			writer.writeHeader();
			writer.beginObjects();
			writer.endObjects();
			writer.end();
			writer.close();
			
			this.directory.clear();
	}
	

	/**
	 * Add an EncogPersistedObject to the collection.
	 * 
	 * @param obj
	 *            The object to add.
	 */
	public void add(final String name, final EncogPersistedObject obj) {
		obj.setName(name);
		PersistWriter writer = new PersistWriter(this.fileTemp);
		writer.begin();
		writer.writeHeader();
		writer.beginObjects();		
		writer.writeObject(obj);
		writer.mergeObjects(this.filePrimary, name);
		writer.endObjects();
		writer.end();
		writer.close();
		mergeTemp();
		this.buildDirectory();
	}

	/**
	 * Clear the collection.
	 */
	public void clear() {

	}

	/**
	 * @return the encogVersion
	 */
	public String getEncogVersion() {
		return this.encogVersion;
	}

	/**
	 * @return the fileVersion
	 */
	public int getFileVersion() {
		return this.fileVersion;
	}


	/**
	 * @return the platform
	 */
	public String getPlatform() {
		return this.platform;
	}



	
	/**
	 * Called to search all Encog objects in this collection for one with a name
	 * that passes what was passed in.
	 * 
	 * @param name
	 *            The name we are searching for.
	 * @return The Encog object with the correct name.
	 */
	public EncogPersistedObject find(String name) {
				
		PersistReader reader = new PersistReader(this.filePrimary);
		EncogPersistedObject result = reader.readObject(name);
		reader.close();
		return result;
	}

	public void delete(String name) {
		PersistWriter writer = new PersistWriter(this.fileTemp);
		writer.begin();
		writer.writeHeader();
		writer.beginObjects();		
		writer.mergeObjects(this.filePrimary, name);
		writer.endObjects();
		writer.end();
		writer.close();
		mergeTemp();
		for(DirectoryEntry d: this.directory)
		{
			if(d.getName().equals(name))
			{
				this.directory.remove(d);
				break;
			}
		}
	}
	
	public void delete(EncogPersistedObject obj)
	{
		delete(obj.getName());
	}
	
	public void mergeTemp()
	{				
		if( !this.filePrimary.delete() )
		{
			String str = "Failure during merge, can't delete:\n"+this.filePrimary;
			if( logger.isErrorEnabled() )
			{
				logger.error(str);
			}
			throw new PersistError(str);
		}
		
		if( !this.fileTemp.renameTo(this.filePrimary) )
		{
			String str = "Failure during merge, can't rename:\n"+this.fileTemp
					+ "to: " + filePrimary;
			
			if( logger.isErrorEnabled() )
			{
				logger.error(str);
			}
			throw new PersistError(str);
		}
	}
	
	public static void throwError(String tag)
	{
		String str = GENERAL_ERROR + tag;
		if( logger.isErrorEnabled() )
		{
			logger.error(str);
		}
		throw new PersistError(str);
	}

	public List<DirectoryEntry> getDirectory() {
		return directory;
	}

	public EncogPersistedObject find(DirectoryEntry d) {
		return find(d.getName());
	}

	public void delete(DirectoryEntry d) {
		this.delete(d.getName());
		
	}

	public void updateProperties(String name, String newName, String newDesc) {
		PersistWriter writer = new PersistWriter(this.fileTemp);
		writer.begin();
		writer.writeHeader();
		writer.beginObjects();		
		writer.modifyObject(this.filePrimary, name, newName, newDesc);
		writer.endObjects();
		writer.end();
		writer.close();
		mergeTemp();
		this.buildDirectory();
		
	}
	
	
}
