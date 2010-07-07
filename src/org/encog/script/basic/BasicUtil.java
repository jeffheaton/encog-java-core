package org.encog.script.basic;


public class BasicUtil {
	public static BasicKey FindKeyword(String token)
	{
		for( BasicKey key : BasicKey.getKeys())
		{
			if( key.getName().equals(token))
				return key;
		}
		return null;
	}
	
	public static BasicKey  FindKeyword(KeyNames token)
	{
		for( BasicKey key : BasicKey.getKeys())
		{
			if( key.getId()==token)
				return key;
		}
		return null;
	}
	public static void DoInput(String str,String cap)
	{
		
	}
	
	public static int FindKeyword(String str,String key)
	{
		boolean quote=false;
		int wptr;
		int rtn = 0;
		String cmp;

		cmp = key.toUpperCase();

		StringBuilder word = new StringBuilder();
		
		int index = 0;
		
		while(index<str.length())
		{
			char ch = str.charAt(index);
			
			if(ch=='\"')
			{
				quote=!quote;
				index++;
				continue;
			}

			if(quote)
			{
				index++;
				continue;
			}

			if( (ch=='\t') || (ch==' ') || (ch==13) || (ch==10) )
			{
				index++;
				if(word.toString().equals(cmp))
					return rtn;
				continue;
			}
			
			word.append(ch);
			rtn++;
			index++;
		}

		return -1;
	}


	public static void SyncVariables(char opp,BasicVariable in1,BasicVariable in2,BasicVariable out1,BasicVariable out2)
	{
		out1.Free();// Just in case
		out2.Free();

		if( (in1.getObjectType()==BasicTypes.typeString) || (in2.getObjectType()==BasicTypes.typeString) )
		{
			out1.edit( in1.GetStr());
			out2.edit( in2.GetStr());
			return;
		}

		if( (in1.getObjectType()==BasicTypes.typeString) || (in2.getObjectType()==BasicTypes.typeString) )
		{
			out1.edit( in1.GetStr());
			out2.edit( in2.GetStr());
			return;
		}

		if( (in1.getObjectType()==BasicTypes.typeDouble) || (in2.getObjectType()==BasicTypes.typeDouble) )
		{
			out1.edit( in1.GetDouble());
			out2.edit( in2.GetDouble());
			return;
		}

		if( (in1.getObjectType()==BasicTypes.typeFloat) || (in2.getObjectType()==BasicTypes.typeFloat) )
		{
			out1.edit( in1.GetFloat());
			out2.edit( in2.GetFloat());
			return;
		}

		if( (in1.getObjectType()==BasicTypes.typeLong) || (in2.getObjectType()==BasicTypes.typeLong) )
		{
			out1.edit( in1.GetLong());
			out2.edit( in2.GetLong());
			return;
		}

		if( (in1.getObjectType()==BasicTypes.typeInteger) || (in2.getObjectType()==BasicTypes.typeInteger) )
		{
			out1.edit( in1.GetShort());
			out2.edit( in2.GetShort());
			return;
		}

		if( (in1.getObjectType()==BasicTypes.typeByte) || (in2.getObjectType()==BasicTypes.typeByte) )
		{
			out1.edit( in1.GetByte());
			out2.edit( in2.GetByte());
			return;
		}


		if( (in1.getObjectType()==BasicTypes.typeCharacter) || (in2.getObjectType()==BasicTypes.typeCharacter) )
		{
			out1.edit( in1.GetShort());
			out2.edit( in2.GetShort());
			return;
		}
				
		if( (in1.getObjectType()==BasicTypes.typeBoolean) || (in2.getObjectType()==BasicTypes.typeBoolean) )
		{
			out1.edit( in1.GetBoolean());
			out2.edit( in2.GetBoolean());
			return;
		}
	}

	public static void PerformAdd(BasicVariable target,BasicVariable var1,BasicVariable var2)
	{
		BasicVariable a = new BasicVariable();
		BasicVariable b = new BasicVariable();

		SyncVariables('+',var1,var2,a,b);
		switch(a.getObjectType())
		{
		case typeString:
			StringBuilder temp = new StringBuilder();
			temp.append(a.GetStr());
			temp.append(b.GetStr());
			target.edit(temp.toString());
			break;
		case typeFloat:target.edit((float)a.GetFloat()+b.GetFloat());break;
		case typeInteger:target.edit((long)(a.GetShort()+b.GetShort()));break;
		case typeLong:target.edit((long)(a.GetLong()+b.GetLong()));break;
		case typeDouble:target.edit((double)a.GetDouble()+b.GetDouble());break;
		case typeByte:target.edit((byte)a.GetByte()+b.GetByte());break;
		case typeCharacter:target.edit((char)a.GetShort()+b.GetShort());break;
		default:
			throw(new BasicError(ErrorNumbers.errorType));
		}
	}

	public static void PerformSub(BasicVariable target,BasicVariable var1,BasicVariable var2)
	{
		BasicVariable a = new BasicVariable();
		BasicVariable b = new BasicVariable();

		SyncVariables('-',var1,var2,a,b);
		switch(a.getObjectType())
		{
		case typeFloat:target.edit((float)a.GetFloat()-b.GetFloat());break;
		case typeInteger:target.edit((long)(a.GetShort()-b.GetShort()));break;
		case typeLong:target.edit((long)a.GetLong()-b.GetLong());break;
		case typeDouble:target.edit((double)a.GetDouble()-b.GetDouble());break;
		case typeByte:target.edit((byte)a.GetByte()-b.GetByte());break;
		case typeCharacter:target.edit((char)a.GetShort()-b.GetShort());break;
		default:
			throw(new BasicError(ErrorNumbers.errorType));
		}
	}

