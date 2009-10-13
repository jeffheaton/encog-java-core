package org.encog.util.math;

public class Equilateral {
	public static double[][] equilat(int n,double high,double low)
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
		double[][] matrix = equilat(4,0,1);
		for(int i=0;i<matrix.length;i++)
		{
			for(int j=0;j<matrix[0].length;j++)
			{
				System.out.println(i + "," + j + ":" + matrix[i][j]);
			}
		}
	}
}
