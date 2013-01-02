package org.encog.ml.prg.train.fitness;

import java.io.Serializable;

import org.encog.ml.MLRegression;
import org.encog.ml.prg.EncogProgram;
import org.encog.neural.networks.training.CalculateScore;

public class ComplexityBasedScore implements CalculateScore, Serializable {
	

	@Override
	public double calculateScore(MLRegression method) {
		EncogProgram prg = (EncogProgram)method;
		return prg.size();
	}

	@Override
	public boolean shouldMinimize() {
		return true;
	}

	@Override
	public boolean requireSingleThreaded() {
		return false;
	}

	
	
}
