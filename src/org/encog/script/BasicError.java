package org.encog.script;

import java.util.HashMap;
import java.util.Map;

import org.encog.EncogError;

public class BasicError extends EncogError {
	
	public static Map<ErrorNumbers,String> ERRORS;
	
	
	public BasicError(ErrorNumbers id)
	{
		super(ERRORS.get(id));
		this.id = id;
	}
	
	static
	{
		if( ERRORS!=null )
		{
			ERRORS = new HashMap<ErrorNumbers,String>();
			ERRORS.put( ErrorNumbers.errorAlreadyDeclared , "Name Already Declared");
			ERRORS.put( ErrorNumbers.errorBounds,"Array Bounds");
			ERRORS.put( ErrorNumbers.errorBreak,"Break");
			ERRORS.put( ErrorNumbers.errorExecute,"Can't Execute Program");
			ERRORS.put( ErrorNumbers.errorDim,"Dim Statement");
			ERRORS.put( ErrorNumbers.errorNoIf,"END IF without IF");
			ERRORS.put( ErrorNumbers.errorExpectedAs,"Expected AS Keyword");
			ERRORS.put( ErrorNumbers.errorNotFound,"File Not Found");
			ERRORS.put( ErrorNumbers.errorNoFunction,"Function Not Found");
			ERRORS.put( ErrorNumbers.errorFunctionProc,"Function/Procedure");
			ERRORS.put( ErrorNumbers.errorGDI,"GDI");
			ERRORS.put( ErrorNumbers.errorDivZero,"Division by Zero");
			ERRORS.put( ErrorNumbers.errorNoThen,"If Without Then");
			ERRORS.put( ErrorNumbers.errorIllegalUse,"Illegal Use");
			ERRORS.put( ErrorNumbers.errorIllegalOperator,"Illegal Use of Operator");
			ERRORS.put( ErrorNumbers.errorIllegalValue,"Illegal Value");
			ERRORS.put( ErrorNumbers.errorInput,"Input");
			ERRORS.put( ErrorNumbers.errorBoolean,"Invalid Boolean Operator");
			ERRORS.put( ErrorNumbers.errorKeyword,"Invalid Use of Keyword");
			ERRORS.put( ErrorNumbers.errorInvalidType,"Invalid Type");
			ERRORS.put( ErrorNumbers.errorModule,"Module Not Loaded");
			ERRORS.put( ErrorNumbers.errorNoEqual,"For Without =");
			ERRORS.put( ErrorNumbers.errorNoFor,"Next Without For");
			ERRORS.put( ErrorNumbers.errorNoTo,"For Without To");
			ERRORS.put( ErrorNumbers.errorNoHandles,"No Handles Left");
			ERRORS.put( ErrorNumbers.errorGlobal,"Not Declarative Statement in Global Area");
			ERRORS.put( ErrorNumbers.errorNoArray,"Not An Array");
			ERRORS.put( ErrorNumbers.errorParamaters,"Not Enough Parameters");
			ERRORS.put( ErrorNumbers.errorNotYet,"Functionality Not Implemented");
			ERRORS.put( ErrorNumbers.errorNoMemory,"Out of Memory");
			ERRORS.put( ErrorNumbers.errorProgram,"Program Mode Only");
			ERRORS.put( ErrorNumbers.errorNoGosub,"Return Without Gosub");
			ERRORS.put( ErrorNumbers.errorString,"String Too Long");
			ERRORS.put( ErrorNumbers.errorSyntax,"Syntax");
			ERRORS.put( ErrorNumbers.errorType,"Type Mismatch");
			ERRORS.put( ErrorNumbers.errorLabel,"Undefined Label");
			ERRORS.put( ErrorNumbers.errorUndefinedObject,"Undefined Object Property/Method");
			ERRORS.put( ErrorNumbers.errorUndefinedVariable,"Undefined Variable");
			ERRORS.put( ErrorNumbers.errorEOL,"Unexpected End of Line");
			ERRORS.put( ErrorNumbers.errorUnknownVariable,"Unknown Variable Type");
			ERRORS.put( ErrorNumbers.errorConversion,"Unsupported Conversion");
			ERRORS.put( ErrorNumbers.errorBlock,"Unterminated Block");
			ERRORS.put( ErrorNumbers.errorBadString,"Unterminated String");
			ERRORS.put( ErrorNumbers.errorAlreadyDefined,"Variable Already Defined");
			ERRORS.put( ErrorNumbers.errorArray,"Variable is an Array");
			ERRORS.put( ErrorNumbers.errorLongName,"Variable Name Too Long");
			ERRORS.put( ErrorNumbers.errorWend,"Wend Without While");
			ERRORS.put( ErrorNumbers.errorDisk,"Disk");
			ERRORS.put( ErrorNumbers.errorHandleUsed,"File Handle In Use");
			ERRORS.put( ErrorNumbers.errorHandleUndefined,"File Handle Undefined");
			ERRORS.put( ErrorNumbers.errorIllegalFunctionName,"Illegal Function Name");
			ERRORS.put( ErrorNumbers.errorEOF,"End of File");
			ERRORS.put( ErrorNumbers.errorAccess,"Access");
			ERRORS.put( ErrorNumbers.errorTrailer,"");
		}
		
	}
	
	private ErrorNumbers id;


	public ErrorNumbers getId() {
		return id;
	}

	
}
