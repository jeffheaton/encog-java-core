package org.encog.script.basic;

public class BasicObject {

	private BasicTypes type;
	
	BasicObject()
	{
		this.type = BasicTypes.typeUndefined;
	}

	/**
	 * @return the type
	 */
	public BasicTypes getObjectType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setObjectType(BasicTypes type) {
		this.type = type;
	}
	
	private BasicObject next;

	public BasicTypes getType() {
		return type;
	}

	public void setType(BasicTypes type) {
		this.type = type;
	}

	public BasicObject getNext() {
		return next;
	}

	public void setNext(BasicObject next) {
		this.next = next;
	}
	
	
	
	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	private String label;
}
