package org.encog.script.basic.commands;

import javax.swing.JOptionPane;

import org.encog.script.basic.BasicError;
import org.encog.script.basic.BasicParse;
import org.encog.script.basic.BasicTypes;
import org.encog.script.basic.BasicVariable;
import org.encog.script.basic.ErrorNumbers;
import org.encog.script.basic.KeyNames;

public class BasicFunctions {

	private BasicParse parse;
	
	public BasicFunctions(BasicParse parse)
	{
		this.parse = parse;
	}
	
	public void callInternalFunction(KeyNames f, BasicVariable target) {
		switch(f)
		{
		case keyABS:		fnAbs(target);break;
		case keyASC:		fnAsc(target);break;
		case keyATN:		fnAtn(target);break;
		case keyCDBL:		fnCDbl(target);break;
		case keyCHR:		fnChr(target);break;
		case keyCINT:		fnCInt(target);break;
		case keyCLNG:		fnCLng(target);break;
		case keyCOS:		fnCos(target);break;
		case keyDATE:		fnDate_(target);break;
		case keyENVIRON:	fnEnvron_(target);break;
		case keyEOF:		fnEof(target);break;
		case keyERR:		fnErr(target);break;
		case keyERROR:		fnError(target);break;
		case keyEXP:		fnExp(target);break;
		case keyFILEATTR:	fnFileattr(target);break;
		case keyFIX:		fnFix(target);break;
		case keyFREEFILE:	fnFreeFile(target);break;
		case keyHEX:		fnHex_(target);break;
		case keyINPUT:		fnInput_(target);break;
		case keyINSTR:		fnInStr(target);break;
		case keyINT:		fnInt(target);break;
		case keyLCASE:		fnLCase(target);break;
		case keyLEFT:		fnLeft(target);break;
		case keyLEN:		fnLen(target);break;
		case keyLOC:		fnLoc(target);break;
		case keyLOG:		fnLog(target);break;
		case keyLTRIM:		fnLTrim(target);break;
		case keyMID:		fnMid_(target);break;
		case keyMSGBOX:		fnMsgBox(target);break;
		case keyOCT:		fnOct_(target);break;
		case keyRIGHT:		fnRight_(target);break;
		case keyRND:		fnRnd(target);break;
		case keyRTRIM:		fnRTrim(target);break;
		case keySHELL:		fnShell(target);break;
		case keySEEK:		fnSeek(target);break;
		case keySGN:		fnSgn(target);break;
		case keySIN:		fnSin(target);break;
		case keySPACE:		fnSpace_(target);break;
		case keySPC:		fnSpc(target);break;
		case keySQR:		fnSqr(target);break;
		case keySTR:		fnStr_(target);break;
		case keySTRIG:		fnStrig(target);break;
		case keySTRING:		fnString_(target);break;
		case keyTAN:		fnTan(target);break;
		case keyTIME:		fnTime_(target);break;
		case keyUCASE:		fnUCase_(target);break;
		case keyVAL:		fnVal(target);break;
		case keyREGISTRY:	fnRegistry(target);break;
		case keyLSET:		fnLSet(target);break;
		case keyRSET:		fnRSet(target);break;
		}
	}
	
	public void fnAbs(BasicVariable target) {
		BasicVariable var;
		double d;

		this.parse.expectToken('(');
		var = this.parse.expr();
		this.parse.expectToken(')');
		d=var.GetDouble();
		target.edit(var);
		if(d<0)
			target.edit(-d);
	}

	public void fnAsc(BasicVariable target) {
		String str;

		BasicVariable var;
		this.parse.expectToken('(');
		var=this.parse.expr();
		this.parse.expectToken(')');

		if(var.getObjectType()==BasicTypes.typeCharacter)
		{
			target.edit((short)var.GetCharacter());
		}
		else
		{
			str=var.GetStr();
			target.edit((short)str.charAt(0));
		}

	}

	public void fnAtn(BasicVariable target) {
		BasicVariable var;
		this.parse.expectToken('(');
		var=this.parse.expr();
		this.parse.expectToken(')');
		target.edit(Math.atan(var.GetDouble()));
	}

