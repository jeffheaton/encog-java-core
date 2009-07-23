package org.encog.neural.networks.training.cpn;

import java.util.Collection;

import org.encog.neural.activation.ActivationCompetitive;
import org.encog.neural.data.NeuralData;
import org.encog.neural.data.NeuralDataPair;
import org.encog.neural.data.NeuralDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.Layer;
import org.encog.neural.networks.synapse.Synapse;
import org.encog.neural.networks.training.BasicTraining;
import org.encog.neural.networks.training.LearningRate;
import org.encog.neural.networks.training.TrainingError;

public class TrainInstar extends BasicTraining implements LearningRate {

	private BasicNetwork network;
	private NeuralDataSet training;
	private double learningRate;
	private boolean mustInit = true;
	private FindCPN parts;
	
	public TrainInstar(BasicNetwork network,NeuralDataSet training,double learningRate)
	{
		this.network = network;
		this.training = training;
		this.learningRate = learningRate;
		this.parts = new FindCPN(network);
	}
	
	public BasicNetwork getNetwork() {
		return this.network;
	}
	
	private void initWeights()
	{
		int j = 0;
		for(NeuralDataPair pair: this.training )
		{
			for(int i=0;i<this.parts.getInstarSynapse().getFromNeuronCount();i++)
			{
				this.parts.getInstarSynapse().getMatrix().set(j++,i,pair.getInput().getData(j));
			}
		}
		
		this.mustInit = false;
	}

	public void iteration() {
		
		if( this.mustInit )
		{
			initWeights();
		}
		
		for(NeuralDataPair pair: this.training )
		{
			NeuralData out = this.parts.getInstarSynapse().compute(pair.getInput());
			int i = this.parts.winner(out);
			for(int j = 0;j<this.parts.getInstarSynapse().getFromNeuronCount();j++)
			{
				double delta = this.learningRate * (pair.getInput().getData(j) 
						- this.parts.getInstarSynapse().getMatrix().get(j,i));
				this.parts.getInstarSynapse().getMatrix().add(j,i,delta);
			}
		}		
	}

	public double getLearningRate() {
		return this.learningRate;
	}

	public void setLearningRate(double rate) {
		this.learningRate = rate;
	}

}
