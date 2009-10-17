package org.encog.normalize.output.nominal;

import org.encog.normalize.input.InputField;
import org.encog.persist.annotations.EGAttribute;
import org.encog.persist.annotations.EGReference;

public class NominalItem {
	
	@EGAttribute
	private final double low;
	
	@EGAttribute
	private final double high;
	
	@EGReference
	private final InputField inputField;	
	
	public NominalItem(InputField inputField, double high, double low) {
		super();		
		this.high = high;
		this.low = low;
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
		
	public boolean isInRange()
	{
		double currentValue = this.inputField.getCurrentValue();
		return( currentValue>=this.low && currentValue<=this.high );
	}
		
	/**
	 * 
	 */
	public void beginRow()
	{		
	}

	
}
