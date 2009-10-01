package org.encog.normalize.output;

import org.encog.normalize.input.InputField;

public abstract class OutputFieldGrouped implements OutputField {
	
	private final OutputFieldGroup group;
	private final InputField sourceField;
	
	public OutputFieldGrouped(OutputFieldGroup group, final InputField sourceField)
	{
		this.group = group;
		this.sourceField = sourceField;
	}

	public OutputFieldGroup getGroup() {
		return group;
	}
	
	public InputField getSourceField()
	{
		return this.sourceField;
	}

}
