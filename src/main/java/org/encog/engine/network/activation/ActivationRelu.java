package org.encog.engine.network.activation;


public class ActivationRelu implements ActivationFunction {

	@Override
	public void activationFunction(double[] x, int start, int size) {
		for (int i = start; i < start + size; i++) {
			x[i] = Math.max(0,x[i]);
		}
		
	}

	@Override
	public double derivativeFunction(double b, double a) {
		return (b<=0)?0:1;
	}

	@Override
	public boolean hasDerivative() {
		return true;
	}

	@Override
	public double[] getParams() {
		return new double[0];
	}

	@Override
	public void setParam(int index, double value) {
		
	}

	@Override
	public String[] getParamNames() {
		return new String[0];
	}

	@Override
	public ActivationFunction clone() {
		return new ActivationRelu();
	}

	@Override
	public String getFactoryCode() { 
		return "";
	}

}
