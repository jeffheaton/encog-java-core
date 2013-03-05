/*
 * Encog(tm) Core v3.2 - Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 
 * Copyright 2008-2012 Heaton Research, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *   
 * For more information on Heaton Research copyrights, licenses 
 * and trademarks visit:
 * http://www.heatonresearch.com/copyright
 */
package org.encog.ml.prg;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;

import org.encog.Encog;
import org.encog.EncogError;
import org.encog.ml.MLError;
import org.encog.ml.MLRegression;
import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.basic.BasicMLData;
import org.encog.ml.ea.genome.BasicGenome;
import org.encog.ml.ea.genome.Genome;
import org.encog.ml.prg.epl.EPLHolder;
import org.encog.ml.prg.epl.EPLUtil;
import org.encog.ml.prg.epl.OpCodeHeader;
import org.encog.ml.prg.exception.EPLTooBig;
import org.encog.ml.prg.exception.EncogEPLError;
import org.encog.ml.prg.exception.EncogProgramError;
import org.encog.ml.prg.expvalue.ExpressionStack;
import org.encog.ml.prg.expvalue.ExpressionValue;
import org.encog.ml.prg.extension.FunctionFactory;
import org.encog.ml.prg.extension.ProgramExtensionTemplate;
import org.encog.ml.prg.extension.StandardExtensions;
import org.encog.ml.prg.util.TraverseProgram;
import org.encog.parse.expression.common.ParseCommonExpression;
import org.encog.parse.expression.common.RenderCommonExpression;
import org.encog.parse.expression.rpn.RenderRPN;
import org.encog.util.datastruct.StackInt;
import org.encog.util.simple.EncogUtility;

