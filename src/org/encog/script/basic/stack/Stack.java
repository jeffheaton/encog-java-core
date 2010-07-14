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

import java.util.ArrayList;
import java.util.List;

/**
 * A stack to allow methods to call each other.
 * Also used for looping operations.
 */
public class Stack {

	
	public void clear()
	{
		this.stack.clear();
	}
	
	public void push(StackEntry entry)
	{
		this.stack.add(entry);
	}
	
	public StackEntry pop()
	{
		StackEntry result = this.stack.get(0);
		this.stack.remove(0);
		return result;
	}
	
	public boolean empty()
	{
		return this.stack.size()==0;
	}

	public StackEntry peek()
	{
		if( stack.size()==0 )
			return null;
		else
			return this.stack.get(0);
	}

	
	public StackEntryType peekType()
	{
		if( stack.size()==0 )
			return StackEntryType.stackError;
		else
			return this.stack.get(0).getType();
	}

	private List<StackEntry> stack = new ArrayList<StackEntry>();
}
