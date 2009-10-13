package org.encog.normalize.output;

public abstract class BasicOutputField implements OutputField {
	private boolean ideal;
	
	public void setIdeal(boolean ideal)
	{
		this.ideal = ideal;
	}
	
	public boolean isIdeal()
	{
		return this.ideal;
	}
}
