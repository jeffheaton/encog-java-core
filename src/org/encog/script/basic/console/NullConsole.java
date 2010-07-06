package org.encog.script.basic.console;

public class NullConsole implements ConsoleInputOutput {

	@Override
	public void print(String line) {		
	}

	@Override
	public void printLine(String line) {
	}
	
	public String input(String prompt) {
		return "";
	}

}
