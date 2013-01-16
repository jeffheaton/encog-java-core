package org.encog.ml.prg.train.fitness;

import java.io.Serializable;

import org.encog.mathutil.error.ErrorCalculation;
import org.encog.ml.CalculateScore;
import org.encog.ml.MLContext;
import org.encog.ml.MLMethod;
import org.encog.ml.MLRegression;
import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.buffer.BufferedMLDataSet;
import org.encog.util.EngineArray;

public class AdaptiveSimpleFitness implements CalculateScore, Serializable {

	private MLDataSet trainingData;
	private double[] minIdeal;
	private double[] maxIdeal;
	private double[] idealRange;
	
	public AdaptiveSimpleFitness(MLDataSet theTrainingData) {
		this.trainingData = theTrainingData;
		this.minIdeal = new double[this.trainingData.getIdealSize()];
		this.maxIdeal = new double[this.trainingData.getIdealSize()];
		this.idealRange = new double[this.trainingData.getIdealSize()];
		
		EngineArray.fill(this.minIdeal, Double.POSITIVE_INFINITY);
		EngineArray.fill(this.maxIdeal, Double.NEGATIVE_INFINITY);
		
		for(MLDataPair pair: this.trainingData) {
			for(int i=0;i<this.trainingData.getIdealSize();i++) {
				this.minIdeal[i] = Math.min(this.minIdeal[i], pair.getIdealArray()[i]);
				this.maxIdeal[i] = Math.max(this.minIdeal[i], pair.getIdealArray()[i]);
			}
		}
		
		for(int i=0;i<this.trainingData.getIdealSize();i++) {
			this.idealRange[i] = Math.max(0.001, this.maxIdeal[i] - this.minIdeal[i]);
		}
	}
	
	public double norm(int idx, double x) {
		return ((x - this.minIdeal[idx]) 
				/ this.idealRange[idx]);
	}
	
	@Override
	public double calculateScore(MLMethod method) {
		final ErrorCalculation errorCalculation = new ErrorCalculation();
		
		// clear context
		if( method instanceof MLContext ) {
			((MLContext)method).clearContext();
		}

		double error = 0;
		int count = 0;
		// calculate error
		for (final MLDataPair pair : this.trainingData) {
			final MLData actual = ((MLRegression)method).compute(pair.getInput());
			
			for(int i=0;i<actual.size();i++) {
				//double n1 = norm(i,actual.getData(i));
				//double n2 = norm(i,pair.getIdeal().getData(i));
				count++;
				//double diff = norm(i,actual.getData(i)) - norm(i,pair.getIdeal().getData(i));
				errorCalculation.updateError(norm(i,actual.getData(i)) , norm(i,pair.getIdeal().getData(i)));
			}
			
			/*errorCalculation.updateError(actual.getData(), pair.getIdeal()
					.getData(),pair.getSignificance());*/
		}
		return errorCalculation.calculate();
	}

	@Override
	public boolean shouldMinimize() {
		return true;
	}

	@Override
	public boolean requireSingleThreaded() {
		if( this.trainingData instanceof BufferedMLDataSet ) {
			return true;
		}
		return false;
	}

}
