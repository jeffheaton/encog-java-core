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

package org.encog.script.basic;

import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.encog.EncogError;
import org.encog.script.EncogScript;
import org.encog.script.basic.console.ConsoleInputOutput;
import org.encog.script.basic.console.NullConsole;
import org.encog.script.basic.variables.BasicVariable;

/**
 * This is the main class for Encog script.  It executes modules.
 */
public class BasicProgram implements Basic {

	private boolean quitProgram;// Should the program be quit(END command)

	private BasicParse function;
	private final Map<String,BasicVariable> globals = new HashMap<String,BasicVariable>();
	private boolean m_noMaint;
	
	private RandomAccessFile fileHandles[];
	private long fileAttr[];
	private short reclen[];

	private ConsoleInputOutput console;
	
	private final List<BasicModule> modules = new ArrayList<BasicModule>();
	private final List<Basic> functions = new ArrayList<Basic>();

		
	public BasicProgram()
	{
		int i;
		this.console = new NullConsole();
		function=null;
		quitProgram=false;
		m_noMaint = false;
	}
	
	public void loadModule(EncogScript script)
	{
		BasicModule module = new BasicModule(this);
		module.load(script);
		this.modules.add(module);	
	}
	
	public void addFunctions(Basic functions)
	{
		this.functions.add(functions);
	}

	public boolean scan(BasicVariable target)
	{
		for( Basic basic: this.functions )
		{
			if( basic.scan(target))
			{
				return true;
			}
		}

		return false;
	}
	
	public boolean update()
	{
		for( Basic basic: this.functions )
		{
			if( basic.update())
			{
				return true;
			}
		}

		return false;
	}
	
	public boolean execute()
	{
		for( Basic basic: this.functions )
		{
			if( basic.execute())
			{
				return true;
			}
		}

		return false;
	}

	public boolean newObject()
	{
		for( Basic basic: this.functions )
		{
			if( basic.newObject())
			{
				return true;
			}
		}

		return false;
	}
	
	public BasicVariable createObject()
	{
		for( Basic basic: this.functions )
		{
			BasicVariable var;
			
			if( (var=basic.createObject()) !=null )
			{
				return var;
			}
		}

		return null;

	}
	
	public void createGlobals()
	{
		globals.clear();
		
		// Create the "system" global variables

		for(Basic basic: this.functions )
		{
			basic.createGlobals();
		}

		
		// Add in global variables for modules

		for(BasicModule module: this.modules )
		{
			BasicParse fn = new BasicParse(module);
			
			if( !fn.call() )
			{
				throw new EncogError("Can't execute script");
			}
		
		}
		
		function=null;

	}
	
	public void clear()
	{
		modules.clear();
		globals.clear();
	}

	public void allocate()
	{
		
	}
	public void free()
	{
		
	}
	
	public void copy(BasicVariable v)
	{
		
	}

	public boolean call(BasicModule module,String name,BasicVariable target)
	{
		BasicVariable v;

		if( module.findFunction(name)==null )
			return false;

		BasicParse fn = new BasicParse(module);
		if(fn.call(name))
		{
		// Check for return code(if any)
			v=(BasicVariable)fn.getVariable(name);
			if(v!=null)
				target.edit(v);
			return true;
		}
		return false;
	}
	
	public boolean call(String fn,BasicVariable target)
	{
		for(BasicModule module: this.modules)
		{
			if( call(module,fn,target))
				return true;
		}

		return false;

	}

	public void closeAllFiles()
	{
		
	}
	public void maint()
	{
		if(m_noMaint)
			return;

		for(Basic basic: this.functions)
		{
			basic.maint();
		}
	}
	
	
	
	public Map<String, BasicVariable> getGlobals() {
		return globals;
	}
		

	public void print(String str) {
		this.console.print(str);
		
	}

	public List<Basic> getFunctions() {
		return functions;
	}

	public BasicParse getFunction() {
		return this.function;
	}

	public boolean getQuitProgram() {
		return this.quitProgram;
	}
	
	
	
	public void setFunction(BasicParse function) {
		this.function = function;
	}
	
	public ConsoleInputOutput getConsole() {
		return console;
	}

	/**
	 * @param console the console to set
	 */
	public void setConsole(ConsoleInputOutput console) {
		this.console = console;
	}

	public void setQuitProgram(boolean b) {
		this.quitProgram = b;
		
	}
	
}
