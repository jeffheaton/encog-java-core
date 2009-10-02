package org.encog.normalize.output.mapped;

import java.util.ArrayList;
import java.util.List;

import org.encog.normalize.input.InputField;
import org.encog.normalize.output.OutputField;

public class OutputFieldEncode implements OutputField {

	private final InputField sourceField;
	private double catchAll;
	private final List<MappedRange> ranges = new ArrayList<MappedRange>();
	
	public OutputFieldEncode(InputField sourceField)
	{
		this.sourceField = sourceField;
	}
	
	public void addRange(double low, double high,double value)
	{
		MappedRange range = new MappedRange(low,high,value);
		this.ranges.add(range);
	}
	
	public double calculate() {
		for(MappedRange range: ranges) {
			if( range.inRange(this.sourceField.getCurrentValue())) {
				return range.getValue();
			}
		}
		
		return this.catchAll;
	}

	public double getCatchAll() {
		return catchAll;
	}

	public void setCatchAll(double catchAll) {
		this.catchAll = catchAll;
	}

	public InputField getSourceField() {
		return sourceField;
	}
	
	

}
