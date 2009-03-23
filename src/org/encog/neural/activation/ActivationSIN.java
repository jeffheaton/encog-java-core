package org.encog.neural.activation;

import org.encog.neural.persist.Persistor;

public class ActivationSIN implements ActivationFunction {

	public void activationFunction(double[] d) {	
		for(int i=0;i<d.length;i++)
		{
			d[i] = Math.sin(d[i]);
		}
	}

	public void derivativeFunction(double[] d) {
		
		for(int i=0;i<d.length;i++)
		{
			d[i] = Math.cos(d[i]);
		}
	}
	
	public Object clone()
	{
		return new ActivationSIN();
	}
}
