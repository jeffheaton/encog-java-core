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

package org.encog.script.basic;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.encog.script.basic.commands.BasicCommands;
import org.encog.script.basic.commands.BasicFunctions;
import org.encog.script.basic.error.BasicError;
import org.encog.script.basic.error.ErrorNumbers;
import org.encog.script.basic.keys.BasicKey;
import org.encog.script.basic.keys.KeyNames;
import org.encog.script.basic.keys.KeyTypes;
import org.encog.script.basic.stack.Stack;
import org.encog.script.basic.util.BasicPerform;
import org.encog.script.basic.util.BasicUtil;
import org.encog.script.basic.variables.BasicObjectVariable;
import org.encog.script.basic.variables.BasicTypes;
import org.encog.script.basic.variables.BasicVariable;
import org.encog.script.basic.variables.objects.ObjectStdFile;

/**
 * Used to parse lines of basic text.
 */
public class BasicParse implements Basic {

	public static final int MAX_VARIABLE_NAME = 80;
	private BasicCommands commands;
	private BasicFunctions functions;
	private int ifs;
	private Stack stack = new Stack();// The stack
	private String line;
	private int ptr;
	private BasicError lastError;

	private long column;
	private BasicVariable is;// Current value of IS keyword
	private Map<String, BasicVariable> variables;// Linked list of the
													// variables, either local
													// or a
	// pointer to globals in program
	private BasicModule module;
	private BasicLine currentLine;
	private List<BasicLine> subLines;
	private String errorLabel;
	private char nextchar;// The next character to be parsed

	public BasicParse(BasicModule m) {
		init();
		module = m;
		this.commands = new BasicCommands(this);
		this.functions = new BasicFunctions(this);
	}

	public BasicParse() {
		module = null;
		currentLine = null;
		init();
	}

	// Parse Functions
	public void advance() {

		if ((ptr + 1) >= this.line.length())
			nextchar = 0;
		else
			nextchar = this.line.charAt(++ptr);
	}

	public char getchr() {
		char rtn;

		if (nextchar == 0)
			return 0;

		rtn = nextchar;
		advance();
		return rtn;
	}

	public void killSpace() {
		while (nextchar > 0 && ((nextchar == 32) || (nextchar == '\t')))
			advance();

	}

	public BasicVariable parseVariable(int l, int u) {
		BasicVariable result = null;

		killSpace();
		result = expr();
		if (l == 0 && u == 0)
			return result;
		if ((result.getLong() < l) || (result.getLong() > u))
			throw (new BasicError(ErrorNumbers.errorIllegalValue));
		return result;
	}

	public boolean parseVariable(BasicVariable var) {
		killSpace();
		BasicVariable temp = expr();
		var.edit(temp);
		return var.getBoolean();
	}

	public void expectToken(char ch) {
		if (!lookAhead(ch))
			throw (new BasicError(ErrorNumbers.errorIllegalUse));

	}

	public boolean lookAhead(char ch) {

		if (ptr == -1)
			return false;
		killSpace();

		if (nextchar == ch) {
			advance();
			return true;
		}
		return false;
	}

	public boolean lookAhead(String lookfor, boolean partial) {

		if (ptr == -1)
			return false;

		String lookfor2 = lookfor.toUpperCase();

		killSpace();

		int lookForIndex = 0;
		int currentIndex = this.ptr;

		while (lookForIndex < lookfor2.length()
				&& currentIndex < this.line.length()) {
			if (Character.toUpperCase(lookfor.charAt(lookForIndex)) != Character
					.toUpperCase(line.charAt(currentIndex)))
				return false;

			lookForIndex++;
			currentIndex++;
		}

		if (lookForIndex != lookfor2.length())
			return false;

		if (!partial && currentIndex < this.line.length()) {
			char ch = line.charAt(currentIndex);
			if ((ch != 32) && (ch != '\t') && Character.isLetterOrDigit(ch))
				return false;
		}

		ptr = currentIndex;
		advance();
		return true;

	}

	public boolean lookAhead(KeyNames token, boolean partial) {
		BasicKey key;
		key = BasicUtil.findKeyword(token);
		return lookAhead(key.getName(), partial);
	}

