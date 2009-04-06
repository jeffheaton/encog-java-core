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
 * Defines an array of data.  This is an array of double values that could
 * be used either for input data, actual output data or ideal output data.
 * @author jheaton
 */
public interface NeuralData extends Cloneable {

	/**
	 * Set all of the data as an array of doubles.
	 * @param data An array of doubles. 
	 */
	void setData(double[] data);
	
	/**
	 * Set the specified element.
	 * @param index The index to set.
	 * @param d The data for the specified element.
	 */
	void setData(int index, double d);
	
	/**	
	 * @return All of the elements as an array.
	 */
	double[] getData();
	
	/**
	 * Get the element specified index value.  
	 * @param index The index to read.
	 * @return The value at the specified inedx.
	 */
	double getData(int index);
	
	/**
	 * @return How many elements are stored in this object.
	 */
	int size();
	
	NeuralData clone();
	
}
