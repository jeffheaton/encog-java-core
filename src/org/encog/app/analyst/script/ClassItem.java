package org.encog.app.analyst.script;

public class ClassItem implements Comparable<ClassItem> {
	
	private String code;
	private String name;
	
	public ClassItem(String code, String name) {
		super();
		this.code = code;
		this.name = name;
	}

	/**
	 * @return the code
	 */
	public String getCode() {
		return code;
	}

	/**
	 * @param code the code to set
	 */
	public void setCode(String code) {
		this.code = code;
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

	@Override
	public int compareTo(ClassItem o) {
		return this.code.compareTo(o.getCode());
	}
	
	
	
}
