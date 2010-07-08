package org.encog.script.basic.commands;

import org.encog.script.basic.BasicError;
import org.encog.script.basic.BasicKey;
import org.encog.script.basic.BasicLine;
import org.encog.script.basic.BasicParse;
import org.encog.script.basic.BasicUtil;
import org.encog.script.basic.BasicVariable;
import org.encog.script.basic.ErrorNumbers;
import org.encog.script.basic.KeyNames;
import org.encog.script.basic.StackEntry;
import org.encog.script.basic.StackEntryType;

public class BasicCommands {

	private BasicParse parse;
	
	public BasicCommands(BasicParse basicParse) {
		this.parse = basicParse;
	}
	
	public boolean process(BasicKey key)
	{
		switch (key.getId()) {
		case keyBEEP:
			CmdBeep();
			break;
		case keyCALL:
			CmdCall();
			break;
		case keyCASE:
			CmdCase();
			break;
		case keyCHDIR:
			CmdChDir();
			break;
		case keyCLOSE:
			CmdClose();
			break;
		case keyCLS:
			CmdCls();
			break;
		case keyCONST:
			CmdConst();
			break;
		case keyCREATELINK:
			CmdCreateLink();
			break;
		case keyDIM:
			CmdDim();
			break;
		case keyDO:
			CmdDo();
			break;
		case keyELSE:
			CmdElse();
			break;
		case keyELSEIF:
			CmdElseIf();
			break;
		case keyEND:
			CmdEnd();
		case keyERASE:
			CmdErase();
			break;
		case keyEXIT:
			return CmdExit();
		case keyFOR:
			CmdFor();
			break;
		case keyFUNCTION:
			CmdFunction();
			break;
		case keyGET:
			CmdGet();
			break;
		case keyGOSUB:
			CmdGosub();
			return false;
		case keyGOTO:
			CmdGoto();
			return false;
		case keyINPUT:
			CmdInput();
			break;
		case keyKILL:
			CmdKill();
			break;
		case keyLET:
			CmdLet();
			break;
		case keyLOAD:
			CmdLoad();
			break;
		case keyLOCK:
			CmdLock();
			break;
		case keyLOOP:
			CmdLoop();
			break;
		case keyLSET:
			CmdLSet();
			break;
		case keyMKDIR:
			CmdMKDir();
			break;
		case keyMSGBOX:
			CmdMsgBox();
			break;
		case keyNAME:
			CmdName();
			break;
		case keyNEXT:
			CmdNext();
			break;
		case keyON:
			CmdOn();
		case keyOPEN:
			CmdOpen();
			break;
		case keyOPTION:
			CmdOption();
			break;
		case keyPRINT:
			CmdPrint();
			break;
		case keyPUT:
			CmdPut();
			break;
		case keyRANDOMIZE:
			CmdRandomize();
			break;
		case keyREDIM:
			CmdReDim();
			break;
		case keyREM:
			CmdRem();
			break;
		case keyRESET:
			CmdReset();
			break;
		case keyRESUME:
			CmdResume();
			break;
		case keyRETURN:
			CmdReturn();
			break;
		case keyRMDIR:
			CmdRmDir();
			break;
		case keyRSET:
			CmdRSet();
			break;
		case keyRUN:
			CmdRun("MAIN");
			return false;
		case keySEEK:
			CmdSeek();
			break;
		case keySELECT:
			CmdSelect();
			break;
		case keySHARED:
			CmdShared();
			break;
		case keySLEEP:
			CmdSleep();
			break;
		case keySTATIC:
			CmdStatic();
			break;
		case keySTOP:
			CmdStop();
			break;
		case keySUB:
			CmdSub();
			break;
		case keyTYPE:
			CmdType();
			break;
		case keyUNLOCK:
			CmdUnLock();
			break;
		case keyWEND:
			CmdWEnd();
			break;
		case keyWHILE:
			CmdWhile();
			break;
		case keyWIDTH:
			CmdWidth();
			break;
		case keyWRITE:
			CmdWrite();
			break;
		case keySETREGISTRY:
			CmdSetRegistry();
			break;
		case keyIF:
			CmdIf();
			break;
		}
		return true;
	}
	 
	public void CmdBeep() {
	}