	public static void PerformMul(BasicVariable target,BasicVariable var1,BasicVariable var2)
	{
		BasicVariable a = new BasicVariable();
		BasicVariable b = new BasicVariable();

		SyncVariables('*',var1,var2,a,b);
		switch(a.getObjectType())
		{
		case typeFloat:target.edit((float)a.GetFloat()*b.GetFloat());break;
		case typeInteger:target.edit((long)(a.GetShort()*b.GetShort()));break;
		case typeLong:target.edit((long)a.GetLong()*b.GetLong());break;
		case typeDouble:target.edit((double)a.GetDouble()*b.GetDouble());break;
		case typeByte:target.edit((byte)a.GetByte()*b.GetByte());break;
		case typeCharacter:target.edit((char)a.GetShort()*b.GetShort());break;
		default:
			throw(new BasicError(ErrorNumbers.errorType));
		}
	}

	public static void PerformDiv(BasicVariable target,BasicVariable var1,BasicVariable var2)
	{
		BasicVariable a = new BasicVariable();
		BasicVariable b = new BasicVariable();

		SyncVariables('/',var1,var2,a,b);
		switch(a.getObjectType())
		{
		case typeFloat:target.edit((float)a.GetFloat()/b.GetFloat());break;
		case typeInteger:target.edit((long)(a.GetShort()/b.GetShort()));break;
		case typeLong:target.edit((long)a.GetLong()/b.GetLong());break;
		case typeDouble:target.edit((double)a.GetDouble()/b.GetDouble());break;
		case typeByte:target.edit((byte)a.GetByte()/b.GetByte());break;
		case typeCharacter:target.edit((char)a.GetShort()/b.GetShort());break;
		default:
			throw(new BasicError(ErrorNumbers.errorType));
		}
	}

	public static void PerformMod(BasicVariable target,BasicVariable var1,BasicVariable var2)
	{
		BasicVariable a = new BasicVariable();
		BasicVariable b = new BasicVariable();

		SyncVariables('%',var1,var2,a,b);
		switch(a.getObjectType())
		{
		case typeFloat:
		case typeInteger:target.edit((long)a.GetShort()%b.GetShort());break;
		case typeDouble:
		case typeLong:target.edit((long)a.GetLong()%b.GetLong());break;
		case typeByte:target.edit((byte)a.GetByte()%b.GetByte());break;
		case typeCharacter:target.edit((char)a.GetShort()%b.GetShort());break;
		default:
			throw(new BasicError(ErrorNumbers.errorType));
		}
	}

	public static void PerformCat(BasicVariable target,BasicVariable var1,BasicVariable var2)
	{
		StringBuilder temp = new StringBuilder();
		temp.append(var1.GetStr());
		temp.append(var2.GetStr());
		target.edit(temp.toString());
		
	}

	public static void PerformPower(BasicVariable target,BasicVariable var1,BasicVariable var2)
	{
		if( (var1.getObjectType()==BasicTypes.typeString) ||
			(var1.getObjectType()==BasicTypes.typeBoolean) ||
			(var2.getObjectType()==BasicTypes.typeString) ||
			(var2.getObjectType()==BasicTypes.typeBoolean) )
		{
			throw(new BasicError(ErrorNumbers.errorType));
		}
		target.edit((double)Math.pow(var1.GetDouble(),var2.GetDouble()));

	}

	public static void PerformNeg(BasicVariable target,BasicVariable var)
	{
		BasicVariable a = new BasicVariable();
		BasicVariable b = new BasicVariable();

		switch(var.getObjectType())
		{
		case typeFloat:target.edit((float)-var.GetFloat());break;
		case typeInteger:target.edit((long)-var.GetShort());break;
		case typeLong:target.edit((long)-var.GetLong());break;
		case typeDouble:target.edit((double)-var.GetDouble());break;
		case typeByte:target.edit((byte)-var.GetByte());break;
		case typeCharacter:target.edit((char)-var.GetShort());break;
		default:
			throw(new BasicError(ErrorNumbers.errorType));
		}

	}


	BasicError FindError(ErrorNumbers err)
	{
		return null;
	}

	public static String basicToUpper(String l) {
		StringBuilder result = new StringBuilder();
		boolean inQuote = false;
		
		for(int i=0;i<l.length();i++)
		{
			char ch = l.charAt(i);
			if( ch=='\"')
				inQuote = !inQuote;
			
			if( !inQuote )
				ch = Character.toUpperCase(ch);
			
			result.append(ch);
		}
		
		return result.toString();
	}
	
	public static int basicIndexOf(String str, int start, String srch)
	{
		boolean quote=false;
		int searchIndex = 0;
		
		for(int sourceIndex = start; sourceIndex<str.length()-srch.length(); sourceIndex++)
		{
			char ch = str.charAt(sourceIndex);
			char ch2 = srch.charAt(searchIndex);
			
			if( ch==34 )
				quote = !quote;
			
			if( !quote )
			{
				if( Character.toUpperCase(ch)==Character.toUpperCase(ch2))
				{
					searchIndex++;
					if( searchIndex==srch.length())
						return sourceIndex - srch.length();
				}
				else
					searchIndex = 0;
			}
		}
		
		return -1;
	}
}
