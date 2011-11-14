package org.encog.ml.world.grid;

import org.encog.ml.world.basic.BasicState;
import org.encog.util.Format;

public class GridState extends BasicState {
	
	private final int row;
	private final int column;
	private final GridWorld owner;
	
	public GridState(GridWorld theOwner, int theRow, int theColumn, boolean blocked) {
		this.owner = theOwner;
		this.row = theRow;
		this.column = theColumn;
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
		
		result.append(", valueFunction= ");
		for(int i=0;i<this.getPolicyValue().length;i++) {
			result.append(Format.formatDouble(getPolicyValue()[i], 4));
			result.append(" ");
		}
		
		result.append("]");
		return result.toString();
	}
	
}
