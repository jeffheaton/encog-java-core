package org.encog.script.basic;

import java.util.ArrayList;
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
		
	public List<BasicLine> getSub() {
		return sub;
	}

	public long getNumber() {
		return number;
	}

	public void setNumber(long number) {
		this.number = number;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	private final List<BasicLine> sub = new ArrayList<BasicLine>();
	private long number;
	private String text;
	
}
