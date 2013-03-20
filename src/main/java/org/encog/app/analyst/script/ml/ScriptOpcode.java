package org.encog.app.analyst.script.ml;

import org.encog.ml.prg.extension.ProgramExtensionTemplate;

public class ScriptOpcode {
	private String name;
	private int argCount;
	
	public ScriptOpcode(String name, int argCount) {
		super();
		this.name = name;
		this.argCount = argCount;
	}

	public ScriptOpcode(ProgramExtensionTemplate temp) {
		this(temp.getName(),temp.getChildNodeCount());
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the argCount
	 */
	public int getArgCount() {
		return argCount;
	}

	/**
	 * @param argCount the argCount to set
	 */
	public void setArgCount(int argCount) {
		this.argCount = argCount;
	}
	
	
	
	
}
