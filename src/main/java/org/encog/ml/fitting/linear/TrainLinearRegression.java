package org.encog.ml.fitting.linear;

import org.encog.EncogError;
import org.encog.ml.MLMethod;
import org.encog.ml.TrainingImplementationType;
import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.train.BasicTraining;
import org.encog.neural.networks.training.propagation.TrainingContinuation;
import org.encog.util.simple.EncogUtility;


public class TrainLinearRegression extends BasicTraining {

	private final LinearRegression method; 
	private final MLDataSet training;
	
	public TrainLinearRegression(LinearRegression theMethod, MLDataSet theTraining) {
		super(theMethod.getInputCount()==1?TrainingImplementationType.OnePass:TrainingImplementationType.Iterative);
		this.method = theMethod;
		this.training = theTraining;
	}
	
	/**
	 * @return the training
	 */
	public MLDataSet getTraining() {
		return training;
	}



	@Override
	public void iteration() {
		int m = (int)this.training.getRecordCount();
		double sumX = 0;
		double sumY = 0;
		double sumXY = 0;
		double sumX2 = 0;
		
		for(MLDataPair pair: this.training) {
			sumX+=pair.getInputArray()[0];
			sumY+=pair.getIdealArray()[0];
			sumX2+=Math.pow(pair.getInputArray()[0], 2);
			sumXY+=pair.getInputArray()[0]*pair.getIdealArray()[0];
		}
		
		this.method.getWeights()[1] = ((m*sumXY)-(sumX*sumY))/((m*sumX2)-Math.pow(sumX, 2));
		this.method.getWeights()[0] = ((1.0/m)*sumY)-( (this.method.getWeights()[1]/m) * sumX);
		
		this.setError(EncogUtility.calculateRegressionError(this.method, this.training));
	}

	@Override
	public boolean canContinue() {
		return false;
	}

	@Override
	public TrainingContinuation pause() {
		return null;
	}

	@Override
	public void resume(TrainingContinuation state) {
		throw new EncogError("Not supported");
	}

	@Override
	public MLMethod getMethod() {
		return this.method;
	}
	
}
