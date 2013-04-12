package org.encog.ml.prg.train;

import java.io.Serializable;

import org.encog.ml.CalculateScore;
import org.encog.ml.MLMethod;
import org.encog.ml.data.MLData;
import org.encog.ml.data.basic.BasicMLData;
import org.encog.ml.prg.EncogProgram;
import org.encog.ml.prg.train.PrgPopulation;

/**
 * This is a very simple evaluation function that simply passes in all zeros for
 * the input arguments. Make sure that the genome you are testing supports
 * floating point numbers for inputs.
 * 
 * This evaluation function is useful to test to very quickly verify that a
 * genome is valid and does not generate any obvious division by zero issues.
 * This allows a population generator to quickly eliminate some invalid genomes.
 */
public class ZeroEvalScoreFunction implements CalculateScore, Serializable {

	/**
	 * The serial id.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public double calculateScore(final MLMethod genome) {
		final EncogProgram prg = (EncogProgram) genome;
		final PrgPopulation pop = (PrgPopulation) prg.getPopulation();
		MLData inputData = new BasicMLData(pop.getContext()
				.getDefinedVariables().size());
		prg.compute(inputData);
		return 0;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean requireSingleThreaded() {
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean shouldMinimize() {
		return true;
	}

}
