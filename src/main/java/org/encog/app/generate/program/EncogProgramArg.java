package org.encog.app.generate.program;

public class EncogProgramArg {
	final EncogArgType type;
	final Object value;
	
	public EncogProgramArg(EncogArgType type, Object value) {
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

	public EncogProgramArg(Object argValue) {
		this(EncogArgType.ObjectType, argValue);
	}

	public EncogArgType getType() {
		return type;
	}

	public Object getValue() {
		return value;
	}	
}
