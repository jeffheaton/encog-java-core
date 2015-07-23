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

import org.encog.Encog;
import org.encog.mathutil.BoundMath;
import org.encog.ml.factory.MLActivationFactory;
import org.encog.util.obj.ActivationUtil;

/**
 * The softmax activation function.
 * 
 * @author jheaton
 */
public class ActivationSoftMax implements ActivationFunction {

	/**
	 * The serial id.
	 */
	private static final long serialVersionUID = -960489243250457611L;

	/**
	 * The parameters.
	 */
	private final double[] params;

	/**
	 * Construct the soft-max activation function.
	 */
	public ActivationSoftMax() {
		this.params = new double[0];
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void activationFunction(final double[] x, final int start,
			final int size) {
		double sum = 0;
		for (int i = start; i < start + size; i++) {
			x[i] = BoundMath.exp(x[i]);
			sum += x[i];
		}
		if(Double.isNaN(sum) || sum <Encog.DEFAULT_DOUBLE_EQUAL ) {
			for (int i = start; i < start + size; i++) {
				x[i] = 1.0/size;
			}
		} else {
			for (int i = start; i < start + size; i++) {
				x[i] = x[i] / sum;
			}
		}
	}

	/**
	 * @return The object cloned;
	 */
	@Override
	public final ActivationFunction clone() {
		return new ActivationSoftMax();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final double derivativeFunction(final double b, final double a) {
		return a * (1.0 - a);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final String[] getParamNames() {
		final String[] result = {};
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final double[] getParams() {
		return this.params;
	}

	/**
	 * @return Return false, softmax has no derivative.
	 */
	@Override
	public final boolean hasDerivative() {
		return true;
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
		return ActivationUtil.generateActivationFactory(MLActivationFactory.AF_SOFTMAX, this);
	}

	@Override
	public String getLabel() {
		return "softmax";
	}
}
