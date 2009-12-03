package org.encog.util.math.rbf;

public class GaussianFunctionMulti implements RadialBasisFunctionMulti {

	/**
	 * The center of the RBF.
	 */
	private final double[] center;
	
	/**
	 * The peak of the RBF.
	 */
	private final double peak;

	/**
	 * The width of the RBF.
	 */
	private final double[] width;
	
	public GaussianFunctionMulti(double peak, double[] center,double[] width)
	{
		this.center = center;
		this.peak = peak;
		this.width = width;
	}
	
	public GaussianFunctionMulti(int dimensions, double peak, double center,double width)
	{
		this.peak = peak;
		this.center = new double[dimensions];		
		this.width = new double[dimensions];
		for(int i=0;i<dimensions;i++)
		{
			this.center[i] = center;
			this.width[i] = width;
		}
	}
	
	
	@Override
	public double calculate(double[] x) {
		double value = 0;
		
		for(int i=0;i<center.length;i++) {
			value+=Math.pow(x[i] - this.center[i], 2)
			/ (2.0 * this.width[i] * this.width[i]);
		}		
		return this.peak * Math.exp(value);
	}

	@Override
	public double getCenter(int dimension) {
		return this.center[dimension];
	}

	@Override
	public int getDimensions() {
		return this.center.length;
	}

	@Override
	public double getPeak() {
		return this.peak;
	}

	@Override
	public double getWidth(int dimension) {
		return this.width[dimension];
	}

}
