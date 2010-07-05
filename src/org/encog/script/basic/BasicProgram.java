package org.encog.script.basic;

import java.io.RandomAccessFile;
import java.util.List;
import java.util.Map;


public class BasicProgram implements Basic {

	static BasicProgram instance;
	
	public static BasicProgram getInstance()
	{
		if( instance==null)
			instance = new BasicProgram();
		return instance;
	}
	
	public BasicProgram()
	{
		int i;

		function=null;
		quitProgram=false;
		m_noMaint = false;
		debugMode=false;
		stepMode=false;
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
	
	
	
	public Map<String, BasicVariable> getGlobals() {
		return globals;
	}



		//BASIC_LINE *FindFunction(char *label){return program.FindLabel(label);
		boolean quitProgram;// Should the program be quit(END command)

	BasicParse function;
	Map<String,BasicVariable> globals;
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
	List<Basic> functions;

	public void print(String str) {
		System.out.println(str);
		
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
	
	
	
}
