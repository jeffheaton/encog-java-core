/*
 * Encog(tm) Core v3.3 - Java Version
 * http://www.heatonresearch.com/encog/
 * https://github.com/encog/encog-java-core
 
 * Copyright 2008-2014 Heaton Research, Inc.
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
package org.encog.app.analyst.csv.process;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.encog.app.analyst.AnalystError;
import org.encog.app.analyst.csv.basic.LoadedRow;
import org.encog.ml.prg.ProgramNode;
import org.encog.ml.prg.expvalue.ExpressionValue;
import org.encog.ml.prg.extension.BasicTemplate;
import org.encog.ml.prg.extension.EncogOpcodeRegistry;
import org.encog.ml.prg.extension.FunctionFactory;
import org.encog.ml.prg.extension.NodeType;
import org.encog.ml.prg.extension.ProgramExtensionTemplate;
import org.encog.util.csv.CSVFormat;
import org.encog.util.csv.ReadCSV;

public class ProcessExtension {

	public final static String EXTENSION_DATA_NAME = "ENCOG-ANALYST-PROCESS";
	private Map<String, Integer> map = new HashMap<String, Integer>();
	private int forwardWindowSize;
	private int backwardWindowSize;
	private int totalWindowSize;
	private List<LoadedRow> data = new ArrayList<LoadedRow>();
	private final CSVFormat format;

	// add field
	public static final ProgramExtensionTemplate OPCODE_FIELD = new BasicTemplate(
			ProgramExtensionTemplate.NO_PREC, "field({s}{i}):{s}",
			NodeType.Function, true, 0) {
		/**
		 * The serial id.
		 */
		private static final long serialVersionUID = 1L;

		@Override
		public ExpressionValue evaluate(ProgramNode actual) {
			ProcessExtension pe = (ProcessExtension) actual.getOwner()
					.getExtraData(EXTENSION_DATA_NAME);
			String fieldName = actual.getChildNode(0).evaluate()
					.toStringValue();
			int fieldIndex = (int) actual.getChildNode(1).evaluate()
					.toFloatValue()
					+ pe.getBackwardWindowSize();
			String value = pe.getField(fieldName, fieldIndex);
			return new ExpressionValue(value);
		}
	};

	// add fieldmax
	public static final ProgramExtensionTemplate OPCODE_FIELDMAX = new BasicTemplate(
			ProgramExtensionTemplate.NO_PREC, "fieldmax({s}{i}{i}):{f}",
			NodeType.Function, true, 0) {
		/**
		 * The serial id.
		 */
		private static final long serialVersionUID = 1L;
		@Override
		public ExpressionValue evaluate(ProgramNode actual) {
			ProcessExtension pe = (ProcessExtension) actual.getOwner()
					.getExtraData(EXTENSION_DATA_NAME);
			String fieldName = actual.getChildNode(0).evaluate()
					.toStringValue();
			int startIndex = (int) actual.getChildNode(1).evaluate()
					.toIntValue();
			int stopIndex = (int) actual.getChildNode(2).evaluate()
					.toIntValue();
			double value = Double.NEGATIVE_INFINITY;

			for (int i = startIndex; i <= stopIndex; i++) {
				String str = pe.getField(fieldName, pe.getBackwardWindowSize()
						+ i);
				double d = pe.getFormat().parse(str);
				value = Math.max(d, value);
			}

			return new ExpressionValue(value);
		}
	};

	// add fieldmaxpip
	public static final ProgramExtensionTemplate OPCODE_FIELDMAXPIP = new BasicTemplate(
			ProgramExtensionTemplate.NO_PREC, "fieldmaxpip({s}{i}{i}):{f}",
			NodeType.Function, true, 0) {
		/**
		 * The serial id.
		 */
		private static final long serialVersionUID = 1L;
		@Override
		public ExpressionValue evaluate(ProgramNode actual) {
			ProcessExtension pe = (ProcessExtension) actual.getOwner()
					.getExtraData(EXTENSION_DATA_NAME);
			String fieldName = actual.getChildNode(0).evaluate()
					.toStringValue();
			int startIndex = (int) actual.getChildNode(1).evaluate()
					.toIntValue();
			int stopIndex = (int) actual.getChildNode(2).evaluate()
					.toIntValue();
			int value = Integer.MIN_VALUE;

			String str = pe.getField(fieldName, pe.getBackwardWindowSize());
			double quoteNow = pe.getFormat().parse(str);

			for (int i = startIndex; i <= stopIndex; i++) {
				str = pe.getField(fieldName, pe.getBackwardWindowSize() + i);
				double d = pe.getFormat().parse(str) - quoteNow;
				d /= 0.0001;
				d = Math.round(d);
				value = Math.max((int) d, value);
			}

			return new ExpressionValue(value);
		}
	};
	
	/**
	 * Add opcodes to the Encog resource registry.
	 */
	static {
		EncogOpcodeRegistry.INSTANCE.add(OPCODE_FIELD);
		EncogOpcodeRegistry.INSTANCE.add(OPCODE_FIELDMAX);
		EncogOpcodeRegistry.INSTANCE.add(OPCODE_FIELDMAXPIP);
	}

	public ProcessExtension(CSVFormat theFormat) {
		this.format = theFormat;
	}

	public String getField(String fieldName, int fieldIndex) {
		if (!map.containsKey(fieldName)) {
			throw new AnalystError("Unknown input field: " + fieldName);
		}

		int idx = map.get(fieldName);

		if (fieldIndex >= this.data.size() || fieldIndex < 0) {
			throw new AnalystError(
					"The specified temporal index "
							+ fieldIndex
							+ " is out of bounds.  You should probably increase the forward window size.");
		}

		return this.data.get(fieldIndex).getData()[idx];
	}

	public void loadRow(LoadedRow row) {
		data.add(0, row);
		if (data.size() > this.totalWindowSize) {
			data.remove(data.size() - 1);
		}
	}

	public void init(ReadCSV csv, int theBackwardWindowSize,
			int theForwardWindowSize) {

		this.forwardWindowSize = theForwardWindowSize;
		this.backwardWindowSize = theBackwardWindowSize;
		this.totalWindowSize = this.forwardWindowSize + this.backwardWindowSize
				+ 1;

		int i = 0;
		for (String name : csv.getColumnNames()) {
			map.put(name, i++);
		}
	}

	public boolean isDataReady() {
		return this.data.size() >= this.totalWindowSize;
	}

	public int getForwardWindowSize() {
		return forwardWindowSize;
	}

	public int getBackwardWindowSize() {
		return backwardWindowSize;
	}

	public int getTotalWindowSize() {
		return totalWindowSize;
	}

	public CSVFormat getFormat() {
		return format;
	}

	public void register(FunctionFactory functions) {
		functions.addExtension(OPCODE_FIELD);
		functions.addExtension(OPCODE_FIELDMAX);
		functions.addExtension(OPCODE_FIELDMAXPIP);
	}

}
