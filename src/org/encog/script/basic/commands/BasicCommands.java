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

package org.encog.script.basic.commands;

import java.awt.Toolkit;
import java.io.File;

import javax.swing.JOptionPane;

import org.encog.script.basic.BasicLine;
import org.encog.script.basic.BasicParse;
import org.encog.script.basic.error.BasicError;
import org.encog.script.basic.error.ErrorNumbers;
import org.encog.script.basic.keys.BasicKey;
import org.encog.script.basic.keys.KeyNames;
import org.encog.script.basic.stack.StackEntry;
import org.encog.script.basic.stack.StackEntryType;
import org.encog.script.basic.util.BasicUtil;
import org.encog.script.basic.variables.BasicVariable;

/**
 * Provides the execution for all of the built-in basic commands.
 */
public class BasicCommands {

	private BasicParse parse;

	public BasicCommands(BasicParse basicParse) {
		this.parse = basicParse;
	}

	public boolean process(BasicKey key) {
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
		Toolkit.getDefaultToolkit().beep();
	}

	public void cmdCall() {
		this.parse.expr();
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

		for (;;) {
			this.parse.killSpace();
			var = this.parse.parseVariable();

			if (this.parse.getVariable(var) != null)
				throw new BasicError(ErrorNumbers.errorDim);

			if (BasicUtil.findKeyword(var) != null)
				throw (new BasicError(ErrorNumbers.errorKeyword));

			if (this.parse.getVariable(var) != null)
				throw (new BasicError(ErrorNumbers.errorAlreadyDefined));

			v = this.parse.createVariable(var);
			this.parse.addVariable(var, v);

			this.parse.killSpace();
			if (!this.parse.lookAhead(','))
				break;
		}
	}

