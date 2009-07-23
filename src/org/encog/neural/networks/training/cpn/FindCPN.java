package org.encog.neural.networks.training.cpn;

import org.encog.neural.data.NeuralData;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.Layer;
import org.encog.neural.networks.synapse.Synapse;
import org.encog.neural.networks.training.TrainingError;

public class FindCPN {
	private Layer inputLayer;
	private Layer instarLayer;
	private Layer outstarLayer;
	private Synapse instarSynapse;
	private Synapse outstarSynapse;
	
	public FindCPN(BasicNetwork network)
	{
		if( network.getStructure().getLayers().size()!=3 )
		{
			throw new TrainingError("A CPN network must have exactly 3 layers");
		}
		
		this.inputLayer = network.getInputLayer();
		this.outstarLayer = network.getOutputLayer();
		this.instarLayer = this.inputLayer.getNext().iterator().next().getToLayer();
		this.instarSynapse = this.inputLayer.getNext().iterator().next();
		this.outstarSynapse = this.instarLayer.getNext().iterator().next();
	}

	public Layer getInputLayer() {
		return inputLayer;
	}

	public Layer getInstarLayer() {
		return instarLayer;
	}

	public Layer getOutstarLayer() {
		return outstarLayer;
	}

	public Synapse getInstarSynapse() {
		return instarSynapse;
	}

	public Synapse getOutstarSynapse() {
		return outstarSynapse;
	}
	
	public int winner(NeuralData data)
	{
		int winner = -1;
		
		for(int i=0;i<data.size();i++)
		{
			if( winner==-1 || data.getData(i)>data.getData(winner))
			{
				winner = i;
			}
		}
		
		return winner;
	}
	
	
}
