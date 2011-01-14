package org.encog.persist.map;

public abstract class PersistedProperty {
	
	private final boolean attribute;
	
	public abstract Object getData();
	public abstract String getString();
	
	public PersistedProperty(boolean attribute) {
		this.attribute = attribute;
	}
	
	public boolean isAttribute() {
		return attribute;
	}
}
