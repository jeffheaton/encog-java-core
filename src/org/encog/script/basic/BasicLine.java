package org.encog.script.basic;

import java.util.ArrayList;
import java.util.List;

public class BasicLine {

	public BasicLine(String t)
	{
		text=t;
	}
	
	public void edit(String t)
	{
		this.text = t;
	}
		
	public String getText()
	{
		return text;
	}
		
	public List<BasicLine> getSub() {
		return sub;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public void setText(String text) {
		this.text = text;
	}
	
	public String toString()
	{
		return this.text;
	}

	private final List<BasicLine> sub = new ArrayList<BasicLine>();
	private int number;
	private String text;
	
}
