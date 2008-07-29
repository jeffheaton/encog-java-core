package org.encog.neural.data.basic;

import org.encog.neural.data.NeuralData;
import org.encog.neural.data.NeuralDataPair;

public class BasicNeuralDataPair implements NeuralDataPair {

	private NeuralData ideal;
	private NeuralData input;
	
	public BasicNeuralDataPair(NeuralData input,NeuralData ideal)
	{
		this.input = input;
		this.ideal = ideal;
	}
	
	@Override
	public NeuralData getIdeal() {
		return this.ideal;
	}

	@Override
	public NeuralData getInput() {
		return this.input;
	}

}
