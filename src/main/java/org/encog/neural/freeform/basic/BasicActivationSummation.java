package org.encog.neural.freeform.basic;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.encog.engine.network.activation.ActivationFunction;
import org.encog.neural.freeform.FreeformConnection;
import org.encog.neural.freeform.InputSummation;

public class BasicActivationSummation implements InputSummation, Serializable {

	/**
	 * Serial id.
	 */
	private static final long serialVersionUID = 1L;
	private ActivationFunction activationFunction;
	private final List<FreeformConnection> inputs = new ArrayList<FreeformConnection>();
	private double sum;
	
	public BasicActivationSummation(ActivationFunction theActivationFunction) {
		this.activationFunction = theActivationFunction;
	}
	
	@Override
	public List<FreeformConnection> list() {
		return this.inputs;
	}
	
	@Override
	public double calculate() {		
		double[] sumArray = new double[1];
		this.sum = 0;
		
		// sum the input connections
		for(FreeformConnection connection: inputs) {
			connection.getSource().performCalculation();
			this.sum+=connection.getWeight() * connection.getSource().getActivation();			
		}
		
		// perform the activation function
		sumArray[0] = this.sum;
		this.activationFunction.activationFunction(sumArray, 0, sumArray.length);
		
		return sumArray[0]; 
	}

	public ActivationFunction getActivationFunction() {
		return activationFunction;
	}

	public void setActivationFunction(ActivationFunction activationFunction) {
		this.activationFunction = activationFunction;
	}

	@Override
	public void add(FreeformConnection connection) {
		this.inputs.add(connection);		
	}
	
	@Override
	public double getSum() {
		return this.sum;
	}

}