	public BasicKey parseNextToken() {
		String str;
		int hold = ptr;
		BasicKey rtn;

		killSpace();
		str = parseVariable();
		if (str.length() < 1)
			return null;

		str = str.toUpperCase();

		rtn = BasicUtil.findKeyword(str);

		if (rtn != null)
			return rtn;

		ptr = hold;
		nextchar = this.line.charAt(this.ptr);

		return null;
	}

	public void init() {
		is = null;
		ptr = 0;
		line = "LINE";
		nextchar = 0;
		variables = new HashMap<String, BasicVariable>();
		ifs = 0;
		column = 0;
	}

	public boolean parse(String l) {
		BasicKey key;

		maint();
		try {
			if (!loadLine(l))
				return true;

			do {
				killSpace();

				if (nextchar == 0)
					return true;

				key = parseNextToken();
				if (key == null) {
					if (isAssignment()) {
						doAssignment();
						continue;
					} else if (this.module.getProgram().execute()) {
						continue;
					} else if (callSub()) {
						lookAhead(')');
						continue;
					} else
						throw (new BasicError(ErrorNumbers.errorSyntax));
				}

				if (key.getType() != KeyTypes.keyStatement
						&& (key.getId() != KeyNames.keyMSGBOX))
					throw (new BasicError(ErrorNumbers.errorKeyword));

				if (!this.commands.process(key))
					return false;

			} while (nextchar == ':');
		} catch (BasicError n) {
			n.setLine(this.currentLine);

			if (errorLabel != null && errorLabel.charAt(0) == '*')
				return true;

			// is the error to be handled?
			if (errorLabel != null) {
				if (this.module.getProgramLabels().containsKey(errorLabel)) {
					go(errorLabel);
					return false;
				}
				this.lastError = n;
			}
			// not handle so throw as a "hard error" (usual case)
			else
				throw (n);

		}

		return true;

	}

	public void doAssignment() {
		BasicVariable varObj;
		String var;

		// First, see if this even looks like an assignment

		// See if we're updating something elsewhere

		if (this.module.getProgram().update())
			return;

		// Try and find a local or global variable
		var = parseVariable();
		varObj = getVariable(var);

		if (varObj == null) {// Automatically "dim" a variable
			varObj = new BasicVariable();

			expectToken('=');
			varObj = expr();
			variables.put(var, varObj);
			return;
		}

		// See if we're really updating an embedded object in an object
		if (varObj.getObjectType() == BasicTypes.typeObject) {
			killSpace();

			if (lookAhead('.')) {
				if (!varObj.getObject().update())
					throw (new BasicError(ErrorNumbers.errorUndefinedObject));
				return;
			}
		}

		expectToken('=');
		varObj.edit(expr());
		variables.put(var, varObj);

	}

	public boolean globalParse(String l) {
		BasicKey key;

		if (!loadLine(l))
			return true;

		do {
			while (nextchar == ':')
				advance();

			killSpace();
			key = parseNextToken();
			if (key == null)
				throw (new BasicError(ErrorNumbers.errorGlobal));
			if (key.getType() != KeyTypes.keyStatement)
				throw (new BasicError(ErrorNumbers.errorGlobal));

			switch (key.getId()) {
			case keyDIM:
				this.commands.cmdDim();
				break;
			case keyFUNCTION:
			case keySUB:
				currentLine = null;
				return false;
			default:
				throw (new BasicError(ErrorNumbers.errorGlobal));
			}

		} while (nextchar == ':');

		return true;
	}

