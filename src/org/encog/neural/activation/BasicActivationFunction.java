package org.encog.neural.activation;

import org.encog.neural.persist.Persistor;

public abstract class BasicActivationFunction implements ActivationFunction
{	
	public Persistor createPersistor() {
		return null;
	}

	public String getDescription() {
		return null;
	}

	public String getName() {
		return null;
	}

	public void setDescription(String theDescription) {
		
	}

	public void setName(String theName) {
		
	}
	
	public Object clone()
	{
		return null;
	}

}
