package org.encog.normalize.output;

import org.encog.normalize.input.InputField;

public abstract class OutputFieldGrouped implements OutputField {

	private final OutputFieldGroup group;
	private final InputField sourceField;

	public OutputFieldGrouped(final OutputFieldGroup group,
			final InputField sourceField) {
		this.group = group;
		this.sourceField = sourceField;
		this.group.getGroupedFields().add(this);
	}

	public OutputFieldGroup getGroup() {
		return this.group;
	}

	public InputField getSourceField() {
		return this.sourceField;
	}

}
