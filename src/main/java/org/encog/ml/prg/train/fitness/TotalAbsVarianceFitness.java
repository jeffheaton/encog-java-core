package org.encog.ml.prg.train.fitness;

import java.io.Serializable;

import org.encog.ml.MLContext;
import org.encog.ml.MLRegression;
import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.buffer.BufferedMLDataSet;
import org.encog.ml.prg.EncogProgram;
import org.encog.neural.networks.training.CalculateScore;

public class TotalAbsVarianceFitness implements CalculateScore, Serializable {

	private MLDataSet trainingData;
	
	public TotalAbsVarianceFitness(MLDataSet theTrainingData) {
		this.trainingData = theTrainingData;
	}
	
	@Override
	public double calculateScore(MLRegression method) {
		EncogProgram program = (EncogProgram)method;
	
		// clear context
		if( program instanceof MLContext ) {
			((MLContext)program).clearContext();
		}

		double error = 0;
		
		// calculate error
		for (final MLDataPair pair : this.trainingData) {
			final MLData actual = method.compute(pair.getInput());
			for(int i=0;i<actual.size();i++) {
				error+=Math.abs(actual.getData(i) - pair.getIdeal().getData(i));
			}
		}
		return error;
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
