package org.encog.app.analyst.script.classify;

import org.encog.app.quant.classify.ClassifyMethod;

public class ClassifyField {
	private String name;
	private ClassifyMethod method;
	
	public ClassifyField(String name, ClassifyMethod method) {
		super();
		this.name = name;
		this.method = method;
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
	 * @return the method
	 */
	public ClassifyMethod getMethod() {
		return method;
	}

	/**
	 * @param method the method to set
	 */
	public void setMethod(ClassifyMethod method) {
		this.method = method;
	}
}
