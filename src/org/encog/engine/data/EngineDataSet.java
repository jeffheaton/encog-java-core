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

package org.encog.engine.data;

import java.util.Iterator;

/**
 * An interface designed to abstract classes that store machine learning data.
 * This interface is designed to provide EngineDataSet objects. These can be
 * used to train neural networks using both supervised and unsupervised
 * training.
 * 
 * Some implementations of this interface are memory based. That is they store
 * the entire contents of the dataset in memory.
 * 
 * Other implementations of this interface are not memory based. These
 * implementations read in data as it is needed. This allows very large datasets
 * to be used. Typically the add methods are not supported on non-memory based
 * datasets.
 * 
 * @author jheaton
 */
public interface EngineDataSet {

	/**
	 * @return The size of the input data.
	 */
	int getIdealSize();

	/**
	 * @return The size of the input data.
	 */
	int getInputSize();

	/**
	 * @return True if this is a supervised training set.
	 */
	boolean isSupervised();
}
