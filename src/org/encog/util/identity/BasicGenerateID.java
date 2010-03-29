package org.encog.util.identity;

import org.encog.persist.annotations.EGAttribute;

public class BasicGenerateID implements GenerateID {
	
	@EGAttribute
	private long currentID;
	
	public BasicGenerateID()
	{
		this.currentID = 1;
	}
	
	public long generate()
	{
		synchronized(this)
		{
			return this.currentID++;
		}
	}
}
