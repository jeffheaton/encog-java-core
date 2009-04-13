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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;

import org.encog.Encog;
import org.encog.parse.tags.write.WriteXML;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PersistWriter {
	
	private WriteXML out;
	private OutputStream fileOutput;
	
	/**
	 * The logging object.
	 */
	@SuppressWarnings("unused")
	final private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	public PersistWriter(File filename)
	{
		try {
			this.fileOutput = new FileOutputStream(filename);
			this.out = new WriteXML(this.fileOutput);
		} catch (FileNotFoundException e) {
			throw new PersistError(e);
		}		
	}
	
	public void writeHeader()
	{
		this.out.beginTag("Header");
		this.out.addProperty("platform", "Java");
		this.out.addProperty("fileVersion", Encog.getInstance().getProperties().get(Encog.ENCOG_FILE_VERSION));
		this.out.addProperty("encogVersion", Encog.getInstance().getProperties().get(Encog.ENCOG_VERSION));
		this.out.addProperty("modified", (new Date()).toString());
		this.out.endTag();
	}
	
	public void begin()
	{
		this.out.beginDocument();
		this.out.beginTag("Document");
	}
	
	public void end()
	{
		this.out.endTag();
		this.out.endDocument();
	}
	
	public void beginObjects()
	{
		this.out.beginTag("Objects");
	}
	
	public void endObjects()
	{
		this.out.endTag();
	}

	public void close() {
		this.out.close();
	}
	
	public void mergeObjects(File filename,String skip)
	{
		PersistReader reader = new PersistReader(filename);
		reader.saveTo(this.out,skip);
		reader.close();
	}

	public void writeObject(EncogPersistedObject obj) {
		Persistor persistor = obj.createPersistor();
		if( persistor==null )
		{
			String str = "Can't find a persistor for object of type " + obj.getClass().getName();
			if( logger.isErrorEnabled())
			{
				logger.error(str);
			}
			throw new PersistError(str);
		}
		persistor.save(obj, this.out);
	}

	public void modifyObject(File filename, String name, String newName,
			String newDesc) {
		
		PersistReader reader = new PersistReader(filename);
		reader.saveModified(this.out,name,newName,newDesc);
		reader.close();
		
	}
	
}