	public void CmdCall() {
	}

	public void CmdCase() {
		this.parse.MoveToEndCase();
	}

	public void CmdChDir() {
	}

	public void CmdClose() {
	}

	public void CmdCls() {
	}

	public void CmdConst() {
	}

	public void CmdCreateLink() {
	}

	public void CmdDim() {
		BasicVariable v;
		String var;

		for(;;)	
		{
			this.parse.kill_space();
			var = this.parse.ParseVariable();
			if( BasicUtil.FindKeyword(var)!=null)
				throw(new BasicError(ErrorNumbers.errorKeyword));

			if(this.parse.GetVariable(var)!=null)
				throw(new BasicError(ErrorNumbers.errorAlreadyDefined));
		
			v = this.parse.CreateVariable(var);
			this.parse.addVariable(var, v);
			
			this.parse.kill_space();
			if( !this.parse.LookAhead(',') )
				break;
		}
	}

	public void CmdDo() {
		this.parse.kill_space();
		
		if( this.parse.getNextChar()!=0  )
		{
			if(this.parse.EvaluateDo())
				this.parse.getStack().push(new StackEntry(StackEntryType.stackDo,this.parse.getCurrentLine(),0));
			else
				this.parse.MoveToLoop();
		}
		else
			this.parse.getStack().push(new StackEntry(StackEntryType.stackDo,this.parse.getCurrentLine(),1));
	}

	public void CmdElse() {
		this.parse.MoveToEndIf(false);
		this.parse.decreaseIFS();
	}

	public void CmdElseIf() {
		CmdElse();
		CmdIf();
	}

	public void CmdEnd() {
		this.parse.kill_space();

		if(this.parse.LookAhead(KeyNames.keyIF, false))
		{
			CmdEndIf();
		}
		else
		{
			this.parse.kill_space();
			
			if(this.parse.getNextChar()==0)			
				this.parse.getModule().getProgram().setQuitProgram(true);
		}		
	}

	public void CmdEndIf() {
		this.parse.decreaseIFS();
	}

	public void CmdEnviron() {
	}

	public void CmdErase() {
	}

	public boolean CmdExit() {
		return false;
	}

	public void CmdFor() {
		BasicVariable a,varObj;
		String var;
		int start,stop,step;

		var = this.parse.ParseVariable();
		this.parse.ExpectToken('=');

		a = this.parse.Expr();
		start=(int)a.GetLong();
		
		if(!this.parse.LookAhead(KeyNames.keyTO,false) )
			throw(new BasicError(ErrorNumbers.errorNoTo));

		a = this.parse.Expr();
		stop=(int)a.GetLong();

		if(this.parse.LookAhead(KeyNames.keySTEP,false))
		{
			a = this.parse.Expr();
			step=(int)a.GetLong();
		}
		else step=1;

		if(step==0)
			throw(new BasicError(ErrorNumbers.errorIllegalUse));

		if(step<0)
		{
			if(start<stop)
			{
				this.parse.MoveToNext();
				return;
			}
		}
		else
		{
			if(start>stop)
			{
				this.parse.MoveToNext();
				return;
			}
		}
			

		varObj=this.parse.GetVariable(var);

		if(varObj==null)
		{
			varObj=new BasicVariable((long)0);
			this.parse.addVariable(var,varObj);
		}

		varObj.edit((long)start);

		parse.getStack().push(new StackEntry(StackEntryType.stackFor,this.parse.getCurrentLine(),varObj,start,stop,step));
	}

	public void CmdFunction() {
	}

	public void CmdGet() {
	}

	public void CmdGosub() {
	}

	public void CmdGoto() {
		String str;

		this.parse.kill_space();
		str = this.parse.ParseVariable();
		this.parse.go(str);
	}

	public void CmdIf() {
		BasicVariable a = new BasicVariable();
		boolean b;

		b=this.parse.ParseVariable(a);
		if(!this.parse.LookAhead(KeyNames.keyTHEN,false))
			throw(new BasicError(ErrorNumbers.errorNoThen));

		if( b )
		{
			this.parse.kill_space();
			if( this.parse.getNextChar()==0 )
			{
				this.parse.increaseIFS();
			}
			else
				throw new BasicError(ErrorNumbers.errorBlock);
		}
		else
			parse.MoveToEndIf(true);
	}