	public BasicVariable createVariable(String var) {
		BasicVariable v;
		int x, y, z;
		BasicVariable varObj;
		BasicKey key;

		killSpace();
		x = y = z = 0;
		if (lookAhead('(')) {
			varObj = parseVariable(0, 0);
			x = varObj.getShort();
			if (x == 0)
				throw (new BasicError(ErrorNumbers.errorDim));
			if (lookAhead(',')) {
				varObj = parseVariable(0, 0);
				y = varObj.getShort();
				if (y == 0)
					throw (new BasicError(ErrorNumbers.errorDim));
				if (lookAhead(',')) {
					varObj = parseVariable(0, 0);
					z = varObj.getShort();
					if (z == 0)
						throw (new BasicError(ErrorNumbers.errorDim));
				}
			}
			expectToken(')');
		}

		killSpace();
		if (nextchar > 0 && lookAhead(KeyNames.keyAS, false)) {
			killSpace();
			key = parseNextToken();

			if (key == null) {
				v = this.module.getProgram().createObject();
				if (v == null) {
					BasicObjectVariable vobj;

					if (lookAhead("STDFILE", false))
						vobj = new ObjectStdFile();
					else
						throw (new BasicError(ErrorNumbers.errorInvalidType));
					v = new BasicVariable(vobj);
				}
			} else
				switch (key.getId()) {
				case keySTRING:
					v = new BasicVariable("");
					break;
				case keyREAL:
					v = new BasicVariable((float) 0.0);
					break;
				case keyLONG:
					v = new BasicVariable((long) 0);
					break;
				case keyINTEGER:
					v = new BasicVariable((short) 0);
					break;
				case keyDOUBLE:
					v = new BasicVariable((double) 0.0);
					break;
				case keyBYTE:
					v = new BasicVariable((byte) 0);
					break;
				case keyBOOLEAN:
					v = new BasicVariable(false);
					break;
				case keyCHARACTER:
					v = new BasicVariable((char) ' ');
					break;
				default:
					throw (new BasicError(ErrorNumbers.errorInvalidType));
				}
		} else {
			// v=new BASIC_VARIABLE((long)0);
			throw (new BasicError(ErrorNumbers.errorExpectedAs));
		}

		if (x > 0)
			v.createArray(x, y, z);
		killSpace();
		return v;
	}

	public boolean callSub() {
		String var;
		int hold = ptr;
		BasicVariable varObj;
		BasicVariable ignore = null;

		var = parseVariable();

		// is it a function inside an object
		varObj = getVariable(var);
		if (varObj != null) {
			if (varObj.getObjectType() == BasicTypes.typeObject) {
				killSpace();
				if (lookAhead('.')) {
					if (varObj.getObject().execute())
						return true;
				}
			}
		}

		// Try and call a global function
		if (this.module.getProgram().call(var, ignore))
			return true;

		ptr = hold;
		nextchar = this.line.charAt(ptr);
		return false;
	}

	public String formatExpression() {
		StringBuilder result = new StringBuilder();
		BasicVariable a;
		String str;
		BasicVariable var;
		long l, space;

		if (lookAhead(KeyNames.keyTAB, false)) {
			expectToken('(');
			var = parseVariable(0, 255);
			expectToken(')');
			l = var.getShort();
			if (l == 0)
				return result.toString();

			space = 10 - (column % 10);
			l--;
			space = space + (l * 10);
			column += space;

			while ((space--) > 0) {
				result.append(" ");
			}

			return result.toString();
		}

		a = expr();
		str = a.toString();

		column += str.length();
		result.append(str);
		return result.toString();
	}

	public void doFileInput(long l) {
	}

	public BasicVariable constant() {
		BasicVariable target = new BasicVariable();
		double value, rtn;
		int fractlnth, exponent;
		char sign;
		boolean neg = false;
		String str = "";

		switch (nextchar) {
		case '\"':
			str = parseString();
			target.edit(str);
			return target;

		case '-':
			advance();
			neg = true;
			break;

		case '+':
			advance();
			break;
		}

		value = 0.0;
		fractlnth = 0;
		exponent = 0;
		sign = '+';

		// whole number part

		while (Character.isDigit(nextchar))
			value = (10.0 * value) + (getchr() - '0');

		// Optional fractional
		if (nextchar == '.') {
			advance();

			while (Character.isDigit(nextchar)) {
				value = (10.0 * value) + (getchr() - '0');
				fractlnth++;
			}
		}

		// Optional exponent

		if (nextchar == 'E') {
			advance();

			if ((nextchar == '+') || (nextchar == '-')) {
				advance();
				sign = getchr();
			}

			while (Character.isDigit(nextchar))
				exponent = (int) (10.0 * exponent) + (getchr() - '0');
		}

		if (sign == '-')
			rtn = value * Math.exp(Math.log(10) * (-exponent - fractlnth));
		else
			rtn = value * Math.exp(Math.log(10) * (exponent - fractlnth));

		if (neg)
			rtn = -rtn;
		target.edit(rtn);
		return target;
	}

