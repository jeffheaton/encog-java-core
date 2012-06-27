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
import org.encog.engine.network.activation.ActivationFunction;
import org.encog.mathutil.matrices.Matrix;
import org.encog.ml.MLMethod;
import org.encog.neural.networks.BasicNetwork;

/**
 * Implementation of <i>Nguyen-Widrow</i> weight initialization. This is the
 * default weight initialization used by Encog, as it generally provides the
 * most train-able neural network.
 */
public class NguyenWidrowRandomizer implements Randomizer {

	public static String MSG = "This type of randomization is not supported by Nguyen-Widrow";
	
	@Override
	public void randomize(MLMethod method) {
		if( !(method instanceof BasicNetwork) ) {
			throw new EncogError("Nguyen-Widrow only supports BasicNetwork.");
		}
		
		BasicNetwork network = (BasicNetwork)method;
		
		for(int fromLayer=0; fromLayer<network.getLayerCount()-1; fromLayer++) {
			randomizeSynapse(network, fromLayer);
		}
		
	}
	
	private double calculateRange(ActivationFunction af, double r) {
		double[] d = { r };
		af.activationFunction(d, 0, 1);
		return d[0];
	}
	
	private void randomizeSynapse(BasicNetwork network, int fromLayer) {
		int toLayer = fromLayer+1;
		int toCount = network.getLayerNeuronCount(toLayer);
		int fromCount = network.getLayerNeuronCount(fromLayer);
		int fromCountTotalCount = network.getLayerTotalNeuronCount(fromLayer);
		ActivationFunction af = network.getActivation(toLayer);
		double low = calculateRange(af,Double.NEGATIVE_INFINITY);
		double high = calculateRange(af,Double.POSITIVE_INFINITY);

		double b = 0.7d * Math.pow(toCount, (1d / fromCount)) / (high-low);

		for(int toNeuron=0; toNeuron<toCount;toNeuron++) {
			if( fromCount!=fromCountTotalCount ) {
				double w = RangeRandomizer.randomize(-b, b);
				network.setWeight(fromLayer, fromCount, toNeuron, w);
			}
			for(int fromNeuron=0; fromNeuron<fromCount;fromNeuron++) {
				double w = RangeRandomizer.randomize(0, b);
				network.setWeight(fromLayer, fromNeuron, toNeuron, w);	
			}
		}
	}

	@Override
	public double randomize(double d) {
		throw new EncogError(MSG);
	}

	@Override
	public void randomize(double[] d) {
		throw new EncogError(MSG);
	}

	@Override
	public void randomize(double[][] d) {
		throw new EncogError(MSG);
	}

	@Override
	public void randomize(Matrix m) {
		throw new EncogError(MSG);
	}

	@Override
	public void randomize(double[] d, int begin, int size) {
		throw new EncogError(MSG);
	}
}
