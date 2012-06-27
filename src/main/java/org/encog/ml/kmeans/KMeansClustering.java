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
import org.encog.ml.MLClustering;
import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.basic.BasicMLDataPair;
import org.encog.util.kmeans.KMeansUtil;

/**
 * This class performs a basic K-Means clustering. This class can be used on
 * either supervised or unsupervised data. For supervised data, the ideal values
 * will be ignored.
 * 
 * http://en.wikipedia.org/wiki/Kmeans
 * 
 */
public class KMeansClustering implements MLClustering {

	/**
	 * The kmeans utility.
	 */
	private KMeansUtil<BasicMLDataPair> kmeans;
	
	/**
	 * The clusters
	 */
	private MLCluster[] clusters;
	private int k;

	/**
	 * Construct the K-Means object.
	 * 
	 * @param k
	 *            The number of clusters to use.
	 * @param theSet
	 *            The dataset to cluster.
	 */
	public KMeansClustering(final int theK, final MLDataSet theSet) {
		List<BasicMLDataPair> list = new ArrayList<BasicMLDataPair>();
		for(MLDataPair pair: theSet) {
			list.add((BasicMLDataPair)pair);
		}
		this.k = theK;
		this.kmeans = new KMeansUtil<BasicMLDataPair>(this.k,list);
	}


	/**
	 * Perform a single training iteration.
	 */
	@Override
	public final void iteration() {
		this.kmeans.process();
		this.clusters = new MLCluster[this.k];
		for(int i=0;i<this.k;i++) {
			this.clusters[i] = new BasicCluster(this.kmeans.getCluster(i));
		}

	}

	/**
	 * The number of iterations to perform.
	 * 
	 * @param count
	 *            The count of iterations.
	 */
	@Override
	public final void iteration(final int count) {
		for (int i = 0; i < count; i++) {
			iteration();
		}
	}


	/**
	 * @return The clusters.
	 */
	@Override
	public MLCluster[] getClusters() {
		return this.clusters;
	}


	/**
	 * @return The number of clusters.
	 */
	@Override
	public int numClusters() {
		return this.k;
	}


}
