package org.encog.neural.networks.training.propagation;

import org.encog.neural.data.NeuralData;

public interface PropagationMethod {

	void init(Propagation propagation);
	void determineDeltas(NeuralData ideal);
}
