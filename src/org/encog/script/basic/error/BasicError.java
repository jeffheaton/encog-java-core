/*
 * Encog(tm) Core v2.5 
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 * 
 * Copyright 2008-2010 by Heaton Research Inc.
 * 
 * Released under the LGPL.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 * 
 * Encog and Heaton Research are Trademarks of Heaton Research, Inc.
 * For information on Heaton Research trademarks, visit:
 * 
 * http://www.heatonresearch.com/copyright.html
 */

package org.encog.script.basic.error;

import java.util.HashMap;
import java.util.Map;

import org.encog.EncogError;
import org.encog.script.basic.BasicLine;

/**
 * Thrown when an error occurs in the script.
 */
public class BasicError extends EncogError {
	
	private static Map<ErrorNumbers,String> errors;
	private BasicLine line;
	
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

	/**
	 * @return the line
	 */
	public BasicLine getLine() {
		return line;
	}

	/**
	 * @param line the line to set
	 */
	public void setLine(BasicLine line) {
		this.line = line;
	}

	
}
