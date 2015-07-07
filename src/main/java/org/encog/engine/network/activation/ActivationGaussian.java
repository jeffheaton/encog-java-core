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
package org.encog.engine.network.activation;

import org.encog.mathutil.BoundMath;
import org.encog.ml.factory.MLActivationFactory;
import org.encog.util.obj.ActivationUtil;

/**
 * An activation function based on the Gaussian function. The output range is
 * between 0 and 1. This activation function is used mainly for the HyperNeat
 * implementation.
 * 
 * A derivative is provided, so this activation function can be used with
 * propagation training.  However, its primary intended purpose is for
 * HyperNeat.  The derivative was obtained with the R statistical package.
 * 
 * If you are looking to implement a RBF-based neural network, see the 
 * RBFNetwork class.
 * 
 * The idea for this activation function was developed by  Ken Stanley, of  
 * the University of Texas at Austin.
 * http://www.cs.ucf.edu/~kstanley/
 */
public class ActivationGaussian implements ActivationFunction {


	/**
	 * The parameters.
	 */
	private double[] params;

	/**
	 * The serial id.
	 */
	private static final long serialVersionUID = -7166136514935838114L;

	public ActivationGaussian() {
		this.params = new double[0];
	}

	/**
	 * @return The object cloned.
	 */
	@Override
	public final ActivationFunction clone() {
		return new ActivationGaussian();
	}

	/**
	 * @return Return true, gaussian has a derivative.
	 */
	public final boolean hasDerivative() {
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void activationFunction(final double[] x, final int start,
			final int size) {

		for (int i = start; i < start + size; i++) {
			x[i] = BoundMath.exp(-Math.pow(2.5*x[i], 2.0));
		}

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final double derivativeFunction(final double b, final double a) {
		return Math.exp( Math.pow(2.5 * b,2.0) * 12.5 * b);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final String[] getParamNames() {
		final String[] result = {  };
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final double[] getParams() {
		return params;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void setParam(final int index, final double value) {
		this.params[index] = value;

	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getFactoryCode() {
		return ActivationUtil.generateActivationFactory(MLActivationFactory.AF_GAUSSIAN, this);
	}
	
	@Override
	public String getLabel() {
		return "gaussian";
	}
}
