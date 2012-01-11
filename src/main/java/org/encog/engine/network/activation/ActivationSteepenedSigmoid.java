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
 * The Steepened Sigmoid is an activation function typically used with NEAT.
 * 
 * Valid derivative calculated with the R package, so this does work with 
 * non-NEAT networks too.
 * 
 * It was developed by  Ken Stanley while at The University of Texas at Austin.
 * http://www.cs.ucf.edu/~kstanley/
 */
public class ActivationSteepenedSigmoid implements ActivationFunction {

    /**
     * The parameters.
     */
    private final double[] params;

    /**
     * Construct a basic HTAN activation function, with a slope of 1.
     */
    public ActivationSteepenedSigmoid() {
        this.params = new double[0];
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void activationFunction(final double[] x, final int start,
            final int size) {
        for (int i = start; i < start + size; i++) {
        	x[i] = 1.0/(1.0 + Math.exp(-4.9*x[i]));
        }
    }

    /**
     * @return The object cloned;
     */
    @Override
    public final ActivationFunction clone() {
        return new ActivationSteepenedSigmoid();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final double derivativeFunction(final double b, final double a) {
    	double s = Math.exp(-4.9 * a);
    	return Math.pow(s * 4.9/(1 + s),2);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final String[] getParamNames() {
        final String[] result = { };
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
