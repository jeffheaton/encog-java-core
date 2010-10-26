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

package org.encog.neural.networks.training.propagation.manhattan;

import org.encog.engine.network.train.prop.OpenCLTrainingProfile;
import org.encog.engine.network.train.prop.TrainFlatNetworkBackPropagation;
import org.encog.engine.network.train.prop.TrainFlatNetworkManhattan;
import org.encog.engine.network.train.prop.TrainFlatNetworkOpenCL;
import org.encog.neural.data.NeuralDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.training.LearningRate;
import org.encog.neural.networks.training.propagation.Propagation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
	 * The logging object.
	 */
	@SuppressWarnings("unused")
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	/**
	 * Construct a Manhattan propagation training object. Use the CPU to train.
	 * 
	 * @param network
	 *            The network to train.
	 * @param training
	 *            The training data to use.
	 * @param learnRate
	 *            The learning rate.
	 */
	public ManhattanPropagation(final BasicNetwork network,
			final NeuralDataSet training, final double learnRate) {
		this(network, training, null, learnRate);
	}

	/**
	 * Construct a Manhattan propagation training object.
	 * 
	 * @param network
	 *            The network to train.
	 * @param training
	 *            The training data to use.
	 * @param learnRate
	 *            The learning rate.
	 * @param profile
	 *            The OpenCL profile to use, null for CPU.
	 */
	public ManhattanPropagation(final BasicNetwork network,
			final NeuralDataSet training, final OpenCLTrainingProfile profile,
			final double learnRate) {
		super(network, training);

		if (profile == null) {
			setFlatTraining(new TrainFlatNetworkManhattan(network
					.getStructure().getFlat(), getTraining(), learnRate));
		} else {
			final TrainFlatNetworkOpenCL rpropFlat = new TrainFlatNetworkOpenCL(
					network.getStructure().getFlat(), getTraining(), profile);
			rpropFlat.learnManhattan(learnRate);
			setFlatTraining(rpropFlat);
		}

	}

	/**
	 * @return The learning rate that was specified in the constructor.
	 */
	public double getLearningRate() {
		return ((TrainFlatNetworkBackPropagation) getFlatTraining())
				.getLearningRate();
	}

	/**
	 * Set the learning rate.
	 * 
	 * @param rate
	 *            The new learning rate.
	 */
	public void setLearningRate(final double rate) {
		((TrainFlatNetworkBackPropagation) getFlatTraining())
				.setLearningRate(rate);
	}

}
