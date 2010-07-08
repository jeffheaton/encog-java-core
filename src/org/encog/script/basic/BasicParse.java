package org.encog.script.basic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.encog.script.basic.commands.BasicCommands;
import org.encog.script.basic.commands.BasicFunctions;


public class BasicParse implements Basic {

	public static final int MAX_VARIABLE_NAME = 80;
	private BasicCommands commands;
	private BasicFunctions functions;
	private int ifs;
	private Stack stack = new Stack();// The stack
	private String line;
	private int ptr;

	private long column;
	private BasicVariable is;// Current value of IS keyword
	private Map<String,BasicVariable> variables;// Linked list of the variables, either local or a
							// pointer to globals in program
	private BasicModule module;
	private BasicLine currentLine;
	private List<BasicLine> subLines;


	BasicParse(BasicModule m) {
		Init();
		module=m;
		this.commands = new BasicCommands(this);
		this.functions = new BasicFunctions(this);
	}

	BasicParse() {
		module=null;
		currentLine=null;
		Init();
	}

	// Parse Functions
	public void advance() {

		if( (ptr+1)>=this.line.length())
			nextchar = 0;
		else
			nextchar=this.line.charAt(++ptr);
	}

	public char getchr() {
		char rtn;
		
		if( nextchar==0 )
			return 0;
		
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

	public boolean ParseVariable(BasicVariable var) {
		kill_space();
		BasicVariable temp = Expr();
		var.edit(temp);
		return var.GetBoolean();
	}

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

		if( lookForIndex!=lookfor2.length() )
			return false;

		if(!partial && currentIndex<this.line.length() )
		{
			char ch = line.charAt(currentIndex);
			if(  (ch!=32) && (ch!='\t') && Character.isLetterOrDigit(ch) )
				return false;
		}
			
		ptr=currentIndex;
		advance();
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
		variables=new HashMap<String,BasicVariable>();
		ifs=0;
		column=0;
	}

	public boolean parse(String l) {
		BasicKey key;

		Maint();
		try {
			if (!LoadLine(l))
				return true;

			do {
				do {
					kill_space();
				} while (LookAhead(':'));
				if (nextchar == 0)
					return true;

				key = ParseNextToken();
				if (key == null) {
					if (IsAssignment()) {
						DoAssignment();
						continue;
					} else if (this.module.getProgram().Execute()) {
						continue;
					} else if (CallSub()) {
						LookAhead(')');
						continue;
					} else
						throw (new BasicError(ErrorNumbers.errorSyntax));
				}

				if (key.getType() != KeyTypes.keyStatement
						&& (key.getId() != KeyNames.keyMSGBOX))
					throw (new BasicError(ErrorNumbers.errorKeyword));

				if( !this.commands.process(key) )
					return false;

			} while (nextchar == ':');
		} catch (BasicError n) {
			//SetERR(n.getId());

			n.setLine(this.currentLine);
			
			if (errorLabel != null && errorLabel.charAt(0) == '*')
				return true;

			// is the error to be handled?
			if (errorLabel != null) {
				if (this.module.getProgramLabels().containsKey(errorLabel)) {
					go(errorLabel);
					return false;
				}
			}
			// not handle so throw as a "hard error" (usual case)
			else
				throw (n);

		}

		return true;

	}

	void DoAssignment() {
		BasicVariable varObj;
		String var;

			// First, see if this even looks like an assignment

			// See if we're updating something elsewhere
			
			if(this.module.getProgram().Update())
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
			varObj.edit( Expr() );
			variables.put(var, varObj);

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
			case keyDIM:this.commands.CmdDim();break;
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
				v=this.module.getProgram().CreateObject();
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
		if(this.module.getProgram().Call(var,ignore))
			return true;

		ptr=hold;
		nextchar=this.line.charAt(ptr);
		return false;
	}

	public String FormatExpression() {
		StringBuilder result = new StringBuilder();
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
					return result.toString();

				space=10-(column%10);
				l--;
				space=space+(l*10);
				column+=space;

				while( (space--)>0 )
				{
					result.append(" ");
				}

				return result.toString();
			}

			a = Expr();
			str = a.ToString();

			column+=str.length();
			result.append(str);
			return result.toString();
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