	public BasicVariable variable() {
		BasicVariable result = new BasicVariable();
		String varName;
		BasicVariable var;
		BasicKey key;

		// Check for "module" function

		if (this.module.getProgram().scan(result))
			return result;

		// First check for built in function, or built-in constant

		varName = parseVariable();
		key = BasicUtil.findKeyword(varName);
		if (key != null) {
			if (key.getType() == KeyTypes.keyConst) {
				switch (key.getId()) {
				case keyTRUE:
					result.edit(true);
					break;
				case keyFALSE:
					result.edit(false);
					break;
				case keyIS:
					if (is == null)
						throw (new BasicError(ErrorNumbers.errorKeyword));
					result.edit(is);
					break;
				}
				return result;
			}

			if ((key.getType() == KeyTypes.keyFunction)) {
				functions.callInternalFunction(key.getId(), result);
				return result;
			}
			throw (new BasicError(ErrorNumbers.errorKeyword));
		}

		// Check for user defined function

		if (this.module.getProgram().call(varName, result))
			return result;

		// Check for actual variable

		var = getVariable(varName);

		if (var == null) {// Undefined variable
			switch (result.getObjectType()) {
			case typeUndefined:
				result.edit((short) 0);
				break;
			case typeString:
				result.edit("");
				break;
			case typeFloat:
			case typeDouble:
				result.edit((double) 0.0);
				break;
			case typeInteger:
			case typeLong:
			case typeByte:
				result.edit((long) 0);
				break;
			case typeBoolean:
				result.edit(false);
				break;
			case typeCharacter:
				result.edit(' ');
				break;
			default:
				throw (new BasicError(ErrorNumbers.errorUnknownVariable));
			}
			return result;
		} else if (var.getObjectType() == BasicTypes.typeObject) {
			killSpace();
			if (lookAhead('.')) {
				if (!var.getObject().scan(result))
					throw (new BasicError(ErrorNumbers.errorUndefinedObject));

				return result;
			}
		}

		result.edit(var);
		return result;
	}

	public BasicVariable expr() {
		BasicVariable rtn = new BasicVariable();
		char sign;
		BasicVariable target = new BasicVariable();
		boolean b;

		killSpace();

		if ((nextchar == '+') || (nextchar == '-'))
			sign = getchr();
		else
			sign = '+';

		if (lookAhead("NOT", false))
			sign = 'N';

		if (rtn.getObjectType() != BasicTypes.typeUndefined) {
			target.edit(rtn);// Force assignments to be of the same type by
								// priming
			// target with the right type
		}

		target = expr1();
		killSpace();

		if (sign == '-')
			BasicPerform.performNeg(target, target);

		if (sign == 'N')
			target.edit(!target.getBoolean());

		while ((nextchar == '&') || (nextchar == '+') || (nextchar == '-')
				|| (this.line.indexOf("OR", ptr) == ptr)
				|| (this.line.indexOf("AND", ptr) == ptr)) {
			if (lookAhead("OR", false)) {
				BasicVariable t;
				t = expr1();
				if (target.getObjectType() == BasicTypes.typeBoolean) {
					b = (target.getBoolean() || t.getBoolean());
					target.edit(b);
				} else
					target.edit(target.getLong() | t.getLong());
			} else if (lookAhead("AND", false)) {
				BasicVariable t;
				t = expr1();
				if (target.getObjectType() == BasicTypes.typeBoolean) {
					b = (target.getBoolean() && t.getBoolean());
					target.edit(b);
				} else
					target.edit(target.getLong() & t.getLong());
			} else if (lookAhead('-')) {
				BasicVariable t;
				t = expr1();

				BasicPerform.performSub(target, target, t);
			} else if (getchr() == '+') {
				BasicVariable t;
				BasicVariable t2 = new BasicVariable();
				t = expr1();
				BasicPerform.performAdd(t2, target, t);
				target.free();
				target.edit(t2);
			} else {
				BasicVariable t;
				BasicVariable t2 = new BasicVariable();
				t = expr1();
				BasicPerform.performCat(t2, target, t);
				target.free();
				target.edit(t2);
			}

		}

		// if( (rtn.GetType()==typeUndefined) || forceRefresh )
		rtn.edit(target);
		return rtn;
	}

