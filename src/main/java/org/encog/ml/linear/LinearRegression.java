package org.encog.ml.linear;

import org.encog.EncogError;
import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.MLDataSet;

public class LinearRegression {
	
	private MLDataSet data;
	private double[] w;
	private int m;
	
	public LinearRegression(MLDataSet theData) {
		this.data = theData;
		
		if( this.data.getInputSize()!=1 || this.data.getIdealSize()!=1 ) {
			throw new EncogError("Input and ideal size must be one.");
		}
		
		this.w = new double[2];
		
		m = (int)this.data.getRecordCount();
		double sumX = 0;
		double sumY = 0;
		double sumXY = 0;
		double sumX2 = 0;
		
		for(MLDataPair pair: this.data) {
			sumX+=pair.getInputArray()[0];
			sumY+=pair.getIdealArray()[0];
			sumX2+=Math.pow(pair.getInputArray()[0], 2);
			sumXY+=pair.getInputArray()[0]*pair.getIdealArray()[0];
		}
		
		w[1] = ((m*sumXY)-(sumX*sumY))/((m*sumX2)-Math.pow(sumX, 2));
		w[0] = ((1.0/m)*sumY)-( (w[1]/m) * sumX);
	}
	
	public int getM() {
		return m;
	}
	
	public double[] getW() {
		return w;
	}
}
