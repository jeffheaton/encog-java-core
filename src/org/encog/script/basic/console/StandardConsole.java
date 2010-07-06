package org.encog.script.basic.console;

public class StandardConsole implements ConsoleInputOutput {
	
	@Override
	public void print(String line) {	
		System.out.print(line);
	}

	@Override
	public void printLine(String line) {
		System.out.println(line);
	}
	
	public String input(String prompt) {
		return System.console().readLine(prompt);
	}

}
