package org.encog.script.basic;

import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.encog.EncogError;
import org.encog.script.EncogScript;
import org.encog.script.basic.console.ConsoleInputOutput;
import org.encog.script.basic.console.NullConsole;


public class BasicProgram implements Basic {

	private boolean quitProgram;// Should the program be quit(END command)

	private BasicParse function;
	private final Map<String,BasicVariable> globals = new HashMap<String,BasicVariable>();
	private boolean m_noMaint;
	private static String m_args;
	
	private RandomAccessFile fileHandles[];
	private long fileAttr[];
	private short reclen[];

	private ConsoleInputOutput console;
	private boolean debugMode;
	private boolean stepMode;
	private Err m_err;
	
	private final List<BasicModule> modules = new ArrayList<BasicModule>();
	private final List<Basic> functions = new ArrayList<Basic>();

		
	public BasicProgram()
	{
		int i;
		this.console = new NullConsole();
		function=null;
		quitProgram=false;
		m_noMaint = false;
		debugMode=false;
		stepMode=false;
	}
	
	public void LoadModule(EncogScript script)
	{
		BasicModule module = new BasicModule(this);
		module.Load(script);
		this.modules.add(module);	
	}
	
	public void AddFunctions(Basic functions)
	{
		this.functions.add(functions);
	}

	public boolean Scan(BasicVariable target)
	{
		for( Basic basic: this.functions )
		{
			if( basic.Scan(target))
			{
				return true;
			}
		}

		return false;
	}
	
	public boolean Update()
	{
		for( Basic basic: this.functions )
		{
			if( basic.Update())
			{
				return true;
			}
		}

		return false;
	}
	
	public boolean Execute()
	{
		for( Basic basic: this.functions )
		{
			if( basic.Execute())
			{
				return true;
			}
		}

		return false;
	}

	public boolean NewObject()
	{
		for( Basic basic: this.functions )
		{
			if( basic.NewObject())
			{
				return true;
			}
		}

		return false;
	}
	
	public BasicVariable CreateObject()
	{
		for( Basic basic: this.functions )
		{
			BasicVariable var;
			
			if( (var=basic.CreateObject()) !=null )
			{
				return var;
			}
		}

		return null;

	}
	
	public void CreateGlobals()
	{
		globals.clear();
		
		// Create the "system" global variables

		for(Basic basic: this.functions )
		{
			basic.CreateGlobals();
		}

		
		// Add in global variables for modules

		for(BasicModule module: this.modules )
		{
			BasicParse fn = new BasicParse(module);
			
			if( !fn.Call() )
			{
				throw new EncogError("Can't execute script");
			}
		
		}
		
		function=null;

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

	public boolean Call(BasicModule module,String name,BasicVariable target)
	{
		BasicVariable v;

		if( module.FindFunction(name)==null )
			return false;

		BasicParse fn = new BasicParse(module);
		if(fn.Call(name))
		{
		// Check for return code(if any)
			v=(BasicVariable)fn.GetVariable(name);
			if(v!=null)
				target.edit(v);
			return true;
		}
		return false;
	}
	
	public boolean Call(String fn,BasicVariable target)
	{
		for(BasicModule module: this.modules)
		{
			if( Call(module,fn,target))
				return true;
		}

		return false;

	}

	public void CloseAllFiles()
	{
		
	}
	public void Maint()
	{
		if(m_noMaint)
			return;

		for(Basic basic: this.functions)
		{
			basic.Maint();
		}
	}
	
	
	
	public Map<String, BasicVariable> getGlobals() {
		return globals;
	}
		

	public void print(String str) {
		this.console.print(str);
		
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
	
	
	
	public void setFunction(BasicParse function) {
		this.function = function;
	}
	
	public ConsoleInputOutput getConsole() {
		return console;
	}

	/**
	 * @param console the console to set
	 */
	public void setConsole(ConsoleInputOutput console) {
		this.console = console;
	}

	public void setQuitProgram(boolean b) {
		this.quitProgram = b;
		
	}
	
}
