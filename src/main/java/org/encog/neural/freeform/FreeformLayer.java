package org.encog.neural.freeform;

import java.util.List;

public interface FreeformLayer {
	List<FreeformNeuron> getNeurons();
	int size();
	void add(FreeformNeuron basicFreeformNeuron);
	void setActivation(int i, double data);
	int sizeNonBias();
	boolean hasBias();
}
