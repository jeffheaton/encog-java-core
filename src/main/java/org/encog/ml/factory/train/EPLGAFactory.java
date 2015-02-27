/*
 * Encog(tm) Core v3.3 - Java Version
 * http://www.heatonresearch.com/encog/
 * https://github.com/encog/encog-java-core
 
 * Copyright 2008-2014 Heaton Research, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *   
 * For more information on Heaton Research copyrights, licenses 
 * and trademarks visit:
 * http://www.heatonresearch.com/copyright
 */
package org.encog.ml.factory.train;

import org.encog.ml.CalculateScore;
import org.encog.ml.MLMethod;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.ea.score.adjust.ComplexityAdjustedScore;
import org.encog.ml.ea.train.basic.TrainEA;
import org.encog.ml.prg.PrgCODEC;
import org.encog.ml.prg.opp.ConstMutation;
import org.encog.ml.prg.opp.SubtreeCrossover;
import org.encog.ml.prg.opp.SubtreeMutation;
import org.encog.ml.prg.species.PrgSpeciation;
import org.encog.ml.prg.train.PrgPopulation;
import org.encog.ml.prg.train.rewrite.RewriteAlgebraic;
import org.encog.ml.prg.train.rewrite.RewriteConstants;
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
		
		final CalculateScore score = new TrainingSetScore(training);		
		TrainEA train = new TrainEA(pop, score);
		train.getRules().addRewriteRule(new RewriteConstants());
		train.getRules().addRewriteRule(new RewriteAlgebraic());
		train.setCODEC(new PrgCODEC());
		train.addOperation(0.8, new SubtreeCrossover());
		train.addOperation(0.1, new SubtreeMutation(pop.getContext(),4));
		train.addOperation(0.1, new ConstMutation(pop.getContext(),0.5,1.0));
		train.addScoreAdjuster(new ComplexityAdjustedScore());
		train.setSpeciation(new PrgSpeciation());
		return train;
	}
}
