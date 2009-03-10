package org.encog.neural.activation;

import org.encog.util.math.GaussianFunction;

public class ActivationGaussian implements ActivationFunction {

	private GaussianFunction gausian;
	
	public ActivationGaussian(double center,double peak, double width)
	{
		this.gausian = new GaussianFunction(center,peak,width);
	}
	
	public void activationFunction(double[] d) {
		for(int i=0;i<d.length;i++)
		{
			d[i] = gausian.gaussian(d[i]);
		}
			
		
	}

	public void derivativeFunction(double[] d) {
		for(int i=0;i<d.length;i++)
		{
			d[i] = gausian.gaussianDerivative(d[i]);
		}
		
	}

}
