package org.encog.neural.hyperneat.substrate;

import java.util.Arrays;

public class SubstrateNode {
	private final int id;
	private final double[] location;
	
	public SubstrateNode(int theID, int size) {
		this.id = theID;
		this.location = new double[size];
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @return the location
	 */
	public double[] getLocation() {
		return location;
	}
	
	public int size() {
		return this.location.length;
	}
	
	public String toString() {
		StringBuilder result = new StringBuilder();
		result.append("[SubstrateNode: id=");
		result.append(this.id);
		result.append(", pos=");
		result.append(Arrays.toString(location));
		result.append("]");
		return result.toString();
	}
}
