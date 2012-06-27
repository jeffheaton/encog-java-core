package org.encog.ca.program.basic;

import org.encog.ca.program.CAProgram;


public abstract class BasicProgram implements CAProgram  {
	
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
