package org.encog.neural.networks.training.propagation;

import org.encog.neural.data.NeuralData;
import org.encog.neural.networks.NeuralOutputHolder;

public interface PropagationMethod {
	
	public void calculateError(
			final NeuralOutputHolder output,
			final PropagationLevel fromLevel,
			final PropagationLevel toLevel);
	
}
