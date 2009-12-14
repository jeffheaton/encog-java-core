package org.encog.neural.networks.training.propagation;

import org.encog.neural.data.NeuralData;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.NeuralOutputHolder;
import org.encog.neural.networks.layers.Layer;
import org.encog.neural.networks.synapse.Synapse;

import java.util.HashMap;
import java.util.Map;

public class PropagateErrors {

	private BasicNetwork network;
	private Map<Layer, Object> layerDeltas = new HashMap<Layer, Object>();
	private double[] errors;
	private NeuralOutputHolder holder;

	public PropagateErrors(BasicNetwork network) {
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

	public void calculate(NeuralData input, NeuralData ideal) {
		clearDeltas();
		
		Layer output = this.network.getLayer(BasicNetwork.TAG_OUTPUT);
		NeuralData actual = this.network.compute(input, this.holder);
		double deltas[] = getLayerDeltas(output);

		for (int i = 0; i < deltas.length; i++) {
			deltas[i] = actual.getData(i);
		}

		// take the derivative of these outputs
		output.getActivationFunction().derivativeFunction(deltas);

		// multiply by the difference between the actual and idea
		for (int i = 0; i < output.getNeuronCount(); i++) {
			deltas[i] = deltas[i] * (ideal.getData(i) - actual.getData(i));
		}

		int index = 0;
		Layer lastLayer = null;
		
		// calculate for each synapse
		for (Synapse synapse : this.network.getStructure().getSynapses()) {
			if( synapse.getToLayer()!=lastLayer ) {
				lastLayer = synapse.getToLayer();
				index+=synapse.getToNeuronCount();
			}
			index = calculate(synapse,index);
		}
	}

	private int calculate(Synapse synapse, int index) {
		
		double toDeltas[] = getLayerDeltas(synapse.getToLayer());
		double fromDeltas[] = getLayerDeltas(synapse.getFromLayer());
		
		NeuralData actual = this.holder.getResult().get(synapse);
		
		for (int x = 0; x < synapse.getToNeuronCount(); x++) {
			for (int y = 0; y < synapse.getFromNeuronCount(); y++) {
				errors[index++] = actual.getData(y)*toDeltas[x];
			}
		}
		
		return index;
	}

	public void clear() {
		for (int i = 0; i < this.errors.length; i++)
			this.errors[i] = 0;
	}

}
