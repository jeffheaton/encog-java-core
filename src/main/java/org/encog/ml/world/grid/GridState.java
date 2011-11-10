package org.encog.ml.world.grid;

public class GridState {
	
	private final int row;
	private final int column;
	private final GridWorld owner;
	private boolean blocked;
	
	public GridState(GridWorld theOwner, int theRow, int theColumn, boolean blocked) {
		this.owner = theOwner;
		this.row = theRow;
		this.column = theColumn;
	}

	/**
	 * @return the blocked
	 */
	public boolean isBlocked() {
		return blocked;
	}

	/**
	 * @param blocked the blocked to set
	 */
	public void setBlocked(boolean blocked) {
		this.blocked = blocked;
	}

	/**
	 * @return the row
	 */
	public int getRow() {
		return row;
	}

	/**
	 * @return the column
	 */
	public int getColumn() {
		return column;
	}

	/**
	 * @return the owner
	 */
	public GridWorld getOwner() {
		return owner;
	}
	
	
}
