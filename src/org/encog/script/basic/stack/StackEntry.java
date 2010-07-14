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

package org.encog.script.basic.stack;

import org.encog.script.basic.BasicLine;
import org.encog.script.basic.variables.BasicVariable;

/**
 * One entry on the stack.
 *
 */
public class StackEntry {
	
	public StackEntry(StackEntryType type, BasicLine line)
	{
		this.type = type;
		this.line = line;
		this.start = 0;
		this.stop = 0;
		this.step = 0;
		this.variable = null;
	}
	
	public StackEntry(StackEntryType type, BasicLine line, int start)
	{
		this.type = type;
		this.line = line;
		this.start = start;
		this.stop = 0;
		this.step = 0;
		this.variable = null;
	}
	
	public StackEntry(StackEntryType type, BasicLine line, BasicVariable variable, int start, int stop, int step)
	{
		this.type = type;
		this.line = line;
		this.start = start;
		this.stop = stop;
		this.step = step;
		this.variable = variable;
	}
	
	public StackEntryType getType() {
		return type;
	}
	public BasicLine getLine() {
		return line;
	}
	
	

	public int getStart() {
		return start;
	}

	public int getStop() {
		return stop;
	}

	public int getStep() {
		return step;
	}

	

	private final BasicVariable variable;
	private final StackEntryType type;
	private final BasicLine line;
	private final int start;
	private final int stop;
	private final int step;
	/**
	 * @return the variable
	 */
	public BasicVariable getVariable() {
		return variable;
	}
}
