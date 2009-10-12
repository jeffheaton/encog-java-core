package org.encog.normalize.output.mapped;

import java.util.ArrayList;
import java.util.List;

import org.encog.normalize.input.InputField;
import org.encog.normalize.output.OutputField;

public class OutputFieldEncode implements OutputField {

	private final InputField sourceField;
	private double catchAll;
	private final List<MappedRange> ranges = new ArrayList<MappedRange>();

	public OutputFieldEncode(final InputField sourceField) {
		this.sourceField = sourceField;
	}

	public void addRange(final double low, final double high, final double value) {
		final MappedRange range = new MappedRange(low, high, value);
		this.ranges.add(range);
	}

	public double calculate() {
		for (final MappedRange range : this.ranges) {
			if (range.inRange(this.sourceField.getCurrentValue())) {
				return range.getValue();
			}
		}

		return this.catchAll;
	}

	public double getCatchAll() {
		return this.catchAll;
	}

	public InputField getSourceField() {
		return this.sourceField;
	}

	public void setCatchAll(final double catchAll) {
		this.catchAll = catchAll;
	}

}
