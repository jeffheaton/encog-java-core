package org.encog.neural.data.buffer;

import org.encog.NullStatusReportable;
import org.encog.engine.StatusReportable;
import org.encog.neural.data.NeuralData;
import org.encog.neural.data.NeuralDataPair;
import org.encog.neural.data.NeuralDataSet;
import org.encog.neural.data.basic.BasicNeuralData;
import org.encog.neural.data.basic.BasicNeuralDataPair;
import org.encog.neural.data.basic.BasicNeuralDataSet;
import org.encog.neural.data.buffer.codec.DataSetCODEC;

/**
 * This class is used, together with a CODEC, load training data from some
 * external file into an Encog memory-based training set.
 */
public class MemoryDataLoader {
	/**
	 * The CODEC to use.
	 */
	private DataSetCODEC codec;

	/**
	 * Used to report the status.
	 */
	private StatusReportable status;

	/**
	 * The dataset to load into.
	 */
	private BasicNeuralDataSet result;

	/**
	 * Construct a loader with the specified CODEC.
	 * 
	 * @param codec
	 *            The codec to use.
	 */
	public MemoryDataLoader(DataSetCODEC codec) {
		this.codec = codec;
		this.status = new NullStatusReportable();
	}

	/**
	 * Convert an external file format, such as CSV, to an Encog memory training
	 * set.
	 * 
	 * @return The binary file to create.
	 */
	public NeuralDataSet External2Memory() {
		status.report(0, 0, "Importing to memory");

		if (result != null) {
			this.result = new BasicNeuralDataSet();
		}

		double[] input = new double[this.codec.getInputSize()];
		double[] ideal = new double[this.codec.getIdealSize()];

		this.codec.prepareRead();

		int currentRecord = 0;
		int lastUpdate = 0;

		while (codec.read(input, ideal)) {
			NeuralData a = null, b = null;

			a = new BasicNeuralData(input);

			if (codec.getIdealSize() > 0)
				b = new BasicNeuralData(ideal);

			NeuralDataPair pair = new BasicNeuralDataPair(a, b);
			result.add(pair);

			currentRecord++;
			lastUpdate++;
			if (lastUpdate >= 10000) {
				lastUpdate = 0;
				this.status.report(0, currentRecord, "Importing...");
			}
		}

		this.codec.close();
		status.report(0, 0, "Done importing to memory");
		return result;
	}

	public StatusReportable getStatus() {
		return status;
	}

	public void setStatus(StatusReportable status) {
		this.status = status;
	}

	public DataSetCODEC getCodec() {
		return codec;
	}

	public BasicNeuralDataSet getResult() {
		return result;
	}

	public void setResult(BasicNeuralDataSet result) {
		this.result = result;
	}

}
