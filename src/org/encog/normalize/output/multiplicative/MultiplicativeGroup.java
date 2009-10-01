package org.encog.normalize.output.multiplicative;

import org.encog.normalize.output.BasicOutputFieldGroup;
import org.encog.normalize.output.OutputFieldGrouped;

public class MultiplicativeGroup extends BasicOutputFieldGroup {

	private double length;
	
	public void rowInit() {
		double value = 0;
		
		for(OutputFieldGrouped field: this.getGroupedFields())
		{
			value+=(field.getSourceField().getCurrentValue()*
					field.getSourceField().getCurrentValue());
		}
		this.length = Math.sqrt(value);
	}
	
	public double getLength()
	{
		return this.length;
	}

}
