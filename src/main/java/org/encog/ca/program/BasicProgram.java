package org.encog.ca.program;

public class BasicProgram  {
	
	private final Movement[] movements;
	
	public BasicProgram(Movement[] theMovements) {
		this.movements = theMovements;
	}

	/**
	 * @return the movements
	 */
	public Movement[] getMovements() {
		return movements;
	}
	
	
}
