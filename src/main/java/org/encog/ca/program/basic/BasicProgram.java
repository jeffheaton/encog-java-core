package org.encog.ca.program.basic;

import java.io.Serializable;

import org.encog.ca.program.CAProgram;


public abstract class BasicProgram implements CAProgram, Serializable  {
	
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
