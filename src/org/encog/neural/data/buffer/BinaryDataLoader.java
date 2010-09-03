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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.DoubleBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;

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

	private StatusReportable status = new NullStatusReportable();

	/**
	 * Construct a loader with the specified CODEC.
	 * 
	 * @param codec
	 *            The codec to use.
	 */
	public BinaryDataLoader(DataSetCODEC codec) {
		this.codec = codec;
	}

	/**
	 * Convert an external file format, such as CSV, to the Encog binary
	 * training format.
	 * 
	 * @param binaryFile
	 *            The binary file to create.
	 */
	public void external2Binary(File binaryFile) {
		try {
			status.report(0, 0, "Importing to binary file: "
					+ binaryFile.toString());
			double[] input = new double[this.codec.getInputSize()];
			double[] ideal = new double[this.codec.getIdealSize()];

			RandomAccessFile fos = new RandomAccessFile(binaryFile, "rw");
			FileChannel fc = fos.getChannel();
			ByteBuffer bb = ByteBuffer
					.allocate(EncogEGBFile.HEADER_SIZE);
			bb.order(ByteOrder.LITTLE_ENDIAN);

			bb.put((byte) 'E');
			bb.put((byte) 'N');
			bb.put((byte) 'C');
			bb.put((byte) 'O');
			bb.put((byte) 'G');
			bb.put((byte) '-');
			bb.put((byte) '0');
			bb.put((byte) '0');

			bb.putDouble(input.length);
			bb.putDouble(ideal.length);

			bb.flip();
			fc.write(bb);
			
			this.codec.prepareRead();

			// now transfer the file. we use a new byte buffer for each record.
			// This allows us to not need to know the size of the data being
			// imported. If we determined the size ahead of time, we could use
			// fewer file channels, and likely increase performance. However,
			// write will not be done that often. Read is where we will really
			// attempt to optimize.
			int index = 3;
			int currentRecord = 0;
			int lastUpdate = 0;

			bb = ByteBuffer.allocate((input.length + ideal.length)
					* EncogEGBFile.DOUBLE_SIZE);

			while (codec.read(input, ideal)) {

				bb.clear();
				bb.order(ByteOrder.LITTLE_ENDIAN);
				for (int i = 0; i < input.length; i++) {
					bb.putDouble(input[i]);
				}

				for (int i = 0; i < ideal.length; i++) {
					bb.putDouble(ideal[i]);
				}
				bb.flip();
				fc.write(bb);
				
				index += input.length;
				index += ideal.length;
				currentRecord++;
				lastUpdate++;
				if (lastUpdate >= 10000) {
					lastUpdate = 0;
					this.status.report(0, currentRecord, "Importing...");
				}
			}

			fc.close();
			fos.close();
			this.codec.close();
			status.report(0, 0, "Done importing to binary file: "
					+ binaryFile.toString());
		} catch (IOException ex) {
			throw new BufferedDataError(ex);
		}
	}

	/**
	 * Convert an Encog binary file to an external form, such as CSV.
	 * 
	 * @param binaryFile
	 */
	public void binary2External(File binaryFile) {
		try {
			status.report(0, 0, "Exporting binary file: "
					+ binaryFile.toString());
			FileInputStream fis = new FileInputStream(binaryFile);
			FileChannel fc = fis.getChannel();

			ByteBuffer bb = ByteBuffer.allocate(EncogEGBFile.HEADER_SIZE);			
			bb.order(ByteOrder.LITTLE_ENDIAN);

			boolean isEncogFile = true;
			
			fc.read(bb);
			bb.position(0);

			isEncogFile = isEncogFile ? bb.get() == 'E' : false;
			isEncogFile = isEncogFile ? bb.get() == 'N' : false;
			isEncogFile = isEncogFile ? bb.get() == 'C' : false;
			isEncogFile = isEncogFile ? bb.get() == 'O' : false;
			isEncogFile = isEncogFile ? bb.get() == 'G' : false;
			isEncogFile = isEncogFile ? bb.get() == '-' : false;

			if (!isEncogFile)
				throw new BufferedDataError(
						"File is not a valid Encog binary file:"
								+ binaryFile.toString());

			char v1 = (char) bb.get();
			char v2 = (char) bb.get();
			String versionStr = "" + v1 + v2;

			try {
				int version = Integer.parseInt(versionStr);
				if (version > 0)
					throw new BufferedDataError(
							"File is from a newer version of Encog than is currently in use.");
			} catch (NumberFormatException ex) {
				throw new BufferedDataError("File has invalid version number.");
			}

			int inputSize = (int) bb.getDouble();
			int idealSize = (int) bb.getDouble();

			int recordSize = inputSize + idealSize;

			int recordCount = (int) ((binaryFile.length() - (EncogEGBFile.DOUBLE_SIZE * 3)) / (EncogEGBFile.DOUBLE_SIZE * recordSize));

			double[] input = new double[inputSize];
			double[] ideal = new double[idealSize];
			
			bb = ByteBuffer.allocate((input.length + ideal.length)
					* EncogEGBFile.DOUBLE_SIZE);

			this.codec.prepareWrite(recordCount, inputSize, idealSize);

			int currentRecord = 0;
			int lastUpdate = 0;

			// now load the data
			for (int i = 0; i < recordCount; i++) {
				
				bb.clear();
				bb.order(ByteOrder.LITTLE_ENDIAN);
				fc.read(bb);
				bb.position(0);
				
				for (int j = 0; j < inputSize; j++) {
					input[j] = bb.getDouble();
				}

				for (int j = 0; j < idealSize; j++) {
					ideal[j] = bb.getDouble();
				}

				this.codec.write(input, ideal);

				currentRecord++;
				lastUpdate++;
				if (lastUpdate >= 10000) {
					lastUpdate = 0;
					this.status.report(recordCount, currentRecord,
							"Exporting...");
				}

			}

			fc.close();
			fis.close();
			this.codec.close();
			status.report(0, 0, "Done exporting binary file: "
					+ binaryFile.toString());
		} catch (IOException ex) {
			throw new BufferedDataError(ex);
		}
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

}
