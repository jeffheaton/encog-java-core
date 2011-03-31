package org.encog.app.analyst.script.prop;

public class PropertyEntry implements Comparable<PropertyEntry> {
	
	private final PropertyType entryType;
	private final String name;
	private final String section;
	
		
	public PropertyEntry(PropertyType entryType, String name, String section) {
		super();
		this.entryType = entryType;
		this.name = name;
		this.section = section;
	}

	/**
	 * @return the entryType
	 */
	public PropertyType getEntryType() {
		return entryType;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * @return the section
	 */
	public String getSection() {
		return section;
	}

	@Override
	public int compareTo(PropertyEntry o) {
		return this.name.compareTo(o.name);
	}

	public String getKey() {
		return section  + "_" + name;
	}
	
	/** {@inheritDoc} */
	public String toString() {
		StringBuilder result = new StringBuilder("[");
		result.append(getClass().getSimpleName());
		result.append(" name=");
		result.append(this.name);
		result.append(", section=");
		result.append(this.section);
		result.append("]");
		return result.toString();
	}
	
}
