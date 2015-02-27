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

/**
 * Defines a freeform connection between neurons.
 *
 */
public interface FreeformConnection extends TempTrainingData {

	/**
	 * Add to the connection weight.
	 * @param delta THe value to add.
	 */
	void addWeight(double delta);

	/**
	 * @return The source neuron.
	 */
	FreeformNeuron getSource();

	/**
	 * @return The target neuron.
	 */
	FreeformNeuron getTarget();

	/**
	 * @return The weight.
	 */
	double getWeight();

	/**
	 * @return Is this a recurrent connection?
	 */
	boolean isRecurrent();

	/**
	 * Determine if this is a recurrent connecton.
	 * @param recurrent True, if this is a recurrent connection.
	 */
	void setRecurrent(boolean recurrent);

	/**
	 * Set the source neuron.
	 * @param source The source neuron.
	 */
	void setSource(FreeformNeuron source);

	/**
	 * Set the target neuron.
	 * @param target The target neuron.
	 */
	void setTarget(FreeformNeuron target);

	/**
	 * Set the weight.
	 * @param weight The weight.
	 */
	void setWeight(double weight);

}
