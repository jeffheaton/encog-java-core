/*
 * Encog(tm) Core v2.6 - Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 
 * Copyright 2008-2010 Heaton Research, Inc.
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
package org.encog.engine.data;

/**
 * Specifies that a data set can be accessed in random order via an index. This
 * property is required for multi-threaded training. 
 */
public interface EngineIndexableSet extends EngineDataSet {
	
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
	void getRecord(long index, EngineData pair);

	/**
	 * Opens an additional instance of this dataset.  
	 * @return The new instance.
	 */
	EngineIndexableSet openAdditional();
}