			if(this.module.getProgram().Scan(result))
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
					functions.callInternalFunction(key.getId(),result);
					return result;
				}
				throw(new BasicError(ErrorNumbers.errorKeyword));
			}

			// Check for user defined function

			if(this.module.getProgram().Call(varName,result))
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
			target = Variable(target);
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
				else
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
		} while ((Character.isLetterOrDigit(nextchar) || (nextchar == '_') )
				&& nextchar != -1 && (i++ < MAX_VARIABLE_NAME));

		if (i >= MAX_VARIABLE_NAME)
			throw (new BasicError(ErrorNumbers.errorLongName));

		return result.toString();
	}

	private boolean IsAssignment() {
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
			rtn=(BasicVariable)this.module.getProgram().getGlobals().get(name);
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
				if(x<1)
					throw(new BasicError(ErrorNumbers.errorDim));
				if(LookAhead(','))
				{
					varObj = ParseVariable(0,0);
					y=varObj.GetShort();
					if(y<1)
						throw(new BasicError(ErrorNumbers.errorDim));
					if(LookAhead(','))
					{
						varObj = ParseVariable(0,0);
						z=varObj.GetShort();
						if(z<1)
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
			int num = currentLine.getNumber();
			num++;
			if( num>=subLines.size() )
				currentLine = null;
			else
				currentLine=subLines.get(num);
			
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
		this.module.getProgram().Maint();
	}

	public boolean Call(String var) {
		boolean isFunct;
		String varName = null;
		BasicParse caller;

		caller = this.module.getProgram().getFunction();
		
		currentLine= module.FindFunction(var);
		
		if(currentLine==null)
			return false;

		boolean callingMain = (caller==null);

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
		
		// Actually run the program

		subLines = this.currentLine.getSub(); 
			
		if(subLines.size()==0)
			return false;
		
		currentLine=subLines.get(0);
		
		while(currentLine!=null && (!this.module.getProgram().getQuitProgram()) )
		{
			if(parse(currentLine.Command()))
			{
				MoveNextLine();
			}
		}
		return true;
	}

	public boolean EventCall(String var, int x, int y, int z) {
		
		currentLine=this.module.FindFunction(var);
		if(currentLine==null)
			return false;

		EventParamaterList(x,y,z);

		currentLine=(BasicLine)currentLine.getSub().get(0);
		if(currentLine==null)
			return false;
		
		// Actually run the program
		while(currentLine!=null && (!this.module.getProgram().getQuitProgram() ))
		{
			if(parse(currentLine.Command()))
				currentLine=(BasicLine)currentLine.getSub().get(0);
		}
		return true;
	}

	// The following call is only used to run the first pass to check
	// for global data.
	public boolean Call() {
		this.variables = this.module.getProgram().getGlobals();

		for(BasicLine currentLine : this.module.getProgramLines() )
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




	String errorLabel;
	char nextchar;// The next character to be parsed

	public boolean LoadLine(String l) {
		
		this.module.getProgram().setFunction(this);

		if(l==null || l.length()==0)
			return false;
		
		this.line = BasicUtil.basicToUpper(l);
		this.ptr = 0;
		this.nextchar = this.line.charAt(0);
		
		return true;
	}

	public void MoveToEndIf(boolean stopOnElse) {
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
						this.commands.CmdIf();
						return;
					}
				}

				MovePastColen();

			} while(nextchar>0);

			MoveNextLine();
		}
	}

	public void MoveToNext() {
		while(currentLine!=null)
		{
			do 
			{
				if( LookAhead(KeyNames.keyFOR,false) )
					MoveToNext();

				if( LookAhead(KeyNames.keyEND,false) )
				{
					kill_space();
					if(!LookAhead(KeyNames.keyIF,false))
						throw(new BasicError(ErrorNumbers.errorBlock));
				} 

				if(LookAhead(KeyNames.keyNEXT,false))
					return;

				MovePastColen();

			} while(nextchar>0);

			MoveNextLine();
		}
	}

	public void MoveToWEnd() {
		while(currentLine!=null)
		{
			do 
			{
				if( LookAhead(KeyNames.keyWHILE,false) )
					MoveToWEnd();

				if(LookAhead(KeyNames.keyEND,false))
				{
					kill_space();
					if(!LookAhead(KeyNames.keyIF,false))
						throw(new BasicError(ErrorNumbers.errorBlock));
				} 

				if(LookAhead(KeyNames.keyWEND,false) )
					return;

				MovePastColen();

			} while(nextchar>0);

			MoveNextLine();
		}
	}

	public void MoveToLoop() {
		while(currentLine!=null)
		{
			do 
			{
				if( LookAhead(KeyNames.keyDO,false) )
					MoveToLoop();

				if(LookAhead(KeyNames.keyEND,false))
				{
					kill_space();
					if(!LookAhead(KeyNames.keyIF,false))
						throw(new BasicError(ErrorNumbers.errorBlock));
				} 

				if(LookAhead(KeyNames.keyLOOP,false) )
					return;

				MovePastColen();

			} while(nextchar>0);

			MoveNextLine();
		}

	}

	public void MoveToEndCase() {
		while(currentLine!=null)
		{
			do 
			{
				if( LookAhead(KeyNames.keySELECT,false) )
					MoveToEndCase();

				if(LookAhead(KeyNames.keyEND,false))
				{
					kill_space();
					if(!LookAhead(KeyNames.keyCASE,false))
						return;
				} 

				MovePastColen();

			} while(nextchar>0);

			MoveNextLine();
		}
	}

	public int getNextChar() {
		return this.nextchar;
	}

	public BasicModule getModule() {
		return this.module;
	}

	/**
	 * @return the column
	 */
	public long getColumn() {
		return column;
	}

	/**
	 * @param column the column to set
	 */
	public void setColumn(long column) {
		this.column = column;
	}

	public void increaseIFS() {
		this.ifs++;
	}
	
	public void decreaseIFS() {
		if( this.ifs==0 )
			throw new BasicError(ErrorNumbers.errorNoIf);
		this.ifs--;
	}

	public int getPtr() {
		return this.ptr;
	}

	public String getLine() {
		return this.line;
	}
	
	public BasicLine getCurrentLine()
	{
		return this.currentLine;
	}
	
	public Stack getStack()
	{
		return this.stack;
	}

	public void setCurrentLine(BasicLine currentLine) {
		this.currentLine = currentLine;
		if( this.currentLine!=null)
			LoadLine(this.currentLine.getText());
	}

	public void parse() {
		if( this.getCurrentLine()!=null)
			parse(this.getCurrentLine().Command());
		
	}

	public void addVariable(String var, BasicVariable varObj) {
		this.variables.put(var, varObj);		
	}
	
	public void go(String label)
	{
		String key = label.toUpperCase();
		
		if( !this.getModule().getProgramLabels().containsKey(key) )
			throw(new BasicError(ErrorNumbers.errorLabel));

		BasicLine line = this.getModule().getProgramLabels().get(key);
		setCurrentLine(line);
	}

	/**
	 * @return the errorLabel
	 */
	public String getErrorLabel() {
		return errorLabel;
	}

	/**
	 * @param errorLabel the errorLabel to set
	 */
	public void setErrorLabel(String errorLabel) {
		this.errorLabel = errorLabel;
	}
	
	
}
