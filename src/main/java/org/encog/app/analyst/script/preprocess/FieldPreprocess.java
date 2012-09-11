package org.encog.app.analyst.script.preprocess;

public class FieldPreprocess {
	private final String name;
	private final String calculation;
	private final PreprocessAction action;
	
	public FieldPreprocess(PreprocessAction theAction, String theName, String theCalculation) {
		super();
		this.action = theAction;
		this.name = theName;
		this.calculation = theCalculation;
	}

	public String getName() {
		return name;
	}

	public String getCalculation() {
		return calculation;
	}

	public PreprocessAction getAction() {
		return action;
	}
	
	
}