	public void CmdInput() {
	}

	public void CmdKill() {
	}

	public void CmdLet() {
	}

	public void CmdLoad() {
	}

	public void CmdLock() {
	}

	public void CmdLoop() {
		int idx,ln;
		BasicVariable varObj;
		StackEntryType type;
		BasicLine bl;

		this.parse.kill_space();
		if(this.parse.getStack().empty())
			throw(new BasicError(ErrorNumbers.errorLongName));

		if(this.parse.getStack().peekType()!=StackEntryType.stackDo)
			throw(new BasicError(ErrorNumbers.errorLongName));

		if( !this.parse.getStack().empty())
		{
			type = this.parse.getStack().peekType();
			bl = this.parse.getStack().peek().getLine();
		}
		else
		{
			type = null;
			bl = null;
		}


		if( this.parse.getNextChar()!=0  )
		{
			if(type==null)
				throw(new BasicError(ErrorNumbers.errorIllegalUse)) ;
			if(this.parse.EvaluateDo())
			{
				if(bl!=null)
				{
					this.parse.setCurrentLine(bl);
					this.parse.parse();
				}
			}
			else
				this.parse.getStack().pop();
		}
		else
		{
			this.parse.setCurrentLine(bl);
			this.parse.parse();
		}
	}

	public void CmdLSet() {
	}

	public void CmdMsgBox() {
	}

	public void CmdMKDir() {
	}

	public void CmdName() {
	}

	public void CmdNext() {
		int idx,ln;
		double d;

		this.parse.kill_space();
		if(this.parse.getStack().empty())
			throw(new BasicError(ErrorNumbers.errorNoFor));

		if(this.parse.getStack().peekType()!=StackEntryType.stackFor)
			throw(new BasicError(ErrorNumbers.errorNoFor));
			
		StackEntry entry = this.parse.getStack().peek();
		int start = entry.getStart();
		int stop = entry.getStop();
		int step = entry.getStep();
		BasicVariable varObj = entry.getVariable();
		
		if(varObj==null)
			throw(new BasicError(ErrorNumbers.errorUndefinedVariable));
		d = varObj.GetDouble();
		d+=step;
		varObj.edit(d);

		boolean done=false;

		if(step>0)
		{
			if(d>stop)
				done=true;
		}
		else
		{
			if(d<stop)
				done=true;
		}

		if(!done)
		{
			this.parse.setCurrentLine(entry.getLine());
		}
		else
			this.parse.getStack().pop();
	}

	public void CmdOn() {

		if( this.parse.LookAhead(KeyNames.keyERROR,false) )
		{
			CmdOnError();
		}
		else
			throw new BasicError(ErrorNumbers.errorIllegalUse);
	}

	public void CmdOnError() {
		String str;

		if( this.parse.LookAhead(KeyNames.keyGOTO,false) )
		{
			str = this.parse.ParseVariable();
			
			if( str.equals("0"))
				this.parse.setErrorLabel(null);
			else
				this.parse.setErrorLabel(str);
			
			return;
		}
		else
		if( this.parse.LookAhead(KeyNames.keyRESUME,false) )
			if( this.parse.LookAhead(KeyNames.keyNEXT,false) )
			{
				this.parse.setErrorLabel("*");
				return;
			}
		throw(new BasicError(ErrorNumbers.errorIllegalUse));
	}

	public void CmdOpen() {
	}

	public void CmdOption() {
	}

	public void CmdPrint() {
		
		boolean no_cr=false;

			this.parse.setColumn(0);

			this.parse.kill_space();

			if( this.parse.LookAhead('#') )
			{
				BasicVariable var;

				var = this.parse.ParseVariable(1,100);
				/*if(theProgram->fileHandles[var.GetShort()]==NULL)
					throw(errorHandleUndefined);
				h=theProgram->fileHandles[var.GetShort()];*/
				if(!this.parse.LookAhead(','))
					throw(new BasicError(ErrorNumbers.errorIllegalUse));
			}

			if( !(this.parse.getNextChar()==0 || (this.parse.getNextChar()==':')) )
			{
				this.parse.getModule().getProgram().print(this.parse.FormatExpression());

				while(this.parse.getNextChar() ==';')
				{
					this.parse.advance();

					if( (this.parse.getNextChar()==0) || (this.parse.getNextChar()==':') )
					{
						no_cr=true;
						break;
					}

					this.parse.getModule().getProgram().print(this.parse.FormatExpression());

					this.parse.kill_space();
				}
			}

			if(!no_cr)
			{
				this.parse.getModule().getProgram().print("\n");
			}
	}

