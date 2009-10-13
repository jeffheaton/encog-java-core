package org.encog.normalize.output.nominal;

import org.encog.normalize.input.InputField;

public class NominalItem {
	private final double low;
	private final double high;
	private final InputField inputField;
	private final double trueValue;
	private final double falseValue;
	
	public NominalItem(InputField inputField, double high, double low,double trueValue,double falseValue) {
		super();
		this.high = high;
		this.low = low;
		this.trueValue = trueValue;
		this.falseValue = falseValue;
		this.inputField = inputField;
	}
	/**
	 * @return the low
	 */
	public double getLow() {
		return low;
	}
	/**
	 * @return the high
	 */
	public double getHigh() {
		return high;
	}
	/**
	 * @return the inputField
	 */
	public InputField getInputField() {
		return inputField;
	}
	
	
	
	/**
	 * @return the trueValue
	 */
	public double getTrueValue() {
		return trueValue;
	}
	/**
	 * @return the falseValue
	 */
	public double getFalseValue() {
		return falseValue;
	}
	public double calculate()
	{
		double currentValue = this.inputField.getCurrentValue();
		if( currentValue>=this.low || currentValue<=this.high )
		{
			return this.trueValue;
		}
		else
		{
			return this.falseValue;
		}
	
	}
	
}
