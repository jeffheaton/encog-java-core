package org.encog.ml.data.versatile.sources;

/**
 * Defines a data source for the versatile data set.
 */
public interface VersatileDataSource {
	/**
	 * Read a line from the source.
	 * @return The values read.
	 */
	String[] readLine();
	
	/**
	 * Rewind the source back to the beginning.
	 */
	void rewind();
	
	/**
	 * Obtain the column index for the specified name.
	 * @param name The column name.
	 * @return The column index, or -1 if not found.
	 */
	int columnIndex(String name);
}
