package org.encog.engine.network.flat;

import org.encog.engine.EncogEngine;
import org.encog.engine.util.EngineArray;

public class FlatLayer {
	
	private final int activation;
	private final int count;
	private final double biasActivation;
	private final double[] params;
	private FlatLayer contextFedBy;
	
	public FlatLayer(int activation, int count, double biasActivation, double[] params)
	{
		this.activation = activation;
		this.count = count;
		this.biasActivation = biasActivation;
		this.params = EngineArray.arrayCopy(params);
		this.contextFedBy = null;
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
		return  Math.abs(this.biasActivation)>EncogEngine.DEFAULT_ZERO_TOLERANCE;
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

	public double[] getParams() {
		return this.params;
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
		
		if( isBias())
			result.append(this.biasActivation);
		else
			result.append("false");
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

	public double getBiasActivation() {
		return this.biasActivation;
	}
}
