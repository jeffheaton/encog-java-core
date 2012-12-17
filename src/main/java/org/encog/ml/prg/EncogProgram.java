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

import java.io.UnsupportedEncodingException;

import org.encog.Encog;
import org.encog.EncogError;
import org.encog.ml.MLError;
import org.encog.ml.MLRegression;
import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.basic.BasicMLData;
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

public class EncogProgram implements MLRegression, MLError {
	public static final int DEFAULT_PROGRAM_SIZE = 1024;

	private EPLHolder holder;
	private final OpCodeHeader header = new OpCodeHeader();
	private int individual;
	private EncogProgramVariables variables = new EncogProgramVariables();
	private EncogProgramContext context = new EncogProgramContext();
	private double score;
	private double effectiveScore;
	private int programLength;
	private int programCounter;
	private ExpressionStack stack;
	private String source;

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

	public EncogProgram() {
		this(new EncogProgramContext(), new EncogProgramVariables(), null, 0);
		StandardExtensions.createAll(this.context.getFunctions());
	}

	public EncogProgram(EncogProgramContext theContext) {
		this(theContext, new EncogProgramVariables(), null, 0);
	}

	public EncogProgram(EncogProgramContext theContext,
			EncogProgramVariables theVariables, EPLHolder theHolder,
			int theIndividual) {
		this.stack = new ExpressionStack(this.context.getParams().getStackSize());
		this.context = theContext;
		this.variables = theVariables;

		if (theHolder == null) {
			this.holder = this.context.getHolderFactory().factor(1, this.context.getParams().getMaxIndividualSize());
			this.individual = 0;
		} else {
			this.holder = theHolder;
			this.individual = theIndividual;
		}

		// define variables
		for (String v : this.context.getDefinedVariables()) {
			this.variables.defineVariable(v);
		}
	}

	public EncogProgram(final String expression) {
		this();
		compileExpression(expression);
	}

	public EncogProgram(EncogProgram prg) {
		this(prg.getContext());
		this.programCounter = prg.programCounter;
		this.holder = prg.holder;
		this.individual = prg.individual;
		setProgramLength( prg.programLength );
	}

	public void compileExpression(final String expression) {
		clear();
		final ParseCommonExpression parser = new ParseCommonExpression(this);
		parser.parse(expression);
		this.source = expression;
	}

	public ExpressionValue evaluate() {
		return evaluate(0, this.programLength-1);
	}

