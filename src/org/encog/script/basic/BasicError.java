package org.encog.script.basic;

import java.util.HashMap;
import java.util.Map;

import org.encog.EncogError;

public class BasicError extends EncogError {
	
	private static Map<ErrorNumbers,String> errors;
	
	
	public BasicError(ErrorNumbers id)
	{
		super(BasicError.getErrors().get(id));
		this.id = id;
	}
	
	static Map<ErrorNumbers,String> getErrors()
	{
		if( errors==null )
		{
			errors = new HashMap<ErrorNumbers,String>();
			errors.put( ErrorNumbers.errorAlreadyDeclared , "Name Already Declared");
			errors.put( ErrorNumbers.errorBounds,"Array Bounds");
			errors.put( ErrorNumbers.errorBreak,"Break");
			errors.put( ErrorNumbers.errorExecute,"Can't Execute Program");
			errors.put( ErrorNumbers.errorDim,"Dim Statement");
			errors.put( ErrorNumbers.errorNoIf,"END IF without IF");
			errors.put( ErrorNumbers.errorExpectedAs,"Expected AS Keyword");
			errors.put( ErrorNumbers.errorNotFound,"File Not Found");
			errors.put( ErrorNumbers.errorNoFunction,"Function Not Found");
			errors.put( ErrorNumbers.errorFunctionProc,"Function/Procedure");
			errors.put( ErrorNumbers.errorGDI,"GDI");
			errors.put( ErrorNumbers.errorDivZero,"Division by Zero");
			errors.put( ErrorNumbers.errorNoThen,"If Without Then");
			errors.put( ErrorNumbers.errorIllegalUse,"Illegal Use");
			errors.put( ErrorNumbers.errorIllegalOperator,"Illegal Use of Operator");
			errors.put( ErrorNumbers.errorIllegalValue,"Illegal Value");
			errors.put( ErrorNumbers.errorInput,"Input");
			errors.put( ErrorNumbers.errorBoolean,"Invalid Boolean Operator");
			errors.put( ErrorNumbers.errorKeyword,"Invalid Use of Keyword");
			errors.put( ErrorNumbers.errorInvalidType,"Invalid Type");
			errors.put( ErrorNumbers.errorModule,"Module Not Loaded");
			errors.put( ErrorNumbers.errorNoEqual,"For Without =");
			errors.put( ErrorNumbers.errorNoFor,"Next Without For");
			errors.put( ErrorNumbers.errorNoTo,"For Without To");
			errors.put( ErrorNumbers.errorNoHandles,"No Handles Left");
			errors.put( ErrorNumbers.errorGlobal,"Not Declarative Statement in Global Area");
			errors.put( ErrorNumbers.errorNoArray,"Not An Array");
			errors.put( ErrorNumbers.errorParamaters,"Not Enough Parameters");
			errors.put( ErrorNumbers.errorNotYet,"Functionality Not Implemented");
			errors.put( ErrorNumbers.errorNoMemory,"Out of Memory");
			errors.put( ErrorNumbers.errorProgram,"Program Mode Only");
			errors.put( ErrorNumbers.errorNoGosub,"Return Without Gosub");
			errors.put( ErrorNumbers.errorString,"String Too Long");
			errors.put( ErrorNumbers.errorSyntax,"Syntax");
			errors.put( ErrorNumbers.errorType,"Type Mismatch");
			errors.put( ErrorNumbers.errorLabel,"Undefined Label");
			errors.put( ErrorNumbers.errorUndefinedObject,"Undefined Object Property/Method");
			errors.put( ErrorNumbers.errorUndefinedVariable,"Undefined Variable");
			errors.put( ErrorNumbers.errorEOL,"Unexpected End of Line");
			errors.put( ErrorNumbers.errorUnknownVariable,"Unknown Variable Type");
			errors.put( ErrorNumbers.errorConversion,"Unsupported Conversion");
			errors.put( ErrorNumbers.errorBlock,"Unterminated Block");
			errors.put( ErrorNumbers.errorBadString,"Unterminated String");
			errors.put( ErrorNumbers.errorAlreadyDefined,"Variable Already Defined");
			errors.put( ErrorNumbers.errorArray,"Variable is an Array");
			errors.put( ErrorNumbers.errorLongName,"Variable Name Too Long");
			errors.put( ErrorNumbers.errorWend,"Wend Without While");
			errors.put( ErrorNumbers.errorDisk,"Disk");
			errors.put( ErrorNumbers.errorHandleUsed,"File Handle In Use");
			errors.put( ErrorNumbers.errorHandleUndefined,"File Handle Undefined");
			errors.put( ErrorNumbers.errorIllegalFunctionName,"Illegal Function Name");
			errors.put( ErrorNumbers.errorEOF,"End of File");
			errors.put( ErrorNumbers.errorAccess,"Access");
			errors.put( ErrorNumbers.errorTrailer,"");
		}
		return errors;
	}
	
	private ErrorNumbers id;


	public ErrorNumbers getId() {
		return id;
	}

	
}
