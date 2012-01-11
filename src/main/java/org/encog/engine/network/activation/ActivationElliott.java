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
package org.encog.engine.network.activation;

/**
 * Computationally efficient alternative to ActivationSigmoid.
 * Its output is in the range [0, 1], and it is derivable.
 * 
 * It will approach the 0 and 1 more slowly than Sigmoid so it 
 * might be more suitable to classification tasks than predictions tasks.
 * 
 * Elliott, D.L. "A better activation function for artificial neural networks", 1993
 * http://citeseerx.ist.psu.edu/viewdoc/download?doi=10.1.1.46.7204&rep=rep1&type=pdf
 */
public class ActivationElliott implements ActivationFunction {

    /**
     * Serial id for this class.
     */
    private static final long serialVersionUID = 1234L;

    /**
     * The parameters.
     */
    private final double[] params;

    /**
     * Construct a basic HTAN activation function, with a slope of 1.
     */
    public ActivationElliott() {
        this.params = new double[1];
        this.params[0] = 1.0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void activationFunction(final double[] x, final int start,
            final int size) {
        for (int i = start; i < start + size; i++) {
        	double s = params[0];
        	x[i] = ((x[i]*s) / 2) / (1 + Math.abs(x[i]*s)) + 0.5;        			
        }
    }

    /**
     * @return The object cloned;
     */
    @Override
    public final ActivationFunction clone() {
        return new ActivationElliott();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final double derivativeFunction(final double b, final double a) {
    	double s = params[0];
    	return s/(2.0*(1.0+Math.abs(b*s))*(1+Math.abs(b*s)));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final String[] getParamNames() {
        final String[] result = { "Slope" };
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
     * @return Return true, Elliott activation has a derivative.
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

}
