package org.encog.ml.data.versatile;

/**
 * http://en.wikipedia.org/wiki/Level_of_measurement
 */
public enum ColumnType {
	/**
	 * A discrete nominal, or categorical, value specifies class membership.  For example, US states.
	 * There is a fixed number, yet no obvious, meaningful ordering.
	 */
	nominal,
	
	/**
	 * A discrete ordinal specifies a non-numeric value that has a specific ordering.  For example,
	 * the months of the year are inherently non-numerical, yet has a specific ordering.
	 */
	ordinal,
	
	/**
	 * A continuous (non-discrete) value is simply floating point numeric.  These values are 
	 * orderable and dense.
	 */
	continuous
}
