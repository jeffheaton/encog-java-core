package org.encog.util.logging;

import java.text.NumberFormat;

import org.encog.matrix.Matrix;

public class DumpMatrix {
	public static String dumpMatrix(Matrix matrix)
	{
		NumberFormat format = NumberFormat.getNumberInstance();
		format.setMaximumFractionDigits(3);
		format.setMaximumFractionDigits(3);
		
		StringBuilder result = new StringBuilder();
		result.append("==");
		result.append(matrix.toString());
		result.append("==\n");
		for(int row = 0; row< matrix.getRows();row++)
		{
			result.append("  [");
			for(int col = 0; col<matrix.getCols();col++)
			{
				if(col!=0)
					result.append(",");
				result.append(format.format(matrix.get(row, col)));
			}
			result.append("]\n");
		}
		return result.toString();
	}

	public static String dumpArray(double[] d) {
		NumberFormat format = NumberFormat.getNumberInstance();
		format.setMaximumFractionDigits(3);
		format.setMaximumFractionDigits(3);
		
		StringBuilder result = new StringBuilder();
		result.append("[");
		for(int i=0;i<d.length;i++)
		{
			if(i!=0)
				result.append(",");
			result.append(format.format(d[i]));
		}
		result.append("]");
		return result.toString();
	}
}
