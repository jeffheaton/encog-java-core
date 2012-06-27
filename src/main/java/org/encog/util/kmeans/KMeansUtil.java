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
package org.encog.util.kmeans;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Generic KMeans clustering object.
 *
 * @param <K> The type to cluster.
 */
public class KMeansUtil<K extends CentroidFactory<? super K>> {
	
	/**
	 * The clusters.
	 */
	private final ArrayList<Cluster<K>> clusters;
	
	/**
	 * The number of clusters.
	 */
	private final int k;

	/**
	 * Construct the clusters.  Call process to perform the cluster.
	 * @param theK The number of clusters.
	 * @param theElements The elements to cluster.
	 */
	public KMeansUtil(int theK, List<? extends K> theElements) {
		this.k = theK;
		clusters = new ArrayList<Cluster<K>>(theK);
		initRandomClusters(theElements);
	}

	/**
	 * Create random clusters.
	 * @param elements The elements to cluster.
	 */
	private void initRandomClusters(List<? extends K> elements) {

		int clusterIndex = 0;
		int elementIndex = 0;

		// first simply fill out the clusters, until we run out of clusters
		while ((elementIndex < elements.size()) && (clusterIndex < k)
				&& (elements.size() - elementIndex > k - clusterIndex)) {
			K element = elements.get(elementIndex);

			boolean added = false;

			// if this element is identical to another, add it to this cluster
			for (int i = 0; i < clusterIndex; i++) {
				Cluster<K> cluster = clusters.get(i);

				if (cluster.centroid().distance(element) == 0) {
					cluster.add(element);
					added = true;
					break;
				}
			}

			if (!added) {
				clusters.add(new Cluster<K>(elements.get(elementIndex)));
				clusterIndex++;
			}
			elementIndex++;
		}

		// create
		while (clusterIndex < k && elementIndex < elements.size()) {
			clusters.add(new Cluster<K>(elements.get(elementIndex)));
			elementIndex++;
			clusterIndex++;
		}

		// handle case where there were not enough clusters created, 
		// create empty ones.
		while (clusterIndex < k) {
			clusters.add(new Cluster<K>());
			clusterIndex++;
		}

		// otherwise, handle case where there were still unassigned elements
		// add them to the nearest clusters.
		while (elementIndex < elements.size()) {
			K element = elements.get(elementIndex);
			nearestCluster(element).add(element);
			elementIndex++;
		}

	}

	/**
	 * Perform the cluster.
	 */
	public void process() {

		boolean done;
		do {
			done = true;

			for (int i = 0; i < k; i++) {
				Cluster<K> thisCluster = clusters.get(i);
				List<K> thisElements = thisCluster.getContents();

				for (int j = 0; j < thisElements.size(); j++) {
					K thisElement = thisElements.get(j);

					// don't make a cluster empty
					if (thisCluster.centroid().distance(thisElement) > 0) {
						Cluster<K> nearestCluster = nearestCluster(thisElement);

						// move to nearer cluster
						if (thisCluster != nearestCluster) {
							nearestCluster.add(thisElement);
							thisCluster.remove(j);
							done = false;
						}
					}
				}
			}
		} while (!done);
	}

	/**
	 * Find the nearest cluster to the element.
	 * @param element The element.
	 * @return The nearest cluster.
	 */
	private Cluster<K> nearestCluster(K element) {
		double distance = Double.MAX_VALUE;
		Cluster<K> result = null;

		for (int i = 0; i < clusters.size(); i++) {
			double thisDistance = clusters.get(i).centroid().distance(element);

			if (distance > thisDistance) {
				distance = thisDistance;
				result = clusters.get(i);
			}
		}

		return result;
	}

	/**
	 * Get a cluster by index.
	 * @param index The index to get.
	 * @return The cluster.
	 */
	public Collection<K> get(int index) {
		return clusters.get(index).getContents();
	}

	/**
	 * @return The number of clusters.
	 */
	public int size() {
		return clusters.size();
	}

	/**
	 * Get a cluster by index.
	 * @param index The index to get.
	 * @return The cluster.
	 */
	public Cluster<K> getCluster(int i) {
		return this.clusters.get(i);
	}
}
