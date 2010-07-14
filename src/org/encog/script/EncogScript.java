/*
 * Encog(tm) Core v2.5 
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 * 
 * Copyright 2008-2010 by Heaton Research Inc.
 * 
 * Released under the LGPL.
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
 * 
 * Encog and Heaton Research are Trademarks of Heaton Research, Inc.
 * For information on Heaton Research trademarks, visit:
 * 
 * http://www.heatonresearch.com/copyright.html
 */

package org.encog.script;

import java.io.RandomAccessFile;
import java.util.List;
import java.util.Map;

import org.encog.EncogError;
import org.encog.persist.EncogCollection;
import org.encog.persist.EncogPersistedObject;
import org.encog.persist.Persistor;
import org.encog.persist.persistors.EncogScriptPersistor;
import org.encog.script.basic.Basic;
import org.encog.script.basic.BasicModule;
import org.encog.script.basic.BasicParse;
import org.encog.script.basic.BasicProgram;
import org.encog.script.basic.console.ConsoleInputOutput;
import org.encog.script.basic.console.NullConsole;
import org.encog.script.basic.variables.BasicVariable;

/**
 * Hold one Encog script program.  Can be saved to an Encog collection.
 */
public class EncogScript implements EncogPersistedObject {

	private String name;
	private String description;
	private String source;
	private BasicProgram program;
	private ConsoleInputOutput console = new NullConsole();
	
	/**
	 * The Encog collection this object belongs to, or null if none.
	 */
	private EncogCollection encogCollection;

	
	@Override
	public Persistor createPersistor() {
		return new EncogScriptPersistor();
	}

	@Override
	public String getDescription() {
		return this.description;
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public void setDescription(String description) {
		this.description = description;
		
	}

	@Override
	public void setName(String name) {
		this.name = name;
		
	}

	/**
	 * @return the source
	 */
	public String getSource() {
		return source;
	}

	/**
	 * @param source the source to set
	 */
	public void setSource(String source) {
		this.source = source;
	}
	
	/**
	 * @return The collection this Encog object belongs to, null if none.
	 */
	public EncogCollection getCollection() {
		return this.encogCollection;
	}

	/**
	 * Set the Encog collection that this object belongs to.
	 */
	public void setCollection(EncogCollection collection) {
		this.encogCollection = collection; 
	}
	
	public void load()
	{
		this.program = new BasicProgram();
		this.program.loadModule(this);
	}
	
	
	
	/**
	 * @return the console
	 */
	public ConsoleInputOutput getConsole() {
		return console;
	}

	/**
	 * @param console the console to set
	 */
	public void setConsole(ConsoleInputOutput console) {
		this.console = console;
	}

	public BasicVariable call(String name)
	{
		BasicVariable result = new BasicVariable();
		this.program.setConsole(this.console);
		
		if(!this.program.call(name, result))
		{
			throw new EncogError("Can't find main sub in script.");
		}
		
		return result;
	}
	
	public BasicVariable call()
	{
		return call("MAIN");
	}
}
