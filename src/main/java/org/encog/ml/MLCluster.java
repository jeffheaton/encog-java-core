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
package org.encog.ml;

import java.util.List;

import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataSet;

/**
 * Defines a cluster. Usually used with the MLClustering method to break input
 * into clusters.
 */
public interface MLCluster {

	/**
	 * Add data to this cluster.
	 * @param pair The data to add.
	 */
	void add(final MLData pair);

	/**
	 * Create a machine learning dataset from the data.
	 * @return A dataset.
	 */
	MLDataSet createDataSet();

	/**
	 * Get the specified data item by index.
	 * @param pos The index of the data item to get.
	 * @return The data item.
	 */
	MLData get(final int pos);

	/**
	 * @return The data in this cluster.
	 */
	List<MLData> getData();

	/**
	 * Remove the specified item.
	 * @param data The item to remove.
	 */
	void remove(final MLData data);

	/**
	 * @return The number of items.
	 */
	int size();
}
