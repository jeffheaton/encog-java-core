package org.encog.neural.activation;

public class ActivationLOG implements ActivationFunction {

	public void activationFunction(double[] d) {
		
		for(int i=0;i<d.length;i++)
		{
	        if (d[i] >= 0)
	            d[i] = Math.log(1 + d[i]);
	        else
	        	d[i] = -Math.log(1 - d[i]);
		}

	}

	public void derivativeFunction(double[] d) {
				
		for(int i=0;i<d.length;i++)
		{		
			if (d[i] >= 0)
		        d[i] = 1 / (1 + d[i]);
		    else
		    	d[i] = 1 / (1 - d[i]);
		}

	}

}
