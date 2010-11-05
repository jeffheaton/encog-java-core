/*
 * Encog(tm) Core v2.5 - Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 
 * Copyright 2008-2010 Heaton Research, Inc.
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
package org.encog.neural.networks.training.cross;

import org.encog.neural.data.folded.FoldedDataSet;
import org.encog.neural.networks.training.Train;

/**
 * Train using K-Fold cross validation. Each iteration will train a number of
 * times equal to the number of folds - 1. Each of these sub iterations will
 * train all of the data minus the fold. The fold is used to validate.
 * 
 * Therefore, you are seeing an error that reflects data that was not always
 * used as part of training. This should give you a better error result based on
 * how the network will perform on non-trained data.(validation).
 * 
 * The cross validation trainer must be provided with some other sort of
 * trainer, perhaps RPROP, to actually perform the training. The training data
 * must be the FoldedDataSet. The folded dataset can wrap most other training
 * sets.
 * 
 * @author jheaton
 * 
 */
public class CrossValidationKFold extends CrossTraining {

	/**
	 * The underlying trainer to use. This trainer does the actual training.
	 */
	private final Train train;

	/**
	 * Construct a cross validation trainer.
	 * 
	 * @param train
	 *            The training
	 * @param k
	 *            The number of folds.
	 */
	public CrossValidationKFold(final Train train, final int k) {
		super(train.getNetwork(), (FoldedDataSet) train.getTraining());
		this.train = train;
		getFolded().fold(k);
	}

	/**
	 * Perform one iteration.
	 */
	@Override
	public void iteration() {

		double error = 0;

		for (int valFold = 0; valFold < getFolded().getNumFolds(); valFold++) {
			// train with non-validation folds
			for (int curFold = 0; curFold < valFold; curFold++) {
				getFolded().setCurrentFold(curFold);
				this.train.iteration();
			}
			for (int curFold = valFold + 1; curFold < getFolded().getNumFolds(); curFold++) {
				getFolded().setCurrentFold(curFold);
				this.train.iteration();
			}

			// evaluate with the validation fold
			error += getNetwork().calculateError(getFolded());
		}

		setError(error / getFolded().getNumFolds());
	}
}