	public void cmdDo() {
		this.parse.killSpace();

		if (this.parse.getNextChar() != 0) {
			if (this.parse.evaluateDo())
				this.parse.getStack().push(
						new StackEntry(StackEntryType.stackDo, this.parse
								.getCurrentLine(), 0));
			else
				this.parse.moveToLoop();
		} else
			this.parse.getStack().push(
					new StackEntry(StackEntryType.stackDo, this.parse
							.getCurrentLine(), 1));
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
		this.parse.killSpace();

		if (this.parse.lookAhead(KeyNames.keyIF, false)) {
			cmdEndIf();
		} else {
			this.parse.killSpace();

			if (this.parse.getNextChar() == 0)
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
		BasicKey key;

		key = this.parse.parseNextToken();
		if (key == null)
			throw (new BasicError(ErrorNumbers.errorIllegalUse));
		switch (key.getId()) {
		case keyDO:
			break;
		case keyFOR:
			if (this.parse.getStack().peekType() != StackEntryType.stackFor)
				throw (new BasicError(ErrorNumbers.errorIllegalUse));
			this.parse.getStack().pop();
			this.parse.moveToNext();
			return true;

		case keyFUNCTION:
		case keySUB:
			this.parse.setCurrentLine(null);
			return false;
		default:
			throw (new BasicError(ErrorNumbers.errorKeyword));
		}
		return true;
	}

	public void cmdFor() {
		BasicVariable a, varObj;
		String var;
		int start, stop, step;

		var = this.parse.parseVariable();
		this.parse.expectToken('=');

		a = this.parse.expr();
		start = (int) a.getLong();

		if (!this.parse.lookAhead(KeyNames.keyTO, false))
			throw (new BasicError(ErrorNumbers.errorNoTo));

		a = this.parse.expr();
		stop = (int) a.getLong();

		if (this.parse.lookAhead(KeyNames.keySTEP, false)) {
			a = this.parse.expr();
			step = (int) a.getLong();
		} else
			step = 1;

		if (step == 0)
			throw (new BasicError(ErrorNumbers.errorIllegalUse));

		if (step < 0) {
			if (start < stop) {
				this.parse.moveToNext();
				return;
			}
		} else {
			if (start > stop) {
				this.parse.moveToNext();
				return;
			}
		}

		varObj = this.parse.getVariable(var);

		if (varObj == null) {
			varObj = new BasicVariable(0);
			this.parse.addVariable(var, varObj);
		}

		varObj.edit(start);

		parse.getStack().push(
				new StackEntry(StackEntryType.stackFor, this.parse
						.getCurrentLine(), varObj, start, stop, step));
	}

	public void cmdFunction() {
		// Just ignore this first line of a function
	}

	public void cmdGet() {
	}

	public void cmdGosub() {
	}

	public void cmdGoto() {
		String str;

		this.parse.killSpace();
		str = this.parse.parseVariable();
		this.parse.go(str);
	}

	public void cmdIf() {
		BasicVariable a = new BasicVariable();
		boolean b;

		b = this.parse.parseVariable(a);
		if (!this.parse.lookAhead(KeyNames.keyTHEN, false))
			throw (new BasicError(ErrorNumbers.errorNoThen));

		if (b) {
			this.parse.killSpace();
			if (this.parse.getNextChar() == 0) {
				this.parse.increaseIFS();
			} else
				throw new BasicError(ErrorNumbers.errorBlock);
		} else
			parse.moveToEndIf(true);
	}

	public void cmdInput() {
	}

	public void cmdKill() {
		BasicVariable var = this.parse.expr();
		File file = new File(var.getStr());
		if (!file.delete())
			throw (new BasicError(ErrorNumbers.errorDisk));
	}

	public void cmdLet() {
		this.parse.doAssignment();
	}

	public void cmdLoad() {

	}

	public void cmdLock() {
	}

	public void cmdLoop() {
		StackEntryType type;
		BasicLine bl;

		this.parse.killSpace();
		if (this.parse.getStack().empty())
			throw (new BasicError(ErrorNumbers.errorLongName));

		if (this.parse.getStack().peekType() != StackEntryType.stackDo)
			throw (new BasicError(ErrorNumbers.errorLongName));

		if (!this.parse.getStack().empty()) {
			type = this.parse.getStack().peekType();
			bl = this.parse.getStack().peek().getLine();
		} else {
			type = null;
			bl = null;
		}

		if (this.parse.getNextChar() != 0) {
			if (type == null)
				throw (new BasicError(ErrorNumbers.errorIllegalUse));
			if (this.parse.evaluateDo()) {
				if (bl != null) {
					this.parse.setCurrentLine(bl);
					this.parse.parse();
				}
			} else
				this.parse.getStack().pop();
		} else {
			this.parse.setCurrentLine(bl);
			this.parse.parse();
		}
	}

	public void cmdMsgBox() {
		BasicVariable a, b = null, c;
		int num = 1;

		this.parse.expectToken('(');
		a = this.parse.expr();

		if (this.parse.lookAhead(',')) {
			num = 2;
			b = this.parse.expr();
			if (this.parse.lookAhead(',')) {
				num = 3;
				c = this.parse.expr();
			}
		}
		this.parse.expectToken(')');
		switch (num) {
		case 1:
			JOptionPane.showMessageDialog(null, a.getStr(), "Encog",
					JOptionPane.PLAIN_MESSAGE);
			break;
		case 2:
			if (b == null)
				throw new BasicError(ErrorNumbers.errorIllegalUse);
			JOptionPane.showMessageDialog(null, a.getStr(), b.getStr(),
					JOptionPane.PLAIN_MESSAGE);
			break;
		case 3:
			JOptionPane.showConfirmDialog(null, a.getStr(), b.getStr(),
					JOptionPane.YES_NO_OPTION);
			break;
		}

	}

	public void cmdMKDir() {
		BasicVariable var = this.parse.expr();
		File file = new File(var.getStr());
		file.mkdir();
	}

	public void cmdName() {
		BasicVariable oldName = this.parse.expr();

		if (this.parse.lookAhead(KeyNames.keyAS, false))
			throw (new BasicError(ErrorNumbers.errorIllegalUse));

		BasicVariable newName = this.parse.expr();

		File oldFile = new File(oldName.getStr());
		File newFile = new File(newName.getStr());

		if (!oldFile.renameTo(newFile))
			throw new BasicError(ErrorNumbers.errorDisk);
	}

	public void cmdNext() {
		double d;

		this.parse.killSpace();
		if (this.parse.getStack().empty())
			throw (new BasicError(ErrorNumbers.errorNoFor));

		if (this.parse.getStack().peekType() != StackEntryType.stackFor)
			throw (new BasicError(ErrorNumbers.errorNoFor));

		StackEntry entry = this.parse.getStack().peek();

		int stop = entry.getStop();
		int step = entry.getStep();
		BasicVariable varObj = entry.getVariable();

		if (varObj == null)
			throw (new BasicError(ErrorNumbers.errorUndefinedVariable));
		d = varObj.getDouble();
		d += step;
		varObj.edit(d);

		boolean done = false;

		if (step > 0) {
			if (d > stop)
				done = true;
		} else {
			if (d < stop)
				done = true;
		}

		if (!done) {
			this.parse.setCurrentLine(entry.getLine());
		} else
			this.parse.getStack().pop();
	}

	public void cmdOn() {

		if (this.parse.lookAhead(KeyNames.keyERROR, false)) {
			cmdOnError();
		} else
			throw new BasicError(ErrorNumbers.errorIllegalUse);
	}

	public void cmdOnError() {
		String str;

		if (this.parse.lookAhead(KeyNames.keyGOTO, false)) {
			str = this.parse.parseVariable();

			if (str.equals("0"))
				this.parse.setErrorLabel(null);
			else
				this.parse.setErrorLabel(str);

			return;
		} else if (this.parse.lookAhead(KeyNames.keyRESUME, false))
			if (this.parse.lookAhead(KeyNames.keyNEXT, false)) {
				this.parse.setErrorLabel("*");
				return;
			}
		throw (new BasicError(ErrorNumbers.errorIllegalUse));
	}

	public void cmdOpen() {

	}

	public void cmdOption() {
	}

	public void cmdPrint() {

		boolean no_cr = false;

		this.parse.setColumn(0);

		this.parse.killSpace();

		if (!(this.parse.getNextChar() == 0 || (this.parse.getNextChar() == ':'))) {
			this.parse.getModule().getProgram().print(
					this.parse.formatExpression());

			while (this.parse.getNextChar() == ';') {
				this.parse.advance();

				if ((this.parse.getNextChar() == 0)
						|| (this.parse.getNextChar() == ':')) {
					no_cr = true;
					break;
				}

				this.parse.getModule().getProgram().print(
						this.parse.formatExpression());

				this.parse.killSpace();
			}
		}

		if (!no_cr) {
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

		BasicVariable v;
		String var;

		for (;;) {
			this.parse.killSpace();
			var = this.parse.parseVariable();
			if (BasicUtil.findKeyword(var) != null)
				throw (new BasicError(ErrorNumbers.errorKeyword));

			if (this.parse.getVariable(var) != null)
				throw (new BasicError(ErrorNumbers.errorAlreadyDefined));

			v = this.parse.createVariable(var);
			this.parse.addVariable(var, v);

			this.parse.killSpace();
			if (!this.parse.lookAhead(','))
				break;
		}

	}

	public void cmdRem() {
		while (this.parse.getNextChar() != 0)
			this.parse.advance();
	}

	public void cmdReset() {
		throw new BasicError(ErrorNumbers.errorNotYet);
	}

	public void cmdResume() {
		throw new BasicError(ErrorNumbers.errorNotYet);
	}

	public void cmdReturn() {
	}

	public void cmdRmDir() {
		BasicVariable var = this.parse.expr();
		File file = new File(var.getStr());
		if (!file.delete())
			throw new BasicError(ErrorNumbers.errorDisk);
	}

	public boolean cmdRun(String str) {
		return false;
	}

	public void cmdSeek() {
	}

	public void cmdSelect() {

		BasicVariable var;
		BasicVariable is;

		if (!this.parse.lookAhead(KeyNames.keyCASE, false))
			throw (new BasicError(ErrorNumbers.errorIllegalUse));

		var = this.parse.expr();
		is = var;

		while (parse.getCurrentLine() != null) {
			do {
				if (this.parse.lookAhead(KeyNames.keySELECT, false))
					this.parse.moveToEndCase();

				if (this.parse.lookAhead(KeyNames.keyEND, false)) {
					this.parse.killSpace();
					if (this.parse.lookAhead(KeyNames.keyCASE, false))
						return;
					if (this.parse.lookAhead(KeyNames.keySUB, false)
							|| this.parse
									.lookAhead(KeyNames.keyFUNCTION, false))
						throw (new BasicError(ErrorNumbers.errorBlock));
				}

				if (this.parse.lookAhead(KeyNames.keyCASE, false)) {
					if (this.parse.lookAhead(KeyNames.keyELSE, false)) {
						this.parse.movePastColen();
						is = null;
						return;
					}

					do {
						BasicVariable b, c;

						this.parse.killSpace();
						if (this.parse.lookAhead("IS", false)) {
							b = this.parse.expr();
							if (b.getBoolean()) {
								this.parse.movePastColen();
								is = null;
								return;
							}
						} else {
							b = this.parse.expr();

							if (this.parse.lookAhead(KeyNames.keyTO, false)) {
								c = this.parse.expr();
								if ((var.compareGTE(b)) && (var.compareLTE(c))) {
									this.parse.movePastColen();
									is = null;
									return;
								}
							}

							if (var.compareE(b)) {
								this.parse.movePastColen();
								is = null;
								return;
							}
						}
					} while (this.parse.lookAhead(','));
				}

				this.parse.movePastColen();

			} while (this.parse.getNextChar() != 0);

			this.parse.moveNextLine();
		}

		is = null;

	}

	public void cmdShared() {
		throw new BasicError(ErrorNumbers.errorNotYet);
	}

	public void cmdSleep() {
		BasicVariable var = this.parse.expr();
		try {
			Thread.sleep((int) var.getLong());
		} catch (InterruptedException e) {
		}
	}

	public void cmdStatic() {
		throw new BasicError(ErrorNumbers.errorNotYet);
	}

	public void cmdStop() {
	}

	public void cmdSub() {
	}

	public void cmdType() {
		throw new BasicError(ErrorNumbers.errorNotYet);
	}

	public void cmdUnLock() {
		throw new BasicError(ErrorNumbers.errorNotYet);
	}

	public void cmdWEnd() {
		BasicVariable varObj;

		this.parse.killSpace();
		if (this.parse.getStack().empty())
			throw (new BasicError(ErrorNumbers.errorWend));

		if (this.parse.getStack().peekType() != StackEntryType.stackWhile)
			throw (new BasicError(ErrorNumbers.errorWend));

		StackEntry entry = this.parse.getStack().pop();
		this.parse.setCurrentLine(entry.getLine());

		this.parse.parse(this.parse.getCurrentLine().getText());
	}

	public void cmdWhile() {
		BasicVariable varObj;

		this.parse.killSpace();

		varObj = this.parse.expr();

		if (varObj.getBoolean())
			this.parse.getStack().push(
					new StackEntry(StackEntryType.stackWhile, this.parse
							.getCurrentLine()));
		else
			this.parse.moveToWEnd();
	}

	public void cmdWidth() {
	}

	public void cmdWrite() {
		throw new BasicError(ErrorNumbers.errorNotYet);
	}

	public void cmdSetRegistry() {
	}

}
