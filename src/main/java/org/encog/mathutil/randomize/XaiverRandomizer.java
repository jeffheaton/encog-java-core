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
package org.encog.mathutil.randomize;

import org.encog.mathutil.matrices.Matrix;
import org.encog.mathutil.randomize.generate.GenerateRandom;
import org.encog.mathutil.randomize.generate.MersenneTwisterGenerateRandom;
import org.encog.ml.MLMethod;
import org.encog.neural.networks.BasicNetwork;

public class XaiverRandomizer implements Randomizer {

	/**
	 * The y2 value.
	 */
	private double y2;

	/**
	 * Should we use the last value.
	 */
	private boolean useLast = false;

	private GenerateRandom rnd;

	public XaiverRandomizer() {
		this(System.currentTimeMillis());
	}
	
	public XaiverRandomizer(long seed) {
		this.rnd = new MersenneTwisterGenerateRandom(seed);
	}

	/**
	 * Compute a Gaussian random number.
	 * 
	 * @param m
	 *            The mean.
	 * @param s
	 *            The standard deviation.
	 * @return The random number.
	 */
	public double boxMuller(final double m, final double s) {
		double x1, x2, w, y1;

		// use value from previous call
		if (this.useLast) {
			y1 = this.y2;
			this.useLast = false;
		} else {
			do {
				x1 = 2.0 * rnd.nextDouble() - 1.0;
				x2 = 2.0 * rnd.nextDouble() - 1.0;
				w = x1 * x1 + x2 * x2;
			} while (w >= 1.0);

			w = Math.sqrt((-2.0 * Math.log(w)) / w);
			y1 = x1 * w;
			this.y2 = x2 * w;
			this.useLast = true;
		}

		return (m + y1 * s);
	}

	/**
	 * Generate a random number.
	 * 
	 * @param d
	 *            The input value, not used.
	 * @return The random number.
	 */
	public double randomize(final double d) {
		return this.rnd.nextDouble();
	}
	
	/**
	 * Randomize one level of a neural network.
	 *
	 * @param network
	 *            The network to randomize
	 * @param fromLayer
	 *            The from level to randomize.
	 */
	public void randomize(final BasicNetwork network,
			final int fromLayer) {
		final int fromCount = network.getLayerTotalNeuronCount(fromLayer);
		final int toCount = network.getLayerNeuronCount(fromLayer + 1);

		for (int fromNeuron = 0; fromNeuron < fromCount; fromNeuron++) {
			for (int toNeuron = 0; toNeuron < toCount; toNeuron++) {
				double sigma = Math.sqrt(2.0/(fromCount+toCount));
				double w = this.boxMuller(0, sigma);
				network.setWeight(fromLayer, fromNeuron, toNeuron, w);
			}
		}
	}


	/**
	 * Randomize the synapses and biases in the basic network based on an array,
	 * modify the array. Previous values may be used, or they may be discarded,
	 * depending on the randomizer.
	 *
	 * @param method
	 *            A network to randomize.
	 */
	@Override
	public void randomize(final MLMethod method) {
		final BasicNetwork network = (BasicNetwork) method;
		for (int i = 0; i < network.getLayerCount() - 1; i++) {
			randomize(network, i);
		}
	}

	@Override
	public void randomize(double[] d) {
		randomize(d,0,d.length);
	}

	@Override
	public void randomize(double[][] d) {
		for(int i=0;i<d.length;i++) {
			for(int j=0;j<d[j].length;j++) {
				d[i][j] = this.rnd.nextDouble();
			}
		}
	}

	@Override
	public void randomize(Matrix m) {
		randomize(m.getData());
	}

	@Override
	public void randomize(double[] d, int begin, int size) {
		for(int i=0;i<size;i++) {
			d[begin+i] = this.rnd.nextDouble();
		}
	}

	@Override
	public void setRandom(GenerateRandom theRandom) {
		this.rnd = theRandom;
	}

	@Override
	public GenerateRandom getRandom() {
		return this.rnd;
	}


}
