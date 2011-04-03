package org.encog.app.analyst.script;

/**
 * Holds a class item for the script. Some columns in a CSV are classes. This
 * object holds the possible class types.
 * 
 */
public class AnalystClassItem implements Comparable<AnalystClassItem> {

	private String code;
	private String name;
	private int count;

	public AnalystClassItem(String code, String name, int count) {
		super();
		this.code = code;
		this.name = name;
		this.count = count;
	}

	/**
	 * @return the code
	 */
	public String getCode() {
		return code;
	}

	/**
	 * @param code
	 *            the code to set
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
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public int compareTo(AnalystClassItem o) {
		return this.code.compareTo(o.getCode());
	}
	
	public int getCount() {
		return this.count;
	}
	
	public void increaseCount() {
		this.count++;
	}

	/** {@inheritDoc} */
	public String toString() {
		StringBuilder result = new StringBuilder("[");
		result.append(getClass().getSimpleName());
		result.append(" name=");
		result.append(this.name);
		result.append(", code=");
		result.append(this.code);
		result.append("]");
		return result.toString();
	}

}