	public BasicVariable expr1() {
		BasicVariable target = new BasicVariable();
		BasicVariable t;
		BasicVariable v = new BasicVariable();
		killSpace();

		target = expr1p5();
		killSpace();

		if (lookAhead(KeyNames.keyMOD, false)) {
			t = expr1p5();
			target.edit((long) (target.getLong() % t.getLong()));
			return target;
		}

		if (!(nextchar > 0 && ("/*<>=".indexOf(nextchar) != -1)))
			return target;

		while (nextchar > 0 && ("/*<>=".indexOf(nextchar) != -1))
			switch (getchr()) {
			case '*':
				t = expr1p5();
				BasicPerform.performMul(target, target, t);
				break;

			case '/':
				t = expr1p5();
				if (t.getDouble() == 0)
					throw (new BasicError(ErrorNumbers.errorDivZero));
				BasicPerform.performDiv(target, target, t);
				break;

			case '=':
				v.edit(target);
				t = expr1p5();
				target.free();
				target.edit((boolean) (v.compareE(t)));
				break;

			case '<':
				killSpace();
				if (nextchar == '>') {
					advance();
					v.edit(target);
					t = expr1p5();
					target.free();
					target.edit(v.compareNE(t));
				} else if (nextchar == '=') {
					advance();
					v.edit(target);
					t = expr1p5();
					target.free();
					target.edit((boolean) (v.compareLTE(t)));
				} else {
					v.edit(target);
					t = expr1p5();
					target.free();
					target.edit((boolean) (v.compareLT(t)));
				}
				break;

			case '>':
				if (nextchar == '=') {
					advance();
					v.edit(target);
					t = expr1p5();
					target.free();
					target.edit((boolean) (v.compareGTE(t)));
				} else {
					v.edit(target);
					t = expr1p5();
					target.free();
					target.edit(v.compareGT(t));
				}
				break;

			}
		;
		return target;
	}

	public BasicVariable expr1p5() {
		BasicVariable target = new BasicVariable();

		killSpace();
		if ((nextchar >= 'A') && (nextchar <= 'Z'))
			target = variable();
		else if ((nextchar == '+') || (nextchar == '-')
				|| Character.isDigit(nextchar) || (nextchar == '.')
				|| (nextchar == '\"'))
			target = constant();
		else if (nextchar == '(') {
			advance();
			target = expr();
			if (nextchar == ')')
				advance();
		} else
			throw (new BasicError(ErrorNumbers.errorSyntax));

		while (nextchar == '^') {
			advance();
			BasicVariable a = new BasicVariable();
			BasicVariable b;
			a.edit(target);
			b = expr1p5();
			target.free();
			BasicPerform.performPower(target, a, b);
		}
		return target;
	}

	public String parseString() {
		StringBuilder str = new StringBuilder();

		char ch;
		int i = 0;

		if (nextchar == '\"')
			advance();
		do {
			ch = getchr();
			if (ch == 34) {
				// handle double quote
				if (nextchar == 34) {
					advance();
					str.append(ch);
					ch = getchr();
				}
			} else
				str.append(ch);
		} while ((ch != 34) && ch > 0);

		if (ch != 34)
			throw (new BasicError(ErrorNumbers.errorBadString));
		return str.toString();
	}

	/**
	 * Parse in a variable name.
	 * 
	 * @return A variable name.
	 */
	public String parseVariable() {
		StringBuilder result = new StringBuilder();

		char ch;
		int i = 0;

		killSpace();
		if (nextchar == 0) {
			return "";
		}
		do {
			ch = getchr();
			result.append(ch);
		} while ((Character.isLetterOrDigit(nextchar) || (nextchar == '_'))
				&& nextchar != -1 && (i++ < MAX_VARIABLE_NAME));

		if (i >= MAX_VARIABLE_NAME)
			throw (new BasicError(ErrorNumbers.errorLongName));

		return result.toString();
	}

	private boolean isAssignment() {
		int p;

		p = ptr;

		// Move to first whitespace char
		while (p < this.line.length()) {
			char ch = this.line.charAt(p);
			if ((ch == ' ') || (ch == '\t') || (ch == '='))
				break;
			p++;
		}
		if (p >= line.length())
			return false;

		// Move past any whitespace
		while (p < line.length() && (line.charAt(p) == ' ')
				|| (line.charAt(p) == '\t'))
			p++;

		if (p >= line.length())
			return false;

		// Is it an assignment

		if (line.charAt(p) != '=')
			return false;

		return true;
	}

