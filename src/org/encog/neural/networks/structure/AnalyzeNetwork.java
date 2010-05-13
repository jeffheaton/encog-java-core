package org.encog.neural.networks.structure;

import java.util.ArrayList;
import java.util.List;

import org.encog.mathutil.NumericRange;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.Layer;
import org.encog.neural.networks.synapse.Synapse;
import org.encog.util.EncogArray;
import org.encog.util.Format;

public class AnalyzeNetwork {
	
	private final NumericRange weights;
	private final NumericRange bias;
	private final NumericRange weightsAndBias;
	private final int disabledConnections;
	private final int totalConnections;
	private double[] allValues;
	private double[] weightValues;
	private double[] biasValues;
	
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

	public NumericRange getWeights() {
		return weights;
	}

	public NumericRange getBias() {
		return bias;
	}

	public NumericRange getWeightsAndBias() {
		return weightsAndBias;
	}

	public int getDisabledConnections() {
		return disabledConnections;
	}

	public int getTotalConnections() {
		return totalConnections;
	}

	public double[] getAllValues() {
		return allValues;
	}

	public double[] getWeightValues() {
		return weightValues;
	}

	public double[] getBiasValues() {
		return biasValues;
	}
	
	
	
	
}
