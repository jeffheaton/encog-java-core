package org.encog.neural.networks.synapse;

import org.encog.neural.data.NeuralData;
import org.encog.neural.data.basic.BasicNeuralData;
import org.encog.neural.networks.layers.BasicLayer;
import org.encog.neural.networks.layers.Layer;

/**
 * A synapse where only some of the neurons are connected. Works like a normal
 * weighted synapse, except that a map is kept to determine if connections are
 * enabled between the two layers.
 * 
 * @author jheaton
 * 
 */
public class PartialSynapse extends WeightedSynapse {

	private boolean[][] enabledMap;
	
	public PartialSynapse(Layer inputLayer, Layer outputLayer) {
		super(inputLayer,outputLayer);
		
		enabledMap = new boolean[inputLayer.getNeuronCount()][outputLayer.getNeuronCount()];
		
		// default everything to "enabled"
		for(int i=0;i<enabledMap.length;i++)
		{
			for(int j=0;j<enabledMap[0].length;i++)
			{
				this.enabledMap[i][j] = true;
			}
		}
	}
	
	public void enableConnection(int fromNeuron, int toNeuron, boolean enabled)
	{
		enabledMap[fromNeuron][toNeuron] = enabled;		
	}
	
	/**
	 * Compute the weighted output from this synapse. Each neuron in the from
	 * layer has a weighted connection to each of the neurons in the next layer.
	 * 
	 * Only connections that are "enabled" between the two layers are used.  If
	 * a connection is not "enabled", the weight will be set to zero.
	 * 
	 * @param input
	 *            The input from the synapse.
	 * @return The output from this synapse.
	 */
	public NeuralData compute(final NeuralData input) {
		final NeuralData result = new BasicNeuralData(getToNeuronCount());

		double[] inputArray = input.getData();
		double[][] matrixArray = getMatrix().getData();
		double[] resultArray = result.getData();

		for (int i = 0; i < getToNeuronCount(); i++) {
			double sum = 0;
			for (int j = 0; j < inputArray.length; j++) {
				if( this.enabledMap[j][i] )
					sum += inputArray[j] * matrixArray[j][i];
				else
					matrixArray[j][i] = 0;
			}
			resultArray[i] = sum;
		}
		return result;
	}
	
	/**
	 * @return The type of synapse this is.
	 */
	public SynapseType getType() {
		return SynapseType.Partial;
	}
	
	public boolean[][] getEnabled()
	{
		return this.enabledMap;
	}
	
	public void setEnabled(boolean[][] e)
	{
		this.enabledMap = e;
	}

}
