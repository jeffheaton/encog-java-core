package org.encog.ml.fitting.linear;

import org.encog.EncogError;
import org.encog.ml.MLError;
import org.encog.ml.MLRegression;
import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.basic.BasicMLData;
import org.encog.util.simple.EncogUtility;

public class LinearRegression implements MLRegression, MLError {
	
	private double[] weights;
	private int inputCount;
	
	public LinearRegression(int theInputCount) {
		
		if( theInputCount!=1  ) {
			throw new EncogError("Input size must be one.");
		}
		
		this.inputCount = theInputCount;
		this.weights = new double[theInputCount+1];	
	}
	
	
	public double[] getWeights() {
		return weights;
	}

	@Override
	public int getInputCount() {
		return this.inputCount;
	}

	@Override
	public int getOutputCount() {
		return 1;
	}

	@Override
	public MLData compute(MLData input) {
		if( input.size()!=this.inputCount) {
			throw new EncogError("Invalid input size, must be " + inputCount);
		}
		double[] sum = new double[1];
		
		sum[0] += this.weights[0];
		
		for(int i=0;i<input.size();i++) {	
			sum[0] += this.weights[i+1] * Math.pow(input.getData(i),i+1);
		}
		
		return new BasicMLData(sum);
	}


	@Override
	public double calculateError(MLDataSet data) {
		return EncogUtility.calculateRegressionError(this, data);
	}
}
