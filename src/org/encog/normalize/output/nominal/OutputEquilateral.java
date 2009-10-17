package org.encog.normalize.output.nominal;

import java.util.ArrayList;
import java.util.List;

import org.encog.normalize.input.InputField;
import org.encog.normalize.output.BasicOutputField;
import org.encog.normalize.output.OutputField;
import org.encog.persist.annotations.EGIgnore;
import org.encog.util.math.Equilateral;

/**
 * 
 * Guiver and Klimasauskas (1991)
 */
public class OutputEquilateral extends BasicOutputField {
	private final List<NominalItem> items = new ArrayList<NominalItem>();
	
	@EGIgnore
	private double[][] matrix;
	private int currentValue;
	private double high;
	private double low;

	public OutputEquilateral(double high, double low) {
		this.high = high;
		this.low = low;
	}
	
	public OutputEquilateral()
	{
		
	}

	public void addItem(final InputField inputField, final double low, final double high) {
		final NominalItem item = new NominalItem(inputField, low, high);
		this.items.add(item);
	}
	
	public void addItem(final InputField inputField, final double value) {
		addItem(inputField,value-0.5,value+0.5);
	}
	


	public double calculate(int subfield) {
		return this.matrix[this.currentValue][subfield];
	}
	
	
	public int getSubfieldCount()
	{
		return this.items.size()-1;
	}
	
	/**
	 * Determine which item's index is the value.
	 */
	public void rowInit()
	{
		for(int i=0;i<this.items.size();i++)
		{
			NominalItem item = this.items.get(i);
			if( item.isInRange() )
			{
				this.currentValue = i;
				break;
			}
		}
		
		if(this.matrix==null)
		{
			this.matrix = Equilateral.equilat(this.items.size(), this.high, this.low);
		}
	}

	public double getHigh() {
		return high;
	}

	public double getLow() {
		return low;
	}
	
	

}