	public boolean evaluateDo() {
		BasicVariable varObj;
		boolean not;

		killSpace();

		if (lookAhead(KeyNames.keyWHILE, false))
			not = false;
		else if (lookAhead(KeyNames.keyUNTIL, false))
			not = true;
		else
			throw (new BasicError(ErrorNumbers.errorIllegalUse));

		varObj = expr();
		if (not) {
			if (varObj.getBoolean())
				return false;
			else
				return true;
		} else {
			if (varObj.getBoolean())
				return true;
			else
				return false;
		}
	}

	public BasicVariable getVariable(String name) {
		BasicVariable rtn;

		rtn = variables.get(name);
		if (rtn == null)
			rtn = (BasicVariable) this.module.getProgram().getGlobals().get(
					name);
		if (rtn == null)
			return null;
		if (rtn.isArray()) {
			killSpace();
			if (lookAhead('(')) {
				BasicVariable varObj;
				int x = 0, y = 0, z = 0;

				varObj = parseVariable(0, 0);
				x = varObj.getShort();
				if (x < 1)
					throw (new BasicError(ErrorNumbers.errorDim));
				if (lookAhead(',')) {
					varObj = parseVariable(0, 0);
					y = varObj.getShort();
					if (y < 1)
						throw (new BasicError(ErrorNumbers.errorDim));
					if (lookAhead(',')) {
						varObj = parseVariable(0, 0);
						z = varObj.getShort();
						if (z < 1)
							throw (new BasicError(ErrorNumbers.errorDim));
					}
				}
				expectToken(')');

				rtn.setArrayLocation(x, y, z);
			} else
				throw (new BasicError(ErrorNumbers.errorArray));
		}

		return rtn;
	}

	/**
	 * Will be defined in subclass.
	 */
	public boolean scan(BasicVariable target) {

		return false;
	}

	/**
	 * Will be defined in subclass.
	 */
	public boolean update() {
		return false;
	}

	/**
	 * Will be defined in subclass.
	 */
	public boolean execute() {
		return false;
	}

	/**
	 * Will be defined in subclass.
	 */
	public boolean newObject() {
		return false;
	}

	/**
	 * Will be defined in subclass.
	 */
	public BasicVariable createObject() {
		return null;
	}

	/**
	 * Will be defined in subclass.
	 */
	public void createGlobals() {

	}

	public void moveNextLine() {
		do {
			int num = currentLine.getNumber();
			num++;
			if (num >= subLines.size())
				currentLine = null;
			else
				currentLine = subLines.get(num);

		} while ((currentLine != null) && !loadLine(currentLine.getText()));
	}

	public void movePastColen() {
		boolean quote;

		killSpace();
		quote = false;
		while (nextchar > 0 && (quote || (nextchar != ':'))) {
			if (nextchar == '\"')
				quote = !quote;
			advance();
		}
		if (nextchar == ':')
			advance();
	}

	public void maint() {
		this.module.getProgram().maint();
	}

	public boolean call(String var) {
		boolean isFunct;
		String varName = null;
		BasicParse caller;

		caller = this.module.getProgram().getFunction();

		currentLine = module.findFunction(var);

		if (currentLine == null)
			return false;

		boolean callingMain = (caller == null);

		if (!loadLine(currentLine.getText()))
			return false;

		if (!lookAhead(KeyNames.keyFUNCTION, false)) {
			if (lookAhead(KeyNames.keySUB, false))
				isFunct = false;
			else
				throw (new BasicError(ErrorNumbers.errorFunctionProc));
		} else
			isFunct = true;

		if (!callingMain) {
			killSpace();
			if (nextchar > 0 && (nextchar != ':')) {

				varName = parseVariable();
				killSpace();

				lookAhead('(');
				paramaterList(caller);
				lookAhead(')');
			}

			killSpace();

			if ((this.line.charAt(this.ptr) == 'A')
					&& (this.line.charAt(this.ptr + 1) == 'S')) {
				BasicVariable v = createVariable(var);
				this.variables.put(varName, v);
			}
		}

		// Actually run the program

		subLines = this.currentLine.getSub();

		if (subLines.size() == 0)
			return false;

		currentLine = subLines.get(0);

		while (currentLine != null
				&& (!this.module.getProgram().getQuitProgram())) {
			if (parse(currentLine.getText())) {
				moveNextLine();
			}
		}
		return true;
	}

