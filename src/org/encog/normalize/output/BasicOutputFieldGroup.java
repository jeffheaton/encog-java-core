package org.encog.normalize.output;

import java.util.ArrayList;
import java.util.Collection;

public abstract class BasicOutputFieldGroup implements OutputFieldGroup {
	
	private final Collection<OutputFieldGrouped> fields = new ArrayList<OutputFieldGrouped>();
	
	public void addField(OutputFieldGrouped field)
	{
		this.fields.add(field);
	}
	
	public Collection<OutputFieldGrouped> getGroupedFields()
	{
		return this.fields;
	}
}
