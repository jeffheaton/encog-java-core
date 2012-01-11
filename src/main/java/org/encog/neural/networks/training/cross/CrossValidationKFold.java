/*
 * Encog(tm) Core v3.1 - Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 
 * Copyright 2008-2012 Heaton Research, Inc.
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

import org.encog.ml.data.folded.FoldedDataSet;
import org.encog.ml.train.MLTrain;
import org.encog.neural.flat.FlatNetwork;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.training.propagation.TrainingContinuation;

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
 */
public class CrossValidationKFold extends CrossTraining {

	/**
	 * The underlying trainer to use. This trainer does the actual training.
	 */
	private final MLTrain train;

	/**
	 * The network folds.
	 */
	private final NetworkFold[] networks;

	/**
	 * The flat network to train.
	 */
	private final FlatNetwork flatNetwork;

	/**
	 * Construct a cross validation trainer.
	 * 
	 * @param train
	 *            The training
	 * @param k
	 *            The number of folds.
	 */
	public CrossValidationKFold(final MLTrain train, final int k) {
		super(train.getMethod(), (FoldedDataSet) train.getTraining());
		this.train = train;
		getFolded().fold(k);

		this.flatNetwork = ((BasicNetwork)train.getMethod()).getStructure().getFlat();

		this.networks = new NetworkFold[k];
		for (int i = 0; i < networks.length; i++) {
			this.networks[i] = new NetworkFold(flatNetwork);
		}

	}

	/**
	 * Perform one iteration.
	 */
	@Override
	public void iteration() {

		double error = 0;

		for (int valFold = 0; valFold < getFolded().getNumFolds(); valFold++) {

			// restore the correct network
			this.networks[valFold].copyToNetwork(this.flatNetwork);
			
			// train with non-validation folds
			for (int curFold = 0; curFold < getFolded().getNumFolds(); curFold++) {
				if (curFold != valFold) {
					getFolded().setCurrentFold(curFold);
					this.train.iteration();
				}
			}

			// evaluate with the validation fold			
			getFolded().setCurrentFold(valFold);
			double e = this.flatNetwork.calculateError(getFolded());
			//System.out.println("Fold " + valFold + ", " + e);
			error += e;
			this.networks[valFold].copyFromNetwork(this.flatNetwork);
		}

		setError(error / getFolded().getNumFolds());
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public final boolean canContinue() {
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final TrainingContinuation pause() {
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void resume(TrainingContinuation state) {
		
	}

}