	public void fnCDbl(BasicVariable target) {
		BasicVariable var;
		this.parse.expectToken('(');
		var = this.parse.expr();
		this.parse.expectToken(')');

		switch(var.getObjectType())
		{
		case typeString:target.edit((double)Double.parseDouble(var.GetStr()));break;
		case typeFloat:target.edit((double)var.GetFloat());break;
		case typeInteger:target.edit((double)var.GetShort());break;
		case typeLong:target.edit((double)var.GetLong());break;
		case typeDouble:target.edit((double)var.GetDouble());break;
		case typeByte:target.edit((double)var.GetByte());break;
		case typeBoolean:target.edit((double)(var.GetBoolean()?-1:0));break;
		default:throw(new BasicError(ErrorNumbers.errorType));
		}

	}

	public void fnChr(BasicVariable target) {
		BasicVariable var;
		String str;

		this.parse.lookAhead('$');// Check for optional $
		this.parse.expectToken('(');
		var = this.parse.expr();
		this.parse.expectToken(')');
		target.edit( (char)var.GetShort() );
	}

	public void fnCInt(BasicVariable target) {
		BasicVariable var;
		this.parse.expectToken('(');
		var = this.parse.expr();
		this.parse.expectToken(')');

		switch(var.getObjectType())
		{
		case typeString:target.edit((short)Double.parseDouble(var.GetStr()));break;
		case typeFloat:target.edit((short)var.GetFloat());break;
		case typeInteger:target.edit((short)var.GetShort());break;
		case typeLong:target.edit((short)var.GetLong());break;
		case typeDouble:target.edit((short)var.GetDouble());break;
		case typeByte:target.edit((short)var.GetByte());break;
		case typeBoolean:target.edit((short)(var.GetBoolean()?-1:0));break;
		default:throw(new BasicError( ErrorNumbers.errorType));
		}
	}

	public void fnCLng(BasicVariable target) {
		BasicVariable var;
		this.parse.expectToken('(');
		var = this.parse.expr();
		this.parse.expectToken(')');

		switch(var.getObjectType())
		{
		case typeString:target.edit((long)Double.parseDouble(var.GetStr()));break;
		case typeFloat:target.edit((long)var.GetFloat());break;
		case typeInteger:target.edit((long)var.GetShort());break;
		case typeLong:target.edit((long)var.GetLong());break;
		case typeDouble:target.edit((long)var.GetDouble());break;
		case typeByte:target.edit((long)var.GetByte());break;
		case typeBoolean:target.edit((long)(var.GetBoolean()?-1:0));break;
		default:throw(new BasicError(ErrorNumbers.errorType));
		}

	}

	public void fnCos(BasicVariable target) {
		BasicVariable var;
		this.parse.expectToken('(');
		var=this.parse.expr();
		this.parse.expectToken(')');
		target.edit(Math.cos(var.GetDouble()));		
	}

	public void fnDate_(BasicVariable target) {
		this.parse.lookAhead('$');// Check for optional $
		throw(new BasicError(ErrorNumbers.errorNotYet));
	}

	public void fnEnvron_(BasicVariable target) {
		BasicVariable var;
		
		this.parse.lookAhead('$');// Check for optional $
		this.parse.expectToken('(');
		var = this.parse.expr();
		this.parse.expectToken(')');
		target.edit(System.getenv(var.GetStr()));
	}

	public void fnEof(BasicVariable target) {
		throw(new BasicError(ErrorNumbers.errorNotYet));
	}

	public void fnErr(BasicVariable target) {
		throw(new BasicError(ErrorNumbers.errorNotYet));
	}

	public void fnError(BasicVariable target) {
		throw(new BasicError(ErrorNumbers.errorNotYet));
	}

	public void fnExp(BasicVariable target) {
		BasicVariable var;
		this.parse.expectToken('(');
		var=this.parse.expr();
		this.parse.expectToken(')');
		target.edit(Math.exp(var.GetDouble()));
	}

	public void fnFileattr(BasicVariable target) {
		throw(new BasicError(ErrorNumbers.errorNotYet));
	}

	public void fnFix(BasicVariable target) {
		BasicVariable var;
		this.parse.expectToken('(');
		var = this.parse.expr();
		this.parse.expectToken(')');
		target.edit((long)var.GetLong());
	}

	public void fnFreeFile(BasicVariable target) {
		throw(new BasicError(ErrorNumbers.errorNotYet));
	}

