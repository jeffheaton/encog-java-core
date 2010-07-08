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
			cmdBeep();
			break;
		case keyCALL:
			cmdCall();
			break;
		case keyCASE:
			cmdCase();
			break;
		case keyCHDIR:
			cmdChDir();
			break;
		case keyCLOSE:
			cmdClose();
			break;
		case keyCLS:
			cmdCls();
			break;
		case keyCONST:
			cmdConst();
			break;
		case keyCREATELINK:
			cmdCreateLink();
			break;
		case keyDIM:
			cmdDim();
			break;
		case keyDO:
			cmdDo();
			break;
		case keyELSE:
			cmdElse();
			break;
		case keyELSEIF:
			cmdElseIf();
			break;
		case keyEND:
			cmdEnd();
		case keyERASE:
			cmdErase();
			break;
		case keyEXIT:
			return cmdExit();
		case keyFOR:
			cmdFor();
			break;
		case keyFUNCTION:
			cmdFunction();
			break;
		case keyGET:
			cmdGet();
			break;
		case keyGOSUB:
			cmdGosub();
			return false;
		case keyGOTO:
			cmdGoto();
			return false;
		case keyINPUT:
			cmdInput();
			break;
		case keyKILL:
			cmdKill();
			break;
		case keyLET:
			cmdLet();
			break;
		case keyLOAD:
			cmdLoad();
			break;
		case keyLOCK:
			cmdLock();
			break;
		case keyLOOP:
			cmdLoop();
			break;
		case keyLSET:
			cmdLSet();
			break;
		case keyMKDIR:
			cmdMKDir();
			break;
		case keyMSGBOX:
			cmdMsgBox();
			break;
		case keyNAME:
			cmdName();
			break;
		case keyNEXT:
			cmdNext();
			break;
		case keyON:
			cmdOn();
		case keyOPEN:
			cmdOpen();
			break;
		case keyOPTION:
			cmdOption();
			break;
		case keyPRINT:
			cmdPrint();
			break;
		case keyPUT:
			cmdPut();
			break;
		case keyRANDOMIZE:
			cmdRandomize();
			break;
		case keyREDIM:
			cmdReDim();
			break;
		case keyREM:
			cmdRem();
			break;
		case keyRESET:
			cmdReset();
			break;
		case keyRESUME:
			cmdResume();
			break;
		case keyRETURN:
			cmdReturn();
			break;
		case keyRMDIR:
			cmdRmDir();
			break;
		case keyRSET:
			cmdRSet();
			break;
		case keyRUN:
			cmdRun("MAIN");
			return false;
		case keySEEK:
			cmdSeek();
			break;
		case keySELECT:
			cmdSelect();
			break;
		case keySHARED:
			cmdShared();
			break;
		case keySLEEP:
			cmdSleep();
			break;
		case keySTATIC:
			cmdStatic();
			break;
		case keySTOP:
			cmdStop();
			break;
		case keySUB:
			cmdSub();
			break;
		case keyTYPE:
			cmdType();
			break;
		case keyUNLOCK:
			cmdUnLock();
			break;
		case keyWEND:
			cmdWEnd();
			break;
		case keyWHILE:
			cmdWhile();
			break;
		case keyWIDTH:
			cmdWidth();
			break;
		case keyWRITE:
			cmdWrite();
			break;
		case keySETREGISTRY:
			cmdSetRegistry();
			break;
		case keyIF:
			cmdIf();
			break;
		}
		return true;
	}
	 
	public void cmdBeep() {
	}

	public void cmdCall() {
	}

	public void cmdCase() {
		this.parse.moveToEndCase();
	}

	public void cmdChDir() {
	}

	public void cmdClose() {
	}

	public void cmdCls() {
	}

	public void cmdConst() {
	}

	public void cmdCreateLink() {
	}

	public void cmdDim() {
		BasicVariable v;
		String var;

		for(;;)	
		{
			this.parse.kill_space();
			var = this.parse.parseVariable();
			if( BasicUtil.FindKeyword(var)!=null)
				throw(new BasicError(ErrorNumbers.errorKeyword));

			if(this.parse.getVariable(var)!=null)
				throw(new BasicError(ErrorNumbers.errorAlreadyDefined));
		
			v = this.parse.createVariable(var);
			this.parse.addVariable(var, v);
			
			this.parse.kill_space();
			if( !this.parse.lookAhead(',') )
				break;
		}
	}

	public void cmdDo() {
		this.parse.kill_space();
		
		if( this.parse.getNextChar()!=0  )
		{
			if(this.parse.evaluateDo())
				this.parse.getStack().push(new StackEntry(StackEntryType.stackDo,this.parse.getCurrentLine(),0));
			else
				this.parse.moveToLoop();
		}
		else
			this.parse.getStack().push(new StackEntry(StackEntryType.stackDo,this.parse.getCurrentLine(),1));
	}

	public void cmdElse() {
		this.parse.moveToEndIf(false);
		this.parse.decreaseIFS();
	}

	public void cmdElseIf() {
		cmdElse();
		cmdIf();
	}

	public void cmdEnd() {
		this.parse.kill_space();

		if(this.parse.lookAhead(KeyNames.keyIF, false))
		{
			cmdEndIf();
		}
		else
		{
			this.parse.kill_space();
			
			if(this.parse.getNextChar()==0)			
				this.parse.getModule().getProgram().setQuitProgram(true);
		}		
	}

	public void cmdEndIf() {
		this.parse.decreaseIFS();
	}

	public void cmdEnviron() {
	}

	public void cmdErase() {
	}

	public boolean cmdExit() {
		return false;
	}

	public void cmdFor() {
		BasicVariable a,varObj;
		String var;
		int start,stop,step;

		var = this.parse.parseVariable();
		this.parse.expectToken('=');

		a = this.parse.expr();
		start=(int)a.GetLong();
		
		if(!this.parse.lookAhead(KeyNames.keyTO,false) )
			throw(new BasicError(ErrorNumbers.errorNoTo));

		a = this.parse.expr();
		stop=(int)a.GetLong();

		if(this.parse.lookAhead(KeyNames.keySTEP,false))
		{
			a = this.parse.expr();
			step=(int)a.GetLong();
		}
		else step=1;

		if(step==0)
			throw(new BasicError(ErrorNumbers.errorIllegalUse));

		if(step<0)
		{
			if(start<stop)
			{
				this.parse.moveToNext();
				return;
			}
		}
		else
		{
			if(start>stop)
			{
				this.parse.moveToNext();
				return;
			}
		}
			

		varObj=this.parse.getVariable(var);

		if(varObj==null)
		{
			varObj=new BasicVariable((long)0);
			this.parse.addVariable(var,varObj);
		}

		varObj.edit((long)start);

		parse.getStack().push(new StackEntry(StackEntryType.stackFor,this.parse.getCurrentLine(),varObj,start,stop,step));
	}

	public void cmdFunction() {
	}

	public void cmdGet() {
	}

	public void cmdGosub() {
	}

	public void cmdGoto() {
		String str;

		this.parse.kill_space();
		str = this.parse.parseVariable();
		this.parse.go(str);
	}

	public void cmdIf() {
		BasicVariable a = new BasicVariable();
		boolean b;

		b=this.parse.parseVariable(a);
		if(!this.parse.lookAhead(KeyNames.keyTHEN,false))
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
			parse.moveToEndIf(true);
	}

	public void cmdInput() {
	}

	public void cmdKill() {
	}

	public void cmdLet() {
	}

	public void cmdLoad() {
	}

	public void cmdLock() {
	}

	public void cmdLoop() {
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
			if(this.parse.evaluateDo())
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

	public void cmdLSet() {
	}

	public void cmdMsgBox() {
	}

	public void cmdMKDir() {
	}

	public void cmdName() {
	}

	public void cmdNext() {
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

	public void cmdOn() {

		if( this.parse.lookAhead(KeyNames.keyERROR,false) )
		{
			cmdOnError();
		}
		else
			throw new BasicError(ErrorNumbers.errorIllegalUse);
	}

	public void cmdOnError() {
		String str;

		if( this.parse.lookAhead(KeyNames.keyGOTO,false) )
		{
			str = this.parse.parseVariable();
			
			if( str.equals("0"))
				this.parse.setErrorLabel(null);
			else
				this.parse.setErrorLabel(str);
			
			return;
		}
		else
		if( this.parse.lookAhead(KeyNames.keyRESUME,false) )
			if( this.parse.lookAhead(KeyNames.keyNEXT,false) )
			{
				this.parse.setErrorLabel("*");
				return;
			}
		throw(new BasicError(ErrorNumbers.errorIllegalUse));
	}

	public void cmdOpen() {
	}

	public void cmdOption() {
	}

	public void cmdPrint() {
		
		boolean no_cr=false;

			this.parse.setColumn(0);

			this.parse.kill_space();

			if( this.parse.lookAhead('#') )
			{
				BasicVariable var;

				var = this.parse.parseVariable(1,100);
				/*if(theProgram->fileHandles[var.GetShort()]==NULL)
					throw(errorHandleUndefined);
				h=theProgram->fileHandles[var.GetShort()];*/
				if(!this.parse.lookAhead(','))
					throw(new BasicError(ErrorNumbers.errorIllegalUse));
			}

			if( !(this.parse.getNextChar()==0 || (this.parse.getNextChar()==':')) )
			{
				this.parse.getModule().getProgram().print(this.parse.formatExpression());

				while(this.parse.getNextChar() ==';')
				{
					this.parse.advance();

					if( (this.parse.getNextChar()==0) || (this.parse.getNextChar()==':') )
					{
						no_cr=true;
						break;
					}

					this.parse.getModule().getProgram().print(this.parse.formatExpression());

					this.parse.kill_space();
				}
			}

			if(!no_cr)
			{
				this.parse.getModule().getProgram().print("\n");
			}
	}

	public void cmdPut() {
	}

	public void cmdRandomize() {
	}

	public void cmdRead() {
	}

	public void cmdReDim() {
	}

	public void cmdRem() {
	}

	public void cmdReset() {
	}

	public void cmdResume() {
	}

	public void cmdReturn() {
	}

	public void cmdRmDir() {
	}

	public void cmdRSet() {
	}

	public boolean cmdRun(String str) {
		return false;
	}

	public void cmdSeek() {
	}

	public void cmdSelect() {

		BasicVariable var;
		BasicVariable is;

		if(!this.parse.lookAhead(KeyNames.keyCASE,false))
			throw(new BasicError(ErrorNumbers.errorIllegalUse));

		var = this.parse.expr();
		is=var;

		while(parse.getCurrentLine()!=null)
		{
			do 
			{
				if( this.parse.lookAhead(KeyNames.keySELECT,false) )
					this.parse.moveToEndCase();

				if( this.parse.lookAhead(KeyNames.keyEND,false) )
				{
					this.parse.kill_space();
					if(this.parse.lookAhead(KeyNames.keyCASE,false))
						return;
					if(this.parse.lookAhead(KeyNames.keySUB,false) || this.parse.lookAhead(KeyNames.keyFUNCTION,false) )
						throw(new BasicError(ErrorNumbers.errorBlock));
				} 

				if(this.parse.lookAhead(KeyNames.keyCASE,false))
				{
					if(this.parse.lookAhead(KeyNames.keyELSE, false))
					{
						this.parse.movePastColen();
						is=null;
						return;
					}

					do	
					{
						BasicVariable b,c;
						boolean bl=false;

						this.parse.kill_space();
						if( this.parse.lookAhead("IS", false)  )
						{
							b = this.parse.expr();
							if(b.GetBoolean())
							{
								this.parse.movePastColen();
								is=null;
								return;
							}
						}
						else
						{
							b = this.parse.expr();

							if(this.parse.lookAhead(KeyNames.keyTO,false))							{
								c = this.parse.expr();
								if( (var.CompareGTE(b)) && (var.CompareLTE(c)) )
								{
									this.parse.movePastColen();
									is=null;
									return;
								}
							}

							if(var.CompareE(b)) 
							{
								this.parse.movePastColen();
								is=null;
								return;
							}
						}
					} while(this.parse.lookAhead(','));
				}

				this.parse.movePastColen();

			} while(this.parse.getNextChar()!=0);

			this.parse.moveNextLine();
		}

		is=null;
		
	}

	public void cmdShared() {
	}

	public void cmdSleep() {
	}

	public void cmdStatic() {
	}

	public void cmdStop() {
	}

	public void cmdSub() {
	}

	public void cmdType() {
	}

	public void cmdUnLock() {
	}

	public void cmdWEnd() {
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

	public void cmdWhile() {
		BasicVariable varObj;

		this.parse.kill_space();
		
		varObj = this.parse.expr();
		
		if(varObj.GetBoolean())
			this.parse.getStack().push( new StackEntry(StackEntryType.stackWhile,this.parse.getCurrentLine()) );
		else
			this.parse.moveToWEnd();
	}

	public void cmdWidth() {
	}

	public void cmdWrite() {
	}

	public void cmdSetRegistry() {
	}


}
