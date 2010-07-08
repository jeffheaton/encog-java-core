package org.encog.script.basic;

import java.util.ArrayList;
import java.util.List;


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
