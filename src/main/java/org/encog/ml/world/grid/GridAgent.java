package org.encog.ml.world.grid;

import org.encog.ml.world.WorldAgent;

public class GridAgent implements WorldAgent {
	private int currentRow;
	private int currentColumn;
	/**
	 * @return the currentRow
	 */
	public int getCurrentRow() {
		return currentRow;
	}
	/**
	 * @param currentRow the currentRow to set
	 */
	public void setCurrentRow(int currentRow) {
		this.currentRow = currentRow;
	}
	/**
	 * @return the currentColumn
	 */
	public int getCurrentColumn() {
		return currentColumn;
	}
	/**
	 * @param currentColumn the currentColumn to set
	 */
	public void setCurrentColumn(int currentColumn) {
		this.currentColumn = currentColumn;
	}
	
	
}
