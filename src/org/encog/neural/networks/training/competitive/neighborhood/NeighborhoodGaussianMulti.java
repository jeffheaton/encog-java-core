package org.encog.neural.networks.training.competitive.neighborhood;

import org.encog.util.math.rbf.GaussianFunctionMulti;
import org.encog.util.math.rbf.RadialBasisFunctionMulti;

public class NeighborhoodGaussianMulti implements NeighborhoodFunction {

	private RadialBasisFunctionMulti rbf;
	private int[] size;
	private int[] displacement;
	
	NeighborhoodGaussianMulti(int[] size, RadialBasisFunctionMulti rbf)
	{
		this.rbf = rbf;
		this.size = size;
		calculateDisplacement();
	}
	
	public NeighborhoodGaussianMulti(int x,int y)
	{
		int[] size = new int[2];
		size[0] = x;
		size[1] = y;
		
		double[] centerArray = new double[2];
		centerArray[0] = 0;
		centerArray[1] = 0;
		
		double[] widthArray = new double[2];
		widthArray[0] = 1;
		widthArray[1] = 1;
		
		RadialBasisFunctionMulti rbf = new GaussianFunctionMulti(1, centerArray, widthArray);
		
		this.rbf = rbf;
		this.size = size;
		
		calculateDisplacement();
	}
	
	private void calculateDisplacement()
	{
		this.displacement = new int[this.size.length];
		for( int i=0;i<this.size.length;i++)
		{
			int value;
			
			if( i==0 )
			{
				value = 0;
			}
			else if (i==1)
			{
				value = this.size[0];
			}
			else
			{
				value = this.displacement[i-1] * this.size[i-1];
			}
			
			this.displacement[i] = value;
		}
	}
	
	private int[] translateCoordinates(int index)
	{
		int[] result = new int[displacement.length];
		int countingIndex = index;
		
		for(int i=displacement.length-1;i>=0;i--)
		{
			int value;
			if( displacement[i]>0 )
				value = countingIndex/displacement[i];
			else
				value = countingIndex;
			
			countingIndex-=displacement[i]*value;
			result[i] = value;
			
		}
		
		return result;
	}
	
	
	public double function(int currentNeuron, int bestNeuron) {
		double[] vector = new double[this.displacement.length]; 
		int[] vectorCurrent = translateCoordinates(currentNeuron);
		int[] vectorBest = translateCoordinates(bestNeuron);
		for(int i=0;i<vectorCurrent.length;i++) {
			vector[i] = vectorCurrent[i]-vectorBest[i];
		}
		return rbf.calculate(vector);
		
	}
	
	public RadialBasisFunctionMulti getRBF()
	{
		return this.rbf;
	}

	public double getRadius() {
		return this.rbf.getWidth(0);
	}

	public void setRadius(double radius) {
		this.rbf.setWidth(radius);
	}

}
