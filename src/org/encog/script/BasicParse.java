package org.encog.script;

import java.util.ArrayList;
import java.util.List;

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
		variables=new ArrayList();
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
			SetERR(n.getId());

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

			return DoError();
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
				variables.add(varObj);
				varObj.setLabel( var);
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

	BasicVariable CreateVariable(String var) {
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

	boolean CallSub() {
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

	void DisplayExpression(long l) {
		
	}

	void DoFileInput(long l) {
	}

	boolean DoError() {
		return false;
	}

	void SetERR(ErrorNumbers n) {
	}

	void Constant(BasicVariable v) {
	}

	void Variable(BasicVariable v) {
	}

	public BasicVariable Expr() {
		return null;
	}

	public BasicVariable Expr1() {
		return null;
	}

	public BasicVariable Expr1p5() {
		return null;
	}

	void ParseString(String str) {
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

	void RequestBreak() {
		requestBreak = true;
	}

	void go(String label) {
	}

	void gosub(String label) {
	}

	void rtn() {
	}

	boolean IsAssignment() {
		return false;
	}

	boolean EvaluateDo() {
		return false;
	}

	BasicVariable GetNextVariable(String n) {
		String str;

		if (n != null) {
			n = ParseVariable();
			return GetVariable(n);
		} else {
			n = ParseVariable();
			return GetVariable(n);
		}

	}

	BasicVariable GetVariable(String name) {
		return null;
	}

	boolean Scan(BasicVariable target) {
		return false;
	}

	boolean Update() {
		return false;
	}

	boolean Execute() {
		return false;
	}

	boolean NewObject() {
		return false;
	}

	BasicVariable CreateObject() {
		return null;
	}

	void CreateGlobals() {

	}

	void MoveNextLine() {
		do {
			currentLine = (BasicLine) currentLine.getNext();
		} while ((currentLine != null) && !LoadLine(currentLine.Command()));
	}

	void MovePastColen() {
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

	void Maint() {
		
	}

	boolean Call(String fn) {
		return false;
	}

	boolean EventCall(String fn, int x, int y, int z) {
		return false;
	}

	boolean Call() {
		return false;
	}

	void ParamaterList(BasicParse caller) {
	}

	void EventParamaterList(int x, int y, int z) {
	}

	void CallInternalFunction(KeyNames f, BasicVariable target) {
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
	private List variables;// Linked list of the variables, either local or a
							// pointer to globals in program
	private BasicModule module;
	private BasicLine currentLine;

}
