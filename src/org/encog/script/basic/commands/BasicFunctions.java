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

		this.parse.expectToken('(');

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
		case keyDATE:		fnDate(target);break;
		case keyENVIRON:	fnEnvron(target);break;
		case keyEOF:		fnEof(target);break;
		case keyERR:		fnErr(target);break;
		case keyERROR:		fnError(target);break;
		case keyEXP:		fnExp(target);break;
		case keyFILEATTR:	fnFileattr(target);break;
		case keyFIX:		fnFix(target);break;
		case keyFREEFILE:	fnFreeFile(target);break;
		case keyHEX:		fnHex(target);break;
		case keyINPUT:		fnInput(target);break;
		case keyINSTR:		fnInStr(target);break;
		case keyINT:		fnInt(target);break;
		case keyLCASE:		fnLCase(target);break;
		case keyLEFT:		fnLeft(target);break;
		case keyLEN:		fnLen(target);break;
		case keyLOC:		fnLoc(target);break;
		case keyLOG:		fnLog(target);break;
		case keyLTRIM:		fnLTrim(target);break;
		case keyMID:		fnMid(target);break;
		case keyMSGBOX:		fnMsgBox(target);break;
		case keyOCT:		fnOct(target);break;
		case keyRIGHT:		fnRight(target);break;
		case keyRND:		fnRnd(target);break;
		case keyRTRIM:		fnRTrim(target);break;
		case keySHELL:		fnShell(target);break;
		case keySEEK:		fnSeek(target);break;
		case keySGN:		fnSgn(target);break;
		case keySIN:		fnSin(target);break;
		case keySPACE:		fnSpace(target);break;
		case keySPC:		fnSpc(target);break;
		case keySQR:		fnSqr(target);break;
		case keySTR:		fnStr(target);break;
		case keySTRIG:		fnStrig(target);break;
		case keySTRING:		fnString(target);break;
		case keyTAN:		fnTan(target);break;
		case keyTIME:		fnTime(target);break;
		case keyUCASE:		fnUCase(target);break;
		case keyVAL:		fnVal(target);break;
		case keyREGISTRY:	fnRegistry(target);break;
		case keyLSET:		fnLSet(target);break;
		case keyRSET:		fnRSet(target);break;
		}
		this.parse.expectToken(')');
	}
	
	public void fnAbs(BasicVariable target) {
		BasicVariable var;
		double d;

		var = this.parse.expr();
		d=var.getDouble();
		target.edit(var);
		if(d<0)
			target.edit(-d);
	}

	public void fnAsc(BasicVariable target) {
		String str;

		BasicVariable var;
		var=this.parse.expr();

		if(var.getObjectType()==BasicTypes.typeCharacter)
		{
			target.edit((short)var.getCharacter());
		}
		else
		{
			str=var.getStr();
			target.edit((short)str.charAt(0));
		}

	}

	public void fnAtn(BasicVariable target) {
		BasicVariable var;
		var=this.parse.expr();
		target.edit(Math.atan(var.getDouble()));
	}

	public void fnCDbl(BasicVariable target) {
		BasicVariable var;
		var = this.parse.expr();

		switch(var.getObjectType())
		{
		case typeString:target.edit((double)Double.parseDouble(var.getStr()));break;
		case typeFloat:target.edit((double)var.getFloat());break;
		case typeInteger:target.edit((double)var.getShort());break;
		case typeLong:target.edit((double)var.getLong());break;
		case typeDouble:target.edit((double)var.getDouble());break;
		case typeByte:target.edit((double)var.getByte());break;
		case typeBoolean:target.edit((double)(var.getBoolean()?-1:0));break;
		default:throw(new BasicError(ErrorNumbers.errorType));
		}

	}

	public void fnChr(BasicVariable target) {
		BasicVariable var;
		String str;

		var = this.parse.expr();
		target.edit( (char)var.getShort() );
	}

	public void fnCInt(BasicVariable target) {
		BasicVariable var;
		var = this.parse.expr();

		switch(var.getObjectType())
		{
		case typeString:target.edit((short)Double.parseDouble(var.getStr()));break;
		case typeFloat:target.edit((short)var.getFloat());break;
		case typeInteger:target.edit((short)var.getShort());break;
		case typeLong:target.edit((short)var.getLong());break;
		case typeDouble:target.edit((short)var.getDouble());break;
		case typeByte:target.edit((short)var.getByte());break;
		case typeBoolean:target.edit((short)(var.getBoolean()?-1:0));break;
		default:throw(new BasicError( ErrorNumbers.errorType));
		}
	}

	public void fnCLng(BasicVariable target) {
		BasicVariable var;
		var = this.parse.expr();

		switch(var.getObjectType())
		{
		case typeString:target.edit((long)Double.parseDouble(var.getStr()));break;
		case typeFloat:target.edit((long)var.getFloat());break;
		case typeInteger:target.edit((long)var.getShort());break;
		case typeLong:target.edit((long)var.getLong());break;
		case typeDouble:target.edit((long)var.getDouble());break;
		case typeByte:target.edit((long)var.getByte());break;
		case typeBoolean:target.edit((long)(var.getBoolean()?-1:0));break;
		default:throw(new BasicError(ErrorNumbers.errorType));
		}

	}

	public void fnCos(BasicVariable target) {
		BasicVariable var;
		var=this.parse.expr();
		target.edit(Math.cos(var.getDouble()));		
	}

	public void fnDate(BasicVariable target) {
		throw(new BasicError(ErrorNumbers.errorNotYet));
	}

	public void fnEnvron(BasicVariable target) {
		BasicVariable var;
		var = this.parse.expr();
		target.edit(System.getenv(var.getStr()));
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
		var=this.parse.expr();
		target.edit(Math.exp(var.getDouble()));
	}

	public void fnFileattr(BasicVariable target) {
		throw(new BasicError(ErrorNumbers.errorNotYet));
	}

	public void fnFix(BasicVariable target) {
		BasicVariable var;
		var = this.parse.expr();
		target.edit((long)var.getLong());
	}

	public void fnFreeFile(BasicVariable target) {
		throw(new BasicError(ErrorNumbers.errorNotYet));
	}

	public void fnHex(BasicVariable target) {
		String dest;
		BasicVariable var;

		var = this.parse.expr();
		dest = Long.toHexString(var.getLong());
		target.edit(dest);
	}

	public void fnInput(BasicVariable target) {
		throw(new BasicError(ErrorNumbers.errorNotYet));
	}

	public void fnInStr(BasicVariable target) {
		BasicVariable var1,var2,var3,var4;
		int start=0;
		String str1,str2;
		int ptr;

		var1 = this.parse.expr();
		if(var1.getObjectType()!=BasicTypes.typeString)
		{
			start=var1.getShort();
			if(start==0)
				throw(new BasicError(ErrorNumbers.errorIllegalUse));
			start--;
			this.parse.expectToken(',');
			var2 = this.parse.expr();
			this.parse.expectToken(',');
			var3 = this.parse.expr();

			str1 = var2.getStr();
			str2 = var3.getStr();
		}
		else
		{
			this.parse.expectToken(',');
			var2 = this.parse.expr();
		
			str1 = var1.getStr();
			str2 = var2.getStr();
		}

		if(this.parse.lookAhead(','))
		{
			var4 = this.parse.parseVariable(0,1);
			if(var4.getShort()==1)
			{
				str1 = str1.toUpperCase();
				str2 = str2.toUpperCase();
			}
		}

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
		var = this.parse.expr();
		if(var.getLong()<0)
		{
			if( var.getLong() == var.getDouble() )
				target.edit((long)var.getLong());
			else
				target.edit((long)(var.getLong()-1));
		}
		else
		{
			target.edit((long)var.getLong());
		}

	}

	public void fnLCase(BasicVariable target) {
		String dest;
		BasicVariable var;

		var = this.parse.expr();
		dest = var.getStr().toLowerCase();
		target.edit(dest);
	}

	public void fnLeft(BasicVariable target) {
		BasicVariable varObj1,varObj2;
		String dest;

		varObj1 = this.parse.expr();
		this.parse.expectToken(',');
		varObj2 = this.parse.expr();

		if(varObj2.getShort()==0)
		{
			target.edit("");
			return;
		}

		if(varObj2.getShort()>=(long)(varObj1.getStr().length()) )
		{
			target.edit(varObj1);
			return;
		}
		else 
		{
			dest = varObj1.getStr().substring(0,varObj2.getShort());
			target.edit(dest);
		}

	}

	public void fnLen(BasicVariable target) {
		BasicVariable var;
		var = this.parse.expr();
		target.edit((long)var.getStr().length());
	}

	public void fnLoc(BasicVariable target) {
		throw(new BasicError(ErrorNumbers.errorNotYet));		
	}

	public void fnLog(BasicVariable target) {
		BasicVariable var;
		var=this.parse.expr();
		target.edit(Math.log(var.getDouble()));
	}

	public void fnLTrim(BasicVariable target) {
		
		BasicVariable var;

		var = this.parse.expr();
		String str = var.getStr();
		
		int ptr = 0;
		
		while( Character.isWhitespace(str.charAt(ptr)) && ptr<str.length() )
			ptr++;
		
		target.edit(str.substring(ptr));
	}

	public void fnMid(BasicVariable target) {
		BasicVariable varObj1 = null,varObj2 = null,varObj3 = null;
		int n;
		String dest;

		varObj1 = this.parse.expr();
		this.parse.expectToken(',');
		varObj2 = this.parse.expr();

		if(this.parse.lookAhead(','))
		{
			varObj3 = this.parse.expr();
			if(varObj3.getShort()==0)
			{
				target.edit("");
				return;
			}

			n=3;
		}
		else n=2;

		if(n==3)
		{
			if( (varObj2.getShort()+varObj3.getShort()) <= varObj1.getStr().length() )
			{
				int b = varObj2.getShort()-1;
				int e = b+varObj3.getShort();
				dest = varObj1.getStr().substring(b,e);
				target.edit(dest);
				return;
			}
		}
		
		if(varObj2.getShort()==0)
			throw(new BasicError(ErrorNumbers.errorIllegalValue));
		
		if( varObj2.getShort()>varObj1.getStr().length() )
		{
			target.edit("");
			return;
		}
		
		int b = varObj2.getShort()-1;
		int e = varObj1.getStr().length()-varObj2.getShort()+1;
		
		dest = varObj1.getStr().substring(b,e);
		target.edit(dest);
	}

	public void fnMsgBox(BasicVariable target) {
		BasicVariable a,b=null,c;
		int num=1;

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

		switch(num)
		{
			case 1:
				JOptionPane.showMessageDialog(null, a.getStr(), "Encog", JOptionPane.PLAIN_MESSAGE);
				target.edit((long)0);
				break;
			case 2:
				if( b==null )
					throw new BasicError(ErrorNumbers.errorIllegalUse);
				JOptionPane.showMessageDialog(null, a.getStr(), b.getStr(), JOptionPane.PLAIN_MESSAGE);
				break;
			case 3:
				boolean r = JOptionPane.showConfirmDialog(null, a.getStr(), b.getStr(),
						JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
				target.edit(r);
				break;
			default:
				target.edit(0);
		}

	}

	public void fnOct(BasicVariable target) {
		String dest;
		BasicVariable var;

		var = this.parse.expr();
		dest = Long.toOctalString(var.getLong());
		target.edit(dest);
	}

	public void fnRight(BasicVariable target) {
		BasicVariable varObj1,varObj2;
		String dest;

		varObj1 = this.parse.expr();
		this.parse.expectToken(',');
		varObj2 = this.parse.expr();

		if(varObj2.getShort()==0)
		{
			target.edit("");
			return;
		}

		if(varObj2.getShort()>=(long)(varObj1.getStr().length()) )
		{
			target.edit(varObj1);
			return;
		}
		else 
		{
			int b = ((varObj1.getStr().length())-varObj2.getShort());
			int e = varObj2.getShort()+b;
			dest = varObj1.getStr().substring(b,e);
			target.edit(dest);
		}
	}

	public void fnRnd(BasicVariable target) {
		target.edit((double)Math.random());
	}

	public void fnRTrim(BasicVariable target) {
		BasicVariable var;

		var = this.parse.expr();
		String dest = var.getStr();
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

		var = this.parse.expr();

		if(var.getLong()==0)
			target.edit((short)0);
		else
		if(var.getLong()<0)
			target.edit((short)-1);
		else
			target.edit((short)1);
	}

	public void fnShell(BasicVariable target) {
		throw(new BasicError(ErrorNumbers.errorNotYet));	
	}

	public void fnSin(BasicVariable target) {
		BasicVariable var;
		var=this.parse.expr();
		target.edit(Math.sin(var.getDouble()));
	}

	public void fnSpace(BasicVariable target) {
		StringBuilder str = new StringBuilder();

		BasicVariable var  = this.parse.expr();
		
		int count = var.getShort();
		
		while( (count--)>0 )
			str.append(' ');
		
		target.edit(str.toString());
	}

	public void fnSpc(BasicVariable target) {
		throw(new BasicError(ErrorNumbers.errorNotYet));	
	}

	public void fnSqr(BasicVariable target) {
		BasicVariable var;
		var=this.parse.expr();
		target.edit(Math.sqrt(var.getDouble()));
	}

	public void fnStr(BasicVariable target) {
		String dest;
		BasicVariable var;
		
		var = this.parse.expr();		
		target.edit(""+var.getLong());
	}

	public void fnStrig(BasicVariable target) {
		throw(new BasicError(ErrorNumbers.errorNotYet));	
	}

	public void fnString(BasicVariable target) {
		throw(new BasicError(ErrorNumbers.errorNotYet));	
	}

	public void fnTan(BasicVariable target) {
		BasicVariable var;
		var=this.parse.expr();
		target.edit(Math.tan(var.getDouble()));
	}

	public void fnTime(BasicVariable target) {
		throw(new BasicError(ErrorNumbers.errorNotYet));	
	}

	public void fnUCase(BasicVariable target) {
		BasicVariable var = this.parse.expr();
		String dest = var.getStr().toUpperCase();
		target.edit(dest);	
	}

	public void fnVal(BasicVariable target) {
		BasicVariable var;
		var = this.parse.expr();
		target.edit((double)Double.parseDouble(var.getStr()));
	}

	public void fnRegistry(BasicVariable target) {
		throw(new BasicError(ErrorNumbers.errorNotYet));	
	}	
	
	public void fnLSet(BasicVariable target) {
		BasicVariable a,b;
		StringBuilder str = new StringBuilder();

		a=this.parse.expr();
		this.parse.expectToken(',');
		b = this.parse.expr();
		int finalLen = b.getShort();

		str.append(a.getStr());
		
		if( finalLen>=a.getStr().length() )
		{
			while(str.length()<finalLen)
				str.append(' ');
		}
		else
		{
			str.setLength(finalLen);
		}

		target.edit(str.toString());
	}
	
	public void fnRSet(BasicVariable target) {
		BasicVariable a,b;
		StringBuilder str = new StringBuilder();

		a=this.parse.expr();
		this.parse.expectToken(',');
		b = this.parse.expr();
		int finalLen = b.getShort();

		str.append(a.getStr());
		if( finalLen>=a.getStr().length() )
		{
			while(str.length()<finalLen)
				str.insert(0, ' ');	
		}
		else
		{
			while( str.length()>finalLen)
				str.deleteCharAt(0);
		}

		target.edit(str.toString());
	}
}
