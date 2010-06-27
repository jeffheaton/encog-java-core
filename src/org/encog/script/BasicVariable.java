package org.encog.script;

public class BasicVariable extends BasicObject {

	public BasicVariable()
	{
		x=y=z=0;
		data=buffer;
		currentData=data;
		setObjectType(BasicTypes.typeUndefined);
		isRef=false;
	}
	
	public BasicVariable(BasicObjectVariable v)
	{
		x=y=z=0;
		data=v;
		currentData=v;
		setObjectType(BasicTypes.typeObject);
		isRef=false;
	}
	
	public BasicVariable(String strData)
	{
		x=y=z=0;
		data=strData;
		currentData=data;
		setObjectType(BasicTypes.typeString);
		isRef=false;
	}

	public BasicVariable(float floatData)
	{
		x=y=z=0;
		data=buffer;
		currentData=floatData;
		data=floatData;
		setObjectType(BasicTypes.typeFloat);
		isRef=false;
	}

	BasicVariable(short shortData)
	{
		x=y=z=0;
		data=buffer;
		currentData=shortData;
		isRef=false;
		data=shortData;
		setObjectType(BasicTypes.typeInteger);
	}

	BasicVariable(long longData)
	{
		x=y=z=0;
		data=buffer;
		currentData=longData;
		data=longData;
		setObjectType(BasicTypes.typeLong);
		isRef=false;
	}

	BasicVariable(double doubleData)
	{
		x=y=z=0;
		data=buffer;
		currentData=doubleData;
		data=doubleData;
		setObjectType(BasicTypes.typeDouble);
		isRef=false;

	}

	BasicVariable(byte byteData)
	{
		x=y=z=0;
		data=buffer;
		currentData=byteData;
		data=byteData;
		setObjectType(BasicTypes.typeInteger);
		isRef=false;

	}

	BasicVariable(boolean booleanData)
	{
		x=y=z=0;
		data=buffer;
		currentData=booleanData;
		data=booleanData;
		setObjectType(BasicTypes.typeBoolean);
		isRef=false;
	}

	BasicVariable(char characterData)
	{
		x=y=z=0;
		data=buffer;
		currentData=characterData;
		setObjectType(BasicTypes.typeCharacter);
		isRef=false;		
	}

	BasicVariable(BasicVariable v)
	{
		edit(v);
	}
	
	void edit(String str)
	{
		if(getObjectType()==BasicTypes.typeUndefined)
		{
			assert(data==buffer);
			data="";
			currentData=data;
			setObjectType(BasicTypes.typeString);
		}

		switch(getObjectType())
		{
		case typeString:
			data = str;
			break;
		default:Mismatch();
		}
	}
	
	void edit(float v)
	{
		if(getObjectType()==BasicTypes.typeUndefined)
			setObjectType(BasicTypes.typeFloat);

		switch(getObjectType())
		{
		case typeFloat:this.data=(float)v;break;
		case typeInteger:this.data=(int)v;break;
		case typeLong:this.data=(long)v;break;
		case typeDouble:this.data=(double)v;break;
		case typeByte:this.data=(byte)v;break;
		default:Mismatch();
		}
	}

	void edit(short v)
	{
		if(getObjectType()==BasicTypes.typeUndefined)
			setObjectType(BasicTypes.typeInteger);
		
		switch(getObjectType())
		{
		case typeFloat:this.data=(float)v;break;
		case typeInteger:this.data=(int)v;break;
		case typeLong:this.data=(long)v;break;
		case typeDouble:this.data=(double)v;break;
		case typeByte:this.data=(byte)v;break;
		default:Mismatch();
		}

	}

	void edit(long v)
	{
		if(getObjectType()==BasicTypes.typeUndefined)
			setObjectType(BasicTypes.typeLong);
		
		switch(getObjectType())
		{
		case typeFloat:this.data=(float)v;break;
		case typeInteger:this.data=(int)v;break;
		case typeLong:this.data=(long)v;break;
		case typeDouble:this.data=(double)v;break;
		case typeByte:this.data=(byte)v;break;
		case typeCharacter:this.data=(char)v;break;
		default:Mismatch();
		}

	}

	void edit(double v)
	{
		if(getObjectType()==BasicTypes.typeUndefined)
			setObjectType(BasicTypes.typeDouble);

		switch(getObjectType())
		{
		case typeFloat:this.data=(float)v;break;
		case typeInteger:this.data=(int)v;break;
		case typeLong:this.data=(long)v;break;
		case typeDouble:this.data=(double)v;break;
		case typeByte:this.data=(byte)v;break;
		default:Mismatch();
		}
	}

