package org.encog.neural.hyperneat.substrate;

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
}
