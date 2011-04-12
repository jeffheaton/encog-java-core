package org.encog.app.analyst.evaluate;

import org.encog.app.csv.basic.LoadedRow;
import org.encog.neural.data.basic.BasicNeuralData;
import org.encog.neural.data.basic.BasicNeuralDataPair;

/**
 * Holds input data and the CSV row for a cluster item.
 */
public class ClusterRow extends BasicNeuralDataPair {

	/**
	 * The serial id.
	 */
	private static final long serialVersionUID = -6542401157014650211L;

	/**
	 * The loaded row of data.
	 */
	 private final LoadedRow row;

	 /**
	  * Construct the cluster row.
	  * @param input The input data.
	  * @param theRow The CSV row.
	  */
	public ClusterRow(final double[] input, final LoadedRow theRow) {
		super(new BasicNeuralData(input));
		this.row = theRow;
	}

	/**
	 * @return the row
	 */
	public final LoadedRow getRow() {
		return this.row;
	}
}
