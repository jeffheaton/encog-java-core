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

package org.encog.neural.data.buffer.codec;


/**
 * A CODEC is used to encode and decode data. The DataSetCODEC is designed to
 * move data to/from the Encog binary training file format, used by
 * BufferedNeuralDataSet.  CODECs are provided for such items as CSV
 * files, arrays and many other sources.
 * 
 */
public interface DataSetCODEC  {

	/**
	 * Read one record of data from an external source.
	 * @param input The input data array.
	 * @param ideal The ideal data array.
	 * @return True, if there is more data to be read.
	 */
	boolean read(double[] input, double[] ideal);
	
	/**
	 * Write one record of data to an external destination.
	 * @param input The input data array.
	 * @param ideal The ideal data array.
	 */
	void write(double[] input, double[] ideal);
	
	/**
	 * Prepare to write to an external data destination.
	 * @param recordCount The total record count, that will be written.
	 * @param inputSize The input size.
	 * @param idealSize The ideal size.
	 */
	void prepareWrite(int recordCount,int inputSize, int idealSize);
	
	/**
	 * Prepare to read from an external data source.
	 */
	void prepareRead();
	
	/**
	 * @return The size of the input data.
	 */
	int getInputSize();
	
	/**
	 * @return The size of the ideal data.
	 */
	int getIdealSize();
	
}
