package org.encog.ml.prg.train;

import java.io.Serializable;

import org.encog.ml.CalculateScore;
import org.encog.ml.MLMethod;
import org.encog.ml.data.MLData;
import org.encog.ml.data.basic.BasicMLData;
import org.encog.ml.prg.EncogProgram;
import org.encog.ml.prg.train.PrgPopulation;

public class ZeroEvalScoreFunction implements CalculateScore, Serializable {

	@Override
	public double calculateScore(final MLMethod genome) {
		final EncogProgram prg = (EncogProgram) genome;
		final PrgPopulation pop = (PrgPopulation)prg.getPopulation();
		MLData inputData = new BasicMLData(pop.getContext().getDefinedVariables().size());
		prg.compute(inputData);
		return 0;
	}

	@Override
	public boolean requireSingleThreaded() {
		return false;
	}

	@Override
	public boolean shouldMinimize() {
		return true;
	}

}
