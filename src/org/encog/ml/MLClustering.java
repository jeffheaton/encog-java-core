package org.encog.ml;


public interface MLClustering extends MLMethod {
	public void iteration();
	public void iteration(final int count);
	/**
	 * @return The clusters.
	 */
	public MLCluster[] getClusters();

	/**
	 * @return The number of clusters.
	 */
	public int numClusters();
}