	public void fnHex_(BasicVariable target) {
		String dest;
		BasicVariable var;

		this.parse.lookAhead('$');// Check for optional $
		this.parse.expectToken('(');
		var = this.parse.expr();
		this.parse.expectToken(')');
		dest = Long.toHexString(var.GetLong());
		target.edit(dest);
	}

	public void fnInput_(BasicVariable target) {
		throw(new BasicError(ErrorNumbers.errorNotYet));
	}

	public void fnInStr(BasicVariable target) {
		BasicVariable var1,var2,var3,var4;
		int start=0;
		String str1,str2;
		int ptr;

		this.parse.expectToken('(');
		var1 = this.parse.expr();
		if(var1.getObjectType()!=BasicTypes.typeString)
		{
			start=var1.GetShort();
			if(start==0)
				throw(new BasicError(ErrorNumbers.errorIllegalUse));
			start--;
			this.parse.expectToken(',');
			var2 = this.parse.expr();
			this.parse.expectToken(',');
			var3 = this.parse.expr();

			str1 = var2.GetStr();
			str2 = var3.GetStr();
		}
		else
		{
			this.parse.expectToken(',');
			var2 = this.parse.expr();
		
			str1 = var1.GetStr();
			str2 = var2.GetStr();
		}

		if(this.parse.lookAhead(','))
		{
			var4 = this.parse.parseVariable(0,1);
			if(var4.GetShort()==1)
			{
				str1 = str1.toUpperCase();
				str2 = str2.toUpperCase();
			}
		}

		this.parse.expectToken(')');

		if(start>=(long)str1.length())
		{
			target.edit((short)0);
			return;
		}

		ptr= str1.indexOf(str2,start);

		target.edit( ptr+1 );
	}

	public void fnInt(BasicVariable target) {
		BasicVariable var;
		this.parse.expectToken('(');
		var = this.parse.expr();
		this.parse.expectToken(')');
		if(var.GetLong()<0)
		{
			if( var.GetLong() == var.GetDouble() )
				target.edit((long)var.GetLong());
			else
				target.edit((long)(var.GetLong()-1));
		}
		else
		{
			target.edit((long)var.GetLong());
		}

	}

	public void fnLCase(BasicVariable target) {
		String dest;
		BasicVariable var;

		this.parse.lookAhead('$');// Check for optional $
		this.parse.expectToken('(');
		var = this.parse.expr();
		this.parse.expectToken(')');
		dest = var.GetStr().toLowerCase();
		target.edit(dest);
	}

	public void fnLeft(BasicVariable target) {
		BasicVariable varObj1,varObj2;
		String dest;

		this.parse.lookAhead('$');// Check for optional $
		this.parse.expectToken('(');
		varObj1 = this.parse.expr();
		this.parse.expectToken(',');
		varObj2 = this.parse.expr();
		this.parse.expectToken(')');

		if(varObj2.GetShort()==0)
		{
			target.edit("");
			return;
		}

		if(varObj2.GetShort()>=(long)(varObj1.GetStr().length()) )
		{
			target.edit(varObj1);
			return;
		}
		else 
		{
			dest = varObj1.GetStr().substring(0,varObj2.GetShort());
			target.edit(dest);
		}

	}

	public void fnLen(BasicVariable target) {
		BasicVariable var;
		this.parse.expectToken('(');
		var = this.parse.expr();
		this.parse.expectToken(')');
		target.edit((long)var.GetStr().length());
	}

	public void fnLoc(BasicVariable target) {
		throw(new BasicError(ErrorNumbers.errorNotYet));		
	}

	public void fnLog(BasicVariable target) {
		BasicVariable var;
		this.parse.expectToken('(');
		var=this.parse.expr();
		this.parse.expectToken(')');
		target.edit(Math.log(var.GetDouble()));
	}

	public void fnLTrim(BasicVariable target) {
		
		BasicVariable var;

		this.parse.lookAhead('$');// Check for optional $
		this.parse.expectToken('(');
		var = this.parse.expr();
		this.parse.expectToken(')');
		String str = var.GetStr();
		
		int ptr = 0;
		
		while( Character.isWhitespace(str.charAt(ptr)) && ptr<str.length() )
			ptr++;
		
		target.edit(str.substring(ptr));
	}

