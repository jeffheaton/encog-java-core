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
	 * Construct a loader with the specified CODEC.
	 * @param codec The codec to use.
	 */
	public BinaryDataLoader(DataSetCODEC codec) {
		this.codec = codec;
	}

	/**
	 * Convert an external file format, such as CSV, to the Encog binary training format.
	 * @param binaryFile The binary file to create.
	 */
	public void external2Binary(File binaryFile) {
		try {
			double[] input = new double[this.codec.getInputSize()];
			double[] ideal = new double[this.codec.getIdealSize()];

			RandomAccessFile fos = new RandomAccessFile(binaryFile, "rw");
			FileChannel fc = fos.getChannel();
			ByteBuffer bb = fc.map(MapMode.READ_WRITE, 0,
					BufferedNeuralDataSet.DOUBLE_SIZE * 3);
			bb.put(0, (byte) 'E');
			bb.put(1, (byte) 'N');
			bb.put(2, (byte) 'C');
			bb.put(3, (byte) 'O');
			bb.put(4, (byte) 'G');
			bb.put(5, (byte) '-');
			bb.put(6, (byte) '0');
			bb.put(7, (byte) '0');
			bb.order(ByteOrder.LITTLE_ENDIAN);
			DoubleBuffer db = bb.asDoubleBuffer();
			db.put(1, input.length);
			db.put(2, ideal.length);

			this.codec.prepareRead();

			// now transfer the file. we use a new byte buffer for each record.
			// This allows us to not need to know the size of the data being
			// imported. If we determined the size ahead of time, we could use
			// fewer file channels, and likely increase performance. However,
			// write will not be done that often. Read is where we will really
			// attempt to optimize.
			int index = 3;
			while (codec.read(input, ideal)) {
				bb = fc.map(
						MapMode.READ_WRITE,
						index * BufferedNeuralDataSet.DOUBLE_SIZE,
						(input.length * BufferedNeuralDataSet.DOUBLE_SIZE)
								+ (ideal.length * BufferedNeuralDataSet.DOUBLE_SIZE));
				db = bb.asDoubleBuffer();

				for (int i = 0; i < input.length; i++) {
					db.put(input[i]);
				}

				for (int i = 0; i < ideal.length; i++) {
					db.put(ideal[i]);
				}
				index += input.length;
				index += ideal.length;
			}

			fc.close();
			fos.close();
		} catch (IOException ex) {
			throw new BufferedDataError(ex);
		}
	}

	/**
	 * Convert an Encog binary file to an external form, such as CSV.
	 * @param binaryFile
	 */
	public void binary2External(File binaryFile) {
		try {
			FileInputStream fis = new FileInputStream(binaryFile);
			FileChannel fc = fis.getChannel();
			ByteBuffer bb = fc.map(MapMode.READ_ONLY, 0,
					BufferedNeuralDataSet.DOUBLE_SIZE * 3);
			bb.order(ByteOrder.LITTLE_ENDIAN);

			boolean isEncogFile = true;

			isEncogFile = isEncogFile ? bb.get(0) == 'E' : false;
			isEncogFile = isEncogFile ? bb.get(1) == 'N' : false;
			isEncogFile = isEncogFile ? bb.get(2) == 'C' : false;
			isEncogFile = isEncogFile ? bb.get(3) == 'O' : false;
			isEncogFile = isEncogFile ? bb.get(4) == 'G' : false;
			isEncogFile = isEncogFile ? bb.get(5) == '-' : false;

			if (!isEncogFile)
				throw new BufferedDataError(
						"File is not a valid Encog binary file.");

			char v1 = (char) bb.get(6);
			char v2 = (char) bb.get(7);
			String versionStr = "" + v1 + v2;

			try {
				int version = Integer.parseInt(versionStr);
				if (version > 0)
					throw new BufferedDataError(
							"File is from a newer version of Encog than is currently in use.");
			} catch (NumberFormatException ex) {
				throw new BufferedDataError("File has invalid version number.");
			}

			DoubleBuffer db = bb.asDoubleBuffer();

			int inputSize = (int) db.get(1);
			int idealSize = (int) db.get(2);

			int recordSize = inputSize + idealSize;

			int recordCount = (int) ((binaryFile.length() - (BufferedNeuralDataSet.DOUBLE_SIZE * 3)) / (BufferedNeuralDataSet.DOUBLE_SIZE * recordSize));

			double[] input = new double[inputSize];
			double[] ideal = new double[idealSize];

			bb = fc.map(MapMode.READ_ONLY,
					3 * BufferedNeuralDataSet.DOUBLE_SIZE, recordCount
							* recordSize * BufferedNeuralDataSet.DOUBLE_SIZE);

			this.codec.prepareWrite(recordCount, inputSize, idealSize);

			// now load the data
			for (int i = 0; i < recordCount; i++) {
				for (int j = 0; j < inputSize; j++) {
					input[j] = bb.getDouble();
				}

				for (int j = 0; j < idealSize; j++) {
					ideal[j] = bb.getDouble();
				}

				this.codec.write(input, ideal);
			}

			fc.close();
			fis.close();
		} catch (IOException ex) {
			throw new BufferedDataError(ex);
		}
	}

}
