package org.encog.ensemble.ml.mlp.factory;

import java.util.Collection;
import java.util.Iterator;

import org.encog.engine.network.activation.ActivationFunction;
import org.encog.engine.network.activation.ActivationSigmoid;
import org.encog.ensemble.EnsembleML;
import org.encog.ensemble.EnsembleMLFactory;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.BasicLayer;

public class MultiLayerPerceptronFactory implements EnsembleMLFactory {

	Collection<Integer> layers;
	ActivationFunction activation;
	
	public void setParameters(Collection<Integer> layers, ActivationFunction activation){
		this.layers=layers;
		this.activation=activation;
	}
	
	@Override
	public EnsembleML createML(int inputs, int outputs) {
		BasicNetwork network = new BasicNetwork();
		network.addLayer(new BasicLayer(null,true,inputs));
		for (Iterator<Integer> layerSize=layers.iterator();layerSize.hasNext();)
			network.addLayer(new BasicLayer(activation,true,layerSize.next()));
		network.addLayer(new BasicLayer(activation,false,outputs));
		network.getStructure().finalizeStructure();
		network.reset();
		return (EnsembleML) network;
	}

}
