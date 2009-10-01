package org.encog.normalize.output;

import java.util.Collection;

public interface OutputFieldGroup {
	public void rowInit();
	public void addField(OutputFieldGrouped field);
	public Collection<OutputFieldGrouped> getGroupedFields();
}
