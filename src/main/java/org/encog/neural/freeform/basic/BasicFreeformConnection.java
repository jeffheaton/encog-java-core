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
