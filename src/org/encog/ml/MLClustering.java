package org.encog.ml;


/**
 * A machine learning method that is used to break data into clusters.  The 
 * number of clusters is usually defined beforehand.  This differs from 
 * the MLClassification method in that the data is clustered as an entire 
 * group.  If additional data must be clustered later, the entire group 
 * must be reclustered.
 */
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
