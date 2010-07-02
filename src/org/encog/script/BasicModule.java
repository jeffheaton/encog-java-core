package org.encog.script;

import java.io.InputStream;
import java.util.List;

public class BasicModule extends BasicObject {
	
	BasicModule()
	{
		name = "<NONE>";
	}

	void Clear()
	{
		program.clear();
	}
	
	void Load(String name)
	{
		
	}
	
	BasicLine Go(String label)
	{
		return null;
	}
	
	void AddLine(String str,long l)
	{
		
	}
	
	BasicLine FindFunction(String label)
	{
		return null;
	}
	
	boolean GetLine(InputStream fp,String str)
	{
		return false;
	}
	
	

	public List getProgram() {
		return program;
	}

	public String getName() {
		return name;
	}



	private List program;// Linked list of the program
	private List addto;
	
	private String name;
}
