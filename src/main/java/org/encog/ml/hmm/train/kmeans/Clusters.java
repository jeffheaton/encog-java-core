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
package org.encog.ml.hmm.train.kmeans;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.List;

import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.MLDataSet;
import org.encog.util.kmeans.KMeansUtil;

/**
 * Clusters used for the KMeans HMM training algorithm.
 *
 */
public class Clusters {
	private final Hashtable<MLDataPair, Integer> clustersHash;
	private final ArrayList<Collection<MLDataPair>> clusters;

	public Clusters(final int k, final MLDataSet observations) {

		this.clustersHash = new Hashtable<MLDataPair, Integer>();
		this.clusters = new ArrayList<Collection<MLDataPair>>();

		final List<MLDataPair> list = new ArrayList<MLDataPair>();
		for (final MLDataPair pair : observations) {
			list.add(pair);
		}
		final KMeansUtil<MLDataPair> kmc = new KMeansUtil<MLDataPair>(k, list);
		kmc.process();

		for (int i = 0; i < k; i++) {
			final Collection<MLDataPair> cluster = kmc.get(i);
			this.clusters.add(cluster);

			for (final MLDataPair element : cluster) {
				this.clustersHash.put(element, i);
			}
		}
	}

	public Collection<MLDataPair> cluster(final int clusterNb) {
		return this.clusters.get(clusterNb);
	}

	public int cluster(final MLDataPair o) {
		return this.clustersHash.get(o);
	}

	public boolean isInCluster(final MLDataPair o, final int x) {
		return cluster(o) == x;
	}

	public void put(final MLDataPair o, final int clusterNb) {
		this.clustersHash.put(o, clusterNb);
		this.clusters.get(clusterNb).add(o);
	}

	public void remove(final MLDataPair o, final int clusterNb) {
		this.clustersHash.put(o, -1);
		this.clusters.get(clusterNb).remove(o);
	}
}
