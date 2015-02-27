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
import java.util.ArrayList;
import java.util.List;

import org.encog.neural.freeform.FreeformConnection;
import org.encog.neural.freeform.FreeformNeuron;
import org.encog.neural.freeform.InputSummation;

/**
 * This class provides a basic implementation of a freeform neuron.
 */
public class BasicFreeformNeuron implements FreeformNeuron, Serializable {

	/**
	 * Serial id.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The input summation.
	 */
	private InputSummation inputSummation;
	
	/**
	 * THe output connections.
	 */
	private final List<FreeformConnection> outputConnections = new ArrayList<FreeformConnection>();
	
	/**
	 * The activation.
	 */
	private double activation;
	
	/**
	 * True if this neuron is a bias neuron.
	 */
	private boolean bias;
	
	/**
	 * Temp training values.
	 */
	private double[] tempTraining;

	public BasicFreeformNeuron(final InputSummation theInputSummation) {
		this.inputSummation = theInputSummation;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addInput(final FreeformConnection connection) {
		this.inputSummation.add(connection);

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addOutput(final FreeformConnection connection) {
		this.outputConnections.add(connection);
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
	public double getActivation() {
		return this.activation;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public InputSummation getInputSummation() {
		return this.inputSummation;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<FreeformConnection> getOutputs() {
		return this.outputConnections;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public double getSum() {
		if (this.inputSummation == null) {
			return this.activation;
		} else {
			return this.inputSummation.getSum();
		}
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
	public boolean isBias() {
		return this.bias;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void performCalculation() {
		// no inputs? Just keep activation as is, probably a bias neuron.
		if (getInputSummation() == null) {
			return;
		}

		this.activation = this.inputSummation.calculate();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setActivation(final double theActivation) {
		this.activation = theActivation;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setBias(final boolean bias) {
		this.bias = bias;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setInputSummation(final InputSummation theInputSummation) {
		this.inputSummation = theInputSummation;
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
	public void updateContext() {
		// nothing to do for a non-context neuron

	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		result.append("[BasicFreeformNeuron: ");
		result.append("inputCount=");
		if( this.inputSummation==null ) {
			result.append("null");
		} else {
			result.append(this.inputSummation.list().size());
		}
		result.append(",outputCount=");
		result.append(this.outputConnections.size());
		result.append("]");
		return result.toString();
	}

}