public class EncogProgram extends BasicGenome implements MLRegression, MLError,
		Serializable {
	public static final int DEFAULT_PROGRAM_SIZE = 1024;

	public static ExpressionValue parse(final String str) {
		final EncogProgram holder = new EncogProgram(str);
		return holder.evaluate();
	}

	public static boolean parseBoolean(final String str) {
		final EncogProgram holder = new EncogProgram(str);
		return holder.evaluate().toBooleanValue();
	}

	public static ExpressionValue parseExpression(final String str) {
		final EncogProgram holder = new EncogProgram(str);
		return holder.evaluate();
	}

	public static double parseFloat(final String str) {
		final EncogProgram holder = new EncogProgram(str);
		return holder.evaluate().toFloatValue();
	}

	public static String parseString(final String str) {
		final EncogProgram holder = new EncogProgram(str);
		return holder.evaluate().toStringValue();
	}

	private EPLHolder holder;
	private final OpCodeHeader header = new OpCodeHeader();
	private int individual;
	private EncogProgramVariables variables = new EncogProgramVariables();

	private EncogProgramContext context = new EncogProgramContext();

	private int programLength;

	private int programCounter;

	private final ExpressionStack stack;

	private String source;

	public EncogProgram() {
		this(new EncogProgramContext(), new EncogProgramVariables(), null, 0);
		StandardExtensions.createAll(this.context.getFunctions());
	}

	public EncogProgram(final EncogProgram prg) {
		this(prg.getContext());
		this.programCounter = prg.programCounter;
		this.holder = prg.holder;
		this.individual = prg.individual;
		setProgramLength(prg.programLength);
	}

	public EncogProgram(final EncogProgramContext theContext) {
		this(theContext, new EncogProgramVariables(), null, 0);
	}

	public EncogProgram(final EncogProgramContext theContext,
			final EncogProgramVariables theVariables,
			final EPLHolder theHolder, final int theIndividual) {
		this.stack = new ExpressionStack(this.context.getParams()
				.getStackSize());
		this.context = theContext;
		this.variables = theVariables;

		if (theHolder == null) {
			this.holder = this.context.getHolderFactory().factor(1,
					this.context.getParams().getMaxIndividualSize());
			this.individual = 0;
		} else {
			this.holder = theHolder;
			this.individual = theIndividual;
		}

		// define variables
		for (final String v : this.context.getDefinedVariables()) {
			this.variables.defineVariable(v);
		}
	}

	public EncogProgram(final String expression) {
		this();
		compileExpression(expression);
	}

	public void advanceProgramCounter(final int i, final boolean adjustLength) {
		this.programCounter += i;
		if (adjustLength) {
			setProgramLength(Math.max(this.programLength, this.programCounter));
		}
	}

	public EncogProgram[] allocateOffspring(final int count) {
		final EncogProgram[] result = new EncogProgram[count];
		for (int i = 0; i < result.length; i++) {
			final EPLHolder newHolder = this.context.getHolderFactory().factor(
					1, getHolder().getMaxIndividualSize());
			result[i] = new EncogProgram(this.context,
					new EncogProgramVariables(), newHolder, 0);
		}
		return result;
	}

	@Override
	public double calculateError(final MLDataSet data) {
		return EncogUtility.calculateRegressionError(this, data);
	}

	public void clear() {
		setProgramLength(0);
		this.programCounter = 0;
	}

	public void compileExpression(final String expression) {
		clear();
		final ParseCommonExpression parser = new ParseCommonExpression(this);
		parser.parse(expression);
		this.source = expression;
		setScore(Double.NaN);
		setAdjustedScore(Double.NaN);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public MLData compute(final MLData input) {
		if (input.size() != getInputCount()) {
			throw new ExpressionError("Invalid input count.");
		}

		for (int i = 0; i < input.size(); i++) {
			this.variables.getVariable(i).setValue(input.getData(i));
		}

		final double d = evaluate().toFloatValue();

		final MLData result = new BasicMLData(1);
		result.setData(0, d);

		return result;
	}

	public void copy(final EncogProgram sourceProgram, final int sourceIndex,
			final int targetIndex, final int size) {
		this.holder.copy(sourceProgram.getHolder(),
				sourceProgram.getIndividual(), sourceIndex, getIndividual(),
				targetIndex, size);
	}

	@Override
	public void copy(final Genome source) {
		final EncogProgram sourceProgram = (EncogProgram) source;
		clear();
		setProgramLength(sourceProgram.programLength);
		copy(sourceProgram, 0, 0, sourceProgram.getProgramLength());
		setScore(sourceProgram.getScore());
		setAdjustedScore(sourceProgram.getAdjustedScore());
	}

	public void deleteSubtree(final int index, final int size) {
		this.holder.deleteSubtree(this.individual, index, size);
		setProgramLength(this.programLength - size);
	}

	public String dumpAsCommonExpression() {
		final RenderCommonExpression render = new RenderCommonExpression();
		return render.render(this);
	}

	public boolean eof() {
		return this.programCounter >= this.programLength;
	}

	public ExpressionValue evaluate() {
		return evaluate(0, this.programLength - 1);
	}

	public ExpressionValue evaluate(final int startAt, final int stopAt) {
		try {
			this.stack.clear();
			this.programCounter = startAt;
			while (this.programCounter <= stopAt) {
				readNodeHeader();
				final int opcode = this.header.getOpcode();
				final ProgramExtensionTemplate temp = this.context
						.getFunctions().getOpCode(opcode);
				temp.evaluate(this);
			}
			return this.stack.pop();
		} catch (final ArithmeticException ex) {
			return new ExpressionValue(Double.NaN);
		}
	}

	public int findFrame(final int position) {
		final TraverseProgram trav = new TraverseProgram(this);
		trav.begin(0);
		int i = 0;
		while (trav.next()) {
			if (i == position) {
				return trav.getFrameIndex();
			}
			i++;
		}
		return -1;
	}

	public int findNodeStart(final int index) {
		final StackInt stack = new StackInt(100);

		final TraverseProgram trav = new TraverseProgram(this);
		trav.begin(0);
		while (trav.next()) {
			if (trav.isLeaf()) {
				stack.push(trav.getFrameIndex());
			} else {
				stack.min(trav.getTemplate().getChildNodeCount());
			}

			if (trav.getFrameIndex() == index) {
				return stack.pop();
			}
		}
		return -1;

	}

	public void fromBase64(final String str) {
		clear();
		setProgramLength(this.holder.fromBase64(this.individual, str));
	}

	public ProgramExtensionTemplate getConstTemplate(final ExpressionValue c) {
		switch (c.getCurrentType()) {
		case booleanType:
			return this.context.getFunctions().getOpCode(
					StandardExtensions.OPCODE_CONST_BOOL);
		case floatingType:
			return this.context.getFunctions().getOpCode(
					StandardExtensions.OPCODE_CONST_FLOAT);
		case intType:
			return this.context.getFunctions().getOpCode(
					StandardExtensions.OPCODE_CONST_INT);
		case stringType:
			return this.context.getFunctions().getOpCode(
					StandardExtensions.OPCODE_CONST_STRING);
		default:
			return null;
		}
	}

	public EncogProgramContext getContext() {
		return this.context;
	}

	public FunctionFactory getFunctions() {
		return this.context.getFunctions();
	}

	/**
	 * @return the header
	 */
	public OpCodeHeader getHeader() {
		return this.header;
	}

	/**
	 * @return the holder
	 */
	public EPLHolder getHolder() {
		return this.holder;
	}

	/**
	 * @return the individual
	 */
	public int getIndividual() {
		return this.individual;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getInputCount() {
		return this.variables.size();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getOutputCount() {
		return 1;
	}

	/**
	 * @return the programCounter
	 */
	public int getProgramCounter() {
		return this.programCounter;
	}

	/**
	 * @return the programLength
	 */
	public int getProgramLength() {
		return this.programLength;
	}

	/**
	 * @return the source
	 */
	public String getSource() {
		return this.source;
	}

	/**
	 * @return the stack
	 */
	public ExpressionStack getStack() {
		return this.stack;
	}

	public EncogProgramVariables getVariables() {
		return this.variables;
	}

	public boolean hasVariable() {
		final TraverseProgram trav = new TraverseProgram(this);
		trav.begin(0);
		while (trav.next()) {
			if (trav.getTemplate().isVariableValue()) {
				return true;
			}
		}
		return false;
	}

	public void insert(final int index, final int len) {
		setProgramLength(this.programLength + len);
		this.holder.insert(this.individual, index, len);
	}

	public boolean isLeaf(final int index) {
		final OpCodeHeader h = new OpCodeHeader();
		this.holder.readNodeHeader(this.individual, index, h);
		final ProgramExtensionTemplate temp = this.context.getFunctions()
				.getOpCode(h.getOpcode());
		return temp.getChildNodeCount() == 0;
	}

	public int nextIndex(final int index) {
		this.holder.readNodeHeader(this.individual, index, this.header);
		final ProgramExtensionTemplate temp = this.context.getFunctions()
				.getOpCode(this.header.getOpcode());
		return index + temp.getInstructionSize(this.header);
	}

	public ProgramExtensionTemplate peekTemplate() {
		this.holder.readNodeHeader(this.individual, this.programCounter,
				this.header);
		final int opcode = this.header.getOpcode();
		final ProgramExtensionTemplate temp = this.context.getFunctions()
				.getOpCode(opcode);
		return temp;
	}

	public double readDouble() {
		final double result = this.holder.readDouble(this.individual,
				this.programCounter);
		advanceProgramCounter(1, false);
		return result;
	}

	public void readNodeHeader() {
		this.holder.readNodeHeader(this.individual, this.programCounter,
				this.header);
		advanceProgramCounter(1, false);
	}

	public String readString(final int encodedLength) {
		final String result = this.holder.readString(this.individual,
				this.programCounter, encodedLength);
		this.programCounter += EPLUtil.roundToFrame(encodedLength)
				/ EPLHolder.FRAME_SIZE;
		return result;
	}

	public void replaceNode(final EncogProgram sourceProgram,
			final int sourceIndex, final int targetIndex) {
		final int sourceStart = sourceProgram.findNodeStart(sourceIndex);
		final int targetStart = findNodeStart(targetIndex);
		final int sourceSize = sourceProgram.nextIndex(sourceIndex)
				- sourceStart;
		final int targetSize = nextIndex(targetIndex) - targetStart;
		deleteSubtree(targetStart, targetSize);
		insert(targetStart, sourceSize);
		this.holder.copy(sourceProgram.getHolder(),
				sourceProgram.getIndividual(), sourceStart, this.individual,
				targetStart, sourceSize);
	}

	public void replaceNodeAtPosition(final EncogProgram sourceProgram,
			final int sourcePosition, final int targetPosition) {
		final int sourceIndex = sourceProgram.findFrame(sourcePosition);
		final int targetIndex = findFrame(targetPosition);
		replaceNode(sourceProgram, sourceIndex, targetIndex);
	}

	/**
	 * @param holder
	 *            the holder to set
	 */
	public void setHolder(final EPLHolder holder) {
		this.holder = holder;
	}

	/**
	 * @param individual
	 *            the individual to set
	 */
	public void setIndividual(final int individual) {
		this.individual = individual;
	}

	/**
	 * @param programCounter
	 *            the programCounter to set
	 */
	public void setProgramCounter(final int programCounter) {
		this.programCounter = programCounter;
	}

	/**
	 * @param programLength
	 *            the programLength to set
	 */
	public void setProgramLength(final int theProgramLength) {
		this.programLength = theProgramLength;
		validateLength();
	}

	@Override
	public int size() {
		try {
			final TraverseProgram trav = new TraverseProgram(this);
			trav.begin(0);
			return trav.countRemaining();
		} catch (final EncogProgramError e) {
			return -1;
		}
	}

	public String toBase64() {
		return this.holder.toBase64(this.individual, this.programLength);
	}

	@Override
	public String toString() {
		final StringBuilder result = new StringBuilder();
		result.append("[EncogProgram: size=");
		result.append(size());
		result.append(", score=");
		result.append(getScore());
		result.append(", Code: ");

		try {
			final RenderRPN rpn = new RenderRPN();
			result.append(rpn.render(this));
		} catch (final EncogProgramError e) {
			result.append("##Invalid Program:");
			result.append(e.toString());
		}

		result.append("]");
		return result.toString();
	}

	public void validate() {
		if (size() < 0) {
			throw new EncogProgramError("Program code invalid");
		}

		if (size() == 0) {
			throw new EncogProgramError("Zero length program is invalid.");
		}

		try {
			final MLData input = new BasicMLData(getInputCount());
			compute(input);
		} catch (final Throwable t) {
			throw new EncogProgramError("Can't evaluate EncogProgram.", t);
		}
	}

	private void validateAdvance(final int c) {
		if (this.programLength + c > this.holder.getMaxIndividualFrames()) {
			throw new EPLTooBig("Program has overrun its maximum length.");
		}
	}

	public void validateLength() {
		if (this.programLength < 0) {
			throw new EncogEPLError("Program length cannot go below zero.");
		}
		if (this.programLength > this.holder.getMaxIndividualFrames()) {
			throw new EPLTooBig("Program has overrun its maximum length.");
		}
	}

	public void writeConstNode(final boolean value) {
		writeNode(StandardExtensions.OPCODE_CONST_BOOL, 0, (short) (value ? 1
				: 0));
	}

	public void writeConstNode(final double value) {
		writeNode(StandardExtensions.OPCODE_CONST_FLOAT, 0, (short) 0);
		writeDouble(value);
	}

	public void writeConstNode(final ExpressionValue c) {
		switch (c.getCurrentType()) {
		case booleanType:
			this.writeConstNode(c.toBooleanValue());
			break;
		case floatingType:
			this.writeConstNode(c.toFloatValue());
			break;
		case intType:
			this.writeConstNode(c.toIntValue());
			break;
		case stringType:
			writeNodeString(c.toStringValue());
			break;
		}
	}

	public void writeConstNode(final long value) {
		writeNode(StandardExtensions.OPCODE_CONST_INT, (int) value, (short) 0);
	}

	public void writeDouble(final double value) {
		validateAdvance(1);
		this.holder.writeDouble(this.individual, this.programCounter, value);
		advanceProgramCounter(1, true);
	}

	public void writeNode(final short opcode) {
		writeNode(opcode, 0, (short) 0);
	}

	public void writeNode(final short opcode, final int param1,
			final short param2) {
		validateAdvance(1);
		this.holder.writeNode(this.individual, this.programCounter, opcode,
				param1, param2);
		advanceProgramCounter(1, true);
	}

	public void writeNodeString(final String str) {
		try {
			final byte[] b = str.getBytes(Encog.DEFAULT_ENCODING);
			writeNode(StandardExtensions.OPCODE_CONST_STRING, 0,
					(short) b.length);
			this.holder.writeByte(this.individual, this.programCounter, b);
			advanceProgramCounter(EPLUtil.roundToFrame(b.length)
					/ EPLHolder.FRAME_SIZE, true);
		} catch (final UnsupportedEncodingException e) {
			throw new EncogError(e);
		}
	}

	public void writeNodeVar(final String name) {
		writeNode(StandardExtensions.OPCODE_VAR, 0,
				(short) this.variables.getVariableIndex(name));
	}
}
