/*
 * Encog Artificial Intelligence Framework v2.x
 * Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 * 
 * Copyright 2008-2009, Heaton Research Inc., and individual contributors.
 * See the copyright.txt in the distribution for a full listing of 
 * individual contributors.
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
 */
package org.encog.neural.data;

/**
 * Specifies that a data set can be accessed in random order via an index. This
 * property is required for MPROP training. 
 */
public interface Indexable extends NeuralDataSet {
	
	/**
	 * Determine the total number of records in the set.
	 * @return The total number of records in the set.
	 */
	long getRecordCount();

	/**
	 * Read an individual record, specified by index, in random order.
	 * @param index The index to read.
	 * @param pair The pair that the record will be copied into.
	 */
	void getRecord(long index, NeuralDataPair pair);

	/**
	 * Opens an additional instance of this dataset.  
	 * @return The new instance.
	 */
	Indexable openAdditional();
}
