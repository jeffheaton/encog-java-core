/*
 * Encog(tm) Core v3.3 - Java Version
 * http://www.heatonresearch.com/encog/
 * https://github.com/encog/encog-java-core
 
 * Copyright 2008-2014 Heaton Research, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *   
 * For more information on Heaton Research copyrights, licenses 
 * and trademarks visit:
 * http://www.heatonresearch.com/copyright
 */
package org.encog.ensemble.ml.mlp.factory;

import java.util.Collection;

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
		network.addLayer(new BasicLayer(activation,false,inputs)); //(inputs));
		for (Integer layerSize: layers)
			network.addLayer(new BasicLayer(activation,true,layerSize));
		network.addLayer(new BasicLayer(activation,true,outputs));
		network.getStructure().finalizeStructure();
		network.reset();
		return network;
	}

	@Override
	public String getLabel() {
		String ret = "mlp{";
		for (int i=0; i < layers.size() - 1; i++)
			ret = ret + layers.toArray()[i] + ",";
		return ret + layers.toArray()[layers.size() - 1] + "}";
	}

	@Override
	public void reInit(MLMethod ml) {
		((BasicNetwork) ml).reset();
	}

}
