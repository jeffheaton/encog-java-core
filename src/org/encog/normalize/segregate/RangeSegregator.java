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
	
	public RangeSegregator(InputField sourceField, boolean include)
	{
		this.sourceField = sourceField;
		this.include = include;
	}
	
	
	public boolean shouldInclude() {
		double value = sourceField.getCurrentValue();
		for(SegregationRange range: ranges)
		{
			if(range.inRange(value))
				return range.isIncluded();
		}
		return this.include;
	}
	
	public void addRange(SegregationRange range)
	{
		this.ranges.add(range);
	}
	
	public void addRange(double low,double high,boolean include)
	{
		SegregationRange range = new SegregationRange(low,high,include);
		addRange(range);
	}


	public InputField getSourceField() {
		return sourceField;
	}


	public Normalization getNormalization() {
		return this.normalization;
	}


	public void init(Normalization normalization) {
		this.normalization = normalization;
	}
	
	

}
