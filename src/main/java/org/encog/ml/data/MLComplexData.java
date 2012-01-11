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
package org.encog.ml.data;

import org.encog.mathutil.ComplexNumber;

/**
 * This class implements a data object that can hold complex numbers.  It 
 * implements the interface MLData, so it can be used with nearly any Encog 
 * machine learning method.  However, not all Encog machine learning methods 
 * are designed to work with complex numbers.  A Encog machine learning method 
 * that does not support complex numbers will only be dealing with the 
 * real-number portion of the complex number. 
 */
public interface MLComplexData extends MLData {

	/**
	 * Add a complex number to the specified index.
	 * @param index The index to use.
	 * @param value The complex number value to add.
	 */
	void add(final int index, final ComplexNumber value);

	/**
	 * @return The complex numbers.
	 */
	ComplexNumber[] getComplexData();

	/**
	 * Get the complex data at the specified index.
	 * @param index The index to get the complex data at.
	 * @return The complex data.
	 */
	ComplexNumber getComplexData(final int index);

	/**
	 * @param theData Set the complex data array.
	 */
	void setData(final ComplexNumber[] theData);

	/**
	 * Set a data element to a complex number.
	 * @param index The index to set.
	 * @param d The complex number.
	 */
	void setData(final int index, final ComplexNumber d);
}
