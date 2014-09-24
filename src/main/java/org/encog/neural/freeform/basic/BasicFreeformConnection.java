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
package org.encog.neural.freeform.basic;

import java.io.Serializable;

import org.encog.neural.freeform.FreeformConnection;
import org.encog.neural.freeform.FreeformNeuron;

/**
 * A basic freeform connection.
 *
 */
public class BasicFreeformConnection implements FreeformConnection,
		Serializable {

	/**
	 * Serial id.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The connection weight.
	 */
	private double weight;
	
	/**
	 * The source neuron.
	 */
	private FreeformNeuron source;
	
	/**
	 * The target neuron.
	 */
	private FreeformNeuron target;
	
	/**
	 * Is this a recurrent link?
	 */
	private boolean recurrent;
	
	/**
	 * Temp training data.
	 */
	private double[] tempTraining;

	/**
	 * Construct a basic freeform connection.
	 * @param theSource The source neuron.
	 * @param theTarget The target neuron.
	 */
	public BasicFreeformConnection(final FreeformNeuron theSource,
			final FreeformNeuron theTarget) {
		this.recurrent = false;
		this.weight = 0.0;
		this.source = theSource;
		this.target = theTarget;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addTempTraining(final int i, final double value) {
		this.tempTraining[i] += value;

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addWeight(final double delta) {
		this.weight += delta;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void allocateTempTraining(final int l) {
		this.tempTraining = new double[l];

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void clearTempTraining() {
		this.tempTraining = null;

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public FreeformNeuron getSource() {
		return this.source;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public FreeformNeuron getTarget() {
		return this.target;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public double getTempTraining(final int index) {
		return this.tempTraining[index];
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public double getWeight() {
		return this.weight;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isRecurrent() {
		return this.recurrent;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setRecurrent(final boolean recurrent) {
		this.recurrent = recurrent;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setSource(final FreeformNeuron source) {
		this.source = source;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setTarget(final FreeformNeuron target) {
		this.target = target;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setTempTraining(final int index, final double value) {
		this.tempTraining[index] = value;

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setWeight(final double weight) {
		this.weight = weight;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		result.append("[BasicFreeformConnection: ");
		result.append("source=");
		result.append(this.source.toString());
		result.append(",target=");
		result.append(this.target.toString());
		result.append(",weight=");
		result.append(this.weight);
		result.append("]");
		return result.toString();
	}
}
