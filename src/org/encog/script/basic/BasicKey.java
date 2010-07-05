package org.encog.script.basic;

import java.util.ArrayList;
import java.util.List;


public class BasicKey {
	
	private String name;
	private KeyTypes type;
	private KeyNames id;
	public static List<BasicKey> KEYS = new ArrayList<BasicKey>();
	
	public BasicKey(String name, KeyTypes type, KeyNames id)
	{
		this.name = name;
		this.type = type;
		this.id = id;
	}
	
	public void init()
	{
		KEYS.add(new BasicKey("ABS",KeyTypes.keyFunction,KeyNames.keyABS));
		KEYS.add(new BasicKey("ACCESS",KeyTypes.keyKeyword,KeyNames.keyACCESS));
		KEYS.add(new BasicKey("AND",KeyTypes.keyOperator,KeyNames.keyAND));
		KEYS.add(new BasicKey("ANY",KeyTypes.keyType,KeyNames.keyANY));
		KEYS.add(new BasicKey("APPEND",KeyTypes.keyKeyword,KeyNames.keyAPPEND));
		KEYS.add(new BasicKey("AS",KeyTypes.keyKeyword,KeyNames.keyAS));
		KEYS.add(new BasicKey("ASC",KeyTypes.keyFunction,KeyNames.keyASC));
		KEYS.add(new BasicKey("ATN",KeyTypes.keyFunction,KeyNames.keyATN));
		KEYS.add(new BasicKey("BASE",KeyTypes.keyKeyword,KeyNames.keyBASE));
		KEYS.add(new BasicKey("BEEP",KeyTypes.keyStatement,KeyNames.keyBEEP));
		KEYS.add(new BasicKey("BINARY",KeyTypes.keyKeyword,KeyNames.keyBINARY));
		KEYS.add(new BasicKey("BOOLEAN",KeyTypes.keyType,KeyNames.keyBOOLEAN));
		KEYS.add(new BasicKey("BYTE",KeyTypes.keyType,KeyNames.keyBYTE));
		KEYS.add(new BasicKey("BYREF",KeyTypes.keyKeyword,KeyNames.keyBYREF));
		KEYS.add(new BasicKey("BYVAL",KeyTypes.keyKeyword,KeyNames.keyBYVAL));
		KEYS.add(new BasicKey("CALL",KeyTypes.keyStatement,KeyNames.keyCALL));
		KEYS.add(new BasicKey("CASE",KeyTypes.keyStatement,KeyNames.keyCASE));
		KEYS.add(new BasicKey("CDBL",KeyTypes.keyFunction,KeyNames.keyCDBL));
		KEYS.add(new BasicKey("CHARACTER",KeyTypes.keyType,KeyNames.keyCHARACTER));
		KEYS.add(new BasicKey("CHDIR",KeyTypes.keyStatement,KeyNames.keyCHDIR));
		KEYS.add(new BasicKey("CHR",KeyTypes.keyFunction,KeyNames.keyCHR));
		KEYS.add(new BasicKey("CINT",KeyTypes.keyFunction,KeyNames.keyCINT));
		KEYS.add(new BasicKey("CLNG",KeyTypes.keyFunction,KeyNames.keyCLNG));
		KEYS.add(new BasicKey("CLOSE",KeyTypes.keyStatement,KeyNames.keyCLOSE));
		KEYS.add(new BasicKey("CLS",KeyTypes.keyStatement,KeyNames.keyCLS));
		KEYS.add(new BasicKey("CONST",KeyTypes.keyStatement,KeyNames.keyCONST));
		KEYS.add(new BasicKey("COS",KeyTypes.keyFunction,KeyNames.keyCOS));
		KEYS.add(new BasicKey("CREATELINK",KeyTypes.keyStatement,KeyNames.keyCREATELINK));
		KEYS.add(new BasicKey("DATE",KeyTypes.keyFunction,KeyNames.keyDATE));
		KEYS.add(new BasicKey("DIM",KeyTypes.keyStatement,KeyNames.keyDIM));
		KEYS.add(new BasicKey("DO",KeyTypes.keyStatement,KeyNames.keyDO));
		KEYS.add(new BasicKey("DOUBLE",KeyTypes.keyType,KeyNames.keyDOUBLE));
		KEYS.add(new BasicKey("ELSE",KeyTypes.keyStatement,KeyNames.keyELSE));
		KEYS.add(new BasicKey("ELSEIF",KeyTypes.keyStatement,KeyNames.keyELSEIF));
		KEYS.add(new BasicKey("END",KeyTypes.keyStatement,KeyNames.keyEND));
		KEYS.add(new BasicKey("ENDIF",KeyTypes.keyStatement,KeyNames.keyENDIF));
		KEYS.add(new BasicKey("ENVIRON",KeyTypes.keyFunction,KeyNames.keyENVIRON));
		KEYS.add(new BasicKey("EOF",KeyTypes.keyFunction,KeyNames.keyEOF));
		KEYS.add(new BasicKey("EQV",KeyTypes.keyOperator,KeyNames.keyEQV));
		KEYS.add(new BasicKey("ERASE",KeyTypes.keyStatement,KeyNames.keyERASE));
		KEYS.add(new BasicKey("ERR",KeyTypes.keyFunction,KeyNames.keyERR));
		KEYS.add(new BasicKey("ERROR",KeyTypes.keyKeyword,KeyNames.keyERROR));
		KEYS.add(new BasicKey("ERROR",KeyTypes.keyFunction,KeyNames.keyERROR));
		KEYS.add(new BasicKey("EXIT",KeyTypes.keyStatement,KeyNames.keyEXIT));
		KEYS.add(new BasicKey("EXP",KeyTypes.keyFunction,KeyNames.keyEXP));
		KEYS.add(new BasicKey("FALSE",KeyTypes.keyConst,KeyNames.keyFALSE));
		KEYS.add(new BasicKey("FILEATTR",KeyTypes.keyFunction,KeyNames.keyFILEATTR));
		KEYS.add(new BasicKey("FIX",KeyTypes.keyFunction,KeyNames.keyFIX));
		KEYS.add(new BasicKey("FOR",KeyTypes.keyStatement,KeyNames.keyFOR));
		KEYS.add(new BasicKey("FREEFILE",KeyTypes.keyFunction,KeyNames.keyFREEFILE));
		KEYS.add(new BasicKey("FUNCTION",KeyTypes.keyStatement,KeyNames.keyFUNCTION));
		KEYS.add(new BasicKey("GET",KeyTypes.keyStatement,KeyNames.keyGET));
		KEYS.add(new BasicKey("GOSUB",KeyTypes.keyStatement,KeyNames.keyGOSUB));
		KEYS.add(new BasicKey("GOTO",KeyTypes.keyStatement,KeyNames.keyGOTO));
		KEYS.add(new BasicKey("HEX",KeyTypes.keyFunction,KeyNames.keyHEX));
		KEYS.add(new BasicKey("IF",KeyTypes.keyStatement,KeyNames.keyIF));
		KEYS.add(new BasicKey("IMP",KeyTypes.keyOperator,KeyNames.keyIMP));
		KEYS.add(new BasicKey("INPUT",KeyTypes.keyStatement,KeyNames.keyINPUT));
		KEYS.add(new BasicKey("INPUT",KeyTypes.keyFunction,KeyNames.keyINPUT));
		KEYS.add(new BasicKey("INSTR",KeyTypes.keyFunction,KeyNames.keyINSTR));
		KEYS.add(new BasicKey("INT",KeyTypes.keyFunction,KeyNames.keyINT));
		KEYS.add(new BasicKey("INTEGER",KeyTypes.keyType,KeyNames.keyINTEGER));
		KEYS.add(new BasicKey("IS",KeyTypes.keyConst,KeyNames.keyIS));
		KEYS.add(new BasicKey("KILL",KeyTypes.keyStatement,KeyNames.keyKILL));
		KEYS.add(new BasicKey("LCASE",KeyTypes.keyFunction,KeyNames.keyLCASE));
		KEYS.add(new BasicKey("LEFT",KeyTypes.keyFunction,KeyNames.keyLEFT));
		KEYS.add(new BasicKey("LEN",KeyTypes.keyFunction,KeyNames.keyLEN));
		KEYS.add(new BasicKey("LET",KeyTypes.keyStatement,KeyNames.keyLET));
		KEYS.add(new BasicKey("LOAD",KeyTypes.keyStatement,KeyNames.keyLOAD));
		KEYS.add(new BasicKey("LOC",KeyTypes.keyFunction,KeyNames.keyLOC));
		KEYS.add(new BasicKey("LOCK",KeyTypes.keyStatement,KeyNames.keyLOCK));
		KEYS.add(new BasicKey("LOG",KeyTypes.keyFunction,KeyNames.keyLOG));
		KEYS.add(new BasicKey("LONG",KeyTypes.keyType,KeyNames.keyLONG));
		KEYS.add(new BasicKey("LOOP",KeyTypes.keyStatement,KeyNames.keyLOOP));
		KEYS.add(new BasicKey("LSET",KeyTypes.keyStatement,KeyNames.keyLSET));
		KEYS.add(new BasicKey("LTRIM",KeyTypes.keyFunction,KeyNames.keyLTRIM));
		KEYS.add(new BasicKey("MID",KeyTypes.keyFunction,KeyNames.keyMID));
		KEYS.add(new BasicKey("MKDIR",KeyTypes.keyStatement,KeyNames.keyMKDIR));
		KEYS.add(new BasicKey("MOD",KeyTypes.keyOperator,KeyNames.keyMOD));
		KEYS.add(new BasicKey("MSGBOX",KeyTypes.keyFunction,KeyNames.keyMSGBOX));
		KEYS.add(new BasicKey("MSGBOX",KeyTypes.keyStatement,KeyNames.keyMSGBOX));
		KEYS.add(new BasicKey("NAME",KeyTypes.keyStatement,KeyNames.keyNAME));
		KEYS.add(new BasicKey("NEXT",KeyTypes.keyStatement,KeyNames.keyNEXT));
		KEYS.add(new BasicKey("NOT",KeyTypes.keyOperator,KeyNames.keyNOT));
		KEYS.add(new BasicKey("OCT",KeyTypes.keyFunction,KeyNames.keyOCT));
		KEYS.add(new BasicKey("ON",KeyTypes.keyStatement,KeyNames.keyON));
		KEYS.add(new BasicKey("ON",KeyTypes.keyKeyword,KeyNames.keyON));
		KEYS.add(new BasicKey("OPEN",KeyTypes.keyStatement,KeyNames.keyOPEN));
		KEYS.add(new BasicKey("OPTION",KeyTypes.keyStatement,KeyNames.keyOPTION));
		KEYS.add(new BasicKey("OR",KeyTypes.keyOperator,KeyNames.keyOR));
		KEYS.add(new BasicKey("OUTPUT",KeyTypes.keyKeyword,KeyNames.keyOUTPUT));
		KEYS.add(new BasicKey("PRINT",KeyTypes.keyStatement,KeyNames.keyPRINT));
		KEYS.add(new BasicKey("PUT",KeyTypes.keyStatement,KeyNames.keyPUT));
		KEYS.add(new BasicKey("RANDOM",KeyTypes.keyKeyword,KeyNames.keyRANDOM));
		KEYS.add(new BasicKey("RANDOMIZE",KeyTypes.keyStatement,KeyNames.keyRANDOMIZE));
		KEYS.add(new BasicKey("READ",KeyTypes.keyKeyword,KeyNames.keyREAD));
		KEYS.add(new BasicKey("REAL",KeyTypes.keyType,KeyNames.keyREAL));
		KEYS.add(new BasicKey("REDIM",KeyTypes.keyStatement,KeyNames.keyREDIM));
		KEYS.add(new BasicKey("REGISTRY",KeyTypes.keyFunction,KeyNames.keyREGISTRY));
		KEYS.add(new BasicKey("REM",KeyTypes.keyStatement,KeyNames.keyREM));
		KEYS.add(new BasicKey("RESET",KeyTypes.keyStatement,KeyNames.keyRESET));
		KEYS.add(new BasicKey("RESUME",KeyTypes.keyKeyword,KeyNames.keyRESUME));
		KEYS.add(new BasicKey("RESUME",KeyTypes.keyStatement,KeyNames.keyRESUME));
		KEYS.add(new BasicKey("RETURN",KeyTypes.keyStatement,KeyNames.keyRETURN));
		KEYS.add(new BasicKey("RIGHT",KeyTypes.keyFunction,KeyNames.keyRIGHT));
		KEYS.add(new BasicKey("RMDIR",KeyTypes.keyStatement,KeyNames.keyRMDIR));
		KEYS.add(new BasicKey("RND",KeyTypes.keyFunction,KeyNames.keyRND));
		KEYS.add(new BasicKey("RSET",KeyTypes.keyStatement,KeyNames.keyRSET));
		KEYS.add(new BasicKey("RTRIM",KeyTypes.keyFunction,KeyNames.keyRTRIM));
		KEYS.add(new BasicKey("RUN",KeyTypes.keyStatement,KeyNames.keyRUN));
		KEYS.add(new BasicKey("SEEK",KeyTypes.keyStatement,KeyNames.keySEEK));
		KEYS.add(new BasicKey("SELECT",KeyTypes.keyStatement,KeyNames.keySELECT));
		KEYS.add(new BasicKey("SETREGISTRY",KeyTypes.keyStatement,KeyNames.keySETREGISTRY));
		KEYS.add(new BasicKey("SGN",KeyTypes.keyFunction,KeyNames.keySGN));
		KEYS.add(new BasicKey("SHARED",KeyTypes.keyStatement,KeyNames.keySHARED));
		KEYS.add(new BasicKey("SHELL",KeyTypes.keyFunction,KeyNames.keySHELL));
		KEYS.add(new BasicKey("SIN",KeyTypes.keyFunction,KeyNames.keySIN));
		KEYS.add(new BasicKey("SLEEP",KeyTypes.keyStatement,KeyNames.keySLEEP));
		KEYS.add(new BasicKey("SPACE",KeyTypes.keyFunction,KeyNames.keySPACE));
		KEYS.add(new BasicKey("SPC",KeyTypes.keyFunction,KeyNames.keySPC));
		KEYS.add(new BasicKey("SQR",KeyTypes.keyFunction,KeyNames.keySQR));
		KEYS.add(new BasicKey("STATIC",KeyTypes.keyStatement,KeyNames.keySTATIC));
		KEYS.add(new BasicKey("STEP",KeyTypes.keyKeyword,KeyNames.keySTEP));
		KEYS.add(new BasicKey("STOP",KeyTypes.keyStatement,KeyNames.keySTOP));
		KEYS.add(new BasicKey("CSTR",KeyTypes.keyFunction,KeyNames.keySTR));
		KEYS.add(new BasicKey("STRIG",KeyTypes.keyFunction,KeyNames.keySTRIG));
		KEYS.add(new BasicKey("STRING",KeyTypes.keyType,KeyNames.keySTRING));
		KEYS.add(new BasicKey("STRING",KeyTypes.keyFunction,KeyNames.keySTRING));
		KEYS.add(new BasicKey("SUB",KeyTypes.keyStatement,KeyNames.keySUB));
		KEYS.add(new BasicKey("SYSTEM",KeyTypes.keyStatement,KeyNames.keySYSTEM));
		KEYS.add(new BasicKey("TAB",KeyTypes.keyFunction,KeyNames.keyTAB));
		KEYS.add(new BasicKey("TAN",KeyTypes.keyFunction,KeyNames.keyTAN));
		KEYS.add(new BasicKey("THEN",KeyTypes.keyKeyword,KeyNames.keyTHEN));
		KEYS.add(new BasicKey("TIME",KeyTypes.keyFunction,KeyNames.keyTIME));
		KEYS.add(new BasicKey("TO",KeyTypes.keyKeyword,KeyNames.keyTO));
		KEYS.add(new BasicKey("TRUE",KeyTypes.keyConst,KeyNames.keyTRUE));
		KEYS.add(new BasicKey("TYPE",KeyTypes.keyStatement,KeyNames.keyTYPE));
		KEYS.add(new BasicKey("UCASE",KeyTypes.keyFunction,KeyNames.keyUCASE));
		KEYS.add(new BasicKey("UNLOCK",KeyTypes.keyStatement,KeyNames.keyUNLOCK));
		KEYS.add(new BasicKey("UNTIL",KeyTypes.keyKeyword,KeyNames.keyUNTIL));
		KEYS.add(new BasicKey("VAL",KeyTypes.keyFunction,KeyNames.keyVAL));
		KEYS.add(new BasicKey("WEND",KeyTypes.keyStatement,KeyNames.keyWEND));
		KEYS.add(new BasicKey("WHILE",KeyTypes.keyStatement,KeyNames.keyWHILE));
		KEYS.add(new BasicKey("WIDTH",KeyTypes.keyStatement,KeyNames.keyWIDTH));
		KEYS.add(new BasicKey("WRITE",KeyTypes.keyStatement,KeyNames.keyWRITE));
		KEYS.add(new BasicKey("XOR",KeyTypes.keyOperator,KeyNames.keyXOR));
		KEYS.add(new BasicKey("",KeyTypes.keyType,KeyNames.keyINVALID));
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public KeyTypes getType() {
		return type;
	}
	public void setType(KeyTypes type) {
		this.type = type;
	}
	public KeyNames getId() {
		return id;
	}
	public void setId(KeyNames id) {
		this.id = id;
	}
	
	
}
