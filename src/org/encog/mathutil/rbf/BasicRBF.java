package org.encog.mathutil.rbf;

import org.encog.engine.network.rbf.RadialBasisFunction;

public abstract class BasicRBF implements RadialBasisFunction {
	
	/**
	 * The center of the RBF.
	 */
	private double[] center;

	/**
	 * The peak of the RBF.
	 */
	private double peak;

	/**
	 * The width of the RBF.
	 */
	private double width;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public double getCenter(int dimension) {
		return this.center[dimension];
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public double[] getCenters() {
		return this.center;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getDimensions() {
		return this.center.length;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public double getPeak() {
		return this.peak;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public double getWidth() {
		return this.width;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setCenters(double[] centers) {
		this.center = centers;
		
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setWidth(double width) {
		this.width = width;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setPeak(double peak) {
		this.peak = peak;
		
	}
}
