package org.encog.script;

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
	
	
}
