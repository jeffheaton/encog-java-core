package org.encog.util.kmeans;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class KMeansUtil<K extends CentroidFactory<? super K>> {
	private final ArrayList<Cluster<K>> clusters;
	private final int k;

	public KMeansUtil(int theK, List<? extends K> theElements) {
		this.k = theK;
		clusters = new ArrayList<Cluster<K>>(theK);
		initRandomClusters(theElements);
	}

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
				elementIndex++;
			}
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

	public Collection<K> get(int index) {
		return clusters.get(index).getContents();
	}

	public int size() {
		return clusters.size();
	}

	public Cluster<K> getCluster(int i) {
		return this.clusters.get(i);
	}
}