	public void fnMid_(BasicVariable target) {
		BasicVariable varObj1 = null,varObj2 = null,varObj3 = null;
		int n;
		String dest;

		this.parse.lookAhead('$');// Check for optional $
		this.parse.expectToken('(');
		varObj1 = this.parse.expr();
		this.parse.expectToken(',');
		varObj2 = this.parse.expr();

		if(this.parse.lookAhead(','))
		{
			varObj3 = this.parse.expr();
			if(varObj3.GetShort()==0)
			{
				target.edit("");
				return;
			}

			n=3;
		}
		else n=2;

		if(n==3)
		{
			if( (varObj2.GetShort()+varObj3.GetShort()) <= varObj1.GetStr().length() )
			{
				int b = varObj2.GetShort()-1;
				int e = b+varObj3.GetShort();
				dest = varObj1.GetStr().substring(b,e);
				target.edit(dest);
				return;
			}
		}
		
		if(varObj2.GetShort()==0)
			throw(new BasicError(ErrorNumbers.errorIllegalValue));
		
		if( varObj2.GetShort()>varObj1.GetStr().length() )
		{
			target.edit("");
			return;
		}
		
		int b = varObj2.GetShort()-1;
		int e = varObj1.GetStr().length()-varObj2.GetShort()+1;
		
		dest = varObj1.GetStr().substring(b,e);
		target.edit(dest);
	}

