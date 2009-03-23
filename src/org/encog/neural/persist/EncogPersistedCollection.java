/*
 * Encog Neural Network and Bot Library for Java v1.x
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 * 
 * Copyright 2008, Heaton Research Inc., and individual contributors.
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
import java.util.List;

/**
 * An EncogPersistedCollection holds a collection of EncogPersistedObjects. This
 * allows the various neural networks and some data sets to be persisted. They
 * are persisted to an XML form.
 * 
 * @author jheaton
 * 
 */
public class EncogPersistedCollection {

	private File filePrimary;
	private File fileTemp;
	private PersistWriter writer;
	
	public EncogPersistedCollection(File file)
	{
		this.filePrimary = file;
		String f = file.getAbsolutePath();
		int index = f.lastIndexOf('.');
		if( index!=-1)
			f = f.substring(0,index);
		f+=".tmp";
		this.fileTemp = new File(f);
	}
	
	
	/**
	 * The platform this collection was created on.
	 */
	private String platform;

	/**
	 * The version of the persisted file.
	 */
	private int fileVersion;

	/**
	 * The version of Encog.
	 */
	private String encogVersion;
	
	
	public void create()
	{

			PersistWriter writer = new PersistWriter(this.filePrimary);
			writer.begin();
			writer.writeHeader();
			writer.beginObjects();
			writer.endObjects();
			writer.end();
			writer.close();

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
		writer.endObjects();
		writer.end();
		writer.close();
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
	 * @return the list
	 */
	public List<EncogPersistedObject> getList() {
		return null;
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
		return null;
	}

	public void delete(String name) {

	}
}
