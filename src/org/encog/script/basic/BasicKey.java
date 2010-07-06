package org.encog.script.basic;

import java.util.ArrayList;
import java.util.List;


public class BasicKey {
	
	private String name;
	private KeyTypes type;
	private KeyNames id;
	private static List<BasicKey> keys;
	
	public BasicKey(String name, KeyTypes type, KeyNames id)
	{
		this.name = name;
		this.type = type;
		this.id = id;
	}
	
	public static List<BasicKey> getKeys()
	{
		if( keys == null )
		{
			keys = new ArrayList<BasicKey>();
		keys.add(new BasicKey("ABS",KeyTypes.keyFunction,KeyNames.keyABS));
		keys.add(new BasicKey("ACCESS",KeyTypes.keyKeyword,KeyNames.keyACCESS));
		keys.add(new BasicKey("AND",KeyTypes.keyOperator,KeyNames.keyAND));
		keys.add(new BasicKey("ANY",KeyTypes.keyType,KeyNames.keyANY));
		keys.add(new BasicKey("APPEND",KeyTypes.keyKeyword,KeyNames.keyAPPEND));
		keys.add(new BasicKey("AS",KeyTypes.keyKeyword,KeyNames.keyAS));
		keys.add(new BasicKey("ASC",KeyTypes.keyFunction,KeyNames.keyASC));
		keys.add(new BasicKey("ATN",KeyTypes.keyFunction,KeyNames.keyATN));
		keys.add(new BasicKey("BASE",KeyTypes.keyKeyword,KeyNames.keyBASE));
		keys.add(new BasicKey("BEEP",KeyTypes.keyStatement,KeyNames.keyBEEP));
		keys.add(new BasicKey("BINARY",KeyTypes.keyKeyword,KeyNames.keyBINARY));
		keys.add(new BasicKey("BOOLEAN",KeyTypes.keyType,KeyNames.keyBOOLEAN));
		keys.add(new BasicKey("BYTE",KeyTypes.keyType,KeyNames.keyBYTE));
		keys.add(new BasicKey("BYREF",KeyTypes.keyKeyword,KeyNames.keyBYREF));
		keys.add(new BasicKey("BYVAL",KeyTypes.keyKeyword,KeyNames.keyBYVAL));
		keys.add(new BasicKey("CALL",KeyTypes.keyStatement,KeyNames.keyCALL));
		keys.add(new BasicKey("CASE",KeyTypes.keyStatement,KeyNames.keyCASE));
		keys.add(new BasicKey("CDBL",KeyTypes.keyFunction,KeyNames.keyCDBL));
		keys.add(new BasicKey("CHARACTER",KeyTypes.keyType,KeyNames.keyCHARACTER));
		keys.add(new BasicKey("CHDIR",KeyTypes.keyStatement,KeyNames.keyCHDIR));
		keys.add(new BasicKey("CHR",KeyTypes.keyFunction,KeyNames.keyCHR));
		keys.add(new BasicKey("CINT",KeyTypes.keyFunction,KeyNames.keyCINT));
		keys.add(new BasicKey("CLNG",KeyTypes.keyFunction,KeyNames.keyCLNG));
		keys.add(new BasicKey("CLOSE",KeyTypes.keyStatement,KeyNames.keyCLOSE));
		keys.add(new BasicKey("CLS",KeyTypes.keyStatement,KeyNames.keyCLS));
		keys.add(new BasicKey("CONST",KeyTypes.keyStatement,KeyNames.keyCONST));
		keys.add(new BasicKey("COS",KeyTypes.keyFunction,KeyNames.keyCOS));
		keys.add(new BasicKey("CREATELINK",KeyTypes.keyStatement,KeyNames.keyCREATELINK));
		keys.add(new BasicKey("DATE",KeyTypes.keyFunction,KeyNames.keyDATE));
		keys.add(new BasicKey("DIM",KeyTypes.keyStatement,KeyNames.keyDIM));
		keys.add(new BasicKey("DO",KeyTypes.keyStatement,KeyNames.keyDO));
		keys.add(new BasicKey("DOUBLE",KeyTypes.keyType,KeyNames.keyDOUBLE));
		keys.add(new BasicKey("ELSE",KeyTypes.keyStatement,KeyNames.keyELSE));
		keys.add(new BasicKey("ELSEIF",KeyTypes.keyStatement,KeyNames.keyELSEIF));
		keys.add(new BasicKey("END",KeyTypes.keyStatement,KeyNames.keyEND));
		keys.add(new BasicKey("ENDIF",KeyTypes.keyStatement,KeyNames.keyENDIF));
		keys.add(new BasicKey("ENVIRON",KeyTypes.keyFunction,KeyNames.keyENVIRON));
		keys.add(new BasicKey("EOF",KeyTypes.keyFunction,KeyNames.keyEOF));
		keys.add(new BasicKey("EQV",KeyTypes.keyOperator,KeyNames.keyEQV));
		keys.add(new BasicKey("ERASE",KeyTypes.keyStatement,KeyNames.keyERASE));
		keys.add(new BasicKey("ERR",KeyTypes.keyFunction,KeyNames.keyERR));
		keys.add(new BasicKey("ERROR",KeyTypes.keyKeyword,KeyNames.keyERROR));
		keys.add(new BasicKey("ERROR",KeyTypes.keyFunction,KeyNames.keyERROR));
		keys.add(new BasicKey("EXIT",KeyTypes.keyStatement,KeyNames.keyEXIT));
		keys.add(new BasicKey("EXP",KeyTypes.keyFunction,KeyNames.keyEXP));
		keys.add(new BasicKey("FALSE",KeyTypes.keyConst,KeyNames.keyFALSE));
		keys.add(new BasicKey("FILEATTR",KeyTypes.keyFunction,KeyNames.keyFILEATTR));
		keys.add(new BasicKey("FIX",KeyTypes.keyFunction,KeyNames.keyFIX));
		keys.add(new BasicKey("FOR",KeyTypes.keyStatement,KeyNames.keyFOR));
		keys.add(new BasicKey("FREEFILE",KeyTypes.keyFunction,KeyNames.keyFREEFILE));
		keys.add(new BasicKey("FUNCTION",KeyTypes.keyStatement,KeyNames.keyFUNCTION));
		keys.add(new BasicKey("GET",KeyTypes.keyStatement,KeyNames.keyGET));
		keys.add(new BasicKey("GOSUB",KeyTypes.keyStatement,KeyNames.keyGOSUB));
		keys.add(new BasicKey("GOTO",KeyTypes.keyStatement,KeyNames.keyGOTO));
		keys.add(new BasicKey("HEX",KeyTypes.keyFunction,KeyNames.keyHEX));
		keys.add(new BasicKey("IF",KeyTypes.keyStatement,KeyNames.keyIF));
		keys.add(new BasicKey("IMP",KeyTypes.keyOperator,KeyNames.keyIMP));
		keys.add(new BasicKey("INPUT",KeyTypes.keyStatement,KeyNames.keyINPUT));
		keys.add(new BasicKey("INPUT",KeyTypes.keyFunction,KeyNames.keyINPUT));
		keys.add(new BasicKey("INSTR",KeyTypes.keyFunction,KeyNames.keyINSTR));
		keys.add(new BasicKey("INT",KeyTypes.keyFunction,KeyNames.keyINT));
		keys.add(new BasicKey("INTEGER",KeyTypes.keyType,KeyNames.keyINTEGER));
		keys.add(new BasicKey("IS",KeyTypes.keyConst,KeyNames.keyIS));
		keys.add(new BasicKey("KILL",KeyTypes.keyStatement,KeyNames.keyKILL));
		keys.add(new BasicKey("LCASE",KeyTypes.keyFunction,KeyNames.keyLCASE));
		keys.add(new BasicKey("LEFT",KeyTypes.keyFunction,KeyNames.keyLEFT));
		keys.add(new BasicKey("LEN",KeyTypes.keyFunction,KeyNames.keyLEN));
		keys.add(new BasicKey("LET",KeyTypes.keyStatement,KeyNames.keyLET));
		keys.add(new BasicKey("LOAD",KeyTypes.keyStatement,KeyNames.keyLOAD));
		keys.add(new BasicKey("LOC",KeyTypes.keyFunction,KeyNames.keyLOC));
		keys.add(new BasicKey("LOCK",KeyTypes.keyStatement,KeyNames.keyLOCK));
		keys.add(new BasicKey("LOG",KeyTypes.keyFunction,KeyNames.keyLOG));
		keys.add(new BasicKey("LONG",KeyTypes.keyType,KeyNames.keyLONG));
		keys.add(new BasicKey("LOOP",KeyTypes.keyStatement,KeyNames.keyLOOP));
		keys.add(new BasicKey("LSET",KeyTypes.keyStatement,KeyNames.keyLSET));
		keys.add(new BasicKey("LTRIM",KeyTypes.keyFunction,KeyNames.keyLTRIM));
		keys.add(new BasicKey("MID",KeyTypes.keyFunction,KeyNames.keyMID));
		keys.add(new BasicKey("MKDIR",KeyTypes.keyStatement,KeyNames.keyMKDIR));
		keys.add(new BasicKey("MOD",KeyTypes.keyOperator,KeyNames.keyMOD));
		keys.add(new BasicKey("MSGBOX",KeyTypes.keyFunction,KeyNames.keyMSGBOX));
		keys.add(new BasicKey("MSGBOX",KeyTypes.keyStatement,KeyNames.keyMSGBOX));
		keys.add(new BasicKey("NAME",KeyTypes.keyStatement,KeyNames.keyNAME));
		keys.add(new BasicKey("NEXT",KeyTypes.keyStatement,KeyNames.keyNEXT));
		keys.add(new BasicKey("NOT",KeyTypes.keyOperator,KeyNames.keyNOT));
		keys.add(new BasicKey("OCT",KeyTypes.keyFunction,KeyNames.keyOCT));
		keys.add(new BasicKey("ON",KeyTypes.keyStatement,KeyNames.keyON));
		keys.add(new BasicKey("ON",KeyTypes.keyKeyword,KeyNames.keyON));
		keys.add(new BasicKey("OPEN",KeyTypes.keyStatement,KeyNames.keyOPEN));
		keys.add(new BasicKey("OPTION",KeyTypes.keyStatement,KeyNames.keyOPTION));
		keys.add(new BasicKey("OR",KeyTypes.keyOperator,KeyNames.keyOR));
		keys.add(new BasicKey("OUTPUT",KeyTypes.keyKeyword,KeyNames.keyOUTPUT));
		keys.add(new BasicKey("PRINT",KeyTypes.keyStatement,KeyNames.keyPRINT));
		keys.add(new BasicKey("PUT",KeyTypes.keyStatement,KeyNames.keyPUT));
		keys.add(new BasicKey("RANDOM",KeyTypes.keyKeyword,KeyNames.keyRANDOM));
		keys.add(new BasicKey("RANDOMIZE",KeyTypes.keyStatement,KeyNames.keyRANDOMIZE));
		keys.add(new BasicKey("READ",KeyTypes.keyKeyword,KeyNames.keyREAD));
		keys.add(new BasicKey("REAL",KeyTypes.keyType,KeyNames.keyREAL));
		keys.add(new BasicKey("REDIM",KeyTypes.keyStatement,KeyNames.keyREDIM));
		keys.add(new BasicKey("REGISTRY",KeyTypes.keyFunction,KeyNames.keyREGISTRY));
		keys.add(new BasicKey("REM",KeyTypes.keyStatement,KeyNames.keyREM));
		keys.add(new BasicKey("RESET",KeyTypes.keyStatement,KeyNames.keyRESET));
		keys.add(new BasicKey("RESUME",KeyTypes.keyKeyword,KeyNames.keyRESUME));
		keys.add(new BasicKey("RESUME",KeyTypes.keyStatement,KeyNames.keyRESUME));
		keys.add(new BasicKey("RETURN",KeyTypes.keyStatement,KeyNames.keyRETURN));
		keys.add(new BasicKey("RIGHT",KeyTypes.keyFunction,KeyNames.keyRIGHT));
		keys.add(new BasicKey("RMDIR",KeyTypes.keyStatement,KeyNames.keyRMDIR));
		keys.add(new BasicKey("RND",KeyTypes.keyFunction,KeyNames.keyRND));
		keys.add(new BasicKey("RSET",KeyTypes.keyStatement,KeyNames.keyRSET));
		keys.add(new BasicKey("RTRIM",KeyTypes.keyFunction,KeyNames.keyRTRIM));
		keys.add(new BasicKey("RUN",KeyTypes.keyStatement,KeyNames.keyRUN));
		keys.add(new BasicKey("SEEK",KeyTypes.keyStatement,KeyNames.keySEEK));
		keys.add(new BasicKey("SELECT",KeyTypes.keyStatement,KeyNames.keySELECT));
		keys.add(new BasicKey("SETREGISTRY",KeyTypes.keyStatement,KeyNames.keySETREGISTRY));
		keys.add(new BasicKey("SGN",KeyTypes.keyFunction,KeyNames.keySGN));
		keys.add(new BasicKey("SHARED",KeyTypes.keyStatement,KeyNames.keySHARED));
		keys.add(new BasicKey("SHELL",KeyTypes.keyFunction,KeyNames.keySHELL));
		keys.add(new BasicKey("SIN",KeyTypes.keyFunction,KeyNames.keySIN));
		keys.add(new BasicKey("SLEEP",KeyTypes.keyStatement,KeyNames.keySLEEP));
		keys.add(new BasicKey("SPACE",KeyTypes.keyFunction,KeyNames.keySPACE));
		keys.add(new BasicKey("SPC",KeyTypes.keyFunction,KeyNames.keySPC));
		keys.add(new BasicKey("SQR",KeyTypes.keyFunction,KeyNames.keySQR));
		keys.add(new BasicKey("STATIC",KeyTypes.keyStatement,KeyNames.keySTATIC));
		keys.add(new BasicKey("STEP",KeyTypes.keyKeyword,KeyNames.keySTEP));
		keys.add(new BasicKey("STOP",KeyTypes.keyStatement,KeyNames.keySTOP));
		keys.add(new BasicKey("CSTR",KeyTypes.keyFunction,KeyNames.keySTR));
		keys.add(new BasicKey("STRIG",KeyTypes.keyFunction,KeyNames.keySTRIG));
		keys.add(new BasicKey("STRING",KeyTypes.keyType,KeyNames.keySTRING));
		keys.add(new BasicKey("STRING",KeyTypes.keyFunction,KeyNames.keySTRING));
		keys.add(new BasicKey("SUB",KeyTypes.keyStatement,KeyNames.keySUB));
		keys.add(new BasicKey("SYSTEM",KeyTypes.keyStatement,KeyNames.keySYSTEM));
		keys.add(new BasicKey("TAB",KeyTypes.keyFunction,KeyNames.keyTAB));
		keys.add(new BasicKey("TAN",KeyTypes.keyFunction,KeyNames.keyTAN));
		keys.add(new BasicKey("THEN",KeyTypes.keyKeyword,KeyNames.keyTHEN));
		keys.add(new BasicKey("TIME",KeyTypes.keyFunction,KeyNames.keyTIME));
		keys.add(new BasicKey("TO",KeyTypes.keyKeyword,KeyNames.keyTO));
		keys.add(new BasicKey("TRUE",KeyTypes.keyConst,KeyNames.keyTRUE));
		keys.add(new BasicKey("TYPE",KeyTypes.keyStatement,KeyNames.keyTYPE));
		keys.add(new BasicKey("UCASE",KeyTypes.keyFunction,KeyNames.keyUCASE));
		keys.add(new BasicKey("UNLOCK",KeyTypes.keyStatement,KeyNames.keyUNLOCK));
		keys.add(new BasicKey("UNTIL",KeyTypes.keyKeyword,KeyNames.keyUNTIL));
		keys.add(new BasicKey("VAL",KeyTypes.keyFunction,KeyNames.keyVAL));
		keys.add(new BasicKey("WEND",KeyTypes.keyStatement,KeyNames.keyWEND));
		keys.add(new BasicKey("WHILE",KeyTypes.keyStatement,KeyNames.keyWHILE));
		keys.add(new BasicKey("WIDTH",KeyTypes.keyStatement,KeyNames.keyWIDTH));
		keys.add(new BasicKey("WRITE",KeyTypes.keyStatement,KeyNames.keyWRITE));
		keys.add(new BasicKey("XOR",KeyTypes.keyOperator,KeyNames.keyXOR));
		keys.add(new BasicKey("",KeyTypes.keyType,KeyNames.keyINVALID));
		}
		
		return keys;
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
