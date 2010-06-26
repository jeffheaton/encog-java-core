package org.encog.script;

import java.io.RandomAccessFile;
import java.util.List;

public class BasicProgram extends Basic {

	BasicProgram()
	{
		
	}
	
	void LoadModule(String str)
	{
		
	}
	
	void AddFunctions(Basic functions)
	{
		
	}

	boolean Scan(BasicVariable target)
	{
		return false;
	}
	boolean Update()
	{
		return false;
	}
	boolean Execute()
	{
		return false;
	}

	boolean NewObject()
	{
		return false;
	}
	
	BasicVariable CreateObject()
	{
		return null;
	}
	
	void CreateGlobals()
	{
		
	}
	void Clear()
	{
		modules.clear();
		globals.clear();
	}

	void Allocate()
	{
		
	}
	void Free()
	{
		
	}
	
	void Copy(BasicVariable v)
	{
		
	}

	boolean Call(BasicModule module,String fn,BasicVariable target)
	{
		return false;
	}
	
	boolean Call(String filename,BasicVariable target)
	{
		return false;
	}

	void CloseAllFiles()
	{
		
	}
	void Maint()
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
	
}
