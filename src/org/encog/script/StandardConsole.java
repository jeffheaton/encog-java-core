package org.encog.script;

public class StandardConsole implements ConsoleInputOutput {

	@Override
	public String input(String prompt) {
		return null;
	}

	@Override
	public void print(String line) {
		System.out.print(line);
	}

	@Override
	public void printLine(String line) {
		System.out.println(line);
	}

}
