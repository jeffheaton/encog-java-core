package org.encog.persist.map;

import java.util.Arrays;

import org.encog.Encog;
import org.encog.engine.network.activation.ActivationFunction;
import org.encog.util.csv.CSVFormat;

public class PersistedActivationFunction extends PersistedProperty {
	private ActivationFunction activationFunction;
	
	public PersistedActivationFunction(ActivationFunction a)
	{
		super(false);
		this.activationFunction = a;
	}
	
	public String toString()
	{
		return activationFunction.toString();
	}
	
	public String getString()
	{
		return toString();
	}

	@Override
	public Object getData() {
		return activationFunction;
	}

	/**
	 * @return the activationFunction
	 */
	public ActivationFunction getActivationFunction() {
		return activationFunction;
	}

	
}
