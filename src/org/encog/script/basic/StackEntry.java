package org.encog.script.basic;

public class StackEntry {
	
	public StackEntry(StackEntryType type, BasicLine line)
	{
		this.type = type;
		this.line = line;
		this.start = 0;
		this.stop = 0;
		this.step = 0;
		this.variable = null;
	}
	
	public StackEntry(StackEntryType type, BasicLine line, int start)
	{
		this.type = type;
		this.line = line;
		this.start = start;
		this.stop = 0;
		this.step = 0;
		this.variable = null;
	}
	
	public StackEntry(StackEntryType type, BasicLine line, BasicVariable variable, int start, int stop, int step)
	{
		this.type = type;
		this.line = line;
		this.start = start;
		this.stop = stop;
		this.step = step;
		this.variable = variable;
	}
	
	public StackEntryType getType() {
		return type;
	}
	public BasicLine getLine() {
		return line;
	}
	
	

	public int getStart() {
		return start;
	}

	public int getStop() {
		return stop;
	}

	public int getStep() {
		return step;
	}

	

	private final BasicVariable variable;
	private final StackEntryType type;
	private final BasicLine line;
	private final int start;
	private final int stop;
	private final int step;
	/**
	 * @return the variable
	 */
	public BasicVariable getVariable() {
		return variable;
	}
}
