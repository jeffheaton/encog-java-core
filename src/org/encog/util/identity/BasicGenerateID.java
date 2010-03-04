package org.encog.util.identity;

public class BasicGenerateID implements GenerateID {
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
