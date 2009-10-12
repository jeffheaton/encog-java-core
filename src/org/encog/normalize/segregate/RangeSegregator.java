package org.encog.normalize.segregate;

import java.util.ArrayList;
import java.util.Collection;

import org.encog.normalize.Normalization;
import org.encog.normalize.input.InputField;

public class RangeSegregator implements Segregator {

	private final InputField sourceField;
	private final boolean include;
	private final Collection<SegregationRange> ranges = new ArrayList<SegregationRange>();
	private Normalization normalization;

	public RangeSegregator(final InputField sourceField, final boolean include) {
		this.sourceField = sourceField;
		this.include = include;
	}

	public void addRange(final double low, final double high,
			final boolean include) {
		final SegregationRange range = new SegregationRange(low, high, include);
		addRange(range);
	}

	public void addRange(final SegregationRange range) {
		this.ranges.add(range);
	}

	public Normalization getNormalization() {
		return this.normalization;
	}

	public InputField getSourceField() {
		return this.sourceField;
	}

	public void init(final Normalization normalization) {
		this.normalization = normalization;
	}

	public boolean shouldInclude() {
		final double value = this.sourceField.getCurrentValue();
		for (final SegregationRange range : this.ranges) {
			if (range.inRange(value)) {
				return range.isIncluded();
			}
		}
		return this.include;
	}

}
