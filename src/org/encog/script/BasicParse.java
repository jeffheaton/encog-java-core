package org.encog.script;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BasicParse extends Basic {

	public static final int MAX_VARIABLE_NAME = 80;

	String name;

	BasicParse(BasicModule m) {
		Init();
		module=m;
	}

	BasicParse() {
		module=null;
		currentLine=null;
		Init();
	}

	// Parse Functions
	public void advance() {
		if(ptr==this.line.length())
			nextchar = 0;
		if(ptr>=this.line.length())
			throw(new BasicError(ErrorNumbers.errorEOL));
		else
			nextchar=this.line.charAt(ptr++);
	}

	public char getchr() {
		char rtn;
		rtn=nextchar;
		advance();
		return rtn;
	}

	public void kill_space() {
		while( nextchar>0 && ((nextchar==32) || (nextchar=='\t')  ) )
			advance();

	}

	public BasicVariable ParseVariable( int l, int u) {
		BasicVariable result = null;
		
		kill_space();
		result = Expr();
		if( l==0 && u==0 )
			return result;
		if( (result.GetLong()<l) || (result.GetLong()>u) )
			throw(new BasicError(ErrorNumbers.errorIllegalValue));
		return result;
	}

	/*public boolean ParseVariable(BasicVariable var) {
		kill_space();
		Expr(var);
		return var.GetBoolean();
	}*/

	public void ExpectToken(char ch) {
		if(!LookAhead(ch))
			throw(new BasicError(ErrorNumbers.errorIllegalUse));

	}

	public boolean LookAhead(char ch) {

		if(ptr==-1)
			return false;
		kill_space();

		if(nextchar==ch)
		{
			advance();
			return true;
		}
		return false;
	}

	public boolean LookAhead(String lookfor, boolean partial) {

		if(ptr==-1)
			return false;

		String lookfor2 = lookfor.toUpperCase();

		kill_space();
		
		int lookForIndex = 0;
		int currentIndex = this.ptr;
		
		while( lookForIndex<lookfor2.length() && currentIndex<this.line.length() )
		{
			if( Character.toUpperCase(lookfor.charAt(lookForIndex))!= 
				Character.toUpperCase(line.charAt(currentIndex)) )
				return false;
			
			lookForIndex++;
			currentIndex++;
			}

		if( lookForIndex==lookfor2.length() )
			return false;

		if(!partial && currentIndex<this.line.length() )
		{
			char ch = line.charAt(currentIndex);
			if(  (ch!=32) && (ch!='\t') && Character.isLetterOrDigit(ch) )
				return false;
		}
			
		ptr=currentIndex;
		nextchar=this.line.charAt(currentIndex);
		return true;

	}

	public boolean LookAhead(KeyNames token, boolean partial) {
		BasicKey key;
		key=BasicUtil.FindKeyword(token);
		return LookAhead(key.getName(),partial);
	}

	public BasicKey ParseNextToken() {
		String str;
		int hold=ptr;
		BasicKey rtn;

		kill_space();
		str = ParseVariable();
		if(str.length()<1)
			return null;

		str = str.toUpperCase();

		rtn=BasicUtil.FindKeyword(str);
		
		if(rtn!=null)
			return rtn;

		ptr=hold;
		nextchar=this.line.charAt(this.ptr);

		return null;
	}

	public void Init() {
		is=null;
		ptr = 0;
		line = "LINE";
		nextchar=0;
		requestBreak=false;
		variables=new HashMap<String,BasicVariable>();
		ifs=0;
		name = "<GLOBAL>";
		column=0;
		errorLabel="";
	}

	public boolean parse(String l) {
		BasicKey key;

		Maint();
		try
		{
			if(!LoadLine(l))
				return true;

			do
			{
				do
				{
					kill_space();
				} while(LookAhead(':') );
				if(nextchar==0)
					return true;

				key=ParseNextToken();
				if(key==null)
				{
					if( IsAssignment() )
					{
						DoAssignment();
						continue;
					}
					else
					if(BasicProgram.getInstance().Execute())
					{
						continue;
					}
					else
					if( CallSub() )
					{
						LookAhead(')');
						continue;
					}
					else
						throw(new BasicError(ErrorNumbers.errorSyntax));
				}

				if(key.getType()!=KeyTypes.keyStatement && (key.getId()!=KeyNames.keyMSGBOX))
					throw(new BasicError(ErrorNumbers.errorKeyword));

				switch(key.getId())
				{
				case keyBEEP:	CmdBeep();break;
				case keyCALL:	CmdCall();break;
				case keyCASE:	CmdCase();break;
				case keyCHDIR:	CmdChDir();break;
				case keyCLOSE:	CmdClose();break;
				case keyCLS:	CmdCls();break;
				case keyCONST:	CmdConst();break;
				case keyCREATELINK:CmdCreateLink();break;
				case keyDIM:	CmdDim();break;
				case keyDO:		CmdDo();break;
				case keyELSE:	CmdElse();break;
				case keyELSEIF:CmdElseIf();break;
				case keyEND:	return CmdEnd();
				case keyERASE:	CmdErase();break;
				case keyEXIT:	return CmdExit();
				case keyFOR:	CmdFor();break;
				case keyFUNCTION:CmdFunction();break;
				case keyGET:	CmdGet();break;
				case keyGOSUB:	CmdGosub();return false;
				case keyGOTO:	CmdGoto();return false;
				case keyINPUT:	CmdInput();break;
				case keyKILL:	CmdKill();break;
				case keyLET:	CmdLet();break;
				case keyLOAD:	CmdLoad();break;
				case keyLOCK:	CmdLock();break;
				case keyLOOP:	CmdLoop();break;
				case keyLSET:	CmdLSet();break;
				case keyMKDIR:	CmdMKDir();break;
				case keyMSGBOX: CmdMsgBox();break;
				case keyNAME:	CmdName();break;
				case keyNEXT:	CmdNext();break;
				case keyON:		return CmdOn();
				case keyOPEN:	CmdOpen();break;
				case keyOPTION:	CmdOption();break;
				case keyPRINT:	CmdPrint();break;
				case keyPUT:	CmdPut();break;
				case keyRANDOMIZE:CmdRandomize();break;
				case keyREDIM:	CmdReDim();break;
				case keyREM:	CmdRem();break;
				case keyRESET:	CmdReset();break;
				case keyRESUME:	CmdResume();break;
				case keyRETURN:	CmdReturn();break;
				case keyRMDIR:	CmdRmDir();break;
				case keyRSET:	CmdRSet();break;
				case keyRUN:	CmdRun("MAIN");return false;
				case keySEEK:	CmdSeek();break;
				case keySELECT:	CmdSelect();break;
				case keySHARED:	CmdShared();break;
				case keySLEEP:	CmdSleep();break;
				case keySTATIC:	CmdStatic();break;
				case keySTOP:	CmdStop();break;
				case keySUB:	CmdSub();break;
				case keyTYPE:	CmdType();break;
				case keyUNLOCK:	CmdUnLock();break;
				case keyWEND:	CmdWEnd();break;
				case keyWHILE:	CmdWhile();break;
				case keyWIDTH:	CmdWidth();break;
				case keyWRITE:	CmdWrite();break;
				case keySETREGISTRY:CmdSetRegistry();break;
				case keyIF:
					switch(CmdIf())
					{
					case 0:return false;
					case 1:return true;
					case 2:break;
					}
					break;
				}

			}while(nextchar==':');
		}
		catch( BasicError n )
		{
			//SetERR(n.getId());

			if(errorLabel.charAt(0)=='*')
				return true;

			if(errorLabel!=null)
			{			
				/*if( this.module.getProgram().FindLabel(errorLabel)!=NULL)
				{
					go(errorLabel);
					return false;
				}*/
			}

		}

		return true;

	}

	void DoAssignment() {
		BasicVariable varObj;
		String var;

			// First, see if this even looks like an assignment

			// See if we're updating something elsewhere
			
			if(BasicProgram.getInstance().Update())
				return;

			// Try and find a local or global variable
			var = ParseVariable();				
			varObj=GetVariable(var);
			
			
			if(varObj==null)
			{// Automatically "dim" a variable
				varObj=new BasicVariable();

				ExpectToken('=');
				varObj = Expr();
				variables.put(var,varObj);
				return;
			}

			// See if we're really updating an embedded object in an object
			if(varObj.getType()==BasicTypes.typeObject)
			{
				kill_space();
			
				if(LookAhead('.'))
				{
					if( !varObj.GetObject().Update() )
						throw(new BasicError(ErrorNumbers.errorUndefinedObject));
					return;
				}
			}

			ExpectToken('=');
			varObj = Expr();

	}

	public boolean GlobalParse(String l) {
		BasicKey key;

		if(!LoadLine(l))
			return true;

		do
		{
			while(nextchar==':')
				advance();

			kill_space();
			key=ParseNextToken();
			if(key==null)
				throw(new BasicError(ErrorNumbers.errorGlobal));
			if(key.getType()!=KeyTypes.keyStatement)
				throw(new BasicError(ErrorNumbers.errorGlobal));

			switch(key.getId())
			{
			case keyDIM:CmdDim();break;
			case keyFUNCTION:
			case keySUB:currentLine=null;return false;
			default:
				throw(new BasicError(ErrorNumbers.errorGlobal));
			}

		}while(nextchar==':');
			
		return true;
	}

	public BasicVariable CreateVariable(String var) {
		BasicVariable v;
		int x,y,z;
		BasicVariable varObj;
		BasicKey key;
		
		kill_space();
		x=y=z=0;
		if(LookAhead('('))
		{
			varObj = ParseVariable(0,0);
			x=varObj.GetShort();
			if(x==0)
				throw(new BasicError(ErrorNumbers.errorDim));
			if(LookAhead(','))
			{
				varObj = ParseVariable(0,0);
				y=varObj.GetShort();
				if(y==0)
					throw(new BasicError(ErrorNumbers.errorDim));
				if(LookAhead(','))
				{
					varObj = ParseVariable(0,0);
					z=varObj.GetShort();
					if(z==0)
						throw(new BasicError(ErrorNumbers.errorDim));
				}
			}
			ExpectToken(')');
		}

		kill_space();
		if(nextchar>0 && LookAhead(KeyNames.keyAS,false) )
		{
			kill_space();
			key=ParseNextToken();
		
			if(key==null)
			{
				v=BasicProgram.getInstance().CreateObject();
				if(v==null)
				{
					BasicObjectVariable vobj;

					if(LookAhead("STDFILE",false))
						vobj=new ObjectStdFile();
					else
						throw(new BasicError(ErrorNumbers.errorInvalidType));
					v=new BasicVariable(vobj);
				}
			}
			else
			switch(key.getId())
			{
			case keySTRING:v=new BasicVariable("");break;
			case keyREAL:v=new BasicVariable((float)0.0);break;
			case keyLONG:v=new BasicVariable((long)0);break;
			case keyINTEGER:v=new BasicVariable((short)0);break;
			case keyDOUBLE:v=new BasicVariable((double)0.0);break;
			case keyBYTE:v=new BasicVariable((byte)0);break;
			case keyBOOLEAN:v=new BasicVariable(false);break;
			case keyCHARACTER:v=new BasicVariable((char)' ');break;
			default:
				throw(new BasicError(ErrorNumbers.errorInvalidType));
			}
		}
		else 
		{
			//v=new BASIC_VARIABLE((long)0);
			throw(new BasicError(ErrorNumbers.errorExpectedAs));
		}
				
		v.setLabel(var);
		if(x>0)
			v.CreateArray(x,y,z);
		kill_space();
		return v;
	}

	public boolean CallSub() {
		String var;
		int hold=ptr;
		BasicVariable varObj;
		BasicVariable ignore = null;

		var = ParseVariable();
		
		// is it a function inside an object
		varObj=GetVariable(var);
		if(varObj!=null)
		{
			if(varObj.getType()==BasicTypes.typeObject)
			{
				kill_space();
				if(LookAhead('.'))
				{
					if( varObj.GetObject().Execute() )
						return true;
				}
			}
		}

		// Try and call a global function
		if(BasicProgram.getInstance().Call(var,ignore))
			return true;

		ptr=hold;
		nextchar=this.line.charAt(ptr);
		return false;
	}

	public void DisplayExpression(long h) {
		BasicVariable a;
		String str;
		BasicVariable var;
		long l,space;

				
			if(LookAhead(KeyNames.keyTAB,false))
			{
				ExpectToken('(');
				var = ParseVariable(0,255);
				ExpectToken(')');
				l=var.GetShort();
				if(l==0)
					return;

				space=10-(column%10);
				l--;
				space=space+(l*10);
				column+=space;

				while( (space--)>0 )
				{
					BasicProgram.getInstance().print(" ");
				}

				return;
			}

			a = Expr();
			str = a.ToString();

			column+=str.length();
			BasicProgram.getInstance().print(str);
	}

	public void DoFileInput(long l) {
	}

	public BasicVariable Constant() {
		BasicVariable target = new BasicVariable();
		double value,rtn;
		int fractlnth,exponent;
		char sign;
		boolean neg=false;
		String str = "";

			switch(nextchar)
			{
			case '\"':
				str = ParseString();
				target.edit(str);
				return target;

			case '-':
				advance();
				neg=true;
				break;

			case '+':
				advance();
				break;
			}

			value=0.0;
			fractlnth=0;
			exponent=0;
			sign='+';

		// whole number part

			while( Character.isDigit(nextchar) )
				value=(10.0*value) + (getchr()-'0');

		// Optional fractional
			if(nextchar=='.')
			{
				advance();

				while(  Character.isDigit(nextchar) )
					{
					value=(10.0*value) + (getchr()-'0');
					fractlnth++;
					}
				}

		// Optional exponent

			if(nextchar=='E')
			{
				advance();

				if( (nextchar=='+') || (nextchar=='-') )
				{
					advance();
					sign=getchr();
				}

				while(  Character.isDigit(nextchar) )
					exponent=(int)(10.0*exponent) + (getchr()-'0');
			}

			if(sign=='-')
				rtn=value*Math.exp( Math.log(10)*(-exponent-fractlnth));
			else	rtn=value*Math.exp( Math.log(10)*(exponent-fractlnth));

			if(neg)
				rtn=-rtn;
			target.edit(rtn);
			return target;
	}

	BasicVariable Variable(BasicVariable v) {
		BasicVariable result = new BasicVariable();
		String varName;
		BasicVariable var;
		BasicKey key;

			// Check for "module" function

			if(BasicProgram.getInstance().Scan(result))
				return result;

			
			// First check for built in function, or built-in constant

			varName = ParseVariable();
			key=BasicUtil.FindKeyword(varName);
			if(key!=null)
			{
				if(key.getType()==KeyTypes.keyConst)
				{
					switch(key.getId())
					{
					case keyTRUE:result.edit(true);break;
					case keyFALSE:result.edit(false);break;
					case keyIS:
						if(is==null)
							throw(new BasicError(ErrorNumbers.errorKeyword));
						result.edit(is);
						break;
					}
					return result;
				}

				if( (key.getType()==KeyTypes.keyFunction) )
				{
					CallInternalFunction(key.getId(),result);
					return result;
				}
				throw(new BasicError(ErrorNumbers.errorKeyword));
			}

			// Check for user defined function

			if(BasicProgram.getInstance().Call(varName,result))
					return result;

			// Check for actual variable

			var=GetVariable(varName);

			if(var==null)
			{// Undefined variable
				switch(result.getObjectType())
				{
				case typeUndefined:result.edit((short)0);break;
				case typeString:result.edit("");break;
				case typeFloat:
				case typeDouble:result.edit((double)0.0);break;
				case typeInteger:
				case typeLong:
				case typeByte:result.edit((long)0);break;
				case typeBoolean:result.edit(false);break;
				case typeCharacter:result.edit(' ');break;
				default:throw(new BasicError(ErrorNumbers.errorUnknownVariable));
				}
				return result;
			}
			else
			if(var.getObjectType()==BasicTypes.typeObject)
			{
				kill_space();
				if(LookAhead('.'))
				{
					if( !var.GetObject().Scan(result) )
						throw(new BasicError(ErrorNumbers.errorUndefinedObject));
					
					return result;
				}
			}
			
			result.edit(var);
			return result;
	}

	public BasicVariable Expr() {
		BasicVariable rtn = new BasicVariable();
		char sign;
		BasicVariable target = new BasicVariable();
		boolean b;

			kill_space();

			if( (nextchar=='+') || (nextchar=='-') )
				sign=getchr();
			else	sign='+';

			if(LookAhead("NOT",false))
				sign='N';

			if(rtn.getObjectType()!=BasicTypes.typeUndefined)
			{
				target.edit(rtn);// Force assignments to be of the same type by priming
				// target with the right type
			}

			target = Expr1();
			kill_space();

			if(sign=='-')
				BasicUtil.PerformNeg(target,target);
				
			if(sign=='N')
				target.edit(!target.GetBoolean());

			while( (nextchar=='&') || (nextchar=='+') || (nextchar=='-') || (this.line.indexOf("OR",ptr)==ptr) || (this.line.indexOf("AND",ptr)==ptr) )
			{
				if(LookAhead("OR",false))
				{
					BasicVariable t;
					t = Expr1();
					if(target.getObjectType()==BasicTypes.typeBoolean)
					{
						b = (target.GetBoolean()||t.GetBoolean());
						target.edit(b);
					}
					else
						target.edit(target.GetLong()|t.GetLong());
				}
				else
				if(LookAhead("AND",false))
				{
					BasicVariable t;
					t = Expr1();
					if(target.getObjectType()==BasicTypes.typeBoolean)
					{
						b = (target.GetBoolean()&&t.GetBoolean());
						target.edit(b);
					}
					else
						target.edit(target.GetLong()&t.GetLong());
				}
				else
				if( LookAhead('-') )
				{
					BasicVariable t;
					t = Expr1();

					BasicUtil.PerformSub(target,target,t);
				}
				else
				if( getchr()=='+')
				{
					BasicVariable t;
					BasicVariable t2 = new BasicVariable();
					t = Expr1();
					BasicUtil.PerformAdd(t2,target,t);
					target.Free();
					target.edit(t2);
				}
				else	
				{
					BasicVariable t;
					BasicVariable t2 = new BasicVariable();
					t = Expr1();
					BasicUtil.PerformCat(t2,target,t);
					target.Free();
					target.edit(t2);
				}

			}

//			if( (rtn.GetType()==typeUndefined) || forceRefresh )
				rtn.edit(target);
				return rtn;
	}

	public BasicVariable Expr1() {
		BasicVariable target = new BasicVariable();
		BasicVariable t;
		BasicVariable v = new BasicVariable();
		kill_space();

		target = Expr1p5();
		kill_space();

		if(LookAhead(KeyNames.keyMOD,false) )
		{
			t = Expr1p5();
			target.edit((long)(target.GetLong()%t.GetLong()));
			return target;
		}

		if(!( nextchar>0 && ("/*<>=".indexOf(nextchar) != -1)) )
			return target;

		while( nextchar>0 && ("/*<>=".indexOf(nextchar) != -1) )
			switch(getchr())
			{
				case '*':
					t = Expr1p5();
					BasicUtil.PerformMul(target,target,t);
					break;

				case '/':
					t = Expr1p5();
					if( t.GetDouble()==0)
						throw(new BasicError(ErrorNumbers.errorDivZero));
					BasicUtil.PerformDiv(target,target,t);
					break;

				case '=':
					v.edit(target);
					t = Expr1p5();
					target.Free();
					target.edit((boolean)(v.CompareE(t)));
					break;

				case '<':
					kill_space();
					if(nextchar=='>')
					{
						advance();
						v.edit(target);
						t = Expr1p5();	
						target.Free();
						target.edit(v.CompareNE(t));
					}
					else
					if(nextchar=='=')
					{
						advance();
						v.edit(target);
						t = Expr1p5();	
						target.Free();
						target.edit((boolean)(v.CompareLTE(t)));
					}
					else
					{
						v.edit(target);
						t = Expr1p5();
						target.Free();
						target.edit((boolean)(v.CompareLT(t)));
					}
					break;

				case '>':
					if(nextchar=='=')
					{
						advance();
						v.edit(target);
						t = Expr1p5();
						target.Free();
						target.edit((boolean)(v.CompareGTE(t)));
					}
					else
					{
						v.edit(target);
						t = Expr1p5();
						target.Free();
						target.edit(v.CompareGT(t));
					}
					break;

				};
				return target;
	}

	public BasicVariable Expr1p5() {
		BasicVariable target = new BasicVariable();
		
		kill_space();
		if( (nextchar>='A') && (nextchar<='Z') )
			Variable(target);
		else
		if( (nextchar=='+') || (nextchar=='-') || Character.isDigit(nextchar) || (nextchar=='.') || (nextchar=='\"') )
			target = Constant();
		else
		if( nextchar=='(' )
		{
			advance();
			target = Expr();
			if(nextchar==')')
				advance();
		}
		else
			throw(new BasicError(ErrorNumbers.errorSyntax));

		while(nextchar=='^')
		{
			advance();
			BasicVariable a = new BasicVariable();
			BasicVariable b;
			a.edit(target);
			b = Expr1p5();
			target.Free();
			BasicUtil.PerformPower(target,a,b);
		}
		return target;
	}

	public String ParseString() {
		StringBuilder str = new StringBuilder();
		
		char ch;
		int i=0;

			if(nextchar=='\"')
				advance();	
			do
			{
				ch=getchr();
				if(ch==34)
				{
					// handle double quote
					if(nextchar==34)
					{
						advance();
						str.append(ch);
						ch=getchr();
					}
				}
				str.append(ch);		
			} while( (ch!=34) && ch>0  );

			if(ch!=34)
				throw(new BasicError(ErrorNumbers.errorBadString));
			return str.toString();
	}

	/**
	 * Parse in a variable name.
	 * 
	 * @return A variable name.
	 */
	public String ParseVariable() {
		StringBuilder result = new StringBuilder();

		char ch;
		int i = 0;

		kill_space();
		if (nextchar == 0) {
			return "";
		}
		do {
			ch = getchr();
			result.append(ch);
		} while ((Character.isDigit(nextchar) || (nextchar == '_') || Character
				.isLetter(ch))
				&& nextchar != -1 && (i++ < MAX_VARIABLE_NAME));

		if (i >= MAX_VARIABLE_NAME)
			throw (new BasicError(ErrorNumbers.errorLongName));

		return result.toString();
	}

	public void RequestBreak() {
		requestBreak = true;
	}

	public void go(String label) {
		if(module==null)
			throw(new BasicError(ErrorNumbers.errorModule));
		currentLine=module.Go(label);	
	}

	boolean IsAssignment() {
		int p;

		p=ptr;

		// Move to first whitespace char
		while(p<this.line.length())
		{
			char ch = this.line.charAt(p);
			if( (ch==' ') || (ch=='\t') || (ch=='=') )
				break;
			p++;
		}
		if(p>=line.length())
			return false;

		// Move past any whitespace
		while(p<line.length() && (line.charAt(p)==' ') || (line.charAt(p)=='\t') )
			p++;
		
		if(p>=line.length())
			return false;
		
		// Is it an assignment

		if(line.charAt(p)!='=')
			return false;

		return true;
	}

	public boolean EvaluateDo() {
		BasicVariable varObj;
		boolean not;

		kill_space();

		if(LookAhead(KeyNames.keyWHILE,false))
			not=false;
		else
		if(LookAhead(KeyNames.keyUNTIL,false) )
			not=true;
		else
			throw(new BasicError(ErrorNumbers.errorIllegalUse));

		varObj=Expr();
		if(not)
		{
			if(varObj.GetBoolean())
				return false;
			else			
				return true;
		}
		else
		{
			if(varObj.GetBoolean())
				return true;
			else
				return false;
		}
	}

	public BasicVariable GetNextVariable(String n) {
		String str;

		if (n != null) {
			n = ParseVariable();
			return GetVariable(n);
		} else {
			n = ParseVariable();
			return GetVariable(n);
		}

	}

	public BasicVariable GetVariable(String name) {
		BasicVariable rtn;
		
		rtn=variables.get(name);	
		if(rtn==null)
			rtn=(BasicVariable)BasicProgram.getInstance().getGlobals().get(name);
		if(rtn==null)
			return null;
		if(rtn.IsArray())
		{
			kill_space();
			if(LookAhead('('))
			{
				BasicVariable varObj;
				int x=0,y=0,z=0;

				varObj = ParseVariable(0,0);
				x=varObj.GetShort();
				if(x>0)
					throw(new BasicError(ErrorNumbers.errorDim));
				if(LookAhead(','))
				{
					varObj = ParseVariable(0,0);
					y=varObj.GetShort();
					if(y>0)
						throw(new BasicError(ErrorNumbers.errorDim));
					if(LookAhead(','))
					{
						varObj = ParseVariable(0,0);
						z=varObj.GetShort();
						if(z>0)
							throw(new BasicError(ErrorNumbers.errorDim));
					}
				}
				ExpectToken(')');

				rtn.SetArrayLocation(x,y,z);
			}
			else
				throw(new BasicError(ErrorNumbers.errorArray));
		}

		return rtn;
	}

	/**
	 * Will be defined in subclass.
	 */
	public boolean Scan(BasicVariable target) {
		
		return false;
	}

	/**
	 * Will be defined in subclass.
	 */
	public boolean Update() {
		return false;
	}

	/**
	 * Will be defined in subclass.
	 */
	public boolean Execute() {
		return false;
	}

	/**
	 * Will be defined in subclass.
	 */
	public boolean NewObject() {
		return false;
	}

	/**
	 * Will be defined in subclass.
	 */
	public BasicVariable CreateObject() {
		return null;
	}

	/**
	 * Will be defined in subclass.
	 */
	public void CreateGlobals() {

	}

	public void MoveNextLine() {
		do {
			currentLine = (BasicLine) currentLine.getNext();
		} while ((currentLine != null) && !LoadLine(currentLine.Command()));
	}

	public void MovePastColen() {
		boolean quote;
		
		kill_space();
		quote=false;
		while(nextchar>0 && ( quote || (nextchar!=':')) )
		{
			if(nextchar=='\"')
				quote=!quote;
			advance();
		}
		if(nextchar==':')
			advance();
	}

	
	public void Maint() {
		BasicProgram.getInstance().Maint();
	}

	public boolean Call(String var) {
		boolean callingMain,isFunct;
		String varName = null;
		BasicParse caller;

		caller = BasicProgram.getInstance().getFunction();
		
		currentLine= module.FindFunction(var);
		if(currentLine==null)
			return false;

		if( caller==null )
			callingMain=true;
		else
			callingMain=false;

		if(!LoadLine(currentLine.Command()))
			return false;

		if(!LookAhead(KeyNames.keyFUNCTION,false) )
		{
			if(LookAhead(KeyNames.keySUB,false) )
				isFunct=false;
			else
				throw(new BasicError(ErrorNumbers.errorFunctionProc));
		}
		else
			isFunct=true;
		

		if(!callingMain)
		{
			kill_space();
			if( nextchar>0 && 
				(nextchar!=':') )
			{

				varName = ParseVariable();
				kill_space();

				LookAhead('(');
				ParamaterList(caller);
				LookAhead(')');
			}

			kill_space();
			
			if( (this.line.charAt(this.ptr)=='A') && (this.line.charAt(this.ptr+1)=='S') )
			{
				BasicVariable v = CreateVariable(var);
				this.variables.put(varName, v);
			}
		}

		name = var;

		
		// Actually run the program
		int index = 0;		
		
		currentLine=this.currentLine.getSub().get(index++);
			
		if(currentLine==null)
			return false;
		
		while(currentLine!=null && (!BasicProgram.getInstance().getQuitProgram()) )
		{
			if(parse(currentLine.Command()))
				currentLine=this.currentLine.getSub().get(index++);
		}
		return true;
	}

	public boolean EventCall(String var, int x, int y, int z) {
		
		currentLine=this.module.FindFunction(var);
		if(currentLine==null)
			return false;

		EventParamaterList(x,y,z);
		name = var;

		currentLine=(BasicLine)currentLine.getSub().get(0);
		if(currentLine==null)
			return false;
		
		// Actually run the program
		while(currentLine!=null && (!BasicProgram.getInstance().getQuitProgram() ))
		{
			if(parse(currentLine.Command()))
				currentLine=(BasicLine)currentLine.getSub().get(0);
		}
		return true;
	}

	// The following call is only used to run the first pass to check
	// for global data.
	public boolean Call() {
		this.variables = BasicProgram.getInstance().getGlobals();

		for(BasicLine currentLine : this.module.getProgram() )
		{
			if(!GlobalParse(currentLine.Command()))
				break;
		}
		
		return true;

	}

	public void ParamaterList(BasicParse caller) {
		String varName;
		BasicVariable v;
		BasicVariable v2;

		while(nextchar>0 && (nextchar!=':') && (nextchar!=')') )
		{
			boolean byref=false;

			if(LookAhead(KeyNames.keyBYREF,false))
				byref=true;
			else
				LookAhead(KeyNames.keyBYVAL,false);

			varName = ParseVariable();

			if(byref)
			{
				v2=CreateVariable(varName);
				v2=new BasicVariable();
				
				if( (caller.nextchar==':') || caller.nextchar>0 )
					throw(new BasicError(ErrorNumbers.errorParamaters));

				v=caller.GetNextVariable(null);
				if(v==null)
					throw(new BasicError(ErrorNumbers.errorUndefinedVariable));
				v.CreateRef(v2);
				variables.put(varName,v2);
			}
			else
			{
				v=CreateVariable(varName);
				v = caller.Expr();
				variables.put(varName,v);
			}
			caller.kill_space();
			kill_space();
			if(nextchar==',')
			{
				caller.ExpectToken(',');
				caller.kill_space();
				ExpectToken(',');
				kill_space();
			}
		}
	}

	public void EventParamaterList(int x, int y, int z) {
		String varName;
		BasicVariable v;
		int count=0;

		if(!LoadLine(currentLine.Command()))
			return;

		if(!LookAhead(KeyNames.keyFUNCTION,false) && !LookAhead(KeyNames.keySUB,false) )
			throw(new BasicError(ErrorNumbers.errorFunctionProc));
		varName = ParseVariable();
		kill_space();
		if( nextchar!='(' )
			return;
		advance();
		kill_space();

		while(nextchar!=')' && nextchar>0)
		{
			LookAhead(KeyNames.keyBYREF,false);// Ignore
			LookAhead(KeyNames.keyBYVAL,false);// Ignore
			varName = ParseVariable();

			v=CreateVariable(varName);
			count++;
			switch(count)
			{
				case 1:v.edit((long)x);break;
				case 2:v.edit((long)y);break;
				case 3:v.edit((long)z);break;
			}
			variables.put(varName,v);

			kill_space();
			if(nextchar==',')
			{
				ExpectToken(',');
				kill_space();
			}
		}
	}

	void CallInternalFunction(KeyNames f, BasicVariable target) {
		switch(f)
		{
		case keyABS:		FnAbs(target);break;
		case keyASC:		FnAsc(target);break;
		case keyATN:		FnAtn(target);break;
		case keyCDBL:		FnCDbl(target);break;
		case keyCHR:		FnChr(target);break;
		case keyCINT:		FnCInt(target);break;
		case keyCLNG:		FnCLng(target);break;
		case keyCOS:		FnCos(target);break;
		case keyDATE:		FnDate_(target);break;
		case keyENVIRON:	FnEnvron_(target);break;
		case keyEOF:		FnEof(target);break;
		case keyERR:		FnErr(target);break;
		case keyERROR:		FnError(target);break;
		case keyEXP:		FnExp(target);break;
		case keyFILEATTR:	FnFileattr(target);break;
		case keyFIX:		FnFix(target);break;
		case keyFREEFILE:	FnFreeFile(target);break;
		case keyHEX:		FnHex_(target);break;
		case keyINPUT:		FnInput_(target);break;
		case keyINSTR:		FnInStr(target);break;
		case keyINT:		FnInt(target);break;
		case keyLCASE:		FnLCase(target);break;
		case keyLEFT:		FnLeft(target);break;
		case keyLEN:		FnLen(target);break;
		case keyLOC:		FnLoc(target);break;
		case keyLOG:		FnLog(target);break;
		case keyLTRIM:		FnLTrim(target);break;
		case keyMID:		FnMid_(target);break;
		case keyMSGBOX:		FnMsgBox(target);break;
		case keyOCT:		FnOct_(target);break;
		case keyRIGHT:		FnRight_(target);break;
		case keyRND:		FnRnd(target);break;
		case keyRTRIM:		FnRTrim(target);break;
		case keySHELL:		FnShell(target);break;
		case keySEEK:		FnSeek(target);break;
		case keySGN:		FnSgn(target);break;
		case keySIN:		FnSin(target);break;
		case keySPACE:		FnSpace_(target);break;
		case keySPC:		FnSpc(target);break;
		case keySQR:		FnSqr(target);break;
		case keySTR:		FnStr_(target);break;
		case keySTRIG:		FnStrig(target);break;
		case keySTRING:		FnString_(target);break;
		case keyTAN:		FnTan(target);break;
		case keyTIME:		FnTime_(target);break;
		case keyUCASE:		FnUCase_(target);break;
		case keyVAL:		FnVal(target);break;
		case keyREGISTRY:	FnRegistry(target);break;
		}
	}

	void FnAbs(BasicVariable target) {
	}

	void FnAsc(BasicVariable target) {
	}

	void FnAtn(BasicVariable target) {
	}

	void FnCDbl(BasicVariable target) {
	}

	void FnChr(BasicVariable target) {
	}

	void FnCInt(BasicVariable target) {
	}

	void FnCLng(BasicVariable target) {
	}

	void FnCos(BasicVariable target) {
	}

	void FnDate_(BasicVariable target) {
	}

	void FnEnvron_(BasicVariable target) {
	}

	void FnEof(BasicVariable target) {
	}

	void FnErr(BasicVariable target) {
	}

	void FnError(BasicVariable target) {
	}

	void FnExp(BasicVariable target) {
	}

	void FnFileattr(BasicVariable target) {
	}

	void FnFix(BasicVariable target) {
	}

	void FnFreeFile(BasicVariable target) {
	}

	void FnHex_(BasicVariable target) {
	}

	void FnInput_(BasicVariable target) {
	}

	void FnInStr(BasicVariable target) {
	}

	void FnInt(BasicVariable target) {
	}

	void FnLCase(BasicVariable target) {
	}

	void FnLeft(BasicVariable target) {
	}

	void FnLen(BasicVariable target) {
	}

	void FnLoc(BasicVariable target) {
	}

	void FnLog(BasicVariable target) {
	}

	void FnLTrim(BasicVariable target) {
	}

	void FnMid_(BasicVariable target) {
	}

	void FnMsgBox(BasicVariable target) {
	}

	void FnOct_(BasicVariable target) {
	}

	void FnRight_(BasicVariable target) {
	}

	void FnRnd(BasicVariable target) {
	}

	void FnRTrim(BasicVariable target) {
	}

	void FnSeek(BasicVariable target) {
	}

	void FnSgn(BasicVariable target) {
	}

	void FnShell(BasicVariable target) {
	}

	void FnSin(BasicVariable target) {
	}

	void FnSpace_(BasicVariable target) {
	}

	void FnSpc(BasicVariable target) {
	}

	void FnSqr(BasicVariable target) {
	}

	void FnStr_(BasicVariable target) {
	}

	void FnStrig(BasicVariable target) {
	}

	void FnString_(BasicVariable target) {
	}

	void FnTan(BasicVariable target) {
	}

	void FnTime_(BasicVariable target) {
	}

	void FnUCase_(BasicVariable target) {
	}

	void FnVal(BasicVariable target) {
	}

	void FnRegistry(BasicVariable target) {
	}

	void CmdBeep() {
	}

	void CmdCall() {
	}

	void CmdCase() {
	}

	void CmdChDir() {
	}

	void CmdClose() {
	}

	void CmdCls() {
	}

	void CmdConst() {
	}

	void CmdCreateLink() {
	}

	void CmdDim() {
	}

	void CmdDo() {
	}

	void CmdElse() {
	}

	void CmdElseIf() {
	}

	boolean CmdEnd() {
		return false;
	}

	boolean CmdEndIf() {
		return false;
	}

	void CmdEnviron() {
	}

	void CmdErase() {
	}

	boolean CmdExit() {
		return false;
	}

	void CmdFor() {
	}

	void CmdFunction() {
	}

	void CmdGet() {
	}

	void CmdGosub() {
	}

	void CmdGoto() {
	}

	int CmdIf() {
		return 0;
	}

	void CmdInput() {
	}

	void CmdKill() {
	}

	void CmdLet() {
	}

	void CmdLoad() {
	}

	void CmdLock() {
	}

	void CmdLoop() {
	}

	void CmdLSet() {
	}

	void CmdMsgBox() {
	}

	void CmdMKDir() {
	}

	void CmdName() {
	}

	void CmdNext() {
	}

	boolean CmdOn() {
		return false;
	}

	void CmdOnError() {
	}

	void CmdOpen() {
	}

	void CmdOption() {
	}

	void CmdPrint() {
	}

	void CmdPut() {
	}

	void CmdRandomize() {
	}

	void CmdRead() {
	}

	void CmdReDim() {
	}

	void CmdRem() {
	}

	void CmdReset() {
	}

	void CmdResume() {
	}

	void CmdReturn() {
	}

	void CmdRmDir() {
	}

	void CmdRSet() {
	}

	boolean CmdRun(String str) {
		return false;
	}

	void CmdSeek() {
	}

	void CmdSelect() {
	}

	void CmdShared() {
	}

	void CmdSleep() {
	}

	void CmdStatic() {
	}

	void CmdStop() {
	}

	void CmdSub() {
	}

	void CmdType() {
	}

	void CmdUnLock() {
	}

	void CmdWEnd() {
	}

	void CmdWhile() {
	}

	void CmdWidth() {
	}

	void CmdWrite() {
	}

	void CmdSetRegistry() {
	}

	String errorLabel;
	char nextchar;// The next character to be parsed

	boolean LoadLine(String l) {
		return false;
	}

	void MoveToEndIf(boolean stopOnElse) {
		while(currentLine!=null)
		{
			do 
			{
				if( LookAhead(KeyNames.keyIF,false) )
				{// Found a nested if, is it block or line?
					BasicVariable ignore = Expr();
					if( !LookAhead(KeyNames.keyTHEN, false) )
						throw(new BasicError(ErrorNumbers.errorNoThen));
					kill_space();
					if(nextchar==0 )
						MoveToEndIf(false);
				}

				if(LookAhead(KeyNames.keyEND,true))
				{
					kill_space();
					if(LookAhead(KeyNames.keyIF, false))
						return;
					else
						throw(new BasicError(ErrorNumbers.errorBlock));
				} 

				if(stopOnElse)
				{
					if( LookAhead(KeyNames.keyELSE, false) )
					{
						ifs++;
						return;
					}

					if( LookAhead(KeyNames.keyELSEIF,false) ) 
					{
						//ifs++;
						CmdIf();
						return;
					}
				}

				MovePastColen();

			} while(nextchar>0);

			MoveNextLine();
		}
	}

	void MoveToNext() {
	}

	void MoveToWEnd() {
	}

	void MoveToLoop() {
	}

	void MoveToEndCase() {
	}

	private int ifs;
	private Stack stack;// The stack
	private boolean requestBreak;
	private String line;
	private int ptr;// The line we're parsing and where we're at

	private long column;
	private BasicVariable is;// Current value of IS keyword
	private Map<String,BasicVariable> variables;// Linked list of the variables, either local or a
							// pointer to globals in program
	private BasicModule module;
	private BasicLine currentLine;

}
