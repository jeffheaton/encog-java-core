package org.encog.script;

import java.util.List;

public class BasicLine extends BasicObject {

	public BasicLine(String t)
	{
		text=t;
		setObjectType(BasicTypes.typeStatement);
	}
	
	public void Edit(String t)
	{
		this.text = t;
	}
		
	public String Command()
	{
		return text;
	}
	
	private List sub;
	private long number;
	private String text;

	
}
