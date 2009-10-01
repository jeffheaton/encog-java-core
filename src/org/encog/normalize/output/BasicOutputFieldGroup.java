package org.encog.normalize.output;

import java.util.Collection;

public abstract class BasicOutputFieldGroup implements OutputFieldGroup {
	
	private Collection<OutputFieldGrouped> fields;
	
	public void addField(OutputFieldGrouped field)
	{
		this.fields.add(field);
	}
	
	public Collection<OutputFieldGrouped> getGroupedFields()
	{
		return this.fields;
	}
}
