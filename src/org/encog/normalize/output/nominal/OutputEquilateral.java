package org.encog.normalize.output.nominal;

import java.util.ArrayList;
import java.util.List;

import org.encog.normalize.input.InputField;
import org.encog.normalize.output.OutputField;

/**
 * 
 * Guiver and Klimasauskas (1991)
 */
public class OutputEquilateral implements OutputField {
	private final List<NominalItem> items = new ArrayList<NominalItem>();
	private double[] result;

	public void addItem(final InputField inputField, final double low, final double high, double trueValue,double falseValue) {
		final NominalItem item = new NominalItem(inputField, low, high, trueValue, falseValue );
		this.items.add(item);
	}
	
	public void addItem(final InputField inputField, final double value, double trueValue,double falseValue) {
		addItem(inputField,value-0.5,value+0.5,trueValue,falseValue);
	}
	
	public void addItemBiPolar(InputField inputField, final double low, final double high)
	{
		addItem(inputField,low,high,1,-1);
	}
	
	public void addItemTF(InputField inputField, final double low, final double high)
	{
		addItem(inputField,low,high,1,0);
	}

	public double calculate(int subfield) {
		return result[subfield];
	}
	
	
	public int getSubfieldCount()
	{
		return this.items.size()-1;
	}
	
	/**
	 * Not needed for this sort of output field.
	 */
	public void beginRow()
	{
		int n = this.items.size();
		int nm1;
		double r;
		double f;
		
		double[] result = new double[n-1];
		nm1 = n - 1;
		result[0] = -1;
		result[nm1] = 1;
		
		for(int k=2;k<n;k++)
		{
			// scale the matrix so far
			r = (double) k;
			f = Math.sqrt( r*r - 1.0)/r;
			for(int i=0;i<k;i++)
			{
				for(int j=0;j<k-1;j++)
				{
					result[i*nm1+j]*=f;
				}
			}
			
			// append a column of all -1/k
			
			r = -1.0/r;
			
			for(int i=0;i<k;i++)
			{
				result[i*nm1+k-1] = r;				
			}
			
			// append new row of all 0's except 1 at end
			
			for(int i=0;i<k-1;i++)
			{
				result[k*nm1+k-1] = 0;
			}
			
			result[k*nm1+k-1] = 1;
		}
	}

}
