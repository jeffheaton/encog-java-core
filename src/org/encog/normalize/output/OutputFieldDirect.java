package org.encog.normalize.output;

import org.encog.normalize.input.InputField;

public class OutputFieldDirect implements OutputField {

	private final InputField sourceField;

	public OutputFieldDirect(final InputField sourceField) {
		this.sourceField = sourceField;
	}

	public double calculate() {
		return this.sourceField.getCurrentValue();
	}

}
