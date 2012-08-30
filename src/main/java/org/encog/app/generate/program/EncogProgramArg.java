package org.encog.app.generate.program;

public class EncogProgramArg {
	final EncogArgType type;
	final String value;
	
	public EncogProgramArg(EncogArgType type, String value) {
		super();
		this.type = type;
		this.value = value;
	}
	
	public EncogProgramArg(String value) {
		this(EncogArgType.String,value);
	}
	
	public EncogProgramArg(int value) {
		this(EncogArgType.Float,""+value);
	}
	
	public EncogProgramArg(double value) {
		this(EncogArgType.Float,""+value);
	}

	public EncogArgType getType() {
		return type;
	}

	public String getValue() {
		return value;
	}	
}
