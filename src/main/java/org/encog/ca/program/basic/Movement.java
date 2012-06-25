package org.encog.ca.program.basic;

public class Movement {
	private final int rowMovement;
	private final int columnmMovement;
	
	public static final Movement[] MOVE_4WAY = { 
		new Movement(-1,0), // N
		new Movement(0,1), // E
		new Movement(1,0), // S
		new Movement(0,-1) // W
	};
	
	public static final Movement[] MOVE_8WAY = { 
		new Movement(-1,0), // N
		new Movement(-1,1), // NE
		new Movement(0,1), // E
		new Movement(1,1), // SE
		new Movement(1,0), // S
		new Movement(1,-1), // SW
		new Movement(0,-1), // W
		new Movement(-1,-1) // NW
	};
	
	public Movement(int theRowMovement, int theColumnMovement) {
		this.rowMovement = theRowMovement;
		this.columnmMovement = theColumnMovement;
	}

	/**
	 * @return the rowMovement
	 */
	public int getRowMovement() {
		return rowMovement;
	}

	/**
	 * @return the columnmMovement
	 */
	public int getColumnmMovement() {
		return columnmMovement;
	}
	
	
}
