package org.encog.script;

public class BasicVariable extends BasicObject {

	public BasicVariable()
	{
		Free();
	}
	
	public BasicVariable(BasicObjectVariable v)
	{
		CreateArray();
		data=new Object[1];
		((Object[])this.data)[0] = v;
		setObjectType(BasicTypes.typeObject);
		isRef=false;
	}
	
	public BasicVariable(String strData)
	{
		CreateArray();
		data=new String[1];
		((String[])this.data)[0] = strData;
		setObjectType(BasicTypes.typeString);
		isRef=false;
	}

	public BasicVariable(float floatData)
	{
		CreateArray();
		data=new Float[1];
		((Float[])this.data)[0] = floatData;
		setObjectType(BasicTypes.typeFloat);
		isRef=false;
	}

	public BasicVariable(short shortData)
	{
		CreateArray();
		data=new Integer[1];
		((Integer[])this.data)[0] = new Integer(shortData);
		setObjectType(BasicTypes.typeInteger);
		isRef=false;
	}

	public BasicVariable(long longData)
	{
		CreateArray();
		data=new Long[1];
		((Long[])this.data)[0] = longData;
		setObjectType(BasicTypes.typeLong);
		isRef=false;
	}

	public BasicVariable(double doubleData)
	{
		CreateArray();
		data=new Double[1];
		((Double[])this.data)[0] = doubleData;
		setObjectType(BasicTypes.typeDouble);
		isRef=false;

	}

	BasicVariable(byte byteData)
	{
		CreateArray();
		data=new Byte[1];
		((Byte[])this.data)[0] = byteData;
		setObjectType(BasicTypes.typeByte);
		isRef=false;

	}

	BasicVariable(boolean booleanData)
	{
		CreateArray();
		data=new boolean[1];
		((boolean[])this.data)[0] = booleanData;
		setObjectType(BasicTypes.typeBoolean);
		isRef=false;
	}

	BasicVariable(char characterData)
	{
		CreateArray();
		data=new char[1];
		((char[])this.data)[0] = characterData;
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
			data=new String[1];
			setObjectType(BasicTypes.typeString);
		}

