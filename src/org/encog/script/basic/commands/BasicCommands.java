package org.encog.script.basic.commands;

import org.encog.script.basic.BasicError;
import org.encog.script.basic.BasicKey;
import org.encog.script.basic.BasicParse;
import org.encog.script.basic.BasicUtil;
import org.encog.script.basic.BasicVariable;
import org.encog.script.basic.ErrorNumbers;
import org.encog.script.basic.KeyNames;

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
			return CmdEnd();
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
			return CmdOn();
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
			switch (CmdIf()) {
			case 0:
				return false;
			case 1:
				return true;
			case 2:
				break;
			}
			break;
		}
		return true;
	}
	 
	public void CmdBeep() {
	}

	public void CmdCall() {
	}

	public void CmdCase() {
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
	}

	public void CmdDo() {
	}

	public void CmdElse() {
	}

	public void CmdElseIf() {
	}

	public boolean CmdEnd() {
		return false;
	}

	public boolean CmdEndIf() {
		return false;
	}

	public void CmdEnviron() {
	}

	public void CmdErase() {
	}

	public boolean CmdExit() {
		return false;
	}

	public void CmdFor() {
	}

	public void CmdFunction() {
	}

	public void CmdGet() {
	}

	public void CmdGosub() {
	}

	public void CmdGoto() {
	}

	public int CmdIf() {
		BasicVariable a = new BasicVariable();
		boolean b;

		b=this.parse.ParseVariable(a);
		if(!this.parse.LookAhead(KeyNames.keyTHEN,false))
			throw(new BasicError(ErrorNumbers.errorNoThen));

		if( b )
		{
			this.parse.kill_space();
			if( this.parse.getNextChar()>0 )
			{
				// Block if
				this.parse.increaseIFS();
				return 1;
			}
				return 2;
		}

		return 1;
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
	}

	public boolean CmdOn() {
		return false;
	}

	public void CmdOnError() {
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
	}

	public void CmdWhile() {
	}

	public void CmdWidth() {
	}

	public void CmdWrite() {
	}

	public void CmdSetRegistry() {
	}


}
