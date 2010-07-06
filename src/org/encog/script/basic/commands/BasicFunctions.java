package org.encog.script.basic.commands;

import org.encog.script.basic.BasicParse;
import org.encog.script.basic.BasicVariable;
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
	
	public void FnAbs(BasicVariable target) {
	}

	public void FnAsc(BasicVariable target) {
	}

	public void FnAtn(BasicVariable target) {
	}

	public void FnCDbl(BasicVariable target) {
	}

	public void FnChr(BasicVariable target) {
	}

	public void FnCInt(BasicVariable target) {
	}

	public void FnCLng(BasicVariable target) {
	}

	public void FnCos(BasicVariable target) {
	}

	public void FnDate_(BasicVariable target) {
	}

	public void FnEnvron_(BasicVariable target) {
	}

	public void FnEof(BasicVariable target) {
	}

	public void FnErr(BasicVariable target) {
	}

	public void FnError(BasicVariable target) {
	}

	public void FnExp(BasicVariable target) {
	}

	public void FnFileattr(BasicVariable target) {
	}

	public void FnFix(BasicVariable target) {
	}

	public void FnFreeFile(BasicVariable target) {
	}

	public void FnHex_(BasicVariable target) {
	}

	public void FnInput_(BasicVariable target) {
	}

	public void FnInStr(BasicVariable target) {
	}

	public void FnInt(BasicVariable target) {
	}

	public void FnLCase(BasicVariable target) {
	}

	public void FnLeft(BasicVariable target) {
	}

	public void FnLen(BasicVariable target) {
	}

	public void FnLoc(BasicVariable target) {
	}

	public void FnLog(BasicVariable target) {
	}

	public void FnLTrim(BasicVariable target) {
	}

	public void FnMid_(BasicVariable target) {
	}

	public void FnMsgBox(BasicVariable target) {
	}

	public void FnOct_(BasicVariable target) {
	}

	public void FnRight_(BasicVariable target) {
	}

	public void FnRnd(BasicVariable target) {
	}

	public void FnRTrim(BasicVariable target) {
	}

	public void FnSeek(BasicVariable target) {
	}

	public void FnSgn(BasicVariable target) {
	}

	public void FnShell(BasicVariable target) {
	}

	public void FnSin(BasicVariable target) {
	}

	public void FnSpace_(BasicVariable target) {
	}

	public void FnSpc(BasicVariable target) {
	}

	public void FnSqr(BasicVariable target) {
	}

	public void FnStr_(BasicVariable target) {
	}

	public void FnStrig(BasicVariable target) {
	}

	public void FnString_(BasicVariable target) {
	}

	public void FnTan(BasicVariable target) {
	}

	public void FnTime_(BasicVariable target) {
	}

	public void FnUCase_(BasicVariable target) {
	}

	public void FnVal(BasicVariable target) {
	}

	public void FnRegistry(BasicVariable target) {
	}	
}
