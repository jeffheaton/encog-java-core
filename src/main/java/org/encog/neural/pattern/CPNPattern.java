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
package org.encog.neural.pattern;

import org.encog.engine.network.activation.ActivationFunction;
import org.encog.ml.MLMethod;
import org.encog.neural.cpn.CPN;

/**
 * Pattern that creates a CPN neural network.
 */
public class CPNPattern implements NeuralNetworkPattern {

	/**
	 * The tag for the INSTAR layer.
	 */
	public static final String TAG_INSTAR = "INSTAR";

	/**
	 * The tag for the OUTSTAR layer.
	 */
	public static final String TAG_OUTSTAR = "OUTSTAR";

	/**
	 * The number of neurons in the instar layer.
	 */
	private int instarCount;

	/**
	 * The number of neurons in the outstar layer.
	 */
	private int outstarCount;

	/**
	 * The number of neurons in the hidden layer.
	 */
	private int inputCount;


	/**
	 * Not used, will throw an error. CPN networks already have a predefined
	 * hidden layer called the instar layer.
	 * 
	 * @param count
	 *            NOT USED
	 */
	public final void addHiddenLayer(final int count) {
		throw new PatternError("A CPN already has a predefined hidden layer.  No additional"
				+ "specification is needed.");
	}

	/**
	 * Clear any parameters that were set.
	 */
	public final void clear() {
		this.inputCount = 0;
		this.instarCount = 0;
		this.outstarCount = 0;
	}

	/**
	 * Generate the network.
	 * 
	 * @return The generated network.
	 */
	public final MLMethod generate() {
		return new CPN(inputCount,instarCount,outstarCount,1);
	}

	/**
	 * This method will throw an error. The CPN network uses predefined
	 * activation functions.
	 * 
	 * @param activation
	 *            NOT USED
	 */
	public final void setActivationFunction(final ActivationFunction activation) {
		throw new PatternError("A CPN network will use the BiPolar & competitive activation "
				+ "functions, no activation function needs to be specified.");
	}

	/**
	 * Set the number of input neurons.
	 * 
	 * @param count
	 *            The input neuron count.
	 */
	public final void setInputNeurons(final int count) {
		this.inputCount = count;

	}

	/**
	 * Set the number of neurons in the instar layer. This level is essentially
	 * a hidden layer.
	 * 
	 * @param instarCount
	 *            The instar count.
	 */
	public final void setInstarCount(final int instarCount) {
		this.instarCount = instarCount;
	}

	/**
	 * Set the number of output neurons. Calling this method maps to setting the
	 * number of neurons in the outstar layer.
	 * 
	 * @param count
	 *            The count.
	 */
	public final void setOutputNeurons(final int count) {
		this.outstarCount = count;

	}

	/**
	 * Set the number of neurons in the outstar level, this level is mapped to
	 * the "output" level.
	 * 
	 * @param outstarCount
	 *            The outstar count.
	 */
	public final void setOutstarCount(final int outstarCount) {
		this.outstarCount = outstarCount;
	}

}
