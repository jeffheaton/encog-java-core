package org.encog.engine.network.flat;

public class FlatLayer {
	
	private final int activation;
	private final int count;
	private final boolean bias;
	private final double slope;
	private FlatLayer contextFedBy;
	
	public FlatLayer(int activation, int count, boolean bias)
	{
		this.activation = activation;
		this.count = count;
		this.bias = bias;
		this.slope = 1;
		this.contextFedBy = null;
	}
	
	public FlatLayer(int activation, int count, boolean bias, FlatLayer contextFedBy)
	{
		this.activation = activation;
		this.count = count;
		this.bias = bias;
		this.slope = 1;
		this.contextFedBy = contextFedBy;
	}

	
	public FlatLayer(int activation, int count, boolean bias, double slope)
	{
		this.activation = activation;
		this.count = count;
		this.bias = bias;
		this.slope = slope;
		this.contextFedBy = null;
	}
	
	public FlatLayer(int activation, int count, boolean bias, double slope, FlatLayer contextFedBy)
	{
		this.activation = activation;
		this.count = count;
		this.bias = bias;
		this.slope = slope;
		this.contextFedBy = contextFedBy;
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
		if( this.contextFedBy==null)
			return getCount() + (isBias()?1:0);
		else
			return getCount() + (isBias()?1:0) + this.contextFedBy.getCount();
	}
	
	public int getContectCount()
	{
		if( this.contextFedBy==null)
			return 0;
		else
			return this.contextFedBy.getCount();
	}

	public double getSlope() {
		return slope;
	}

	public FlatLayer getContextFedBy() {
		return contextFedBy;
	}
	
	public String toString()
	{
		StringBuilder result = new StringBuilder();
		result.append("[FlatLayer: count=");
		result.append(this.count);
		result.append(",bias=");
		result.append(this.bias);
		if( this.contextFedBy!=null ) {
			result.append(",contextFed=");
			if( this.contextFedBy==this )
				result.append("itself");
			else
				result.append(this.contextFedBy);
		}
		result.append("]");
		return result.toString();
	}

	public void setContextFedBy(FlatLayer from) {
		this.contextFedBy = from;
	}
}
