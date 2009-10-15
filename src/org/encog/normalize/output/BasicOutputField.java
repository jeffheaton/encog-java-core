package org.encog.normalize.output;

import org.encog.persist.annotations.EGAttribute;

public abstract class BasicOutputField implements OutputField {
	
	@EGAttribute
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
