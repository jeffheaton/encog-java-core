package org.encog.script.javascript;

import java.io.Writer;

import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.encog.script.ConsoleInputOutput;
import org.encog.script.EncogScript;
import org.encog.script.EncogScriptEngine;

public class EncogJavascriptEngine implements EncogScriptEngine {

	private ScriptEngineManager mgr;
	private ScriptEngine engine;
	private ConsoleInputOutput console;
	
	public EncogJavascriptEngine()
	{
		this.mgr = new ScriptEngineManager();
		this.engine = mgr.getEngineByName("JavaScript");
	}
	
	public void run(EncogScript script)
	{
        try {
            engine.put("name", "test");
            engine.getContext().setWriter(null);
            engine.eval(script.getSource());
        } catch (ScriptException ex) {
            ex.printStackTrace();
        } 
	}
	
	
}
