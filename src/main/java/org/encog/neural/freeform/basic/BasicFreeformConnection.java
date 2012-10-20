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

import org.encog.neural.freeform.FreeformConnection;
import org.encog.neural.freeform.FreeformNeuron;

public class BasicFreeformConnection implements FreeformConnection, Serializable {
	
	/**
	 * Serial id.
	 */
	private static final long serialVersionUID = 1L;
	
	private double weight;
	private FreeformNeuron source;
	private FreeformNeuron target;
	private boolean recurrent;
	private double[] tempTraining;
	
	public BasicFreeformConnection(FreeformNeuron theSource, FreeformNeuron theTarget) {
		this.recurrent = false;
		this.weight = 0.0;
		this.source = theSource;
		this.target = theTarget;
	}

	@Override
	public double getWeight() {
		return weight;
	}

	@Override
	public void setWeight(double weight) {
		this.weight = weight;
	}

	@Override
	public FreeformNeuron getSource() {
		return source;
	}

	@Override
	public void setSource(FreeformNeuron source) {
		this.source = source;
	}

	@Override
	public FreeformNeuron getTarget() {
		return target;
	}

	@Override
	public void setTarget(FreeformNeuron target) {
		this.target = target;
	}
	
	@Override
	public boolean isRecurrent() {
		return recurrent;
	}

	@Override
	public void setRecurrent(boolean recurrent) {
		this.recurrent = recurrent;
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
	public void addTempTraining(int i, double value) {
		this.tempTraining[i]+=value;
		
	}

	@Override
	public void addWeight(double delta) {
		this.weight+=delta;
	}	
}
