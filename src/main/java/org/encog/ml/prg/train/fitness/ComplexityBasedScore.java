package org.encog.ml.prg.train.fitness;

import java.io.Serializable;

import org.encog.ml.CalculateScore;
import org.encog.ml.MLMethod;
import org.encog.ml.prg.EncogProgram;

public class ComplexityBasedScore implements CalculateScore, Serializable {
	

	@Override
	public double calculateScore(MLMethod method) {
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
