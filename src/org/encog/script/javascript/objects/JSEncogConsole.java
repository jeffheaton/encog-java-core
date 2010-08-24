package org.encog.script.javascript.objects;

import org.encog.script.ConsoleInputOutput;

import sun.org.mozilla.javascript.internal.ScriptableObject;

public class JSEncogConsole {

	private ConsoleInputOutput console;
	
	public JSEncogConsole()
	{
	}
	
	public void print(Object str)
	{
		this.console.print(str.toString());
	}
	
	public void println(Object str)
	{
		this.console.printLine(str.toString());
	}

	public void setConsole(ConsoleInputOutput c) {
		this.console = c;
	}
	
	public void test(int x)
	{
		System.out.println(x);
	}
}
