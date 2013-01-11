package org.encog.util.obj;

import java.io.Serializable;


public class ObjectHolder<T> implements Serializable {
	private final T obj;
	private final double probability;
	
	public ObjectHolder(T theObj, double probability) {
		this.obj = theObj;
		this.probability = probability;
	}

	/**
	 * @return the opp
	 */
	public T getObj() {
		return this.obj;
	}

	/**
	 * @return the probability
	 */
	public double getProbability() {
		return probability;
	}
}
