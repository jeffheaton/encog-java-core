package org.encog.normalize.output.nominal;

import java.util.ArrayList;
import java.util.List;

import org.encog.normalize.input.InputField;
import org.encog.normalize.output.BasicOutputField;
import org.encog.normalize.output.OutputField;

public class OutputOneOf extends BasicOutputField {

	private List<NominalItem> items = new ArrayList<NominalItem>();
	private double trueValue;
	private double falseValue;

	public OutputOneOf(double trueValue, double falseValue) {
		this.trueValue = trueValue;
		this.falseValue = falseValue;
	}
	
	public OutputOneOf()
	{
		
	}

	public void addItem(final InputField inputField, final double low, final double high) {
		final NominalItem item = new NominalItem(inputField, low, high );
		this.items.add(item);
	}
	
	public void addItem(final InputField inputField, final double value) {
		addItem(inputField,value-0.5,value+0.5);
	}

	public double calculate(int subfield) {
		NominalItem item = this.items.get(subfield);
		return item.isInRange()?this.trueValue:this.falseValue;
	}
	
	public int getSubfieldCount()
	{
		return this.items.size();
	}
	
	/**
	 * Not needed for this sort of output field.
	 */
	public void rowInit()
	{		
	}

	public double getTrueValue() {
		return trueValue;
	}

	public double getFalseValue() {
		return falseValue;
	}
	
	


}
