package org.encog.script.basic;

public abstract class BasicObjectVariable  {
	
	public BasicObjectVariable()
	{
		objectType=-1;
	}

	abstract boolean scan(BasicVariable target);
	abstract boolean update();
	abstract boolean execute();
	abstract void allocate();
	abstract BasicObjectVariable createObject(long num);
	{	
	}
	
	abstract void copy(BasicVariable target);

	int getObjectType()
	{
		return objectType;
	}
	
	void setObjectType(int i)
	{
		objectType=i;
	}
	
	long getElementSize()
	{
		return elementSize;
	}

	private int objectType;
	private long elementSize;
}
