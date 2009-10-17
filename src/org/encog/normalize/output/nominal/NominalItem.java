package org.encog.normalize.output.nominal;

import org.encog.normalize.input.InputField;
import org.encog.persist.annotations.EGAttribute;
import org.encog.persist.annotations.EGReference;

public class NominalItem {
	
	@EGAttribute
	private double low;
	
	@EGAttribute
	private double high;
	
	@EGReference
	private InputField inputField;	
	
	public NominalItem(InputField inputField, double high, double low) {
		super();		
		this.high = high;
		this.low = low;
		this.inputField = inputField;
	}
	
	public NominalItem()
	{
		
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
