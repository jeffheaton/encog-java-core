package org.encog.normalize.input;

public class InputFieldEncogCollection extends BasicInputField {

	private String resourceName;
	private int offset;
	
	
	public InputFieldEncogCollection()
	{
		
	}
	
	public InputFieldEncogCollection(String resourceName, int offset) {
		super();
		this.resourceName = resourceName;
		this.offset = offset;
	}

	/**
	 * @return the resourceName
	 */
	public String getResourceName() {
		return resourceName;
	}
	/**
	 * @param resourceName the resourceName to set
	 */
	public void setResourceName(String resourceName) {
		this.resourceName = resourceName;
	}
	/**
	 * @return the offset
	 */
	public int getOffset() {
		return offset;
	}
	/**
	 * @param offset the offset to set
	 */
	public void setOffset(int offset) {
		this.offset = offset;
	}
	
	
	
	
}
