package org.encog.ml.world.grid;

import org.encog.ml.world.basic.BasicState;
import org.encog.util.Format;

public class GridState extends BasicState {
	
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
	
	public String toString() {
		StringBuilder result = new StringBuilder();
		result.append("[GridState: row=");
		result.append(this.row);
		result.append(", col=");
		result.append(this.column);
		result.append("]");
		return result.toString();
	}
	
}
