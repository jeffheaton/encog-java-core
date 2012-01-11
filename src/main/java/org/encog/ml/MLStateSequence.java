package org.encog.ml;

import java.util.List;

import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.MLDataSet;

public interface MLStateSequence extends MLMethod {
	public int[] getStatesForSequence(MLDataSet oseq);
	public double probability(MLDataSet oseq);
	public double probability(MLDataSet seq, int[] states);
}
