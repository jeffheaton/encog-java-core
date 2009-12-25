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
package org.encog.neural.networks.training.propagation.gradient;

import java.util.HashMap;
import java.util.Map;

import org.encog.neural.data.NeuralData;
import org.encog.neural.data.NeuralDataPair;
import org.encog.neural.data.NeuralDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.NeuralOutputHolder;
import org.encog.neural.networks.layers.Layer;
import org.encog.neural.networks.synapse.Synapse;
import org.encog.util.ErrorCalculation;

public class GradientUtil {
	private BasicNetwork network;
	private Map<Layer, Object> layerDeltas = new HashMap<Layer, Object>();
	private double[] errors;
	private double[] weights;
	private NeuralOutputHolder holder;
	private ErrorCalculation error = new ErrorCalculation();
	private int count;
	
	public GradientUtil(BasicNetwork network) {
		this.network = network;
		int size = network.getStructure().calculateSize();
		this.errors = new double[size];
		this.holder = new NeuralOutputHolder();
	}

	public double[] getErrors() {
		return this.errors;
	}

	private double[] getLayerDeltas(Layer layer) {
		if (this.layerDeltas.containsKey(layer)) {
			return (double[]) this.layerDeltas.get(layer);
		}

		double[] result = new double[layer.getNeuronCount()];
		this.layerDeltas.put(layer, result);
		return result;
	}
	
	private void clearDeltas()
	{
		for(Object obj: this.layerDeltas.values())
		{
			double[] d = (double[])obj;
			for(int i=0;i<d.length;i++) {
				d[i] = 0;
			}
		}
	}
	
	public void calculate(NeuralDataSet training, double[] weights) {
		reset(weights);
		for(NeuralDataPair pair: training) {
			calculate(pair.getInput(), pair.getIdeal());
		}				
	}

	public void calculate(NeuralData input, NeuralData ideal) {
		clearDeltas();
		this.count++;
		this.holder = new NeuralOutputHolder();
		Layer output = this.network.getLayer(BasicNetwork.TAG_OUTPUT);
		NeuralData actual = this.network.compute(input, this.holder);
		
		this.error.updateError(actual.getData(), ideal.getData());
		
		double deltas[] = getLayerDeltas(output);
		double idealData[] = ideal.getData();
		double actualData[] = actual.getData();

		if ( output.getActivationFunction().hasDerivative() ) {
			for (int i = 0; i < deltas.length; i++) {
				deltas[i] = actual.getData(i);
			}

			// take the derivative of these outputs
			output.getActivationFunction().derivativeFunction(deltas);

			// multiply by the difference between the actual and idea
			for (int i = 0; i < output.getNeuronCount(); i++) {
				deltas[i] = deltas[i] * (idealData[i] - actualData[i]);
			}
		}
		else
		{
			for (int i = 0; i < output.getNeuronCount(); i++) 
				deltas[i] = (idealData[i] - actualData[i]);
		}

		int index = 0;
		for(Layer layer: this.network.getStructure().getLayers()) {
			double layerDeltas[] = getLayerDeltas(layer);
			
			if( layer.hasThreshold() ) {
				for(int i=0;i<layerDeltas.length;i++) {
					this.errors[index++] += layerDeltas[i]; 
				}	
			}
			
			for(Synapse synapse: this.network.getStructure().getPreviousSynapses(layer)) {
				if( synapse.getMatrix()!=null )
					index = calculate(synapse,index);
			}			
		}		
	}

	private int calculate(Synapse synapse, int index) {
		
		double toDeltas[] = getLayerDeltas(synapse.getToLayer());
		double fromDeltas[] = getLayerDeltas(synapse.getFromLayer());
		
		NeuralData actual = this.holder.getResult().get(synapse);
		double[] actualData = actual.getData();
		
		for (int x = 0; x < synapse.getToNeuronCount(); x++) {
			for (int y = 0; y < synapse.getFromNeuronCount(); y++) {
				double value = actualData[y]*toDeltas[x];
				errors[index] += value;
				fromDeltas[y] +=  this.weights[index]*toDeltas[x];
				index++;
			}
		}
		
		double[] temp = new double[fromDeltas.length];

		for (int i = 0; i < fromDeltas.length; i++) {
			temp[i] = actualData[i];
		}

		// get an activation function to use
		synapse.getFromLayer().getActivationFunction().derivativeFunction(temp);

		for (int i = 0; i < temp.length; i++) {
			fromDeltas[i] *= temp[i];
		}
				
		return index;
	}

	public void reset(double[] weights) {
		this.error.reset();
		this.weights = weights;
		this.layerDeltas.clear();
		this.count = 0;
		
		for (int i = 0; i < this.errors.length; i++)
			this.errors[i] = 0;
	}

	public double getError() {
		return this.error.calculateRMS();
	}
	
	public int getCount() {
		return this.count;
	}
	

}
