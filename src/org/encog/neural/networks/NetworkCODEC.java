package org.encog.neural.networks;

import org.encog.neural.networks.layers.Layer;
import org.encog.neural.networks.synapse.Synapse;

public class NetworkCODEC {

	/**
	 * Private constructor.
	 */
	private NetworkCODEC() {
		
	}
	
	/**
	 * Use an array to populate the memory of the neural network.
	 * 
	 * @param array
	 *            An array of doubles.
	 * @param network The network to encode.
	 */
	public static void arrayToNetwork(final Double[] array,
			final BasicNetwork network) {

		arrayToLayer(network.getInputLayer(),array,0);

	}
	
	private static int arrayToLayer(final Layer layer,final Double[] array, final int index )
	{
		int currentIndex = index;
		for(Synapse synapse: layer.getNext())
		{
			currentIndex = synapse.getMatrix().fromPackedArray(array, currentIndex);
			arrayToLayer(synapse.getToLayer(),array,currentIndex);
		}
		return currentIndex;
	}
	
	
	
	

	/**
	 * Convert to an array. This is used with some training algorithms that
	 * require that the "memory" of the neuron(the weight and threshold values)
	 * be expressed as a linear array.
	 * @param network The network to encode.
	 * @return The memory of the neuron.
	 */
	public static Double[] networkToArray(final BasicNetwork network) {
		int size = 0;

		// first determine size
		for (final Synapse synapse : network.getSynapses()) {
				size += synapse.getMatrixSize();
		}

		// allocate an array to hold
		final Double[] result = new Double[size];

		layerToArray(0,network.getInputLayer(),result);

		return result;
	}
	
	private static int layerToArray(final int index, Layer layer, final Double[] array)
	{
		int currentIndex = index;
		
		for(Synapse synapse: layer.getNext())
		{
			if( synapse.getMatrix()!=null )
			{
				Double[] temp = synapse.getMatrix().toPackedArray();
				for(int i=0;i<temp.length;i++)
				{
					array[currentIndex++] = temp[i];
				}
				currentIndex = layerToArray(currentIndex,synapse.getToLayer(),array);
			}
		}
		return currentIndex;
	}
	
	
}