		switch(getObjectType())
		{
		case typeString:
			((String[])data)[0] = str;
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

		this.data = new BasicObjectVariable[1];

		((BasicObjectVariable[])this.data)[0] = v;

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
		currentIndex = 0;
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
	
	public void CreateArray()
	{
		this.x = 0;
		this.y = 0;
		this.z = 0;
		this.currentIndex = 0;
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

	public void SetArrayLocation(int a,int b,int c)
	{
		int disp;
		long elementSize;

		if(z>0)
		{
			if( (a<1) || (a>x) || (b<1) || (b>y) || (c<1) || (c>z) )
				throw(new BasicError(ErrorNumbers.errorBounds));

			a--;
			b--;
			c--;
			disp=c+(b*x)+(a*y);
		}
		else
		if(y>0)
		{
			if( (a<1) || (a>x) || (b<1) || (b>y) )
				throw(new BasicError(ErrorNumbers.errorBounds));
			a--;
			b--;

			disp=a+b+(a*x);
		}
		if(x>0)
		{
			if( (a<1) || (a>x) )
				throw(new BasicError(ErrorNumbers.errorBounds));
			a--;
			disp=a;
		}
		else throw(new BasicError(ErrorNumbers.errorNoArray));

		
		if( getObjectType()==BasicTypes.typeUndefined )
		{
			throw(new BasicError(ErrorNumbers.errorType));
		}
		else
		{
			this.currentIndex = disp;
		}
	}

	
	public String GetStr()
	{
		switch(getObjectType())
		{
		case typeString:
			return ((String[])this.data)[this.currentIndex];
		case typeCharacter:
			return ""+((char[])this.data)[this.currentIndex];
		default:
			throw(new BasicError(ErrorNumbers.errorType));
		}
	}

	public float GetFloat()
	{
		HandleNumericType(this);

		switch(getObjectType())
		{
		case typeFloat:return	(float)	((float[])this.data)[this.currentIndex];
		case typeInteger:return (float)	((int[])this.data)[this.currentIndex];
		case typeLong:return	(float)	((long[])this.data)[this.currentIndex];
		case typeDouble:return	(float) ((double[])this.data)[this.currentIndex];
		case typeByte:return	(float)	((byte[])this.data)[this.currentIndex];
		default:return 0;
		}
	}

	public short GetShort()
	{
		HandleNumericType(this);

		switch(getObjectType())
		{
		case typeFloat:return	(short)	((float[])this.data)[this.currentIndex];
		case typeInteger:return (short)	((int[])this.data)[this.currentIndex];
		case typeLong:return	(short)	((long[])this.data)[this.currentIndex];
		case typeDouble:return	(short) ((double[])this.data)[this.currentIndex];
		case typeByte:return	(short)	((byte[])this.data)[this.currentIndex];
		default:return 0;
		}
	}

	public long GetLong()
	{
		HandleNumericType(this);

		switch(getObjectType())
		{
		case typeFloat:return	(long)	((float[])this.data)[this.currentIndex];
		case typeInteger:return (long)	((int[])this.data)[this.currentIndex];
		case typeLong:return	(long)	((long[])this.data)[this.currentIndex];
		case typeDouble:return	(long) ((double[])this.data)[this.currentIndex];
		case typeByte:return	(long)	((byte[])this.data)[this.currentIndex];
		default:return 0;
		}
	}

	public double GetDouble()	
	{
		HandleNumericType(this);

		switch(getObjectType())
		{
		case typeFloat:return	(double)	((float[])this.data)[this.currentIndex];
		case typeInteger:return (double)	((int[])this.data)[this.currentIndex];
		case typeLong:return	(double)	((long[])this.data)[this.currentIndex];
		case typeDouble:return	(double) ((double[])this.data)[this.currentIndex];
		case typeByte:return	(double)	((byte[])this.data)[this.currentIndex];
		default:return 0;
		}
	}

	public byte GetByte()
	{
		HandleNumericType(this);

		switch(getObjectType())
		{
		case typeFloat:return	(byte)	((float[])this.data)[this.currentIndex];
		case typeInteger:return (byte)	((int[])this.data)[this.currentIndex];
		case typeLong:return	(byte)	((long[])this.data)[this.currentIndex];
		case typeDouble:return	(byte) ((double[])this.data)[this.currentIndex];
		case typeByte:return	(byte)	((byte[])this.data)[this.currentIndex];
		default:return 0;
		}
	}

	public boolean GetBoolean()
	{
		if(getObjectType()!=BasicTypes.typeBoolean)
			throw(new BasicError(ErrorNumbers.errorType));
		
		return ((boolean[])this.data)[this.currentIndex];
	}

	public char GetCharacter()
	{
		switch(getObjectType())
		{
		case typeCharacter:
			return ((char[])this.data)[this.currentIndex];
		default:
			throw(new BasicError(ErrorNumbers.errorType));
		}
	}

	public BasicObjectVariable GetObject()
	{
		if(getObjectType()!=BasicTypes.typeObject)
			throw(new BasicError(ErrorNumbers.errorType));
	
		return ((BasicObjectVariable[])this.data)[this.currentIndex];
	}


	private void HandleNumericType(BasicVariable var)
	{
		switch(var.getObjectType())
		{
	case typeBoolean:
	case typeCharacter:
	case typeString:
		throw(new BasicError(ErrorNumbers.errorType));
		}
	}
	
	private void HandleNonNumericType(BasicVariable var)
	{
		switch(var.getObjectType())
		{
		case typeFloat:
		case typeInteger:
		case typeLong:
		case typeByte:
		case typeDouble:
			throw(new BasicError(ErrorNumbers.errorType));
		}
	}
	
	public String ToString()
	{
		switch(getObjectType())
		{
		case typeString:return GetStr();
		case typeFloat:
		case typeInteger:
		case typeLong:
		case typeDouble:
		case typeByte:return "" + GetDouble();
		case typeBoolean:return GetBoolean()?"true":"false";
		case typeCharacter:return ""+GetCharacter();
		default:return "[OBJECT]";
		}
	}


	boolean CompareE(BasicVariable v)
	{
		switch(getObjectType())
		{
		case typeUndefined:
		case typeObject:
			throw(new BasicError(ErrorNumbers.errorInvalidType));
		case typeString:
			return GetStr().equals(v.GetStr());
		case typeInteger:
		case typeLong:
		case typeByte:
			return(GetLong()==v.GetLong());
		case typeCharacter:
			return(GetCharacter()==v.GetCharacter());
			
		case typeFloat:
		case typeDouble:
			return(GetDouble()==v.GetDouble());
		case typeBoolean:
			return(GetBoolean()==v.GetBoolean());
		}
		return false;
	}

	boolean CompareNE(BasicVariable v)
	{
		return !CompareE(v);
	}

	boolean CompareGT(BasicVariable v)
	{
		switch(getObjectType())
		{
		case typeUndefined:
		case typeObject:
			throw(new BasicError(ErrorNumbers.errorInvalidType));
		case typeString:
			return(GetStr().compareTo(v.GetStr())>0);
		case typeInteger:
		case typeLong:
		case typeByte:
			return(GetLong()>v.GetLong());
		case typeCharacter:
			return(GetCharacter()>v.GetCharacter());
		case typeFloat:
		case typeDouble:
			return(GetDouble()>v.GetDouble());
		}
		return false;
	}

	boolean CompareLT(BasicVariable v)
	{
		switch(getObjectType())
		{
		case typeUndefined:
		case typeObject:
			throw(new BasicError(ErrorNumbers.errorInvalidType));
		case typeString:
			return(GetStr().compareTo(v.GetStr())<0);
		case typeInteger:
		case typeLong:
		case typeByte:
			return(GetLong()<v.GetLong());
		case typeCharacter:
			return(GetCharacter()<v.GetCharacter());

		case typeFloat:
		case typeDouble:
			return(GetDouble()<v.GetDouble());
		}
		return false;
	}

	boolean CompareGTE(BasicVariable v)
	{
		return( CompareE(v) || CompareGT(v) );
	}

	boolean CompareLTE(BasicVariable v)
	{
		return( CompareE(v) || CompareLT(v) );
	}

	private void Mismatch()
	{
		throw new BasicError(ErrorNumbers.errorType);
	}
	
	public void Free() {
		x=y=z=0;
		setObjectType(BasicTypes.typeUndefined);
		isRef=false;
	}

	private Object data;
	private int currentIndex;
	private Object buffer;
	private int x;
	private int y;
	private int z;
	private boolean isRef;

	
}
