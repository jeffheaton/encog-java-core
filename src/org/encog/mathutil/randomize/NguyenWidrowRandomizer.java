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
package org.encog.mathutil.randomize;

import org.encog.neural.networks.BasicNetwork;

/**
 * Implementation of <i>Nguyen-Widrow</i> weight initialization. This is the
 * default weight initialization used by Encog, as it generally provides the
 * most trainable neural network.
 * 
 * 
 * @author St?phan Corriveau
 * 
 */
public class NguyenWidrowRandomizer extends RangeRandomizer implements
		Randomizer {
	
	private int inputCount;
	private double beta;

	/**
	 * Construct a Nguyen-Widrow randomizer.
	 * 
	 * @param min
	 *            The min of the range.
	 * @param max
	 *            The max of the range.
	 */
	public NguyenWidrowRandomizer(final double min, final double max) {
		super(min, max);
	}
	
	/**
	 * The <i>Nguyen-Widrow</i> initialization algorithm is the following :
	 * <br>
	 * 1. Initialize all weight of hidden layers with (ranged) random values<br>
	 * 2. For each hidden layer<br>
	 * 2.1 calculate beta value, 0.7 * Nth(#neurons of input layer) root of
	 * #neurons of current layer <br>
	 * 2.2 for each synapse<br>
	 * 2.1.1 for each weight <br>
	 * 2.1.2 Adjust weight by dividing by norm of weight for neuron and
	 * multiplying by beta value
	 * @param network The network to randomize.
	 */
	@Override
	public final void randomize(final BasicNetwork network) {

		new RangeRandomizer(getMin(), getMax()).randomize(network);

		int hiddenNeurons = 0;

		for(int i=1;i<network.getLayerCount()-1;i++)
		{
			hiddenNeurons+=network.getLayerTotalNeuronCount(i);
		}

		// can't really do much, use regular randomization
		if (hiddenNeurons < 1) {
			return;
		}

		this.inputCount = network.getInputCount();
		this.beta = 0.7 * Math.pow(hiddenNeurons, 1.0 / network.getInputCount());

		super.randomize(network);
	}
	
	/**
	 * Randomize one level of a neural network.
	 * @param network The network to randomize
	 * @param fromLevel The from level to randomize.
	 */
	public void randomize(final BasicNetwork network, int fromLayer)
	{
		int fromCount = network.getLayerTotalNeuronCount(fromLayer);
		int toCount = network.getLayerNeuronCount(fromLayer+1);
		
		for(int fromNeuron = 0; fromNeuron<fromCount; fromNeuron++)
		{
			for(int toNeuron = 0; toNeuron<toCount; toNeuron++)
			{
				double v = network.getWeight(fromLayer, fromNeuron, toNeuron);
				v = (this.beta * v) / this.inputCount;
				network.setWeight(fromLayer, fromNeuron, toNeuron, v);
			}
		}
	}


}