	void edit(byte v)
	{
		if(getObjectType()==BasicTypes.typeUndefined)
			setObjectType(BasicTypes.typeByte);

		switch(getObjectType())
		{
		case typeFloat:this.data=(float)v;break;
		case typeInteger:this.data=(int)v;break;
		case typeLong:this.data=(long)v;break;
		case typeDouble:this.data=(double)v;break;
		case typeByte:this.data=(byte)v;break;

		default:Mismatch();
		}
	}

	void edit(boolean v)
	{
		if(getObjectType()==BasicTypes.typeUndefined)
			setObjectType(BasicTypes.typeBoolean);
		else
		if(getObjectType()!=BasicTypes.typeBoolean)
			Mismatch();

		this.data = v;
	}

	void edit(char v)
	{
		if(getObjectType()==BasicTypes.typeUndefined)
			setObjectType(BasicTypes.typeCharacter);
		else
		if(getObjectType()!=BasicTypes.typeCharacter)
			Mismatch();

		this.data=v;
	}

	void edit(BasicObjectVariable v)
	{
		if(getObjectType()==BasicTypes.typeUndefined)
			setObjectType(BasicTypes.typeObject);
		else
		if(getObjectType()!=BasicTypes.typeObject)
			throw( new BasicError(ErrorNumbers.errorType));


		if(data!=buffer)
			data = null;

		data=v;
		currentData=data;

	}

	void edit(BasicVariable c)
	{
		switch( c.getObjectType() )
		{
			case typeFloat:
			case typeInteger:
			case typeLong:
			case typeDouble:
			case typeByte:
				if( getObjectType()==BasicTypes.typeUndefined )
					setObjectType(c.getObjectType());

				switch(getObjectType())
				{
					case typeFloat:edit(c.GetFloat());return;
					case typeInteger:edit((long)c.GetShort());return;
					case typeLong:edit(c.GetLong());return;
					case typeDouble:edit(c.GetDouble());return;
					case typeByte:edit(c.GetByte());return;
				}
				throw(new BasicError(ErrorNumbers.errorType));

			case typeString:
				edit( c.GetStr() );
				return;

			case typeCharacter:
				edit( c.GetCharacter() );
				return;

			case typeBoolean:
				edit( c.GetBoolean() );
				return;

			case typeObject:
				// This assert is usually called from the source of the c
				// paramater not being setup properly, that is not having
				// a proper data member setup for the object.
				assert(((BasicObjectVariable)c.data)!=c.buffer);
				((BasicObjectVariable)c.data).Copy(this);
				return;

				
			
			default:
				throw(new BasicError(ErrorNumbers.errorType));
			
		}
	}

	void CreateRef(BasicVariable v)
	{
		v.HelpCreateRef(data,getObjectType(),x,y,z);
	}

	void HelpCreateRef(Object b,BasicTypes t,int xx,int yy,int zz)
	{
		data=b;
		currentData=b;
		setObjectType(t);
		isRef=true;
		x=xx;
		y=yy;
		z=zz;
	}

	boolean IsArray()
	{
		return x!=0;
	}
	
	boolean IsNULL()
	{
		return( (data==null) || (data==buffer) );
	}
	
	public void CreateArray(int a,int b,int c)
	{
		int elements;

		x=a;
		y=b;
		z=c;
		
		elements=(x+1)*(y+1)*(z+1);

		switch(getObjectType())
		{
		case typeUndefined:throw(new BasicError(ErrorNumbers.errorDim));
		case typeString:
			data=new String[elements];
			break;

		case typeFloat:
			data=new float[elements];
			break;

		case typeInteger:
			data=new short[elements];
			break;

		case typeLong:
			data=new long[elements];
			break;

		case typeDouble:
			data=new double[elements];
			break;

		case typeByte:
			data=new byte[elements];
			break;

		case typeBoolean:
			data=new boolean[elements];
			break;

		case typeCharacter:
			data=new char[elements];
			break;

		case typeObject:
			data=((BasicObjectVariable)data).CreateObject(elements);
			break;
		}

	}

	public void SetArrayLocation(long a,long b,long c)
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

	private void Mismatch()
	{
		
	}

	private Object data;
	private Object currentData;
	private Object buffer;
	private int x;
	private int y;
	private int z;
	private boolean isRef;
	
}