	public FunctionFactory getFunctions() {
		return this.context.getFunctions();
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

	public EncogProgramVariables getVariables() {
		return variables;
	}

	public EncogProgramContext getContext() {
		return context;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public MLData compute(MLData input) {
		if (input.size() != getInputCount()) {
			throw new ExpressionError("Invalid input count.");
		}

		for (int i = 0; i < input.size(); i++) {
			this.variables.getVariable(i).setValue(input.getData(i));
		}

		double d = evaluate().toFloatValue();

		MLData result = new BasicMLData(1);
		result.setData(0, d);

		return result;
	}

	public double getScore() {
		return score;
	}

	public void setScore(double score) {
		this.score = score;
	}

	public String toString() {
		StringBuilder result = new StringBuilder();
		result.append("[EncogProgram: size=");
		result.append(size());
		result.append(", score=");
		result.append(this.score);
		result.append(", Code: ");

		try {
			RenderRPN rpn = new RenderRPN();
			result.append(rpn.render(this));
		} catch(EncogProgramError e) {
			result.append("##Invalid Program:");
			result.append(e.toString());
		}

		result.append("]");
		return result.toString();
	}

	public int size() {
		try {
			TraverseProgram trav = new TraverseProgram(this);
			trav.begin(0);
			return trav.countRemaining();
		} catch (EncogProgramError e) {
			return -1;
		}
	}

	public ProgramExtensionTemplate peekTemplate() {
		this.holder.readNodeHeader(this.individual, this.programCounter,
				this.header);
		int opcode = this.header.getOpcode();
		ProgramExtensionTemplate temp = this.context.getFunctions().getOpCode(
				opcode);
		return temp;
	}

	@Override
	public double calculateError(MLDataSet data) {
		return EncogUtility.calculateRegressionError(this, data);
	}

	/**
	 * @return the effectiveScore
	 */
	public double getEffectiveScore() {
		return effectiveScore;
	}

	/**
	 * @param effectiveScore
	 *            the effectiveScore to set
	 */
	public void setEffectiveScore(double effectiveScore) {
		this.effectiveScore = effectiveScore;
	}

	/**
	 * @return the programLength
	 */
	public int getProgramLength() {
		return programLength;
	}

	/**
	 * @param programLength
	 *            the programLength to set
	 */
	public void setProgramLength(int theProgramLength) {
		this.programLength = theProgramLength;
		this.validateLength();
	}

	/**
	 * @return the programCounter
	 */
	public int getProgramCounter() {
		return programCounter;
	}

	/**
	 * @param programCounter
	 *            the programCounter to set
	 */
	public void setProgramCounter(int programCounter) {
		this.programCounter = programCounter;
	}

	public void writeNode(short opcode, int param1, short param2) {
		validateAdvance(1);
		this.holder.writeNode(this.individual, this.programCounter, opcode,
				param1, param2);
		advanceProgramCounter(1, true);
	}

	public void writeDouble(double value) {
		validateAdvance(1);
		this.holder.writeDouble(this.individual, this.programCounter, value);
		advanceProgramCounter(1, true);
	}

	public void writeNode(short opcode) {
		writeNode(opcode, 0, (short) 0);
	}

	public void writeConstNode(boolean value) {
		writeNode(StandardExtensions.OPCODE_CONST_BOOL, 0, (short) (value ? 1
				: 0));
	}

	public void writeConstNode(double value) {
		writeNode(StandardExtensions.OPCODE_CONST_FLOAT, 0, (short) 0);
		writeDouble(value);
	}

	public void writeConstNode(long value) {
		writeNode(StandardExtensions.OPCODE_CONST_INT, (int) value, (short) 0);
	}

	public void writeNodeVar(String name) {
		writeNode(StandardExtensions.OPCODE_VAR, 0,
				(short) this.variables.getVariableIndex(name));
	}

	public void readNodeHeader() {
		this.holder
				.readNodeHeader(this.individual, this.programCounter, header);
		advanceProgramCounter(1, false);
	}

	public double readDouble() {
		double result = this.holder.readDouble(this.individual,
				this.programCounter);
		advanceProgramCounter(1, false);
		return result;
	}

	/**
	 * @return the holder
	 */
	public EPLHolder getHolder() {
		return holder;
	}

	/**
	 * @param holder
	 *            the holder to set
	 */
	public void setHolder(EPLHolder holder) {
		this.holder = holder;
	}

	/**
	 * @return the individual
	 */
	public int getIndividual() {
		return individual;
	}

	/**
	 * @param individual
	 *            the individual to set
	 */
	public void setIndividual(int individual) {
		this.individual = individual;
	}

	/**
	 * @return the header
	 */
	public OpCodeHeader getHeader() {
		return header;
	}

	public boolean eof() {
		return (this.programCounter >= this.programLength);
	}

	/**
	 * @return the stack
	 */
	public ExpressionStack getStack() {
		return stack;
	}

	public String readString(int encodedLength) {
		String result = this.holder.readString(this.individual,
				this.programCounter, encodedLength);
		this.programCounter += EPLUtil.roundToFrame(encodedLength)
				/ EPLHolder.FRAME_SIZE;
		return result;
	}

	public void advanceProgramCounter(int i, boolean adjustLength) {
		this.programCounter += i;
		if (adjustLength) {
			setProgramLength(Math.max(this.programLength,
					this.programCounter));
		}
	}

	public void writeNodeString(String str) {
		try {
			byte[] b = str.getBytes(Encog.DEFAULT_ENCODING);
			writeNode(StandardExtensions.OPCODE_CONST_STRING, 0,
					(short) b.length);
			this.holder.writeByte(this.individual, this.programCounter, b);
			advanceProgramCounter(EPLUtil.roundToFrame(b.length)
					/ EPLHolder.FRAME_SIZE, true);
		} catch (UnsupportedEncodingException e) {
			throw new EncogError(e);
		}
	}

	public boolean isLeaf(int index) {
		OpCodeHeader h = new OpCodeHeader();
		this.holder.readNodeHeader(this.individual, index, h);
		ProgramExtensionTemplate temp = this.context.getFunctions().getOpCode(
				h.getOpcode());
		return temp.getChildNodeCount() == 0;
	}

	public ExpressionValue evaluate(int startAt, int stopAt) {
		try {
			this.stack.clear();
			this.programCounter = startAt;
			while (this.programCounter<=stopAt) {
				readNodeHeader();
				int opcode = this.header.getOpcode();
				ProgramExtensionTemplate temp = this.context.getFunctions()
						.getOpCode(opcode);
				temp.evaluate(this);
			}
			return stack.pop();
		} catch (ArithmeticException ex) {
			return new ExpressionValue(Double.NaN);
		}
	}

	public void deleteSubtree(int index, int size) {
		this.holder.deleteSubtree(this.individual, index, size);
		setProgramLength(this.programLength-size);
	}
	
	public ProgramExtensionTemplate getConstTemplate(ExpressionValue c) {
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

	public void writeConstNode(ExpressionValue c) {
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
			this.writeNodeString(c.toStringValue());
			break;
		}
	}

	public void insert(int index, int len) {
		setProgramLength(this.programLength + len);
		this.holder.insert(this.individual, index, len);
	}

	public int findNodeStart(int index) {
		StackInt stack = new StackInt(100);

		TraverseProgram trav = new TraverseProgram(this);
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

	public void replaceNode(EncogProgram sourceProgram, int sourceIndex,
			int targetIndex) {
		int sourceStart = sourceProgram.findNodeStart(sourceIndex);
		int targetStart = findNodeStart(targetIndex);
		int sourceSize = (sourceProgram.nextIndex(sourceIndex) - sourceStart);
		int targetSize = (nextIndex(targetIndex) - targetStart);
		deleteSubtree(targetStart,targetSize);
		this.insert(targetStart, sourceSize);
		this.holder.copy(sourceProgram.getHolder(), sourceProgram.getIndividual(), sourceStart, this.individual, targetStart, sourceSize);
	}

	public int nextIndex(int index) {
		this.holder.readNodeHeader(this.individual, index, header);
		ProgramExtensionTemplate temp = this.context.getFunctions().getOpCode(header.getOpcode());
		return index+temp.getInstructionSize(header);
	}

	/**
	 * @return the source
	 */
	public String getSource() {
		return source;
	}

	public String dumpAsCommonExpression() {
		RenderCommonExpression render = new RenderCommonExpression();
		return render.render(this);
	}

	public String toBase64() {
		return this.holder.toBase64(individual, this.programLength);
	}

	public void fromBase64(String str) {
		clear();
		setProgramLength( this.holder.fromBase64(individual, str));
	}

	public void clear() {
		setProgramLength(0);
		this.programCounter = 0;
	}

	public void copy(EncogProgram source) {
		clear();
		setProgramLength( source.programLength);
		copy(source,0,0,source.getProgramLength());
		this.score = source.score;
		this.effectiveScore = source.effectiveScore;
	}

	public void copy(EncogProgram sourceProgram, int sourceIndex,
			int targetIndex, int size) {
		this.holder.copy(sourceProgram.getHolder(), sourceProgram.getIndividual(), sourceIndex,
				getIndividual(), targetIndex, size);
	}

	public EncogProgram[] allocateOffspring(int count) {
		EncogProgram[] result = new EncogProgram[count];
		for (int i = 0; i < result.length; i++) {
			EPLHolder newHolder = this.context.getHolderFactory().factor(1,
					this.getHolder().getMaxIndividualSize());
			result[i] = new EncogProgram(this.context,
					new EncogProgramVariables(), newHolder, 0);
		}
		return result;
	}

	public int findFrame(int position) {
		TraverseProgram trav = new TraverseProgram(this);
		trav.begin(0);
		int i = 0;
		while(trav.next()) {
			if( i==position )
				return trav.getFrameIndex();
			i++;
		}
		return -1;
	}

	public void replaceNodeAtPosition(EncogProgram sourceProgram, int sourcePosition,
			int targetPosition) {
		int sourceIndex = sourceProgram.findFrame(sourcePosition);
		int targetIndex = findFrame(targetPosition);
		replaceNode(sourceProgram,sourceIndex,targetIndex);
	}
	
	private void validateAdvance(int c) {
		if( (this.programLength+c)>this.holder.getMaxIndividualFrames() ) {
			throw new EPLTooBig("Program has overrun its maximum length.");
		}
	}
	
	public void validate() {
		if( size()<0 ) {
			throw new EncogProgramError("Program code invalid");
		}
		
		if( size()==0 ) {
			throw new EncogProgramError("Zero length program is invalid.");
		}
		
		try {
		MLData input = new BasicMLData(this.getInputCount());
		this.compute(input);
		} catch(Throwable t) {
			throw new EncogProgramError("Can't evaluate EncogProgram.",t);
		}
	}
	
	public void validateLength() {
		if( this.programLength<0 ) {
			throw new EncogEPLError("Program length cannot go below zero.");
		}
		if( this.programLength>this.holder.getMaxIndividualFrames() ) {
			throw new EPLTooBig("Program has overrun its maximum length.");
		}
	}
}
