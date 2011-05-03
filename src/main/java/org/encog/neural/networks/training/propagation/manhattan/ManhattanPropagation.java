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
package org.encog.neural.networks.training.propagation.manhattan;

import org.encog.ml.data.MLDataSet;
import org.encog.neural.flat.train.prop.TrainFlatNetworkManhattan;
import org.encog.neural.networks.ContainsFlat;
import org.encog.neural.networks.training.LearningRate;
import org.encog.neural.networks.training.propagation.Propagation;
import org.encog.neural.networks.training.propagation.TrainingContinuation;

/**
 * One problem that the backpropagation technique has is that the magnitude of
 * the partial derivative may be calculated too large or too small. The
 * Manhattan update algorithm attempts to solve this by using the partial
 * derivative to only indicate the sign of the update to the weight matrix. The
 * actual amount added or subtracted from the weight matrix is obtained from a
 * simple constant. This constant must be adjusted based on the type of neural
 * network being trained. In general, start with a higher constant and decrease
 * it as needed.
 * 
 * The Manhattan update algorithm can be thought of as a simplified version of
 * the resilient algorithm. The resilient algorithm uses more complex techniques
 * to determine the update value.
 * 
 * @author jheaton
 * 
 */
public class ManhattanPropagation extends Propagation implements LearningRate {

	/**
	 * The default tolerance to determine of a number is close to zero.
	 */
	static final double DEFAULT_ZERO_TOLERANCE = 0.001;

	/**
	 * Construct a Manhattan propagation training object.
	 * 
	 * @param network
	 *            The network to train.
	 * @param training
	 *            The training data to use.
	 * @param learnRate
	 *            The learning rate.
	 */
	public ManhattanPropagation(final ContainsFlat network,
			final MLDataSet training, 
			final double learnRate) {
		super(network, training);

		setFlatTraining(new TrainFlatNetworkManhattan(network
				.getFlat(), getTraining(), learnRate));

	}

	/**
	 * @return The learning rate that was specified in the constructor.
	 */
	public final double getLearningRate() {
		return ((TrainFlatNetworkManhattan) getFlatTraining())
				.getLearningRate();
	}

	/**
	 * Set the learning rate.
	 * 
	 * @param rate
	 *            The new learning rate.
	 */
	public final void setLearningRate(final double rate) {
		((TrainFlatNetworkManhattan) getFlatTraining())
				.setLearningRate(rate);
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
