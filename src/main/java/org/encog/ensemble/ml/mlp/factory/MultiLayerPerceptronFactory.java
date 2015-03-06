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

import java.util.List;

import org.encog.engine.network.activation.ActivationFunction;
import org.encog.ensemble.EnsembleMLMethodFactory;
import org.encog.ml.MLMethod;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.BasicLayer;

public class MultiLayerPerceptronFactory implements EnsembleMLMethodFactory {

	List<Integer> layers;
	List<Double> dropoutRates;
	ActivationFunction activation;
	ActivationFunction lastLayerActivation;
	int sizeMultiplier = 1;

	public void setParameters(List<Integer> layers, ActivationFunction activation,
			ActivationFunction lastLayerActivation, List<Double> dropoutRates) {
		this.layers=layers;
		this.activation=activation;
		this.lastLayerActivation=lastLayerActivation;
		this.dropoutRates = dropoutRates;
	}

	public void setParameters(List<Integer> layers, ActivationFunction activation,
			ActivationFunction lastLayerActivation){
		setParameters(layers,activation,lastLayerActivation,null);
	}
	
	public void setParameters(List<Integer> layers, ActivationFunction activation){
		setParameters(layers,activation,activation, null);
	}

	public void setParameters(List<Integer> layers, ActivationFunction activation, List<Double> dropoutRates){
		setParameters(layers,activation,activation, dropoutRates);
	}

	@Override
	public MLMethod createML(int inputs, int outputs) {
		BasicNetwork network = new BasicNetwork();
		if(this.dropoutRates != null)
		{
			network.addLayer(new BasicLayer(activation,false,inputs, dropoutRates.get(0))); //(inputs));
		} else {
			network.addLayer(new BasicLayer(activation,false,inputs)); //(inputs));			
		}
		for (int i = 0; i < layers.size(); i++)
		{
			if(this.dropoutRates != null)
			{
				network.addLayer(new BasicLayer(activation,true,layers.get(i) * sizeMultiplier, dropoutRates.get(i + 1)));				
			} else {
				network.addLayer(new BasicLayer(activation,true,layers.get(i) * sizeMultiplier));				
			}
		}
		
		if(dropoutRates != null) {
			network.addLayer(new BasicLayer(lastLayerActivation,true,outputs, dropoutRates.get(dropoutRates.size() - 1)));			
		} else {
			network.addLayer(new BasicLayer(lastLayerActivation,true,outputs));
		}
		network.getStructure().finalizeStructure(dropoutRates != null);
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

	@Override
	public void setSizeMultiplier(int sizeMultiplier) {
		this.sizeMultiplier = sizeMultiplier;
	}

}