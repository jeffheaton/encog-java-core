package org.encog.script.basic.console;

public interface ConsoleInputOutput {
	void printLine(String line);
	void print(String line);
	String input(String prompt);
}
