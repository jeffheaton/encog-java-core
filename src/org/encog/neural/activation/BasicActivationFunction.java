package org.encog.neural.activation;

import org.encog.neural.persist.Persistor;

public abstract class BasicActivationFunction implements ActivationFunction
{	
	@Override
	public Persistor createPersistor() {
		return null;
	}

	@Override
	public String getDescription() {
		return null;
	}

	@Override
	public String getName() {
		return null;
	}

	@Override
	public void setDescription(String theDescription) {
		
	}

	@Override
	public void setName(String theName) {
		
	}
	
	public Object clone()
	{
		return null;
	}

}
