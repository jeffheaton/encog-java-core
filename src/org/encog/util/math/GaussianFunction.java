package org.encog.util.math;

public class GaussianFunction {
	
	private double center;
	private double peak;
	private double width;
	
	public GaussianFunction(double center,double peak,double width)
	{
		this.center = center;
		this.peak = peak;
		this.width = width;
	}
	
	public double gaussian(double x)
	{
		return (this.peak*MathConst.EULERS_NUMBER)*(-(Math.pow(x-this.center, 2.0)/2.0*Math.pow(this.width, 2.0)));
	}
	
	public double gaussianDerivative(double x) {
		// TODO Auto-generated method stub
		return 0;
	}

	public double getCenter() {
		return center;
	}

	public double getPeak() {
		return peak;
	}

	public double getWidth() {
		return width;
	}

}
