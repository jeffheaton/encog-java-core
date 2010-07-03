package org.encog.script;

import java.io.RandomAccessFile;
import java.util.List;

public class BasicProgram extends Basic {

	static BasicProgram instance;
	
	public static BasicProgram getInstance()
	{
		if( instance==null)
			instance = new BasicProgram();
		return instance;
	}
	
	public BasicProgram()
	{
		
	}
	
	public void LoadModule(String str)
	{
		
	}
	
	public void AddFunctions(Basic functions)
	{
		
	}

	public boolean Scan(BasicVariable target)
	{
		return false;
	}
	
	public boolean Update()
	{
		return false;
	}
	
	public boolean Execute()
	{
		return false;
	}

	public boolean NewObject()
	{
		return false;
	}
	
	public BasicVariable CreateObject()
	{
		return null;
	}
	
	public void CreateGlobals()
	{
		
	}
	
	public void Clear()
	{
		modules.clear();
		globals.clear();
	}

	public void Allocate()
	{
		
	}
	public void Free()
	{
		
	}
	
	public void Copy(BasicVariable v)
	{
		
	}

	public boolean Call(BasicModule module,String fn,BasicVariable target)
	{
		return false;
	}
	
	public boolean Call(String filename,BasicVariable target)
	{
		return false;
	}

	public void CloseAllFiles()
	{
		
	}
	public void Maint()
	{
		
	}
	
	//BASIC_LINE *FindFunction(char *label){return program.FindLabel(label);
		boolean quitProgram;// Should the program be quit(END command)

	BasicParse function;
	List globals;
	boolean m_noMaint;
	static String m_args;
	
	RandomAccessFile fileHandles[];
	long fileAttr[];
	short reclen[];

	Console con;
	boolean debugMode;
	boolean stepMode;
	Err m_err;
	
	List modules;
	List functions;

	public void print(String str) {
		System.out.println(str);
		
	}
	
}
