package org.encog.neural.networks.training.cpn;

import org.encog.neural.data.NeuralData;
import org.encog.neural.data.NeuralDataPair;
import org.encog.neural.data.NeuralDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.training.BasicTraining;
import org.encog.neural.networks.training.LearningRate;

public class TrainOutstar  extends BasicTraining implements LearningRate  {

	private double learningRate;
	private BasicNetwork network;
	private NeuralDataSet training;
	private boolean mustInit = true;
	private FindCPN parts;
	
	public TrainOutstar(BasicNetwork network,NeuralDataSet training,double learningRate)
	{
		this.network = network;
		this.training = training;
		this.learningRate = learningRate;
		this.parts = new FindCPN(this.network);
	}
	
	public double getLearningRate() {
		return this.learningRate;
	}

	public void setLearningRate(double rate) {
		this.learningRate = rate;
	}

	public BasicNetwork getNetwork() {
		return this.network;
	}
	
	private void initWeight()
	{
		for (int i=0; i<this.parts.getOutstarLayer().getNeuronCount(); i++) {
			int j = 0;
			for(NeuralDataPair pair: this.training)
			{
				this.parts.getOutstarSynapse().getMatrix().set(j++,i,pair.getIdeal().getData(i));
			}
		}
		this.mustInit = false;
	}

	public void iteration() {
		
		if( this.mustInit )
			initWeight();
		   
		for(NeuralDataPair pair: this.training)
		{
			NeuralData out = this.parts.getInstarSynapse().compute(pair.getInput());
			int j = this.parts.winner(out);
			for(int i=0;i<this.parts.getOutstarLayer().getNeuronCount();i++)
			{
				double delta = this.learningRate*(pair.getIdeal().getData(i)-
						this.parts.getOutstarSynapse().getMatrix().get(j,i));
				this.parts.getOutstarSynapse().getMatrix().add(j,i,delta);
			}
		}
	}
}