	public void CmdPut() {
	}

	public void CmdRandomize() {
	}

	public void CmdRead() {
	}

	public void CmdReDim() {
	}

	public void CmdRem() {
	}

	public void CmdReset() {
	}

	public void CmdResume() {
	}

	public void CmdReturn() {
	}

	public void CmdRmDir() {
	}

	public void CmdRSet() {
	}

	public boolean CmdRun(String str) {
		return false;
	}

	public void CmdSeek() {
	}

	public void CmdSelect() {

		BasicVariable var;
		BasicVariable is;

		if(!this.parse.LookAhead(KeyNames.keyCASE,false))
			throw(new BasicError(ErrorNumbers.errorIllegalUse));

		var = this.parse.Expr();
		is=var;

		while(parse.getCurrentLine()!=null)
		{
			do 
			{
				if( this.parse.LookAhead(KeyNames.keySELECT,false) )
					this.parse.MoveToEndCase();

				if( this.parse.LookAhead(KeyNames.keyEND,false) )
				{
					this.parse.kill_space();
					if(this.parse.LookAhead(KeyNames.keyCASE,false))
						return;
					if(this.parse.LookAhead(KeyNames.keySUB,false) || this.parse.LookAhead(KeyNames.keyFUNCTION,false) )
						throw(new BasicError(ErrorNumbers.errorBlock));
				} 

				if(this.parse.LookAhead(KeyNames.keyCASE,false))
				{
					if(this.parse.LookAhead(KeyNames.keyELSE, false))
					{
						this.parse.MovePastColen();
						is=null;
						return;
					}

					do	
					{
						BasicVariable b,c;
						boolean bl=false;

						this.parse.kill_space();
						if( this.parse.LookAhead("IS", false)  )
						{
							b = this.parse.Expr();
							if(b.GetBoolean())
							{
								this.parse.MovePastColen();
								is=null;
								return;
							}
						}
						else
						{
							b = this.parse.Expr();

							if(this.parse.LookAhead(KeyNames.keyTO,false))							{
								c = this.parse.Expr();
								if( (var.CompareGTE(b)) && (var.CompareLTE(c)) )
								{
									this.parse.MovePastColen();
									is=null;
									return;
								}
							}

							if(var.CompareE(b)) 
							{
								this.parse.MovePastColen();
								is=null;
								return;
							}
						}
					} while(this.parse.LookAhead(','));
				}

				this.parse.MovePastColen();

			} while(this.parse.getNextChar()!=0);

			this.parse.MoveNextLine();
		}

		is=null;
		
	}

	public void CmdShared() {
	}

	public void CmdSleep() {
	}

	public void CmdStatic() {
	}

	public void CmdStop() {
	}

	public void CmdSub() {
	}

	public void CmdType() {
	}

	public void CmdUnLock() {
	}

	public void CmdWEnd() {
		BasicVariable varObj;

		this.parse.kill_space();
		if( this.parse.getStack().empty())
			throw(new BasicError(ErrorNumbers.errorWend));

		if(this.parse.getStack().peekType()!=StackEntryType.stackWhile)
			throw(new BasicError(ErrorNumbers.errorWend));
			
		StackEntry entry = this.parse.getStack().pop();
		this.parse.setCurrentLine(entry.getLine());
		
		this.parse.parse(this.parse.getCurrentLine().Command());
	}

	public void CmdWhile() {
		BasicVariable varObj;

		this.parse.kill_space();
		
		varObj = this.parse.Expr();
		
		if(varObj.GetBoolean())
			this.parse.getStack().push( new StackEntry(StackEntryType.stackWhile,this.parse.getCurrentLine()) );
		else
			this.parse.MoveToWEnd();
	}

	public void CmdWidth() {
	}

	public void CmdWrite() {
	}

	public void CmdSetRegistry() {
	}


}
