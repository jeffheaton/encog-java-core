package org.encog.ml.prg.train.fitness;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.encog.EncogError;
import org.encog.ml.MLRegression;
import org.encog.ml.data.buffer.BufferedMLDataSet;
import org.encog.neural.networks.training.CalculateScore;

public class MultiObjectiveFitness  implements CalculateScore, Serializable {
	
	private final List<FitnessObjective> objectives = new ArrayList<FitnessObjective>();
	private boolean min;

	public void addObjective(double weight, CalculateScore fitnessFunction) {
		if( this.objectives.size()==0 ) {
			this.min = fitnessFunction.shouldMinimize();
		} else {
			if( fitnessFunction.shouldMinimize() != this.min) {
				throw new EncogError("Multi-objective mismatch, some objectives are min and some are max.");
			}
		}
		this.objectives.add(new FitnessObjective(weight,fitnessFunction));
	}
	
	@Override
	public double calculateScore(MLRegression method) {
		double result = 0;
		
		for(FitnessObjective obj: this.objectives) {
			result+=obj.getScore().calculateScore(method) * obj.getWeight();
		}
		
		return result;
	}

	@Override
	public boolean shouldMinimize() {
		return this.min;
	}
	
	@Override
	public boolean requireSingleThreaded() {
		for(FitnessObjective obj: this.objectives) {
			if( obj.getScore().requireSingleThreaded() ) {
				return true;
			}
		}
		return false;
	}

}
