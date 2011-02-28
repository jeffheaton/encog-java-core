package org.encog.app.analyst.script.ScriptProperties;

public class PropertyEntry {
	
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
	
}
