package org.encog.neural.networks.logic;

import java.io.Serializable;

import org.encog.neural.data.NeuralData;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.NeuralOutputHolder;
import org.encog.neural.networks.layers.Layer;
import org.encog.neural.networks.synapse.Synapse;

public interface NeuralLogic extends Serializable {
	
	NeuralData compute(NeuralData input,
			NeuralOutputHolder useHolder);
	void init(BasicNetwork network);

}
