/*
 * Encog(tm) Core v3.0 - Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 
 * Copyright 2008-2011 Heaton Research, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *   
 * For more information on Heaton Research copyrights, licenses 
 * and trademarks visit:
 * http://www.heatonresearch.com/copyright
 */
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
	public NeuralDataSet external2Memory() {
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
