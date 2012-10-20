/*
 * Encog(tm) Core v3.2 - Java Version
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
package org.encog.neural.freeform.basic;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.encog.neural.freeform.FreeformConnection;
import org.encog.neural.freeform.FreeformNeuron;
import org.encog.neural.freeform.InputSummation;

public class BasicFreeformNeuron implements FreeformNeuron, Serializable {

	/**
	 * Serial id.
	 */
	private static final long serialVersionUID = 1L;
	
	private InputSummation inputSummation;
	private final List<FreeformConnection> outputConnections = new ArrayList<FreeformConnection>();
	private double activation;
	private boolean bias;
	private double[] tempTraining;
	
	public BasicFreeformNeuron(InputSummation theInputSummation) {
		this.inputSummation = theInputSummation;
	}
	
	@Override
	public void setActivation(double theActivation) {
		this.activation = theActivation;		
	}

	@Override
	public double getActivation() {
		return this.activation;
	}

	@Override
	public InputSummation getInputSummation() {
		return this.inputSummation;
	}
	
	@Override
	public void setInputSummation(InputSummation theInputSummation) {
		this.inputSummation = theInputSummation;
	}

	@Override
	public List<FreeformConnection> getOutputs() {
		return this.outputConnections;
	}

	@Override
	public void addInput(FreeformConnection connection) {
		this.inputSummation.add(connection);
		
	}

	@Override
	public void addOutput(FreeformConnection connection) {
		this.outputConnections.add(connection);		
	}

	@Override
	public void performCalculation() {
		// no inputs?  Just keep activation as is, probably a bias neuron.
		if( this.getInputSummation()==null ) {
			return;
		}
		
		this.activation = this.inputSummation.calculate();
	}

	@Override
	public boolean isBias() {
		return bias;
	}

	public void setBias(boolean bias) {
		this.bias = bias;
	}
	
	@Override
	public void clearTempTraining() {
		this.tempTraining = null;
		
	}

	@Override
	public void allocateTempTraining(int l) {
		this.tempTraining = new double[l];
		
	}

	@Override
	public void setTempTraining(int index, double value) {
		this.tempTraining[index] = value;
		
	}

	@Override
	public double getTempTraining(int index) {
		return this.tempTraining[index];
	}

	@Override
	public double getSum() {
		if( this.inputSummation==null ) {
			return this.activation;
		} else {
			return this.inputSummation.getSum();
		}
	}

	@Override
	public void addTempTraining(int i, double value) {
		this.tempTraining[i]+=value;
	}

	@Override
	public void updateContext() {
		// nothing to do for a non-context neuron
		
	}
	

}
