package org.encog.mathutil.rbf;

import org.encog.engine.network.rbf.RadialBasisFunction;

/**
 * Basic radial basis function. Defines centers for each of the RBF's. All RBF's
 * have a common radius and peak.
 * 
 */
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
	public void setCenters(final double[] centers) {
		this.center = centers;

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setWidth(final double width) {
		this.width = width;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setPeak(final double peak) {
		this.peak = peak;

	}
}
