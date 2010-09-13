/*
 * Encog(tm) Core v2.5 
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 * 
 * Copyright 2008-2010 by Heaton Research Inc.
 * 
 * Released under the LGPL.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 * 
 * Encog and Heaton Research are Trademarks of Heaton Research, Inc.
 * For information on Heaton Research trademarks, visit:
 * 
 * http://www.heatonresearch.com/copyright.html
 */
package org.encog.neural.data.buffer;

import java.io.File;

import org.encog.NullStatusReportable;
import org.encog.engine.StatusReportable;
import org.encog.neural.data.buffer.codec.DataSetCODEC;

/**
 * This class is used, together with a CODEC, to move data to/from the Encog
 * binary training file format. The same Encog binary files can be used on all
 * Encog platforms. CODEC's are used to import/export with other formats, such
 * as CSV.
 */
public class BinaryDataLoader {

	/**
	 * The CODEC to use.
	 */
	private DataSetCODEC codec;

	/**
	 * Used to report the status.
	 */
	private StatusReportable status = new NullStatusReportable();

	/**
	 * Construct a loader with the specified CODEC.
	 * 
	 * @param codec
	 *            The codec to use.
	 */
	public BinaryDataLoader(final DataSetCODEC codec) {
		this.codec = codec;
	}

	/**
	 * Convert an external file format, such as CSV, to the Encog binary
	 * training format.
	 * 
	 * @param binaryFile
	 *            The binary file to create.
	 */
	public void external2Binary(final File binaryFile) {

		status.report(0, 0, "Importing to binary file: "
				+ binaryFile.toString());

		EncogEGBFile egb = new EncogEGBFile(binaryFile);

		egb.create(codec.getInputSize(), codec.getIdealSize());

		double[] input = new double[this.codec.getInputSize()];
		double[] ideal = new double[this.codec.getIdealSize()];

		this.codec.prepareRead();

		int index = 3;
		int currentRecord = 0;
		int lastUpdate = 0;

		while (codec.read(input, ideal)) {

			egb.write(input);
			egb.write(ideal);

			index += input.length;
			index += ideal.length;
			currentRecord++;
			lastUpdate++;
			if (lastUpdate >= 10000) {
				lastUpdate = 0;
				this.status.report(0, currentRecord, "Importing...");
			}
		}

		egb.close();
		this.codec.close();
		status.report(0, 0, "Done importing to binary file: "
				+ binaryFile.toString());

	}

	/**
	 * Convert an Encog binary file to an external form, such as CSV.
	 * 
	 * @param binaryFile
	 *            THe binary file to use.
	 */
	public void binary2External(final File binaryFile) {
		status.report(0, 0, "Exporting binary file: " + binaryFile.toString());

		EncogEGBFile egb = new EncogEGBFile(binaryFile);
		egb.open();

		this.codec.prepareWrite(egb.getNumberOfRecords(), egb.getInputCount(),
				egb.getIdealCount());

		int inputCount = egb.getInputCount();
		int idealCount = egb.getIdealCount();

		double[] input = new double[inputCount];
		double[] ideal = new double[idealCount];

		int currentRecord = 0;
		int lastUpdate = 0;

		// now load the data
		for (int i = 0; i < egb.getNumberOfRecords(); i++) {

			for (int j = 0; j < inputCount; j++) {
				input[j] = egb.read();
			}

			for (int j = 0; j < idealCount; j++) {
				ideal[j] = egb.read();
			}

			this.codec.write(input, ideal);

			currentRecord++;
			lastUpdate++;
			if (lastUpdate >= 10000) {
				lastUpdate = 0;
				this.status.report(egb.getNumberOfRecords(), currentRecord,
						"Exporting...");
			}

		}

		egb.close();
		this.codec.close();
		status.report(0, 0, "Done exporting binary file: "
				+ binaryFile.toString());
	}

	/**
	 * @return The object that status is reported to.
	 */
	public StatusReportable getStatus() {
		return status;
	}

	/**
	 * Set the object that status is reported to.
	 * @param status THe object to report status to.
	 */
	public void setStatus(final StatusReportable status) {
		this.status = status;
	}

	/**
	 * @return The CODEC that is being used.
	 */
	public DataSetCODEC getCodec() {
		return codec;
	}

}
