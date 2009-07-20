/*
 * Encog Artificial Intelligence Framework v2.x
 * Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 * 
 * Copyright 2008-2009, Heaton Research Inc., and individual contributors.
 * See the copyright.txt in the distribution for a full listing of 
 * individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.encog.neural.networks.training.simple;

import org.encog.neural.NeuralNetworkError;
import org.encog.neural.data.NeuralData;
import org.encog.neural.data.NeuralDataPair;
import org.encog.neural.data.NeuralDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.synapse.Synapse;
import org.encog.neural.networks.training.BasicTraining;

public class TrainAdaline extends BasicTraining {

	private BasicNetwork network;
	private Synapse synapse;
	private NeuralDataSet training;
	
	public TrainAdaline(BasicNetwork network,NeuralDataSet training)
	{
		if( network.getStructure().getLayers().size()>2 )
			throw new NeuralNetworkError("An ADALINE network only has two layers.");
		this.network = network;
		
		this.synapse = network.getInputLayer().getNext().get(0);
		this.training = training;
	}
	
	public BasicNetwork getNetwork() {
		return this.network;
	}

	public void iteration() {	
		
		double error = 0;
		double[] outputError = new double[this.network.getOutputLayer().getNeuronCount()];
		
		for(NeuralDataPair pair: this.training )
		{
			NeuralData output = this.network.compute(pair.getInput());
			for(int i = 0;i<output.size();i++)
			{
				double diff = pair.getIdeal().getData(i) - output.getData(i);
				outputError[i] = diff;
				error+=0.5*Math.sqrt(diff);
			}
		}
		
		this.setError(error);
	}

}