	public void fnMsgBox(BasicVariable target) {
		BasicVariable a,b=null,c;
		int num=1;

		this.parse.expectToken('(');
		a = this.parse.expr();
		
		if(this.parse.lookAhead(',') )
		{
			num=2;
			b = this.parse.expr();
			if(this.parse.lookAhead(',') )
			{
				num=3;
				c = this.parse.expr();
			}
		}
		this.parse.expectToken(')');
		switch(num)
		{
			case 1:
				JOptionPane.showMessageDialog(null, a.GetStr(), "Encog", JOptionPane.PLAIN_MESSAGE);
				target.edit((long)0);
				break;
			case 2:
				if( b==null )
					throw new BasicError(ErrorNumbers.errorIllegalUse);
				JOptionPane.showMessageDialog(null, a.GetStr(), b.GetStr(), JOptionPane.PLAIN_MESSAGE);
				break;
			case 3:
				boolean r = JOptionPane.showConfirmDialog(null, a.GetStr(), b.GetStr(),
						JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
				target.edit(r);
				break;
			default:
				target.edit(0);
		}

	}

	public void fnOct_(BasicVariable target) {
		String dest;
		BasicVariable var;

		this.parse.lookAhead('$');// Check for optional $
		this.parse.expectToken('(');
		var = this.parse.expr();
		this.parse.expectToken(')');
		dest = Long.toOctalString(var.GetLong());
		target.edit(dest);
	}

	public void fnRight_(BasicVariable target) {
		BasicVariable varObj1,varObj2;
		String dest;

		this.parse.lookAhead('$');// Check for optional $
		this.parse.expectToken('(');
		varObj1 = this.parse.expr();
		this.parse.expectToken(',');
		varObj2 = this.parse.expr();
		this.parse.expectToken(')');

		if(varObj2.GetShort()==0)
		{
			target.edit("");
			return;
		}

		if(varObj2.GetShort()>=(long)(varObj1.GetStr().length()) )
		{
			target.edit(varObj1);
			return;
		}
		else 
		{
			int b = ((varObj1.GetStr().length())-varObj2.GetShort());
			int e = varObj2.GetShort()+b;
			dest = varObj1.GetStr().substring(b,e);
			target.edit(dest);
		}
	}

	public void fnRnd(BasicVariable target) {
		this.parse.expectToken('(');
		this.parse.expectToken(')');
		target.edit((double)Math.random());
	}

	public void fnRTrim(BasicVariable target) {
		BasicVariable var;

		this.parse.lookAhead('$');// Check for optional $
		this.parse.expectToken('(');
		var = this.parse.expr();
		this.parse.expectToken(')');
		String dest = var.GetStr();
		int ptr = dest.length()-1;
		
		while( Character.isWhitespace(dest.charAt(ptr)) && ptr>0 )
		{
			ptr--;
		}
		
		target.edit(dest.substring(0,ptr+1));

	}

	public void fnSeek(BasicVariable target) {
		throw(new BasicError(ErrorNumbers.errorNotYet));		

	}

	public void fnSgn(BasicVariable target) {
		BasicVariable var;

		this.parse.expectToken('(');
		var = this.parse.expr();
		this.parse.expectToken(')');

		if(var.GetLong()==0)
			target.edit((short)0);
		else
		if(var.GetLong()<0)
			target.edit((short)-1);
		else
			target.edit((short)1);
	}

	public void fnShell(BasicVariable target) {
		throw(new BasicError(ErrorNumbers.errorNotYet));	
	}

	public void fnSin(BasicVariable target) {
		BasicVariable var;
		this.parse.expectToken('(');
		var=this.parse.expr();
		this.parse.expectToken(')');
		target.edit(Math.sin(var.GetDouble()));
	}

	public void fnSpace_(BasicVariable target) {
		StringBuilder str = new StringBuilder();

		BasicVariable var;
		this.parse.lookAhead('$');// Check for optional $
		this.parse.expectToken('(');
		var = this.parse.expr();
		this.parse.expectToken(')');
		
		int count = var.GetShort();
		
		while( (count--)>0 )
			str.append(' ');
		
		target.edit(str.toString());
	}

	public void fnSpc(BasicVariable target) {
		throw(new BasicError(ErrorNumbers.errorNotYet));	
	}

	public void fnSqr(BasicVariable target) {
		BasicVariable var;
		this.parse.expectToken('(');
		var=this.parse.expr();
		this.parse.expectToken(')');
		target.edit(Math.sqrt(var.GetDouble()));

	}

	public void fnStr_(BasicVariable target) {
		String dest;
		BasicVariable var;
		
		this.parse.lookAhead('$');// Check for optional $
		this.parse.expectToken('(');
		var = this.parse.expr();
		this.parse.expectToken(')');
		
		target.edit(""+var.GetLong());
	}

	public void fnStrig(BasicVariable target) {
		throw(new BasicError(ErrorNumbers.errorNotYet));	
	}

	public void fnString_(BasicVariable target) {
		throw(new BasicError(ErrorNumbers.errorNotYet));	
	}

	public void fnTan(BasicVariable target) {
		BasicVariable var;
		this.parse.expectToken('(');
		var=this.parse.expr();
		this.parse.expectToken(')');
		target.edit(Math.tan(var.GetDouble()));
	}

	public void fnTime_(BasicVariable target) {
		throw(new BasicError(ErrorNumbers.errorNotYet));	
	}

	public void fnUCase_(BasicVariable target) {
		String dest;
		BasicVariable var;

		this.parse.lookAhead('$');// Check for optional $
		this.parse.expectToken('(');
		var = this.parse.expr();
		this.parse.expectToken(')');
		dest = var.GetStr().toUpperCase();
		target.edit(dest);	
	}

	public void fnVal(BasicVariable target) {
		BasicVariable var;
		this.parse.expectToken('(');
		var = this.parse.expr();
		this.parse.expectToken(')');
		target.edit((double)Double.parseDouble(var.GetStr()));
	}

	public void fnRegistry(BasicVariable target) {
		throw(new BasicError(ErrorNumbers.errorNotYet));	
	}	
	
	public void fnLSet(BasicVariable target) {
		BasicVariable a,b;
		StringBuilder str = new StringBuilder();

		this.parse.expectToken('(');

		a=this.parse.expr();
		this.parse.expectToken(',');
		b = this.parse.expr();
		int finalLen = b.GetShort();

		str.append(a.GetStr());
		
		if( finalLen>=a.GetStr().length() )
		{
			while(str.length()<finalLen)
				str.append(' ');
		}
		else
		{
			str.setLength(finalLen);
		}

		this.parse.expectToken(')');

		target.edit(str.toString());
	}
	
	public void fnRSet(BasicVariable target) {
		BasicVariable a,b;
		StringBuilder str = new StringBuilder();

		this.parse.expectToken('(');

		a=this.parse.expr();
		this.parse.expectToken(',');
		b = this.parse.expr();
		int finalLen = b.GetShort();

		str.append(a.GetStr());
		if( finalLen>=a.GetStr().length() )
		{
			while(str.length()<finalLen)
				str.insert(0, ' ');	
		}
		else
		{
			while( str.length()>finalLen)
				str.deleteCharAt(0);
		}

		this.parse.expectToken(')');

		target.edit(str.toString());
	}
}
