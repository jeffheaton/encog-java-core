package org.encog.script;

public class BasicVariable {

	BasicVariable()
	{
	}
	
	BasicVariable(BasicVariable v)
	{
	}
	
	BasicVariable(String strData)
	{
	}

	BasicVariable(float floatData)
	{
	}

	BasicVariable(short shortData)
	{
	}

	BasicVariable(long longData)
	{
	}

	BasicVariable(double doubleData)
	{
	}

	BasicVariable(byte byteData)
	{
	}

	BasicVariable(boolean booleanData)
	{
	}

	BasicVariable(char characterData)
	{
	}

	BasicVariable(BasicObjectVariable v)
	{
	}
	
	void edit(String str)
	{
		
	}
	
	void edit(float v)
	{
	}

	void edit(short v)
	{
	}

	void edit(long v)
	{
	}

	void edit(double v)
	{
	}

	void edit(byte v)
	{
	}

	void edit(boolean v)
	{
	}

	void edit(char v)
	{
	}

	void edit(BasicObjectVariable v)
	{
	}

	void edit(BasicVariable v)
	{
	}

	void CreateRef(BasicVariable v)
	{
	}

	void HelpCreateRef(Object b,BasicTypes t,int xx,int yy,int zz)
	{
	}

	boolean IsArray()
	{
		return x!=0;
	}
	
	boolean IsNULL()
	{
		return( (data==null) || (data==buffer) );
	}
	
	void CreateArray(long a,long b,long c)
	{
	}

	void SetArrayLocation(long a,long b,long c)
	{
	}

	
	String GetStr()
	{
		return null;
	}

	float GetFloat()
	{
		return 0;	
	}

	short GetShort()
	{
		return 0;
	}

	long GetLong()
	{
		return 0;	
	}

	double GetDouble()	
	{
		return 0;
	}

	byte GetByte()
	{
		return 0;
	}

	boolean GetBoolean()
	{
		return false;
	}

	char GetCharacter()
	{
		return 0;
	}

	BasicObjectVariable GetObject()
	{
		return null;
	}


	void HandleNumericType(BasicVariable var)
	{
		
	}
	
	void HandleNonNumericType(BasicVariable var)
	{
	}


	boolean CompareE(BasicVariable v)
	{
		return false;
	}

	boolean CompareNE(BasicVariable v)
	{
		return false;
	}

	boolean CompareGT(BasicVariable v)
	{
		return false;
	}

	boolean CompareLT(BasicVariable v)
	{
		return false;
	}

	boolean CompareGTE(BasicVariable v)
	{
		return false;
	}

	boolean CompareLTE(BasicVariable v)
	{
		return false;
	}


	private Object data;
	private Object currentData;
	private byte buffer[];
	private long x;
	private long y;
	private long z;
	private boolean isRef;
	
}
