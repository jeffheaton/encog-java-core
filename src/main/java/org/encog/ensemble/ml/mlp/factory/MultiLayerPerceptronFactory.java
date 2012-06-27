package org.encog.ensemble.ml.mlp.factory;

import java.util.Collection;
import java.util.Iterator;

import org.encog.engine.network.activation.ActivationFunction;
import org.encog.ensemble.EnsembleMLMethodFactory;
import org.encog.ml.MLMethod;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.BasicLayer;

public class MultiLayerPerceptronFactory implements EnsembleMLMethodFactory {

	Collection<Integer> layers;
	ActivationFunction activation;
	
	public void setParameters(Collection<Integer> layers, ActivationFunction activation){
		this.layers=layers;
		this.activation=activation;
	}
	
	@Override
	public MLMethod createML(int inputs, int outputs) {
		BasicNetwork network = new BasicNetwork();
		network.addLayer(new BasicLayer(inputs));
		for (Integer layerSize: layers)
			network.addLayer(new BasicLayer(layerSize));
		network.addLayer(new BasicLayer(outputs));
		network.getStructure().finalizeStructure();
		network.reset();
		return network;
	}

}
