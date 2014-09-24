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
package org.encog.neural.freeform;

import java.util.List;

/**
 * This interface defines a freeform neuron. By freeform that this neuron is not
 * necessarily part of a layer.
 */
public interface FreeformNeuron extends TempTrainingData {

	/**
	 * Add an input connection to this neuron.
	 * 
	 * @param inputConnection
	 *            The input connection.
	 */
	void addInput(FreeformConnection inputConnection);

	/**
	 * Add an output connection to this neuron.
	 * 
	 * @param outputConnection
	 *            The output connection.
	 */
	void addOutput(FreeformConnection outputConnection);

	/**
	 * @return The activation for this neuron. This is the final output after
	 *         the activation function has been applied.
	 */
	double getActivation();

	/**
	 * @return The input summation method.
	 */
	InputSummation getInputSummation();

	/**
	 * @return The outputs from this neuron.
	 */
	List<FreeformConnection> getOutputs();

	/**
	 * @return The output sum for this neuron. This is the output prior to the
	 *         activation function being applied.
	 */
	double getSum();

	/**
	 * @return True, if this is a bias neuron.
	 */
	boolean isBias();

	/**
	 * Perform the internal calculation for this neuron.
	 */
	void performCalculation();

	/**
	 * Set the activation, or final output for this neuron.
	 * @param activation THe activation.
	 */
	void setActivation(double activation);

	/**
	 * Determine if this neuron is a bias neuron.
	 * @param b True, if this neuron is considered a bias neuron.
	 */
	void setBias(boolean b);

	/**
	 * Set the input summation method.
	 * @param theInputSummation The input summation method.
	 */
	void setInputSummation(InputSummation theInputSummation);

	/**
	 * Update the context value for this neuron.
	 */
	void updateContext();
}
