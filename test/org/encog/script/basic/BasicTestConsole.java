package org.encog.script.basic;

import org.encog.script.basic.console.ConsoleInputOutput;

public class BasicTestConsole implements ConsoleInputOutput {

	private StringBuilder result = new StringBuilder();
	
	@Override
	public String input(String prompt) {
		return null;
	}

	@Override
	public void print(String line) {
		while( line.endsWith("\n"))
			line = line.substring(0,line.length()-1);
		result.append(line);
		
	}

	@Override
	public void printLine(String line) {
		while( line.endsWith("\n"))
			line = line.substring(0,line.length()-1);
		result.append(line);
	}
	
	public String toString()
	{
		return result.toString();
	}

}
