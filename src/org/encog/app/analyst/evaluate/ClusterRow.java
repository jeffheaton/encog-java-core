package org.encog.app.analyst.evaluate;

import org.encog.app.csv.basic.LoadedRow;
import org.encog.neural.data.NeuralData;
import org.encog.neural.data.basic.BasicNeuralData;
import org.encog.neural.data.basic.BasicNeuralDataPair;

public class ClusterRow extends BasicNeuralDataPair {

	public ClusterRow(double[] input, LoadedRow row) {
		super(new BasicNeuralData(input));
		this.row = row;
	}

	final LoadedRow row;	

	/**
	 * @return the row
	 */
	public LoadedRow getRow() {
		return row;
	}
}
