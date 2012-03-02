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
package org.encog.ml.kmeans;

import java.util.ArrayList;
import java.util.List;

import org.encog.ml.MLCluster;
import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.basic.BasicMLDataPair;
import org.encog.ml.data.basic.BasicMLDataPairCentroid;
import org.encog.ml.data.basic.BasicMLDataSet;
import org.encog.util.kmeans.Centroid;
import org.encog.util.kmeans.Cluster;

/**
 * Holds a cluster of MLData items that have been clustered 
 * by the KMeansClustering class.
 */
public class BasicCluster implements MLCluster {

	/**
	 * The centroid.
	 */
	private BasicMLDataPairCentroid centroid;
	
	/**
	 * The contents of the cluster.
	 */
	private final List<MLData> data = new ArrayList<MLData>();

	/**
	 * Construct a cluster from another.
	 * @param cluster The other cluster.
	 */
	public BasicCluster(Cluster<BasicMLDataPair> cluster) {
		this.centroid = (BasicMLDataPairCentroid)cluster.centroid();
		for(MLDataPair pair : cluster.getContents()) {
			this.data.add(pair.getInput());
		}
	}

	/**
	 * Add to the cluster.
	 * @param pair The pair to add.
	 */
	@Override
	public final void add(final MLData pair) {
		this.data.add(pair);
	}

	/**
	 * Create a dataset from the clustered data.
	 * @return The dataset.
	 */
	@Override
	public final MLDataSet createDataSet() {
		final MLDataSet result = new BasicMLDataSet();

		for (final MLData dataItem : this.data) {
			result.add(dataItem);
		}

		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final MLData get(final int pos) {
		return this.data.get(pos);
	}

	/**
	 * @return The centroid.
	 */
	public final Centroid<?> getCentroid() {
		return this.centroid;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final List<MLData> getData() {
		return this.data;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void remove(final MLData pair) {
		this.data.remove(pair);
	}

	/**
	 * Set the centroid.
	 * @param c The new centroid.
	 */
	public final void setCentroid(final BasicMLDataPairCentroid c) {
		this.centroid = c;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final int size() {
		return this.data.size();
	}

}
