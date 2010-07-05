package org.encog.script.basic;

public abstract class BasicObjectVariable  {
	
	BasicObjectVariable()
	{
		objectType=-1;
	}

	abstract boolean Scan(BasicVariable target);
	abstract boolean Update();
	abstract boolean Execute();
	abstract void Allocate();
	abstract BasicObjectVariable CreateObject(long num);
	void Free()
	{	
	}
	
	abstract void Copy(BasicVariable target);

	int GetObjectType()
	{
		return objectType;
	}
	
	void SetObjectType(int i)
	{
		objectType=i;
	}
	
	long GetElementSize()
	{
		return elementSize;
	}

	private int objectType;
	private long elementSize;
}
