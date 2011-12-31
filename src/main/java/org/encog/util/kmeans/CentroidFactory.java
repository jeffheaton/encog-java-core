package org.encog.util.kmeans;

public interface CentroidFactory<O>
{
	public Centroid<O> createCentroid();
}
