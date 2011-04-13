package org.encog.ml;

import java.util.List;

import org.encog.neural.data.NeuralData;
import org.encog.neural.data.NeuralDataSet;

/**
 * Defines a cluster.  Usually used with the MLClustering method to 
 * break input into clusters.
 */
public interface MLCluster {
	
	void add(final NeuralData pair);
	NeuralDataSet createDataSet();
	NeuralData get(final int pos);
	List<NeuralData> getData();
	void remove(final NeuralData data);
	int size();
}
