package org.encog.neural.networks;

import java.util.HashMap;
import java.util.Map;

import org.encog.neural.data.NeuralData;
import org.encog.neural.networks.synapse.Synapse;

public class NeuralOutputHolder {
	
	private Map<Synapse,NeuralData> result;
	private NeuralData output;
	
	public NeuralOutputHolder()
	{
		this.result = new HashMap<Synapse,NeuralData>();
	}

	public Map<Synapse, NeuralData> getResult() {
		return result;
	}

	public NeuralData getOutput() {
		return output;
	}

	public void setOutput(NeuralData output) {
		this.output = output;
	}
	
	
	
}
