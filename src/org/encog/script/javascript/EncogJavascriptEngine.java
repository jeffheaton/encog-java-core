package org.encog.script.javascript;

import java.io.PrintWriter;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;

import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.encog.persist.EncogMemoryCollection;
import org.encog.script.ConsoleInputOutput;
import org.encog.script.EncogScript;
import org.encog.script.EncogScriptEngine;
import org.encog.script.EncogScriptError;
import org.encog.script.EncogScriptRuntimeError;
import org.encog.script.javascript.objects.JSEncogCollection;
import org.encog.script.javascript.objects.JSEncogConsole;
import org.encog.script.javascript.objects.JSNeuralNetwork;
import org.encog.script.javascript.objects.JSTrainer;
import org.encog.script.javascript.objects.JSTrainingData;

import sun.org.mozilla.javascript.internal.Context;
import sun.org.mozilla.javascript.internal.EcmaError;
import sun.org.mozilla.javascript.internal.EvaluatorException;
import sun.org.mozilla.javascript.internal.Scriptable;
import sun.org.mozilla.javascript.internal.ScriptableObject;

public class EncogJavascriptEngine implements EncogScriptEngine {

	private ScriptEngineManager mgr;
	private ScriptEngine engine;
	private ConsoleInputOutput externalConsole;
	private EncogMemoryCollection currentCollection;
	
	private JSEncogConsole console;
	private JSEncogCollection current;
	
	public EncogJavascriptEngine(EncogMemoryCollection collection)
	{
		this.currentCollection = collection;
		this.mgr = new ScriptEngineManager();
		this.engine = mgr.getEngineByName("JavaScript");
	}
	
	public EncogJavascriptEngine() {
		this(null);
	}

	public void setConsole(ConsoleInputOutput console)
	{
		this.externalConsole = console;
	}
	
	public void run(EncogScript script)
	{
		Context cx = Context.enter();
        try {
        	
        	Scriptable scope = cx.initStandardObjects();
        	
        	
        	ScriptableObject.defineClass(scope, JSTrainingData.class);
        	ScriptableObject.defineClass(scope, JSNeuralNetwork.class);
        	ScriptableObject.defineClass(scope, JSTrainer.class);
        	ScriptableObject.defineClass(scope, JSEncogCollection.class);
        	//ScriptableObject.defineClass(scope, EncogJavascriptConsole.class);
        	        	
        	this.console = new JSEncogConsole();
        	this.console.setConsole(this.externalConsole);
        	Object con2 = Context.javaToJS(this.console, scope);
        	ScriptableObject.putProperty(scope, "console", con2);
        	
        	this.current = (JSEncogCollection)cx.newObject(scope,"EncogCollection");
        	this.current.setCollection(this.currentCollection);
        	ScriptableObject.putProperty(scope, "current",this.current);
  
        	cx.evaluateString(scope, script.getSource(), script.getName(), 1, null);
  
        } catch(EvaluatorException e) {
        	throw new EncogScriptRuntimeError(e);
        } catch(EcmaError e) {
        	throw new EncogScriptRuntimeError(e);
        } catch (IllegalAccessException e) {
			throw new EncogScriptError(e);
		} catch (InstantiationException e) {
			throw new EncogScriptError(e);
		} catch (InvocationTargetException e) {
			throw new EncogScriptError(e);
		} 
        finally {
        	Context.exit();
        }
	}
}
