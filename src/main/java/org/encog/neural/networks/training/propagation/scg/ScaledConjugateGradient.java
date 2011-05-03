/*
 * Encog(tm) Core v3.0 - Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 
 * Copyright 2008-2011 Heaton Research, Inc.
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
package org.encog.neural.networks.training.propagation.scg;

import org.encog.ml.data.MLDataSet;
import org.encog.neural.flat.train.prop.TrainFlatNetworkSCG;
import org.encog.neural.networks.ContainsFlat;
import org.encog.neural.networks.training.propagation.Propagation;
import org.encog.neural.networks.training.propagation.TrainingContinuation;

/**
 * This is a training class that makes use of scaled conjugate gradient methods.
 * It is a very fast and efficient training algorithm.
 * 
 */
public class ScaledConjugateGradient extends Propagation {

	/**
	 * Construct a training class.
	 * 
	 * @param network
	 *            The network to train.
	 * @param training
	 *            The training data.
	 */
	public ScaledConjugateGradient(final ContainsFlat network,
			final MLDataSet training) {
		super(network, training);

		final TrainFlatNetworkSCG rpropFlat = new TrainFlatNetworkSCG(network
				.getFlat(), getTraining());
		setFlatTraining(rpropFlat);
	}
	
	/**
	 * This training type does not support training continue.
	 * @return Always returns false.
	 */
	@Override
	public final boolean canContinue() {
		return false;
	}

	/**
	 * This training type does not support training continue.
	 * @return Always returns null.
	 */
	@Override
	public final TrainingContinuation pause() {
		return null;
	}

	/**
	 * This training type does not support training continue.
	 * @param state Not used.
	 */
	@Override
	public final void resume(final TrainingContinuation state) {
		
	}

}
