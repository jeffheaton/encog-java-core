package org.encog.util.math;

public class Equilateral {
	
	private double[][] matrix;
	
	public Equilateral(int count, double high, double low)
	{
		this.matrix = equilat(count,high,low);
	}
	
	public double[] encode(int set)
	{
		return matrix[set];
	}
	
	public int decode(double[] activations)
	{
		double minValue = Double.POSITIVE_INFINITY;
		int minSet = -1;
		
		for(int i=0;i<matrix.length;i++)
		{
			double dist = getDistance(activations,i);
			if( dist<minValue)
			{
				minValue = dist;
				minSet = i;
			}
		}
		return minSet;
	}
	
	public double getDistance(double[] data,int set)
	{
		double result = 0;
		for(int i=0;i<data.length;i++)
		{
			result+=Math.pow(data[i]-this.matrix[set][i], 2);
		}
		return Math.sqrt(result);
	}
	
	private double[][] equilat(int n,double high,double low)
	{
		double r, f;
		double[][] result = new double[n][n-1];
		
		result[0][0] = -1;
		result[1][0] = 1.0;
		
		for(int k=2;k<n;k++)
		{
			// scale the matrix so far
			r = (double)k;
			f = Math.sqrt(r*r-1.0)/r;
			for(int i=0;i<k;i++)
			{
				for(int j=0;j<k-1;j++)
				{
					result[i][j]*=f;
				}
			}
			
			r = -1.0/r;
			for(int i=0;i<k;i++)
				result[i][k-1]=r;
			
			for(int i=0;i<k-1;i++)
				result[k][i]=0.0;
			result[k][k-1]=1.0;
		}
		
		// scale it
		for(int row = 0;row<result.length;row++)
		{
			for(int col=0;col<result[0].length;col++)
			{
				final double min = -1;
				final double max = 1;
				result[row][col] = 
				 ((result[row][col] - min) / (max - min))
						* (high - low) + low;
			}
		}
		
		return result;
	}
	
	public static void main(String args[])
	{
		Equilateral eq = new Equilateral(5,0.9,0.1);
		double[] d = eq.encode(2);
		System.out.println(eq.getDistance(d, 0));
		System.out.println(eq.getDistance(d, 1));
		System.out.println(eq.getDistance(d, 2));
		System.out.println(eq.getDistance(d, 3));
		System.out.println(eq.getDistance(d, 4));
	}
}
