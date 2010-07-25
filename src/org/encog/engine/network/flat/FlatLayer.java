package org.encog.engine.network.flat;

public class FlatLayer {
	
	private final int activation;
	private final int count;
	private final boolean bias;
	private final double slope;
	
	public FlatLayer(int activation, int count, boolean bias)
	{
		this.activation = activation;
		this.count = count;
		this.bias = bias;
		this.slope = 1;
	}
	
	public FlatLayer(int activation, int count, boolean bias, double slope)
	{
		this.activation = activation;
		this.count = count;
		this.bias = bias;
		this.slope = slope;
	}

	/**
	 * @return the activation
	 */
	public int getActivation() {
		return activation;
	}

	/**
	 * @return the count
	 */
	public int getCount() {
		return count;
	}

	/**
	 * @return the bias
	 */
	public boolean isBias() {
		return bias;
	}
	
	public int getTotalCount()
	{
		return getCount() + (isBias()?1:0);
	}

	public double getSlope() {
		return slope;
	}
	
	
		
}
