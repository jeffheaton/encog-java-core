package org.encog.ml.factory.train;

import org.encog.ml.CalculateScore;
import org.encog.ml.MLMethod;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.ea.score.adjust.ComplexityAdjustedScore;
import org.encog.ml.ea.train.basic.TrainEA;
import org.encog.ml.prg.PrgCODEC;
import org.encog.ml.prg.opp.SubtreeCrossover;
import org.encog.ml.prg.opp.SubtreeMutation;
import org.encog.ml.prg.train.PrgPopulation;
import org.encog.ml.prg.train.rewrite.RewriteConstants;
import org.encog.ml.prg.train.rewrite.algebraic.RewriteAlgebraic;
import org.encog.ml.train.MLTrain;
import org.encog.neural.networks.training.TrainingSetScore;

public class EPLGAFactory {
	/**
	 * Create an EPL GA trainer.
	 * 
	 * @param method
	 *            The method to use.
	 * @param training
	 *            The training data to use.
	 * @param argsStr
	 *            The arguments to use.
	 * @return The newly created trainer.
	 */
	public MLTrain create(final MLMethod method,
			final MLDataSet training, final String argsStr) {
		
		PrgPopulation pop = (PrgPopulation)method;
		
		pop.addRewriteRule(new RewriteConstants());
		pop.addRewriteRule(new RewriteAlgebraic());

		final CalculateScore score = new TrainingSetScore(training);		
		TrainEA train = new TrainEA(pop, score);
		train.setValidationMode(true);
		train.setCODEC(new PrgCODEC());
		train.addOperation(0.95, new SubtreeCrossover());
		train.addOperation(0.05, new SubtreeMutation(pop.getContext(),4));
		train.addScoreAdjuster(new ComplexityAdjustedScore());

		return train;
	}
}
