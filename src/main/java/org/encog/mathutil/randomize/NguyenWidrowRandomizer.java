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
package org.encog.mathutil.randomize;

import org.encog.EncogError;
import org.encog.ml.MLMethod;
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
	 * Construct a Nguyen-Widrow randomizer, with the standard range of -0.5 to +0.5,
	 * as specified in the origional paper from which this class is based.
	 */
	public NguyenWidrowRandomizer() {
		super(-0.5, 0.5);
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
	 * @param method The network to randomize.
	 */
	@Override
	public final void randomize(final MLMethod method) {
		
		if( !(method instanceof BasicNetwork) ) {
			throw new EncogError("Nguyen Widrow only works on BasicNetwork.");
		}
		
		BasicNetwork network = (BasicNetwork)method;

		new RangeRandomizer(getMin(), getMax()).randomize(network);

		int hiddenNeurons = network.getLayerNeuronCount(1);

		// can't really do much, use regular randomization
		if (hiddenNeurons < 1) {
			return;
		}

		this.inputCount = network.getInputCount();
		this.beta = 0.7 * Math.pow(hiddenNeurons, 1.0 / network.getInputCount());
		int totalInput = network.getLayerTotalNeuronCount(0);
		
		for( int i = 0; i<hiddenNeurons;i++) {
			// calculate mag
			double mag = 0;
			for(int j=0; j<totalInput;j++) {
				double w = network.getWeight(0, j, i);
				mag+=w*w;
			}
			mag = Math.sqrt(mag);
			
			// now adjust weights
			for(int j=0; j<totalInput;j++) {
				double w = network.getWeight(0, j, i);
				w=(this.beta*w)/mag;
				network.setWeight(0, j, i, w);
			}
		}
	}
}
