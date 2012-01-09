package org.encog.ml.data;

import java.util.Collection;

public interface MLSequenceSet extends MLDataSet {
	void startNewSequence();
	int getSequenceCount();
	MLDataSet getSequence(int i);
	Collection<MLDataSet> getSequences();
	void add(MLDataSet sequence);
}
