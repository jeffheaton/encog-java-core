/*
 * Encog(tm) Core v3.1 - Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 
 * Copyright 2008-2012 Heaton Research, Inc.
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
package org.encog.ml.data.buffer;

import org.encog.NullStatusReportable;
import org.encog.StatusReportable;
import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.basic.BasicMLData;
import org.encog.ml.data.basic.BasicMLDataPair;
import org.encog.ml.data.basic.BasicMLDataSet;
import org.encog.ml.data.buffer.codec.DataSetCODEC;

/**
 * This class is used, together with a CODEC, load training data from some
 * external file into an Encog memory-based training set.
 */
public class MemoryDataLoader {
	/**
	 * The CODEC to use.
	 */
	private final DataSetCODEC codec;

	/**
	 * Used to report the status.
	 */
	private StatusReportable status;

	/**
	 * The dataset to load into.
	 */
	private BasicMLDataSet result;

	/**
	 * Construct a loader with the specified CODEC.
	 * 
	 * @param theCodec
	 *            The codec to use.
	 */
	public MemoryDataLoader(final DataSetCODEC theCodec) {
		this.codec = theCodec;
		this.status = new NullStatusReportable();
	}

	/**
	 * Convert an external file format, such as CSV, to an Encog memory training
	 * set.
	 * 
	 * @return The binary file to create.
	 */
	public final MLDataSet external2Memory() {
		this.status.report(0, 0, "Importing to memory");

		if (this.result == null) {
			this.result = new BasicMLDataSet();
		}

		final double[] input = new double[this.codec.getInputSize()];
		final double[] ideal = new double[this.codec.getIdealSize()];
		final double[] significance = new double[1];

		this.codec.prepareRead();

		int currentRecord = 0;
		int lastUpdate = 0;

		while (this.codec.read(input, ideal, significance)) {
			MLData a = null, b = null;

			a = new BasicMLData(input);

			if (this.codec.getIdealSize() > 0) {
				b = new BasicMLData(ideal);
			}

			final MLDataPair pair = new BasicMLDataPair(a, b);
			pair.setSignificance(significance[0]);
			this.result.add(pair);

			currentRecord++;
			lastUpdate++;
			if (lastUpdate >= 10000) {
				lastUpdate = 0;
				this.status.report(0, currentRecord, "Importing...");
			}
		}

		this.codec.close();
		this.status.report(0, 0, "Done importing to memory");
		return this.result;
	}

	/**
	 * @return The CODEC that is being used.
	 */
	public final DataSetCODEC getCodec() {
		return this.codec;
	}

	/**
	 * @return The resuling dataset.
	 */
	public final BasicMLDataSet getResult() {
		return this.result;
	}

	/**
	 * @return The object that status is reported to.
	 */
	public final StatusReportable getStatus() {
		return this.status;
	}

	/**
	 * Set the resulting dataset.
	 * 
	 * @param theResult
	 *            The resulting dataset.
	 */
	public final void setResult(final BasicMLDataSet theResult) {
		this.result = theResult;
	}

	/**
	 * Set the object that status will be reported to.
	 * 
	 * @param theStatus
	 *            The object to report status to.
	 */
	public final void setStatus(final StatusReportable theStatus) {
		this.status = theStatus;
	}

}
