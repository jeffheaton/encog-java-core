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
		}
	}
	
	public void fnAbs(BasicVariable target) {
	}

	public void fnAsc(BasicVariable target) {
	}

	public void fnAtn(BasicVariable target) {
	}

	public void fnCDbl(BasicVariable target) {
	}

	public void fnChr(BasicVariable target) {
	}

	public void fnCInt(BasicVariable target) {
	}

	public void fnCLng(BasicVariable target) {
	}

	public void fnCos(BasicVariable target) {
	}

	public void fnDate_(BasicVariable target) {
	}

	public void fnEnvron_(BasicVariable target) {
	}

	public void fnEof(BasicVariable target) {
	}

	public void fnErr(BasicVariable target) {
	}

	public void fnError(BasicVariable target) {
	}

	public void fnExp(BasicVariable target) {
	}

	public void fnFileattr(BasicVariable target) {
	}

	public void fnFix(BasicVariable target) {
	}

	public void fnFreeFile(BasicVariable target) {
	}

	public void fnHex_(BasicVariable target) {
	}

	public void fnInput_(BasicVariable target) {
	}

	public void fnInStr(BasicVariable target) {
	}

	public void fnInt(BasicVariable target) {
	}

	public void fnLCase(BasicVariable target) {
	}

	public void fnLeft(BasicVariable target) {
	}

	public void fnLen(BasicVariable target) {
	}

	public void fnLoc(BasicVariable target) {
	}

	public void fnLog(BasicVariable target) {
	}

	public void fnLTrim(BasicVariable target) {
	}

	public void fnMid_(BasicVariable target) {
	}

	public void fnMsgBox(BasicVariable target) {
	}

	public void fnOct_(BasicVariable target) {
	}

	public void fnRight_(BasicVariable target) {
	}

	public void fnRnd(BasicVariable target) {
	}

	public void fnRTrim(BasicVariable target) {
	}

	public void fnSeek(BasicVariable target) {
	}

	public void fnSgn(BasicVariable target) {
	}

	public void fnShell(BasicVariable target) {
	}

	public void fnSin(BasicVariable target) {
	}

	public void fnSpace_(BasicVariable target) {
	}

	public void fnSpc(BasicVariable target) {
	}

	public void fnSqr(BasicVariable target) {
	}

	public void fnStr_(BasicVariable target) {
	}

	public void fnStrig(BasicVariable target) {
	}

	public void fnString_(BasicVariable target) {
	}

	public void fnTan(BasicVariable target) {
	}

	public void fnTime_(BasicVariable target) {
	}

	public void fnUCase_(BasicVariable target) {
	}

	public void fnVal(BasicVariable target) {
	}

	public void fnRegistry(BasicVariable target) {
	}	
}