	public boolean eventCall(String var, int x, int y, int z) {

		currentLine = this.module.findFunction(var);
		if (currentLine == null)
			return false;

		eventParamaterList(x, y, z);

		currentLine = (BasicLine) currentLine.getSub().get(0);
		if (currentLine == null)
			return false;

		// Actually run the program
		while (currentLine != null
				&& (!this.module.getProgram().getQuitProgram())) {
			if (parse(currentLine.getText()))
				currentLine = (BasicLine) currentLine.getSub().get(0);
		}
		return true;
	}

	// The following call is only used to run the first pass to check
	// for global data.
	public boolean call() {
		this.variables = this.module.getProgram().getGlobals();

		for (BasicLine currentLine : this.module.getProgramLines()) {
			if (!globalParse(currentLine.getText()))
				break;
		}

		return true;

	}

	public void paramaterList(BasicParse caller) {
		String varName;
		BasicVariable v;
		BasicVariable v2;

		while (nextchar > 0 && (nextchar != ':') && (nextchar != ')')) {
			boolean byref = false;

			if (lookAhead(KeyNames.keyBYREF, false))
				byref = true;
			else
				lookAhead(KeyNames.keyBYVAL, false);

			varName = parseVariable();

			if (byref) {
				v2 = createVariable(varName);
				v2 = new BasicVariable();

				if ((caller.nextchar == ':') || caller.nextchar > 0)
					throw (new BasicError(ErrorNumbers.errorParamaters));

				v = caller.parseVariable(0, 0);
				if (v == null)
					throw (new BasicError(ErrorNumbers.errorUndefinedVariable));
				v.CreateRef(v2);
				variables.put(varName, v2);
			} else {
				v = createVariable(varName);
				v = caller.expr();
				variables.put(varName, v);
			}
			caller.killSpace();
			killSpace();
			if (nextchar == ',') {
				caller.expectToken(',');
				caller.killSpace();
				expectToken(',');
				killSpace();
			}
		}
	}

	public void eventParamaterList(int x, int y, int z) {
		String varName;
		BasicVariable v;
		int count = 0;

		if (!loadLine(currentLine.getText()))
			return;

		if (!lookAhead(KeyNames.keyFUNCTION, false)
				&& !lookAhead(KeyNames.keySUB, false))
			throw (new BasicError(ErrorNumbers.errorFunctionProc));
		varName = parseVariable();
		killSpace();
		if (nextchar != '(')
			return;
		advance();
		killSpace();

		while (nextchar != ')' && nextchar > 0) {
			lookAhead(KeyNames.keyBYREF, false);// Ignore
			lookAhead(KeyNames.keyBYVAL, false);// Ignore
			varName = parseVariable();

			v = createVariable(varName);
			count++;
			switch (count) {
			case 1:
				v.edit((long) x);
				break;
			case 2:
				v.edit((long) y);
				break;
			case 3:
				v.edit((long) z);
				break;
			}
			variables.put(varName, v);

			killSpace();
			if (nextchar == ',') {
				expectToken(',');
				killSpace();
			}
		}
	}

	public boolean loadLine(String l) {

		this.module.getProgram().setFunction(this);

		if (l == null || l.length() == 0)
			return false;

		this.line = BasicUtil.basicToUpper(l);
		this.ptr = 0;
		this.nextchar = this.line.charAt(0);

		return true;
	}

	public void moveToEndIf(boolean stopOnElse) {
		while (currentLine != null) {
			do {
				if (lookAhead(KeyNames.keyIF, false)) {// Found a nested if, is
														// it block or line?
					BasicVariable ignore = expr();
					if (!lookAhead(KeyNames.keyTHEN, false))
						throw (new BasicError(ErrorNumbers.errorNoThen));
					killSpace();
					if (nextchar == 0)
						moveToEndIf(false);
				}

				if (lookAhead(KeyNames.keyEND, true)) {
					killSpace();
					if (lookAhead(KeyNames.keyIF, false))
						return;
					else
						throw (new BasicError(ErrorNumbers.errorBlock));
				}

				if (stopOnElse) {
					if (lookAhead(KeyNames.keyELSE, false)) {
						ifs++;
						return;
					}

					if (lookAhead(KeyNames.keyELSEIF, false)) {
						// ifs++;
						this.commands.cmdIf();
						return;
					}
				}

				movePastColen();

			} while (nextchar > 0);

			moveNextLine();
		}
	}

