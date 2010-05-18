package org.encog.neural.networks.structure;

import java.util.ArrayList;
import java.util.List;

import org.encog.mathutil.NumericRange;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.Layer;
import org.encog.neural.networks.synapse.Synapse;
import org.encog.util.EncogArray;
import org.encog.util.Format;

/**
 * Allows the weights and bias values of the neural network to be analyzed.
 */
public class AnalyzeNetwork {
	
	/**
	 * The ranges of the weights.
	 */
	private final NumericRange weights;
	
	/**
	 * The ranges of the bias values.
	 */
	private final NumericRange bias;
	
	/**
	 * The ranges of both the weights and biases.
	 */
	private final NumericRange weightsAndBias;
	
	/**
	 * The number of disabled connections.
	 */
	private final int disabledConnections;
	
	/**
	 * The total number of connections.
	 */
	private final int totalConnections;
	
	/**
	 * All of the values in the neural network.
	 */
	private double[] allValues;
	
	/**
	 * The weight values in the neural network.
	 */
	private double[] weightValues;
	
	/**
	 * The bias values in the neural network.
	 */
	private double[] biasValues;
	
	/**
	 * Construct a network analyze class.  Analyze the specified network.
	 * @param network The network to analyze.
	 */
	public AnalyzeNetwork(BasicNetwork network)
	{
		int assignDisabled = 0;
		int assignedTotal = 0;
		List<Double> biasList = new ArrayList<Double>();
		List<Double> weightList = new ArrayList<Double>();
		List<Double> allList = new ArrayList<Double>();	
		
		for(Layer layer: network.getStructure().getLayers() )
		{
			if( layer.hasBias() )
			{
				for(int i=0;i<layer.getNeuronCount();i++) {
					biasList.add(layer.getBiasWeight(i));
					allList.add(layer.getBiasWeight(i));
				}
			}
		}
		
		for(Synapse synapse: network.getStructure().getSynapses() )
		{
			if( synapse.getMatrixSize()>0 )
			{
				for(int from = 0;from<synapse.getFromNeuronCount();from++)
				{
					for(int to = 0;to<synapse.getToNeuronCount();to++)
					{
						if( network.isConnected(synapse, from, to))
						{
							double d = synapse.getMatrix().get(from, to);
							weightList.add(d);
							allList.add(d);
						}
						else
						{
							assignDisabled++;
						}
						assignedTotal++;
					}	
				}
			}
		}
		
		this.disabledConnections = assignDisabled;
		this.totalConnections = assignedTotal;
		this.weights = new NumericRange(weightList);
		this.bias = new NumericRange(biasList);
		this.weightsAndBias = new NumericRange(allList);	
		this.weightValues = EncogArray.listToDouble(weightList);
		this.allValues = EncogArray.listToDouble(allList);
		this.biasValues = EncogArray.listToDouble(biasList);
	}
	
	/**
	 * @return The network analysis as a string.
	 */
	public String toString()
	{
		StringBuilder result = new StringBuilder();
		result.append("All Values : ");
		result.append(this.weightsAndBias.toString());
		result.append("\n");
		result.append("Bias : ");
		result.append(this.bias.toString());
		result.append("\n");
		result.append("Weights    : ");
		result.append(this.weights.toString());
		result.append("\n");
		result.append("Disabled   : ");
		result.append(Format.formatInteger(this.disabledConnections));
		result.append("\n");
		return result.toString();
	}

	/**
	 * @return The numeric range of the weights values.
	 */
	public NumericRange getWeights() {
		return weights;
	}


	/**
	 * @return The numeric range of the bias values.
	 */
	public NumericRange getBias() {
		return bias;
	}

	/**
	 * @return The numeric range of the weights and bias values.
	 */
	public NumericRange getWeightsAndBias() {
		return weightsAndBias;
	}

	/**
	 * @return The number of disabled connections in the network.
	 */
	public int getDisabledConnections() {
		return disabledConnections;
	}

	/**
	 * @return The total number of connections in the network.
	 */
	public int getTotalConnections() {
		return totalConnections;
	}

	/**
	 * @return All of the values in the neural network.
	 */
	public double[] getAllValues() {
		return allValues;
	}

	/**
	 * @return The weight values in the neural network.
	 */
	public double[] getWeightValues() {
		return weightValues;
	}

	/**
	 * @return The bias values in the neural network.
	 */
	public double[] getBiasValues() {
		return biasValues;
	}
	
	
	
	
}
