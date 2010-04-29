package org.encog.neural.networks.structure;

import java.util.ArrayList;
import java.util.List;

import org.encog.mathutil.NumericRange;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.Layer;
import org.encog.neural.networks.synapse.Synapse;
import org.encog.util.Format;

public class AnalyzeNetwork {
	
	private final NumericRange weights;
	private final NumericRange thresholds;
	private final NumericRange weightsAndThresholds;
	private final int disabledConnections;
	
	public AnalyzeNetwork(BasicNetwork network)
	{
		int assignDisabled = 0;
		List<Double> thresholdValues = new ArrayList<Double>();
		List<Double> weightValues = new ArrayList<Double>();
		List<Double> allValues = new ArrayList<Double>();	
		
		for(Layer layer: network.getStructure().getLayers() )
		{
			if( layer.hasThreshold() )
			{
				for(int i=0;i<layer.getNeuronCount();i++) {
					thresholdValues.add(layer.getThreshold(i));
					allValues.add(layer.getThreshold(i));
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
							weightValues.add(d);
							allValues.add(d);
						}
						else
						{
							assignDisabled++;
						}
					}	
				}
			}
		}
		
		this.disabledConnections = assignDisabled;
		this.weights = new NumericRange(weightValues);
		this.thresholds = new NumericRange(thresholdValues);
		this.weightsAndThresholds = new NumericRange(allValues);		
	}
	
	public String toString()
	{
		StringBuilder result = new StringBuilder();
		result.append("All Values : ");
		result.append(this.weightsAndThresholds.toString());
		result.append("\n");
		result.append("Thresholds : ");
		result.append(this.thresholds.toString());
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

	public NumericRange getThresholds() {
		return thresholds;
	}

	public NumericRange getWeightsAndThresholds() {
		return weightsAndThresholds;
	}

	public int getDisabledConnections() {
		return disabledConnections;
	}
	
	
	
}