	public void moveToNext() {
		while (currentLine != null) {
			do {
				if (lookAhead(KeyNames.keyFOR, false))
					moveToNext();

				if (lookAhead(KeyNames.keyEND, false)) {
					killSpace();
					if (!lookAhead(KeyNames.keyIF, false))
						throw (new BasicError(ErrorNumbers.errorBlock));
				}

				if (lookAhead(KeyNames.keyNEXT, false))
					return;

				movePastColen();

			} while (nextchar > 0);

			moveNextLine();
		}
	}

	public void moveToWEnd() {
		while (currentLine != null) {
			do {
				if (lookAhead(KeyNames.keyWHILE, false))
					moveToWEnd();

				if (lookAhead(KeyNames.keyEND, false)) {
					killSpace();
					if (!lookAhead(KeyNames.keyIF, false))
						throw (new BasicError(ErrorNumbers.errorBlock));
				}

				if (lookAhead(KeyNames.keyWEND, false))
					return;

				movePastColen();

			} while (nextchar > 0);

			moveNextLine();
		}
	}

	public void moveToLoop() {
		while (currentLine != null) {
			do {
				if (lookAhead(KeyNames.keyDO, false))
					moveToLoop();

				if (lookAhead(KeyNames.keyEND, false)) {
					killSpace();
					if (!lookAhead(KeyNames.keyIF, false))
						throw (new BasicError(ErrorNumbers.errorBlock));
				}

				if (lookAhead(KeyNames.keyLOOP, false))
					return;

				movePastColen();

			} while (nextchar > 0);

			moveNextLine();
		}

	}

	public void moveToEndCase() {
		while (currentLine != null) {
			do {
				if (lookAhead(KeyNames.keySELECT, false))
					moveToEndCase();

				if (lookAhead(KeyNames.keyEND, false)) {
					killSpace();
					if (!lookAhead(KeyNames.keyCASE, false))
						return;
				}

				movePastColen();

			} while (nextchar > 0);

			moveNextLine();
		}
	}

	public int getNextChar() {
		return this.nextchar;
	}

	public BasicModule getModule() {
		return this.module;
	}

	/**
	 * @return the column
	 */
	public long getColumn() {
		return column;
	}

	/**
	 * @param column
	 *            the column to set
	 */
	public void setColumn(long column) {
		this.column = column;
	}

	public void increaseIFS() {
		this.ifs++;
	}

	public void decreaseIFS() {
		if (this.ifs == 0)
			throw new BasicError(ErrorNumbers.errorNoIf);
		this.ifs--;
	}

	public int getPtr() {
		return this.ptr;
	}

	public String getLine() {
		return this.line;
	}

	public BasicLine getCurrentLine() {
		return this.currentLine;
	}

	public Stack getStack() {
		return this.stack;
	}

	public void setCurrentLine(BasicLine currentLine) {
		this.currentLine = currentLine;
		if (this.currentLine != null)
			loadLine(this.currentLine.getText());
	}

	public void parse() {
		if (this.getCurrentLine() != null)
			parse(this.getCurrentLine().getText());

	}

	public void addVariable(String var, BasicVariable varObj) {
		this.variables.put(var, varObj);
	}

	public void go(String label) {
		String key = label.toUpperCase();

		if (!this.getModule().getProgramLabels().containsKey(key))
			throw (new BasicError(ErrorNumbers.errorLabel));

		BasicLine line = this.getModule().getProgramLabels().get(key);
		setCurrentLine(line);
	}

	/**
	 * @return the errorLabel
	 */
	public String getErrorLabel() {
		return errorLabel;
	}

	/**
	 * @param errorLabel
	 *            the errorLabel to set
	 */
	public void setErrorLabel(String errorLabel) {
		this.errorLabel = errorLabel;
	}

	public BasicFunctions getStandardFunctions() {
		return this.functions;
	}

	/**
	 * @return the lastError
	 */
	public BasicError getLastError() {
		return lastError;
	}

	/**
	 * @param lastError the lastError to set
	 */
	public void setLastError(BasicError lastError) {
		this.lastError = lastError;
	}
	
	

}
